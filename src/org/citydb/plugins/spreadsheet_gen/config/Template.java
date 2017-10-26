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
package org.citydb.plugins.spreadsheet_gen.config;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.citydb.plugins.spreadsheet_gen.gui.datatype.CSVColumns;



// TODO: Auto-generated Javadoc
/**
 * The Class Template.
 */
@XmlType(name="TemplateType", propOrder={
		"path"
})
public class Template {

	/** The path. */
	private String path="";
	
	/** The columns list. */
	@XmlTransient
	private ArrayList<CSVColumns> columnsList=null;
	
	/** The is manual template. */
	@XmlTransient
	private boolean isManualTemplate=false;
	
	/** The last visit path. */
	@XmlTransient
	private String lastVisitPath="";

	/**
	 * Instantiates a new template.
	 */
	public Template(){
	}
	
	/**
	 * Gets the columns list.
	 *
	 * @return the columns list
	 */
	public ArrayList<CSVColumns> getColumnsList() {
		return columnsList;
	}

	/**
	 * Sets the columns list.
	 *
	 * @param columnsList the new columns list
	 */
	public void setColumnsList(ArrayList<CSVColumns> columnsList) {
		this.columnsList = columnsList;
	}
	
	/**
	 * Checks if is manual template.
	 *
	 * @return true, if is manual template
	 */
	public boolean isManualTemplate() {
		return isManualTemplate;
	}
	
	/**
	 * Sets the manual template.
	 *
	 * @param isManualTemplate the new manual template
	 */
	public void setManualTemplate(boolean isManualTemplate) {
		this.isManualTemplate = isManualTemplate;
	}
	
	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Sets the path.
	 *
	 * @param path the new path
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Gets the last visit path.
	 *
	 * @return the last visit path
	 */
	public String getLastVisitPath() {
		return lastVisitPath;
	}

	/**
	 * Sets the last visit path.
	 *
	 * @param lastVisitPath the new last visit path
	 */
	public void setLastVisitPath(String lastVisitPath) {
		this.lastVisitPath = lastVisitPath;
	}
}
