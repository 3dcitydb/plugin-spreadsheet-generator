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
 * The Class IntoCloud.
 */
@XmlType(name="IntoCloud", propOrder={
		"email",
		"spreadsheetName",
		"serviceName"
})

public class IntoCloud {
	
	/** The service name. */
	private String serviceName="";
	
	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * Sets the service name.
	 *
	 * @param serviceName the new service name
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	/** The email. */
	private String email="";
	
	/** The spreadsheet name. */
	private String spreadsheetName="";
	
	/**
	 * Gets the email.
	 *
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the email.
	 *
	 * @param email the new email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Gets the spreadsheet name.
	 *
	 * @return the spreadsheet name
	 */
	public String getSpreadsheetName() {
		return spreadsheetName;
	}

	/**
	 * Sets the spreadsheet name.
	 *
	 * @param spreadsheetName the new spreadsheet name
	 */
	public void setSpreadsheetName(String spreadsheetName) {
		this.spreadsheetName = spreadsheetName;
	}

	/**
	 * Instantiates a new into cloud.
	 */
	public IntoCloud(){
		
	}
}
