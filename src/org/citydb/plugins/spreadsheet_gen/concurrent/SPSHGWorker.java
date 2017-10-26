/*
 * 3D City Database - The Open Source CityGML Database
 * http://www.3dcitydb.org/
 * 
 * Copyright 2013 - 2017
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
package org.citydb.plugins.spreadsheet_gen.concurrent;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import org.citydb.api.concurrent.DefaultWorkerImpl;
import org.citydb.api.concurrent.WorkerPool;
import org.citydb.api.database.BalloonTemplateHandler;
import org.citydb.api.event.EventDispatcher;
import org.citydb.api.registry.ObjectRegistry;
import org.citydb.database.DatabaseConnectionPool;
import org.citydb.plugins.spreadsheet_gen.concurrent.work.CityObjectWork;
import org.citydb.plugins.spreadsheet_gen.concurrent.work.RowofCSVWork;
import org.citydb.plugins.spreadsheet_gen.config.ConfigImpl;
import org.citydb.plugins.spreadsheet_gen.config.Output;
import org.citydb.plugins.spreadsheet_gen.controller.cloudservice.CloudService;
import org.citydb.plugins.spreadsheet_gen.controller.cloudservice.CloudServiceRegistery;
import org.citydb.plugins.spreadsheet_gen.events.StatusDialogMessage;
import org.citydb.plugins.spreadsheet_gen.gui.datatype.SeparatorPhrase;



// TODO: Auto-generated Javadoc
/**
 * The Class SPSHGWorker.
 */
public class SPSHGWorker extends DefaultWorkerImpl<CityObjectWork>{
	
	/** The io writer pool. */
	private final WorkerPool<RowofCSVWork> ioWriterPool;
	
	/** The bth. */
	private BalloonTemplateHandler bth;
	
	/** The connection. */
	private Connection connection;
	
	/** The seperator character. */
	private String seperatorCharacter;
	
	/** The lod. */
	private int lod;
	
	/** The counter. */
	public static long counter=0;

	/** The event dispatcher. */
	private final EventDispatcher eventDispatcher;
	
	/** The selected cloud service. */
	private CloudService selectedCloudService=null;
	
	/** The should run. */
	private boolean shouldRun;
	
	
	/**
	 * Instantiates a new SPSHG worker.
	 *
	 * @param dbConnectionPool the db connection pool
	 * @param ioWriterPool the io writer pool
	 * @param config the config
	 * @param template the template
	 * @throws SQLException the SQL exception
	 */
	public SPSHGWorker(DatabaseConnectionPool dbConnectionPool,
			WorkerPool<RowofCSVWork> ioWriterPool,
			ConfigImpl config, String template)throws SQLException{
		eventDispatcher = ObjectRegistry.getInstance().getEventDispatcher();
		this.ioWriterPool=ioWriterPool;

		connection = dbConnectionPool.getConnection();
		
		// try and change workspace if needed
		if (dbConnectionPool.getActiveDatabaseAdapter().hasVersioningSupport()) {
			dbConnectionPool.getActiveDatabaseAdapter().getWorkspaceManager().gotoWorkspace(connection, 
					config.getWorkspace());
		}
		
		if (config.getOutput().getType().equalsIgnoreCase(Output.ONLINE_CONFIG) || config.getOutput().getType().equalsIgnoreCase(Output.XLSX_FILE_CONFIG))
			seperatorCharacter =SeparatorPhrase.getInstance().getIntoCloudDefaultSeperator();
		else
			seperatorCharacter =SeparatorPhrase.getInstance().decode(config.getOutput().getCsvfile().getSeparator().trim());
		if (config.getOutput().getType() == Output.ONLINE_CONFIG)
			selectedCloudService=CloudServiceRegistery.getInstance().getSelectedService();
		else
			selectedCloudService=null;
		shouldRun=true;
		bth = dbConnectionPool.getActiveDatabaseAdapter().getBalloonTemplateHandler(template);
		lod=2;		
	}
	
	/* (non-Javadoc)
	 * @see org.citydb.api.concurrent.DefaultWorkerImpl#doWork(java.lang.Object)
	 */
	@Override
	public void doWork(CityObjectWork cityobj) {
		try {
			if (!this.shouldRun)
				return;
			String data = bth.getBalloonContent(cityobj.getGmlid(), lod, connection);
			String[] cells=data.split("\\Q"+SeparatorPhrase.getInstance().getTempPhrase()+"\\E");
		
			StringBuffer sb=new StringBuffer();
			boolean firstround=true;
			
			for (String st:cells){
				if (!firstround){
					sb.append(seperatorCharacter);
					sb.append("\"");
				}else{
					sb.append('"');
					firstround=false;
				}
				sb.append(st);	
				sb.append("\"");
			}
			sb.append("\r\n");
			ioWriterPool.addWork(new RowofCSVWork(sb.toString(),cityobj.getClassid()));
			counter++;
			eventDispatcher.triggerEvent(new StatusDialogMessage(" "+counter, this));
		}
		catch (Exception e) {}
	}

	/* (non-Javadoc)
	 * @see org.citydb.api.concurrent.DefaultWorkerImpl#shutdown()
	 */
	@Override
	public void shutdown() {
		this.shouldRun=false;
		if (connection != null) {
			try {
				connection.close();
			}
			catch (SQLException sqlEx) {}
			connection = null;
		}
	}
	
	/**
	 * Generate header.
	 *
	 * @param header the header
	 * @param separator the separator
	 * @return the string
	 */
	public static String generateHeader(ArrayList<String>header,String separator){
		StringBuffer sb=new StringBuffer();
		boolean firstround=true;
		for (String st:header){
			if (!firstround){
				sb.append(separator);
				sb.append("\"");
			}else{
				sb.append('"');
				firstround=false;
			}
			sb.append(st);
			sb.append("\"");
		}
		sb.append("\r\n");
		return sb.toString();
	}
}
