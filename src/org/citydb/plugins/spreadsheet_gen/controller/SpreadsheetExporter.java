/*
 * This file is part of the Spreadsheet Generator Plugin
 * developed for the 3D City Database Importer/Exporter v1.4.0
 * Copyright (c) 2012
 * Institute for Geodesy and Geoinformation Science
 * Technische Universitaet Berlin, Germany
 * http://www.gis.tu-berlin.de/
 * 
 * The Spreadsheet Generator Plugin program is free software:
 * you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 */
package org.citydb.plugins.spreadsheet_gen.controller;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.citydb.plugins.spreadsheet_gen.spsheet_gen;
import org.citydb.plugins.spreadsheet_gen.concurrent.CSVWriter;
import org.citydb.plugins.spreadsheet_gen.concurrent.SPSHGWorker;
import org.citydb.plugins.spreadsheet_gen.concurrent.SPSHGWorkerFactory;
import org.citydb.plugins.spreadsheet_gen.concurrent.UploadFileFactory;
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
import org.citydb.plugins.spreadsheet_gen.gui.view.SPSHGPanel;
import org.citydb.plugins.spreadsheet_gen.util.Util;


import de.tub.citydb.api.concurrent.PoolSizeAdaptationStrategy;
import de.tub.citydb.api.concurrent.SingleWorkerPool;
import de.tub.citydb.api.concurrent.WorkerPool;
import de.tub.citydb.api.controller.DatabaseController;
import de.tub.citydb.api.controller.LogController;
import de.tub.citydb.api.event.Event;
import de.tub.citydb.api.event.EventDispatcher;
import de.tub.citydb.api.event.EventHandler;
import de.tub.citydb.api.registry.ObjectRegistry;
import de.tub.citydb.config.project.database.Workspace;
import de.tub.citydb.database.DatabaseConnectionPool;


public class SpreadsheetExporter implements EventHandler{
	private final DatabaseConnectionPool dbPool;
	private final spsheet_gen plugin;
	private final EventDispatcher eventDispatcher;
	private final LogController logController;	
	
	private volatile boolean shouldRun = true;
	private AtomicBoolean isInterrupted = new AtomicBoolean(false);
	
	private WorkerPool<CityObjectWork> workerPool;
	private SingleWorkerPool<RowofCSVWork> ioWriterPool;
	private SingleWorkerPool<UploadFileWork> uploaderPool;
	
	private DBManager dbm = null;
	public SpreadsheetExporter(spsheet_gen plugin){
		this.plugin = plugin;		
		dbPool = DatabaseConnectionPool.getInstance();
		eventDispatcher = ObjectRegistry.getInstance().getEventDispatcher();
		logController = ObjectRegistry.getInstance().getLogController();
		
	}
	
	public void cleanup() {
		eventDispatcher.removeEventHandler(this);
	}
	
