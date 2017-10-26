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
package org.citydb.plugins.spreadsheet_gen.concurrent.work;

// TODO: Auto-generated Javadoc
/**
 * The Class RowofCSVWork.
 */
public class RowofCSVWork {
	
	/** The text. */
	private String text;
	
	/** The classid. */
	private int classid;
	
	/** The Constant UNKNOWN_CLASS_ID. */
	public final static int UNKNOWN_CLASS_ID=-1;
	
	/**
	 * Instantiates a new rowof CSV work.
	 *
	 * @param text the text
	 * @param classID the class ID
	 */
	public RowofCSVWork(String text, int classID){
		this.text=text;
		this.classid=classID;
	}
	
	

	/**
	 * Gets the text.
	 *
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the text.
	 *
	 * @param text the new text
	 */
	public void setText(String text) {
		this.text = text;
	}



	/**
	 * Gets the classid.
	 *
	 * @return the classid
	 */
	public int getClassid() {
		return classid;
	}

	/**
	 * Sets the classid.
	 *
	 * @param classid the new classid
	 */
	public void setClassid(int classid) {
		this.classid = classid;
	}
}
