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

import de.tub.citydb.api.concurrent.DefaultWorkerImpl;
import de.tub.citydb.api.controller.LogController;
import de.tub.citydb.api.event.EventDispatcher;
import de.tub.citydb.api.registry.ObjectRegistry;

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
