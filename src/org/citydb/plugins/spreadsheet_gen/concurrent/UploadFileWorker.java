/*
 * 3D City Database - The Open Source CityGML Database
 * http://www.3dcitydb.org/
 * 
 * Copyright 2013 - 2016
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
