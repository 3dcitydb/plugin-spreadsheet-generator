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

import javax.xml.bind.annotation.XmlType;



// TODO: Auto-generated Javadoc
/**
 * The Class Output.
 */
@XmlType(name="OutputType", propOrder={
		"type",
		"csvfile",
		"xlsxfile",
		"cloud"
})

public class Output {
	
	/** The Constant CSV_FILE_CONFIG. */
	public static final String CSV_FILE_CONFIG="CSV_FILE";
	
	/** The Constant XLSX_FILE_CONFIG. */
	public static final String XLSX_FILE_CONFIG="XLSX_FILE";
	
	/** The Constant ONLINE_CONFIG. */
	public static final String ONLINE_CONFIG="INTOCLOUD";
	
	/** The type. */
	private String type = CSV_FILE_CONFIG;
	
	/** The csvfile. */
	private CSVFile csvfile;
	
	/** The xlsxfile. */
	private XLSXFile xlsxfile;
	
	/** The cloud. */
	private IntoCloud cloud;
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the csvfile.
	 *
	 * @return the csvfile
	 */
	public CSVFile getCsvfile() {
		return csvfile;
	}

	/**
	 * Sets the csvfile.
	 *
	 * @param csvfile the new csvfile
	 */
	public void setCsvfile(CSVFile csvfile) {
		this.csvfile = csvfile;
	}

	/**
	 * Gets the xlsxfile.
	 *
	 * @return the xlsxfile
	 */
	public XLSXFile getXlsxfile() {
		return xlsxfile;
	}

	/**
	 * Sets the xlsxfile.
	 *
	 * @param xlsxfile the new xlsxfile
	 */
	public void setXlsxfile(XLSXFile xlsxfile) {
		this.xlsxfile = xlsxfile;
	}
	
	/**
	 * Instantiates a new output.
	 */
	public Output(){
		csvfile = new CSVFile();
		xlsxfile = new XLSXFile();
		cloud = new IntoCloud();
	}

	/**
	 * Gets the cloud.
	 *
	 * @return the cloud
	 */
	public IntoCloud getCloud() {
		return cloud;
	}

	/**
	 * Sets the cloud.
	 *
	 * @param cloud the new cloud
	 */
	public void setCloud(IntoCloud cloud) {
		this.cloud = cloud;
	}

	
}
