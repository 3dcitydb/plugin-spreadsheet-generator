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
package de.tub.citydb.plugins.spreadsheet_gen.controller.cloudservice;

@SuppressWarnings("serial")
public class UploadException extends Exception {
	public final static int UNKNOWN=0;
	public final static int UPLOAD_ERROR=1;
	public final static int PUBLISH_ERROR=2;
	
	String message;
	int type;
	public UploadException(int type){
		message="unknown";
		this.type=type;
	}
	public UploadException(int type,String message){
		this.message=message;
		this.type=type;
	}
	
	public String getMessage(){
		return message;
	}
	public int getType(){
		return this.type;
	}
}
