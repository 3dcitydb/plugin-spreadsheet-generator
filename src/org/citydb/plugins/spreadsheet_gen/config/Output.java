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

import javax.xml.bind.annotation.XmlType;



@XmlType(name="OutputType", propOrder={
		"type",
		"csvfile",
		"xlsxfile",
		"cloud"
})

public class Output {
	public static final String CSV_FILE_CONFIG="CSV_FILE";
	public static final String XLSX_FILE_CONFIG="XLSX_FILE";
	public static final String ONLINE_CONFIG="INTOCLOUD";
	
	private String type = CSV_FILE_CONFIG;
	
	private CSVFile csvfile;
	private XLSXFile xlsxfile;
	private IntoCloud cloud;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public CSVFile getCsvfile() {
		return csvfile;
	}

	public void setCsvfile(CSVFile csvfile) {
		this.csvfile = csvfile;
	}

	public XLSXFile getXlsxfile() {
		return xlsxfile;
	}

	public void setXlsxfile(XLSXFile xlsxfile) {
		this.xlsxfile = xlsxfile;
	}
	
	public Output(){
		csvfile = new CSVFile();
		xlsxfile = new XLSXFile();
		cloud = new IntoCloud();
	}

	public IntoCloud getCloud() {
		return cloud;
	}

	public void setCloud(IntoCloud cloud) {
		this.cloud = cloud;
	}

	
}
