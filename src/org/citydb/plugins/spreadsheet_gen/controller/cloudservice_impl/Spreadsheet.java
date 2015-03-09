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
