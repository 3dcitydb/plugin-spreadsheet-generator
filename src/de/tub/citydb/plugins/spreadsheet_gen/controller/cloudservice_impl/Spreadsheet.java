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
package de.tub.citydb.plugins.spreadsheet_gen.controller.cloudservice_impl;

import java.util.TimeZone;

import com.google.gdata.data.DateTime;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.extensions.LastModifiedBy;

import de.tub.citydb.plugins.spreadsheet_gen.util.Util;

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
