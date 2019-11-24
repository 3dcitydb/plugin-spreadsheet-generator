/*
 * 3D City Database - The Open Source CityGML Database
 * http://www.3dcitydb.org/
 *
 * Copyright 2013 - 2019
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

import org.citydb.plugins.spreadsheet_gen.gui.datatype.CSVColumns;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;



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
