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
package org.citydb.plugins.spreadsheet_gen.events;
import org.citydb.api.event.Event;
// TODO: Auto-generated Javadoc

/**
 * The Class UploadEvent.
 */
public class UploadEvent extends Event{
	
	/** The url. */
	private String url="";
	
	/** The is success. */
	private boolean isSuccess=false;
	
	/**
	 * Instantiates a new upload event.
	 *
	 * @param isSuccess the is success
	 * @param URL the url
	 * @param source the source
	 */
	public UploadEvent(boolean isSuccess,String URL, Object source) {
		super(EventType.UPLOAD_EVENT, GLOBAL_CHANNEL, source);
		this.url=URL;
		this.isSuccess=isSuccess;
	}
	
	/**
	 * Checks if is success.
	 *
	 * @return true, if is success
	 */
	public boolean isSuccess(){
		return isSuccess;
	}
	
	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getURL(){
		return this.url;
	}

}
