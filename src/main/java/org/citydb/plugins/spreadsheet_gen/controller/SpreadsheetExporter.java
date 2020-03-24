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
import org.citydb.database.connection.DatabaseConnectionPool;
import org.citydb.event.Event;
import org.citydb.event.EventDispatcher;
import org.citydb.event.EventHandler;
import org.citydb.log.Logger;
import org.citydb.plugins.spreadsheet_gen.SPSHGPlugin;
import org.citydb.plugins.spreadsheet_gen.concurrent.CSVWriter;
import org.citydb.plugins.spreadsheet_gen.concurrent.SPSHGWorker;
import org.citydb.plugins.spreadsheet_gen.concurrent.SPSHGWorkerFactory;
import org.citydb.plugins.spreadsheet_gen.concurrent.WriterFactory;
import org.citydb.plugins.spreadsheet_gen.concurrent.work.CityObjectWork;
import org.citydb.plugins.spreadsheet_gen.concurrent.work.RowofCSVWork;
import org.citydb.plugins.spreadsheet_gen.concurrent.work.UploadFileWork;
import org.citydb.plugins.spreadsheet_gen.config.ConfigImpl;
import org.citydb.plugins.spreadsheet_gen.config.Output;
import org.citydb.plugins.spreadsheet_gen.database.DBManager;
import org.citydb.plugins.spreadsheet_gen.database.Translator;
import org.citydb.plugins.spreadsheet_gen.events.EventType;
import org.citydb.plugins.spreadsheet_gen.events.InterruptEvent;
import org.citydb.plugins.spreadsheet_gen.events.StatusDialogTitle;
import org.citydb.plugins.spreadsheet_gen.gui.datatype.SelectedCityObjects;
import org.citydb.plugins.spreadsheet_gen.gui.datatype.SeparatorPhrase;
import org.citydb.plugins.spreadsheet_gen.util.Util;
import org.citydb.registry.ObjectRegistry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class SpreadsheetExporter implements EventHandler {
    private final DatabaseConnectionPool dbPool;
    private final SPSHGPlugin plugin;
    private final EventDispatcher eventDispatcher;
    private final Logger log = Logger.getInstance();

    private volatile boolean shouldRun = true;
    private AtomicBoolean isInterrupted = new AtomicBoolean(false);

    private WorkerPool<CityObjectWork> workerPool;
    private SingleWorkerPool<RowofCSVWork> ioWriterPool;

    private DBManager dbm = null;

    public SpreadsheetExporter(SPSHGPlugin plugin) {
        this.plugin = plugin;
        dbPool = DatabaseConnectionPool.getInstance();
        eventDispatcher = ObjectRegistry.getInstance().getEventDispatcher();
    }

    public void cleanup() {
        eventDispatcher.removeEventHandler(this);
    }

    public boolean doProcess() {
		
/*		BalloonTemplateHandlerImpl bth = new BalloonTemplateHandlerImpl();
		HashMap<String, Set<String>> _3dcitydbcontent = bth.getSupportedTablesAndColumns();
		Set<String> keys = _3dcitydbcontent.keySet();
		for (String tableName:keys){
			for (String column:_3dcitydbcontent.get(tableName)){
			//	System.out.println("put(\"" + tableName + "__" + column + "\", 1);");
				System.out.println(tableName + "_" + column + ":" + tableName + "/[COUNT]" + column);
			}			
		}*/

        eventDispatcher.addEventHandler(EventType.INTERRUPT, this);

        ConfigImpl config = plugin.getConfig();


        // worker pool settings
        int minThreads = 2;
        int maxThreads = 10;

        // checking templatefile
        File templatefile = null;
        if (!config.getTemplate().isManualTemplate()) {
            templatefile = new File(config.getTemplate().getPath());
            if (!templatefile.isFile()) {
                log.error(Util.I18N.getString("spshg.message.export.template.notavailable"));
                return false;
            }
        }

        // checking workspace...
        Workspace workspace = config.getWorkspace();

        if (shouldRun && dbPool.getActiveDatabaseAdapter().hasVersioningSupport() &&
                !dbPool.getActiveDatabaseAdapter().getWorkspaceManager().equalsDefaultWorkspaceName(workspace.getName()) &&
                !dbPool.getActiveDatabaseAdapter().getWorkspaceManager().existsWorkspace(workspace, true))
            return false;


        // output file
        String filename = "";
        String path = "";
        UploadFileWork ufw = null;

        if (config.getOutput().getType() == Output.XLSX_FILE_CONFIG)
            path = config.getOutput().getXlsxfile().getOutputPath().trim();
        else
            path = config.getOutput().getCsvfile().getOutputPath().trim();

        if (path.lastIndexOf(File.separator) == -1) {
            filename = path;
            path = ".";
        } else {
            if (path.lastIndexOf(".") == -1) {
                filename = path
                        .substring(path.lastIndexOf(File.separator) + 1);
            } else {
                filename = path.substring(
                        path.lastIndexOf(File.separator) + 1,
                        path.lastIndexOf("."));
            }
            path = path.substring(0, path.lastIndexOf(File.separator));
        }

        String csvFilePath = path + File.separator + filename + ".csv";
        if (config.getOutput().getType().equalsIgnoreCase(Output.XLSX_FILE_CONFIG)) {
            csvFilePath = System.getProperty("java.io.tmpdir") + File.separator + filename + ".csv";
        }
        File outputfile = new File(csvFilePath);

        File dummyOutputfile = null;
        if (config.getOutput().getType() == Output.XLSX_FILE_CONFIG)
            dummyOutputfile = new File(path + File.separator + filename + ".xlsx");
        else
            dummyOutputfile = new File(path + File.separator + filename + ".csv");


        if (!dummyOutputfile.exists())
            try {
                dummyOutputfile.createNewFile();
            } catch (Exception e) {
                log.error(Util.I18N.getString("spshg.message.export.file.error"));
                return false;
            }

        ioWriterPool = new SingleWorkerPool<RowofCSVWork>("spsh_writer_pool", new WriterFactory(outputfile), 100, true);
        String tmplFile = "";
        try {
            if (!config.getTemplate().isManualTemplate())
                // load from file
                tmplFile = Translator.getInstance().translateToBalloonTemplate(
                        new File(config.getTemplate().getPath()));
            else// manually generated template
                tmplFile = Translator.getInstance().translateToBalloonTemplate(
                        config.getTemplate().getColumnsList());
        } catch (Exception e) {
            log.error(Util.I18N.getString("spshg.message.export.file.error.reading"));
            return false;
        }
        workerPool = new WorkerPool<CityObjectWork>(
                "spsh_generator_pool",
                minThreads,
                maxThreads,
                PoolSizeAdaptationStrategy.AGGRESSIVE,
                new SPSHGWorkerFactory(dbPool, ioWriterPool, config, tmplFile), 300, false);

        workerPool.setContextClassLoader(SpreadsheetExporter.class.getClassLoader());
        // start pool workers
        ioWriterPool.prestartCoreWorkers();
        workerPool.prestartCoreWorkers();
        String seperatorCharacter;
        seperatorCharacter = SeparatorPhrase.getInstance().decode(config.getOutput().getCsvfile().getSeparator().trim());
        ioWriterPool.addWork(new RowofCSVWork(SPSHGWorker.generateHeader(Translator
                .getInstance().getColumnTitle(), seperatorCharacter),
                RowofCSVWork.UNKNOWN_CLASS_ID));


        // reset the loging system in CSVWRITER
        CSVWriter.resetLogStorage();
        // get database splitter and start query

        try {
            dbm = new DBManager(dbPool, config, workerPool);
            SPSHGWorker.counter = 0;
            eventDispatcher.triggerEvent(new StatusDialogTitle(Util.I18N.getString("spshg.dialog.status.state.generating"), this));

            if (shouldRun)
                dbm.startQuery(SelectedCityObjects.getInstance().getSelectedCityObjects());
        } catch (SQLException sqlE) {
            log.error("SQL error: " + sqlE.getMessage());
            return false;
        }

        try {
            if (shouldRun)
                workerPool.shutdownAndWait();

            ioWriterPool.shutdownAndWait();
        } catch (InterruptedException e) {
            log.error(Util.I18N.getString("common.error") + e.getMessage());
            shouldRun = false;
        }

        if (config.getOutput().getType().equalsIgnoreCase(Output.XLSX_FILE_CONFIG)) {
            try {
                convertToXSLX(csvFilePath, path, filename);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        // Summary
        if (shouldRun)
            writeReport();

        return shouldRun;
    }

    @Override
    public void handleEvent(Event e) throws Exception {
        if (isInterrupted.compareAndSet(false, true)) {
            shouldRun = false;
            if (dbm != null) dbm.shutdown();
            if (workerPool != null) workerPool.shutdownNow();
            log.log(((InterruptEvent) e).getLogLevelType(), ((InterruptEvent) e).getLogMessage());
        }
    }

    public void writeReport() {
        HashMap<Integer, AtomicInteger> countingStorage = CSVWriter.getRportStructure();
//		StringBuffer mReport= new StringBuffer();
        StringBuffer line = new StringBuffer();
//		mReport.append(Util.I18N.getString("spshg.message.summery.title"));
        log.info(Util.I18N.getString("spshg.message.summery.title"));
//		mReport.append(System.getProperty("line.separator"));
        int sum = 0;
        for (Integer mClass : countingStorage.keySet()) {
            line.append(SelectedCityObjects.getInstance().getCityObjectName(org.citydb.util.Util.getCityGMLClass(mClass.intValue())));
            line.append(": ");
            line.append(countingStorage.get(mClass).intValue());
            log.info(line.toString());
            line.setLength(0);
//			mReport.append(System.getProperty("line.separator"));
            sum += countingStorage.get(mClass).intValue();
        }
//		mReport.append(String.format(Util.I18N.getString("spshg.message.summery.sumery"),sum));
        log.info(String.format(Util.I18N.getString("spshg.message.summery.sumery"), sum));

    }

    public void convertToXSLX(String csvPath, String path, String filename) throws Exception {
        // convert csv to excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Countries");
        CsvReader reader = null;

        int rowIndex = 0;

        String xlsxFullpath = path + File.separator + filename + ".xlsx";

        reader = new CsvReader(csvPath, SeparatorPhrase.getInstance().getIntoCloudDefaultSeperator().charAt(0), Charset.forName("UTF-8"));

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

        try {
            while (reader.readRecord()) {
                row = sheet.createRow(rowIndex);
                String[] valueArray = reader.getValues();
                if (valueArray != null && valueArray.length > 0) {
                    for (int i = 0; i < valueArray.length; i++) {
                        if (valueArray[i] != null && String.valueOf(valueArray[i].trim()).length() > 0) {
                            String dbTableColumn = templateMap.get(spshColumnNames[i]);
                            Cell cell = row.createCell(i);
                            int dataType = Util._3DCITYDB_TABLES_AND_COLUMNS.get(dbTableColumn);
                            if (dataType == Util.NUMBER_COLUMN_VALUE) {
                                try {
                                    cell.setCellValue(Double.valueOf(valueArray[i]));
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
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        reader.close();

        // lets write the excel data to file now
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(xlsxFullpath));
            workbook.write(fos);
            fos.close();
        } catch (IOException ioe) {
            log.error(ioe.getMessage());
            shouldRun = false;
        }

    }

}
