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
package de.tub.citydb.plugins.spreadsheet_gen.events;

import de.tub.citydb.api.event.Event;
import de.tub.citydb.plugins.spreadsheet_gen.controller.cloudservice_impl.gui.Users;

public class SharingEvent extends Event {
	private int type;
	private Users user;
	private String message;
	private String messageTitle;
	
	public SharingEvent(int type, Object source, Users user) {
		super(EventType.SHARING_EVENT, source);
		this.type = type;
		this.user = user;
	}
	public SharingEvent(int type, Object source, String messageTitle,String message) {
		super(EventType.SHARING_EVENT, source);
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
