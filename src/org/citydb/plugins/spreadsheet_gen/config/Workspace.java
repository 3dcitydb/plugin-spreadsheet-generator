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
 * The Class Workspace.
 */
@XmlType(name="WorkspaceType", propOrder={
		"name",
		"timestamp"
})
public class Workspace {

	/** The name. */
	private String name = "LIVE";
	
	/** The timestamp. */
	private String timestamp = "";

	/**
	 * Instantiates a new workspace.
	 */
	public Workspace() {
	}
	
	/**
	 * Instantiates a new workspace.
	 *
	 * @param name the name
	 */
	public Workspace(String name) {
		setName(name);
	}
	
	/**
	 * Instantiates a new workspace.
	 *
	 * @param name the name
	 * @param timestamp the timestamp
	 */
	public Workspace(String name, String timestamp) {
		this(name);
		setTimestamp(timestamp);
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		if (name != null)
			this.name = name;
	}

	/**
	 * Gets the timestamp.
	 *
	 * @return the timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * Sets the timestamp.
	 *
	 * @param timestamp the new timestamp
	 */
	public void setTimestamp(String timestamp) {
		if (timestamp != null)
			this.timestamp = timestamp;
	}

}