	public boolean doProcess(){
		eventDispatcher.addEventHandler(EventType.INTERRUPT, this);
		
		ConfigImpl config = plugin.getConfig();
		

		// worker pool settings
		int minThreads = 2;
		int maxThreads = 10;
		
		// checking templatefile
		File templatefile =null;
		if (!config.getTemplate().isManualTemplate()){
			templatefile = new File(config.getTemplate().getPath());
			if (!templatefile.isFile()){
					logController.error(Util.I18N.getString("spshg.message.export.template.notavailable"));
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
		String filename="";
		String path="";
		UploadFileWork ufw=null;
		if (config.getOutput().getType() == Output.CSV_FILE_CONFIG) {
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
		} else {
			path = System.getProperty("java.io.tmpdir");
			filename = config.getOutput().getCloud().getSpreadsheetName();
			ufw= new UploadFileWork(path==null?"":path
					+ filename + ".csv", 
					config.getOutput().getCloud().getSpreadsheetName());

		}

		File outputfile = new File(path + File.separator + filename + ".csv");
		
		if (!outputfile.exists())
			try{
			outputfile.createNewFile();
			}catch(Exception e){
				logController.error(Util.I18N.getString("spshg.message.export.file.error"));
				return false;
			}
		
		ioWriterPool= new SingleWorkerPool<RowofCSVWork>("spsh_writer_pool", new WriterFactory(outputfile), 100, true);
		String tmplFile="";
		try {
			if (!config.getTemplate().isManualTemplate())
				// load from file
				tmplFile = Translator.getInstance().translateToBalloonTemplate(
					new File(config.getTemplate().getPath()));
			else// manually generated template
				tmplFile = Translator.getInstance().translateToBalloonTemplate(
						config.getTemplate().getColumnsList());
		} catch (Exception e) {
			logController.error(Util.I18N.getString("spshg.message.export.file.error.reading"));
			return false;
		}
		workerPool = new WorkerPool<CityObjectWork>(
				"spsh_generator_pool",
				minThreads,
				maxThreads,
				PoolSizeAdaptationStrategy.AGGRESSIVE,
				new SPSHGWorkerFactory(dbPool, ioWriterPool, config,tmplFile),300,false) ;
		
		workerPool.setContextClassLoader(SpreadsheetExporter.class.getClassLoader());
		// start pool workers
		ioWriterPool.prestartCoreWorkers();
		workerPool.prestartCoreWorkers();
		String seperatorCharacter;
		if (config.getOutput().getType().equalsIgnoreCase(Output.ONLINE_CONFIG))
			seperatorCharacter =SeparatorPhrase.getInstance().getIntoCloudDefaultSeperator();
		else
			seperatorCharacter =SeparatorPhrase.getInstance().decode(config.getOutput().getCsvfile().getSeparator().trim());
		ioWriterPool.addWork(new RowofCSVWork(SPSHGWorker.generateHeader(Translator
				.getInstance().getColumnTitle(), seperatorCharacter),
				RowofCSVWork.UNKNOWN_CLASS_ID));
		
		// reset the loging system in CSVWRITER
		CSVWriter.resetLogStorage();
		// get database splitter and start query
		
		try {
			dbm = new DBManager(dbPool,config,workerPool);
			SPSHGWorker.counter=0;
			eventDispatcher.triggerEvent(new StatusDialogTitle(Util.I18N.getString("spshg.dialog.status.state.generating"), this));
			
			if (shouldRun)
				dbm.startQuery(SelectedCityObjects.getInstance().getSelectedCityObjects());
		} catch (SQLException sqlE) {
			logController.error("SQL error: " + sqlE.getMessage());
			return false;
		}

		try {
			if (shouldRun)
				workerPool.shutdownAndWait();

			ioWriterPool.shutdownAndWait();
		} catch (InterruptedException e) {
			logController.error(Util.I18N.getString("common.error") + e.getMessage());
			shouldRun=false;
		}
		// Summary
		writeReport();
		
		if (config.getOutput().getType()==Output.ONLINE_CONFIG){
			uploaderPool= new SingleWorkerPool<UploadFileWork>("spsh_upload_pool", new UploadFileFactory(),10);
			uploaderPool.setContextClassLoader(SPSHGPanel.class.getClassLoader());
			uploaderPool.prestartCoreWorkers();

			try {
				if (shouldRun)
					uploaderPool.addWork(ufw);
				uploaderPool.shutdownAndWait();
			} catch (InterruptedException e) {
				logController.error(Util.I18N.getString("common.error")  + e.getMessage());
				shouldRun=false;
			}
		}
		
		return shouldRun;
	}
	
	@Override
	public void handleEvent(Event e) throws Exception {
		if (isInterrupted.compareAndSet(false, true)) {
			shouldRun = false;
			if (dbm!=null) dbm.shutdown();
			if (workerPool!=null)workerPool.shutdownNow();
			logController.log(((InterruptEvent)e).getLogLevelType(), ((InterruptEvent)e).getLogMessage());
		}
	}
	
	public void  writeReport(){
		HashMap<Integer,AtomicInteger> countingStorage= CSVWriter.getRportStructure();
//		StringBuffer mReport= new StringBuffer();
		StringBuffer line=new StringBuffer();
//		mReport.append(Util.I18N.getString("spshg.message.summery.title"));
		logController.info(Util.I18N.getString("spshg.message.summery.title"));
//		mReport.append(System.getProperty("line.separator"));
		int sum=0;
		for (Integer mClass:countingStorage.keySet()){
			line.append(SelectedCityObjects.getInstance().getCityObjectName(de.tub.citydb.util.Util.classId2cityObject(mClass.intValue())));
			line.append(": ");
			line.append(countingStorage.get(mClass).intValue());
			logController.info(line.toString());
			line.setLength(0);
//			mReport.append(System.getProperty("line.separator"));
			sum+=countingStorage.get(mClass).intValue();
		}
//		mReport.append(String.format(Util.I18N.getString("spshg.message.summery.sumery"),sum));
		logController.info(String.format(Util.I18N.getString("spshg.message.summery.sumery"),sum));

	}

}
