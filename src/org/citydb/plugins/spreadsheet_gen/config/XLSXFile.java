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

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

// TODO: Auto-generated Javadoc
/**
 * The Class XLSXFile.
 */
@XmlType(name="OutputXLSXFileType", propOrder={
		"outputPath"
})
public class XLSXFile {
	
	/** The output path. */
	private String outputPath="";
	
	/** The last visit path. */
	@XmlTransient
	private String lastVisitPath="";
	
	
	/**
	 * Gets the output path.
	 *
	 * @return the output path
	 */
	public String getOutputPath() {
		return outputPath;
	}
	
	/**
	 * Sets the output path.
	 *
	 * @param outputPath the new output path
	 */
	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
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
	 * @param lastVisit the new last visit path
	 */
	public void setLastVisitPath(String lastVisit) {
		this.lastVisitPath = lastVisit;
	}
	
	/**
	 * Instantiates a new XLSX file.
	 */
	public XLSXFile(){
		
	}

}
