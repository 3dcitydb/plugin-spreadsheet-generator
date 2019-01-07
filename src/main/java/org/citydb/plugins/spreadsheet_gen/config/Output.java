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

import javax.xml.bind.annotation.XmlType;

@XmlType(name="OutputType", propOrder={
		"type",
		"csvfile",
		"xlsxfile"
})

public class Output {
	public static final String CSV_FILE_CONFIG="CSV_FILE";
	public static final String XLSX_FILE_CONFIG="XLSX_FILE";

	private String type = CSV_FILE_CONFIG;
	
	private CSVFile csvfile;
	private XLSXFile xlsxfile;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public CSVFile getCsvfile() {
		return csvfile;
	}

	public XLSXFile getXlsxfile() {
		return xlsxfile;
	}

	public Output(){
		csvfile = new CSVFile();
		xlsxfile = new XLSXFile();
	}
}
