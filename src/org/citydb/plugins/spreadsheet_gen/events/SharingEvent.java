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
package org.citydb.plugins.spreadsheet_gen.events;

import org.citydb.plugins.spreadsheet_gen.controller.cloudservice_impl.gui.Users;

import org.citydb.api.event.Event;

public class SharingEvent extends Event {
	private int type;
	private Users user;
	private String message;
	private String messageTitle;
	
	public SharingEvent(int type, Object source, Users user) {
		super(EventType.SHARING_EVENT, GLOBAL_CHANNEL, source);
		this.type = type;
		this.user = user;
	}
	public SharingEvent(int type, Object source, String messageTitle,String message) {
		super(EventType.SHARING_EVENT, GLOBAL_CHANNEL, source);
		this.type = type;
		this.user = null;
		this.message=message;
		this.messageTitle=messageTitle;
	}

	public int getType() {
		return this.type;
	}

	public Users getUser() {
		return this.user;
	}
	public String getMessage() {
		return message;
	}
	public String getMessageTitle() {
		return messageTitle;
	}
	
	
}
