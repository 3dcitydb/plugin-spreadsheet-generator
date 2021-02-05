/*
 * 3D City Database - The Open Source CityGML Database
 * http://www.3dcitydb.org/
 *
 * Copyright 2013 - 2020
 * Chair of Geoinformatics
 * Technical University of Munich, Germany
 * https://www.gis.bgu.tum.de/
 *
 * The 3D City Database is jointly developed with the following
 * cooperation partners:
 *
 * virtualcitySYSTEMS GmbH, Berlin <http://www.virtualcitysystems.de/>
 * M.O.S.S. Computer Grafik Systeme GmbH, Taufkirchen <http://www.moss.de/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.citydb.plugins.spreadsheet_gen.controller;

import com.csvreader.CsvReader;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.citydb.concurrent.PoolSizeAdaptationStrategy;
import org.citydb.concurrent.SingleWorkerPool;
import org.citydb.concurrent.WorkerPool;
import org.citydb.config.project.database.Workspace;
import org.citydb.config.project.query.QueryConfig;
import org.citydb.config.project.query.filter.selection.SelectionFilter;
import org.citydb.config.project.query.filter.selection.spatial.BBOXOperator;
import org.citydb.database.adapter.AbstractDatabaseAdapter;
import org.citydb.database.connection.DatabaseConnectionPool;
import org.citydb.database.schema.mapping.SchemaMapping;
import org.citydb.event.Event;
import org.citydb.event.EventDispatcher;
import org.citydb.event.EventHandler;
import org.citydb.event.global.EventType;
import org.citydb.event.global.InterruptEvent;
import org.citydb.event.global.ObjectCounterEvent;
import org.citydb.event.global.StatusDialogTitle;
import org.citydb.log.Logger;
import org.citydb.plugins.spreadsheet_gen.concurrent.CSVWriterFactory;
import org.citydb.plugins.spreadsheet_gen.concurrent.SPSHGWorker;
import org.citydb.plugins.spreadsheet_gen.concurrent.SPSHGWorkerFactory;
import org.citydb.plugins.spreadsheet_gen.concurrent.work.CityObjectWork;
import org.citydb.plugins.spreadsheet_gen.concurrent.work.RowofCSVWork;
import org.citydb.plugins.spreadsheet_gen.config.ConfigImpl;
import org.citydb.plugins.spreadsheet_gen.config.Output;
import org.citydb.plugins.spreadsheet_gen.config.OutputFileType;
import org.citydb.plugins.spreadsheet_gen.database.DBManager;
import org.citydb.plugins.spreadsheet_gen.database.Translator;
import org.citydb.plugins.spreadsheet_gen.gui.datatype.SeparatorPhrase;
import org.citydb.plugins.spreadsheet_gen.util.Util;
import org.citydb.query.Query;
import org.citydb.query.builder.QueryBuildException;
import org.citydb.query.builder.config.ConfigQueryBuilder;
import org.citydb.registry.ObjectRegistry;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpreadsheetExporter implements EventHandler {
    private final Logger log = Logger.getInstance();
    private final DatabaseConnectionPool dbPool;
    private final AbstractDatabaseAdapter databaseAdapter;
    private final ConfigImpl config;
    private final EventDispatcher eventDispatcher;
    private final SchemaMapping schemaMapping;
    private final AtomicBoolean isInterrupted = new AtomicBoolean(false);
    private final Map<Integer, Long> featureCounter;

    private volatile boolean shouldRun = true;
    private TableExportException exception;

    private WorkerPool<CityObjectWork> workerPool;
    private DBManager dbm = null;

    public SpreadsheetExporter(ConfigImpl pluginConfig) {
        config = pluginConfig;
        dbPool = DatabaseConnectionPool.getInstance();
        databaseAdapter = dbPool.getActiveDatabaseAdapter();
        schemaMapping = ObjectRegistry.getInstance().getSchemaMapping();
        eventDispatcher = ObjectRegistry.getInstance().getEventDispatcher();

        featureCounter = new HashMap<>();
    }

    public boolean doProcess() throws TableExportException {
        eventDispatcher.addEventHandler(EventType.INTERRUPT, this);
        eventDispatcher.addEventHandler(EventType.OBJECT_COUNTER, this);

        try {
            return process();
        } finally {
            eventDispatcher.removeEventHandler(this);
        }
    }

    private boolean process() throws TableExportException {
        // worker pool settings
        int minThreads = 2;
        int maxThreads = Math.max(minThreads, Runtime.getRuntime().availableProcessors());

        // checking template file
        File templatefile;
        if (!config.getTemplate().isManualTemplate()) {
            templatefile = new File(config.getTemplate().getPath());
            if (!templatefile.isFile()) {
                throw new TableExportException("Failed to load template file " + templatefile + ".");
            }
        }

        // log workspace
        if (databaseAdapter.hasVersioningSupport() && databaseAdapter.getConnectionDetails().isSetWorkspace()) {
            Workspace workspace = databaseAdapter.getConnectionDetails().getWorkspace();
            if (!databaseAdapter.getWorkspaceManager().equalsDefaultWorkspaceName(workspace.getName())) {
                log.info("Exporting from workspace " + databaseAdapter.getConnectionDetails().getWorkspace() + ".");
            }
        }

        // build query from config settings
        Query query;
        try {
            QueryConfig queryConfig = new QueryConfig();
            queryConfig.setFeatureTypeFilter(config.getFeatureTypeFilter());

            if (config.isUseBoundingBoxFilter()) {
                BBOXOperator bboxOperator = new BBOXOperator();
                bboxOperator.setEnvelope(config.getBoundingBox());
                SelectionFilter selectionFilter = new SelectionFilter();
                selectionFilter.setPredicate(bboxOperator);
                queryConfig.setSelectionFilter(selectionFilter);
            }

            ConfigQueryBuilder builder = new ConfigQueryBuilder(schemaMapping, databaseAdapter);
            query = builder.buildQuery(queryConfig, ObjectRegistry.getInstance().getConfig().getNamespaceFilter());
        } catch (QueryBuildException e) {
            throw new TableExportException("Failed to build the export query expression.", e);
        }

        // output file
        String filename;
        String path;

        if (config.getOutput().getType() == OutputFileType.XLSX)
            path = config.getOutput().getXlsxfile().getOutputPath().trim();
        else
            path = config.getOutput().getCsvfile().getOutputPath().trim();

        if (path.lastIndexOf(File.separator) == -1) {
            filename = path;
            path = ".";
        } else {
            if (path.lastIndexOf(".") == -1) {
                filename = path.substring(path.lastIndexOf(File.separator) + 1);
            } else {
                filename = path.substring(
                        path.lastIndexOf(File.separator) + 1,
                        path.lastIndexOf("."));
            }
            path = path.substring(0, path.lastIndexOf(File.separator));
        }

        String csvFilePath = path + File.separator + filename + ".csv";
        if (config.getOutput().getType() == OutputFileType.XLSX) {
            csvFilePath = System.getProperty("java.io.tmpdir") + File.separator + filename + ".csv";
        }
        File outputfile = new File(csvFilePath);

        File dummyOutputfile;
        if (config.getOutput().getType() == OutputFileType.XLSX)
            dummyOutputfile = new File(path + File.separator + filename + ".xlsx");
        else
            dummyOutputfile = new File(path + File.separator + filename + ".csv");

        if (!dummyOutputfile.exists()) {
            try {
                dummyOutputfile.createNewFile();
            } catch (Exception e) {
                throw new TableExportException("Failed to create output file.", e);
            }
        }

        String templateFile;
        try {
            if (!config.getTemplate().isManualTemplate()) {
                templateFile = Translator.getInstance().translateToBalloonTemplate(new File(config.getTemplate().getPath()));
            } else {
                templateFile = Translator.getInstance().translateToBalloonTemplate(config.getTemplate().getColumnsList());
            }
        } catch (Exception e) {
            throw new TableExportException("Failed to read template file.", e);
        }

        long start = System.currentTimeMillis();
        SingleWorkerPool<RowofCSVWork> writerPool = null;

        try {
            writerPool = new SingleWorkerPool<>("spsh_writer_pool", new CSVWriterFactory(outputfile), 100, true);

            workerPool = new WorkerPool<>(
                    "spsh_generator_pool",
                    minThreads,
                    maxThreads,
                    PoolSizeAdaptationStrategy.AGGRESSIVE,
                    new SPSHGWorkerFactory(dbPool, writerPool, config, templateFile), 300, false);

            workerPool.setContextClassLoader(SpreadsheetExporter.class.getClassLoader());

            // start pool workers
            writerPool.prestartCoreWorkers();
            workerPool.prestartCoreWorkers();

            String separatorCharacter = config.getOutput().getType() == OutputFileType.CSV ?
                    SeparatorPhrase.getInstance().decode(config.getOutput().getCsvfile().getSeparator().trim()) :
                    SeparatorPhrase.getInstance().getExcelSeparator();

            writerPool.addWork(new RowofCSVWork(SPSHGWorker.generateHeader(
                    Translator.getInstance().getColumnTitle(), separatorCharacter),
                    RowofCSVWork.UNKNOWN_CLASS_ID));

            try {
                log.info("Exporting to file: " + dummyOutputfile);

                dbm = new DBManager(query, schemaMapping, dbPool, workerPool, eventDispatcher);
                eventDispatcher.triggerEvent(new StatusDialogTitle(Util.I18N.getString("spshg.dialog.status.state.generating"), this));

                if (shouldRun) {
                    dbm.startQuery();
                }
            } catch (QueryBuildException | SQLException e) {
                throw new TableExportException("Failed to query the database.", e);
            }

            try {
                workerPool.shutdownAndWait();
                writerPool.shutdownAndWait();
            } catch (InterruptedException e) {
                throw new TableExportException("Failed to write to output file.", e);
            }

            if (config.getOutput().getType() == OutputFileType.XLSX) {
                try {
                    convertToXSLX(csvFilePath, path, filename);
                } catch (Exception e) {
                    throw new TableExportException("Failed to write to output file.", e);
                }
            }
        } catch (TableExportException e) {
            throw e;
        } catch (Throwable e) {
            throw new TableExportException("An unexpected error occurred.", e);
        } finally {
            if (workerPool != null && !workerPool.isTerminated()) {
                workerPool.shutdownNow();
            }

            if (writerPool != null && !writerPool.isTerminated()) {
                writerPool.shutdownNow();
            }

            try {
                eventDispatcher.flushEvents();
            } catch (InterruptedException e) {
                //
            }

            // Summary
            log.info("Exported city objects:");

            featureCounter.keySet().stream()
                    .sorted()
                    .forEach(id -> log.info(schemaMapping.getFeatureType(id) + ": " + featureCounter.get(id)));

            long sum = featureCounter.values().stream().mapToLong(Long::longValue).sum();
            log.info("Total exported CityGML features: " + sum);

            featureCounter.clear();
        }

        if (shouldRun) {
            log.info("Total export time: " + org.citydb.util.Util.formatElapsedTime(System.currentTimeMillis() - start) + ".");
        } else if (exception != null) {
            throw exception;
        }

        return shouldRun;
    }

    private void setException(String message, Throwable cause) {
        if (exception == null) {
            exception = new TableExportException(message, cause);
        }
    }

    @Override
    public void handleEvent(Event e) throws Exception {
        if (e.getEventType() == EventType.OBJECT_COUNTER) {
            Map<Integer, Long> counter = ((ObjectCounterEvent) e).getCounter();
            for (Map.Entry<Integer, Long> entry : counter.entrySet()) {
                Long tmp = featureCounter.get(entry.getKey());
                featureCounter.put(entry.getKey(), tmp == null ? entry.getValue() : tmp + entry.getValue());
            }
        } else if (e.getEventType() == EventType.INTERRUPT) {
            if (isInterrupted.compareAndSet(false, true)) {
                shouldRun = false;

                InterruptEvent event = (InterruptEvent) e;

                log.log(event.getLogLevelType(), event.getLogMessage());
                if (event.getCause() != null) {
                    setException("Aborting export due to errors.", event.getCause());
                }

                if (dbm != null) {
                    dbm.shutdown();
                }

                if (workerPool != null) {
                    workerPool.drainWorkQueue();
                }
            }
        }
    }

    public void convertToXSLX(String csvPath, String path, String filename) throws Exception {
        // convert csv to excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Countries");
        CsvReader reader = null;

        int rowIndex = 0;

        String xlsxFullpath = path + File.separator + filename + ".xlsx";

        reader = new CsvReader(csvPath, SeparatorPhrase.getInstance().getExcelSeparator().charAt(0), StandardCharsets.UTF_8);

        // avoid error message of CsvReader in case of column lengths greater than 100,000 characters
        reader.setSafetySwitch(false);

        reader.readRecord();
        String[] spshColumnNames = reader.getValues();
        Row row = sheet.createRow(rowIndex);
        for (int i = 0; i < spshColumnNames.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(spshColumnNames[i]);
        }
        rowIndex++;
        Map<String, String> templateMap = Translator.getInstance().getTemplateHashmap();

        while (reader.readRecord()) {
            row = sheet.createRow(rowIndex);
            String[] valueArray = reader.getValues();
            if (valueArray != null && valueArray.length > 0) {
                for (int i = 0; i < valueArray.length; i++) {
                    if (valueArray[i] != null && valueArray[i].trim().length() > 0) {
                        String dbTableColumn = templateMap.get(spshColumnNames[i]);
                        Cell cell = row.createCell(i);
                        Integer dataType = Util._3DCITYDB_TABLES_AND_COLUMNS.get(dbTableColumn);
                        if (dataType == Util.NUMBER_COLUMN_VALUE.intValue()) {
                            try {
                                cell.setCellValue(Double.parseDouble(valueArray[i]));
                            } catch (NumberFormatException nfe) {
                                cell.setCellValue(String.valueOf(valueArray[i]));
                            }
                        } else {
                            cell.setCellValue(String.valueOf(valueArray[i]));
                        }
                    }
                }
                rowIndex++;
            }
        }

        reader.close();

        // lets write the excel data to file now
        try (FileOutputStream fos = new FileOutputStream(xlsxFullpath)) {
            workbook.write(fos);
        }
    }
}
