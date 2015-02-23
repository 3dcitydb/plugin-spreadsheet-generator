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
package org.citydb.plugins.spreadsheet_gen.concurrent;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import org.citydb.plugins.spreadsheet_gen.concurrent.work.CityObjectWork;
import org.citydb.plugins.spreadsheet_gen.concurrent.work.RowofCSVWork;
import org.citydb.plugins.spreadsheet_gen.config.ConfigImpl;
import org.citydb.plugins.spreadsheet_gen.config.Output;
import org.citydb.plugins.spreadsheet_gen.controller.cloudservice.CloudService;
import org.citydb.plugins.spreadsheet_gen.controller.cloudservice.CloudServiceRegistery;
import org.citydb.plugins.spreadsheet_gen.events.StatusDialogMessage;
import org.citydb.plugins.spreadsheet_gen.gui.datatype.SeparatorPhrase;


import org.citydb.api.concurrent.DefaultWorkerImpl;
import org.citydb.api.concurrent.WorkerPool;
import org.citydb.api.controller.DatabaseController;
import org.citydb.api.database.BalloonTemplateHandler;
import org.citydb.api.event.EventDispatcher;
import org.citydb.api.registry.ObjectRegistry;
import org.citydb.config.project.database.Database;
import org.citydb.database.DatabaseConnectionPool;
import org.citydb.modules.kml.database.BalloonTemplateHandlerImpl;


public class SPSHGWorker extends DefaultWorkerImpl<CityObjectWork>{
	private final WorkerPool<RowofCSVWork> ioWriterPool;
	
	private BalloonTemplateHandler bth;
	private Connection connection;
	private String seperatorCharacter;
	private int lod;
	public static long counter=0;

	private final EventDispatcher eventDispatcher;
	private CloudService selectedCloudService=null;
	private boolean shouldRun;
	
	
	public SPSHGWorker(DatabaseConnectionPool dbConnectionPool,
			WorkerPool<RowofCSVWork> ioWriterPool,
			ConfigImpl config, String template)throws SQLException{
		eventDispatcher = ObjectRegistry.getInstance().getEventDispatcher();
		this.ioWriterPool=ioWriterPool;

		connection = dbConnectionPool.getConnection();
		String timestamp =config.getWorkspace().getTimestamp();
		
		// try and change workspace if needed
		if (dbConnectionPool.getActiveDatabaseAdapter().hasVersioningSupport()) {
			dbConnectionPool.getActiveDatabaseAdapter().getWorkspaceManager().gotoWorkspace(connection, 
					config.getWorkspace());
		}
		
		if (config.getOutput().getType().equalsIgnoreCase(Output.ONLINE_CONFIG) || config.getOutput().getType().equalsIgnoreCase(Output.CSV_FILE_CONFIG))
			seperatorCharacter =SeparatorPhrase.getInstance().getIntoCloudDefaultSeperator();
		else
			seperatorCharacter =SeparatorPhrase.getInstance().decode(config.getOutput().getCsvfile().getSeparator().trim());
		if (config.getOutput().getType() == Output.ONLINE_CONFIG)
			selectedCloudService=CloudServiceRegistery.getInstance().getSelectedService();
		else
			selectedCloudService=null;
		shouldRun=true;
		bth = new BalloonTemplateHandlerImpl(template, connection);
		lod=2;		
	}
	
	@Override
	public void doWork(CityObjectWork cityobj) {
		try {
			if (!this.shouldRun)
				return;
			String data =bth.getBalloonContent(cityobj.getGmlid(), lod);
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

	@Override
	public void shutdown() {
		this.shouldRun=false;
	}
	
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
