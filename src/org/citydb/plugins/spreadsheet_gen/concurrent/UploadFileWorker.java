/*
 * This file is part of the Spreadsheet Generator Plugin
 * developed for the Importer/Exporter v3.0 of the
 * 3D City Database - The Open Source CityGML Database
 * http://www.3dcitydb.org/
 * 
 * (C) 2013 - 2015,
 * Chair of Geoinformatics,
 * Technische Universitaet Muenchen, Germany
 * http://www.gis.bgu.tum.de/
 * 
 * The 3D City Database is jointly developed with the following
 * cooperation partners:
 * 
 * Chair of Geoinformatics, TU Munich, <http://www.gis.bgu.tum.de/>
 * virtualcitySYSTEMS GmbH, Berlin <http://www.virtualcitysystems.de/>
 * M.O.S.S. Computer Grafik Systeme GmbH, Muenchen <http://www.moss.de/>
 * 
 * The Spreadsheet Generator Plugin program is free software:
 * you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 */
package org.citydb.plugins.spreadsheet_gen.concurrent;

import java.io.IOException;
import java.net.MalformedURLException;

import org.citydb.plugins.spreadsheet_gen.concurrent.work.UploadFileWork;
import org.citydb.plugins.spreadsheet_gen.controller.cloudservice.CloudServiceRegistery;
import org.citydb.plugins.spreadsheet_gen.controller.cloudservice.UploadException;
import org.citydb.plugins.spreadsheet_gen.events.StatusDialogMessage;
import org.citydb.plugins.spreadsheet_gen.events.StatusDialogTitle;
import org.citydb.plugins.spreadsheet_gen.events.UploadEvent;
import org.citydb.plugins.spreadsheet_gen.util.Util;


import com.google.gdata.util.ServiceException;

import org.citydb.api.concurrent.DefaultWorkerImpl;
import org.citydb.api.controller.LogController;
import org.citydb.api.event.EventDispatcher;
import org.citydb.api.registry.ObjectRegistry;

public class UploadFileWorker extends DefaultWorkerImpl<UploadFileWork>{
	public final static String URL="URL_INTO_CLOUD";
	private final EventDispatcher eventDispatcher;
	private final LogController logController;
	public UploadFileWorker(){
		eventDispatcher = ObjectRegistry.getInstance().getEventDispatcher();
		logController= ObjectRegistry.getInstance().getLogController();
	}
	@Override
	public void doWork(UploadFileWork work) {
		eventDispatcher.triggerEvent(new StatusDialogTitle(Util.I18N.getString("spshg.dialog.status.state.upload"), this));
		eventDispatcher.triggerEvent(new StatusDialogMessage("Title: "+work.getTitle(), this));
		String onlinepath="";
		try{
			onlinepath= uploadFile(work.getLocalpath(), work.getTitle());
			
			eventDispatcher.triggerEvent(new StatusDialogMessage(work.getTitle()+" "+Util.I18N.getString("spshg.dialog.status.state.upload.done"), this));
			logController.info(Util.I18N.getString("spshg.message.upload.done.success")+ (onlinepath==null?"":
				" Address:"+onlinepath));
			CloudServiceRegistery.getInstance().getSelectedService().afterUpload();
			eventDispatcher.triggerEvent(new UploadEvent(true,onlinepath,this));
		}catch(Exception e){
			if (e instanceof UploadException){
				switch (((UploadException)e).getType()){
				case UploadException.PUBLISH_ERROR:
					logController.error(e.getMessage());
					eventDispatcher.triggerEvent(new UploadEvent(true,onlinepath,this));
					return;
				}
			}
			logController.error(Util.I18N.getString("spshg.message.upload.done.fail") + e.getMessage());
			eventDispatcher.triggerEvent(new UploadEvent(false,null,this));
		}
		
	}
	/**
	 * 
	 * @param title
	 * @return address of spreadsheet; null|exception means it was not
	 *         successful;
	 * @throws ServiceException
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	private String uploadFile(String filepath,String title) throws Exception {
		if (filepath == null) {
			return null;
		}
		return CloudServiceRegistery.getInstance().getSelectedService().uploadFile(filepath, title);
	}

	@Override
	public void shutdown() {
		// No idea!
	}

}
