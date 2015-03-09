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
package org.citydb.plugins.spreadsheet_gen.config;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.citydb.plugins.spreadsheet_gen.gui.datatype.CSVColumns;



@XmlType(name="TemplateType", propOrder={
		"path"
})
public class Template {

	private String path="";
	@XmlTransient
	private ArrayList<CSVColumns> columnsList=null;
	@XmlTransient
	private boolean isManualTemplate=false;
	@XmlTransient
	private String lastVisitPath="";

	public Template(){
	}
	public ArrayList<CSVColumns> getColumnsList() {
		return columnsList;
	}

	public void setColumnsList(ArrayList<CSVColumns> columnsList) {
		this.columnsList = columnsList;
	}
	
	public boolean isManualTemplate() {
		return isManualTemplate;
	}
	public void setManualTemplate(boolean isManualTemplate) {
		this.isManualTemplate = isManualTemplate;
	}
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getLastVisitPath() {
		return lastVisitPath;
	}

	public void setLastVisitPath(String lastVisitPath) {
		this.lastVisitPath = lastVisitPath;
	}
}
