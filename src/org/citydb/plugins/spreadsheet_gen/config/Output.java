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

import javax.xml.bind.annotation.XmlType;



@XmlType(name="OutputType", propOrder={
		"type",
		"csvfile",
		"cloud"
})

public class Output {
	public static final String CSV_FILE_CONFIG="CSV_FILE";
	public static final String ONLINE_CONFIG="INTOCLOUD";
	
	private String type = CSV_FILE_CONFIG;
	
	private CSVFile csvfile;
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

	
	public Output(){
		csvfile = new CSVFile();
		cloud = new IntoCloud();
	}

	public IntoCloud getCloud() {
		return cloud;
	}

	public void setCloud(IntoCloud cloud) {
		this.cloud = cloud;
	}
}
