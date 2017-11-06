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
package org.citydb.plugins.spreadsheet_gen.controller.cloudservice_impl;

import java.util.TimeZone;

import org.citydb.plugins.spreadsheet_gen.util.Util;

import com.google.gdata.data.DateTime;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.extensions.LastModifiedBy;


public class Spreadsheet {
	public final static int NEW=0;;
	public final static int OVERWRITE=1;
	String name;
	String resourceId;
	String link;
	String updatedBy;
	String lastUpdate;
	int type;
	DocumentListEntry entry;
	Spreadsheet(DocumentListEntry entry){
		this.type=OVERWRITE;
		setEntry(entry);
	}
	
	Spreadsheet(){
		this.type=NEW;
	}
	
	public void setEntry(DocumentListEntry entry){
		this.name=entry.getTitle().getPlainText();
		this.resourceId= entry.getResourceId();
		this.link=entry.getDocumentLink().getHref();
		LastModifiedBy lastModifiedBy = entry.getLastModifiedBy();
		this.updatedBy=lastModifiedBy.getName() + " - " + lastModifiedBy.getEmail();
		DateTime dt=entry.getUpdated();
		dt.setTzShift(TimeZone.getDefault().getRawOffset()/60000);
		this.lastUpdate=dt.toUiString();
		this.entry=entry;
	}
	
	
	public String getInfo(){
		return String.format(Util.I18N.getString("spshg.message.overwrite.on"),name,updatedBy,lastUpdate);
	}
	public String toString(){
		if (this.type==OVERWRITE)
			return getInfo();
		return Util.I18N.getString("spshg.message.overwrite.new");
	}
	public int getType(){
		return this.type;
	}
	
	public DocumentListEntry getEntry(){
		return this.entry;
	}
}
