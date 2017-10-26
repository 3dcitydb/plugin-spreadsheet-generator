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
package org.citydb.plugins.spreadsheet_gen.controller.cloudservice;

// TODO: Auto-generated Javadoc
/**
 * The Class UploadException.
 */
@SuppressWarnings("serial")
public class UploadException extends Exception {
	
	/** The Constant UNKNOWN. */
	public final static int UNKNOWN=0;
	
	/** The Constant UPLOAD_ERROR. */
	public final static int UPLOAD_ERROR=1;
	
	/** The Constant PUBLISH_ERROR. */
	public final static int PUBLISH_ERROR=2;
	
	/** The message. */
	String message;
	
	/** The type. */
	int type;
	
	/**
	 * Instantiates a new upload exception.
	 *
	 * @param type the type
	 */
	public UploadException(int type){
		message="unknown";
		this.type=type;
	}
	
	/**
	 * Instantiates a new upload exception.
	 *
	 * @param type the type
	 * @param message the message
	 */
	public UploadException(int type,String message){
		this.message=message;
		this.type=type;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	public String getMessage(){
		return message;
	}
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public int getType(){
		return this.type;
	}
}
