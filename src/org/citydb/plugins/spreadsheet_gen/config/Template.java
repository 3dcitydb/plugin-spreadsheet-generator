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
