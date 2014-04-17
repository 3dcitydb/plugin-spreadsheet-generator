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
package de.tub.citydb.plugins.spreadsheet_gen.controller.cloudservice_impl.gui;


import javax.swing.JComboBox;

import de.tub.citydb.plugins.spreadsheet_gen.controller.cloudservice_impl.GoogleSpreadSheetService;
import de.tub.citydb.plugins.spreadsheet_gen.util.Util;

public class Users {
	private String name;
	private String email;
	private int permissionType=GoogleSpreadSheetService.ROLE_READER;
	private int scope=GoogleSpreadSheetService.SCOPE_USER;
	
	
	public Users(String name,String email, String type,int scope){
		setEmail(email);
		setName(name);
		this.scope=scope;
		setPermissionType(type);
	}
	
	public Users(String name,String email, int type,int scope){
		setEmail(email);
		setName(name);
		this.permissionType=type;
		this.scope=scope;
	}

	public String getEmail() {
		if (email!=null)
			return this.email; 
		return "";
	}
	
	public String getName(){
		return name;
	}
	
	public boolean isVisibilityDescription(){
		return (this.email==null);
	}
	
	public void setEmail(String email) {
		if (email!=null)
			this.email = email.trim();
		else
			this.email =null;
	}
	public void setName(String name){
		if (name!=null)
			this.name=name.trim();
		else
			this.name="";
	}

	public int getScope() {
		return scope;
	}

	public void setScope(int scope) {
		this.scope = scope;
	}

	public int getPermissionType() {
		return permissionType;
	}

	public void setPermissionType(String type) {
		if (type.equalsIgnoreCase(Util.I18N.getString("spshg.dialog.sharesettings.read")))
			this.permissionType = GoogleSpreadSheetService.ROLE_READER;
		else
			this.permissionType = GoogleSpreadSheetService.ROLE_WRITER;
	}

	public Object getValue(int col){
		switch (col){
		case 0:
			if (name==null ||name.trim().length()==0|| name.equalsIgnoreCase(email))
				return email;
			return name+" ("+email+")";
		case 1: 
			if (permissionType==GoogleSpreadSheetService.ROLE_READER)
				return Util.I18N.getString("spshg.dialog.sharesettings.read");
			if (permissionType==GoogleSpreadSheetService.ROLE_WRITER)
				return Util.I18N.getString("spshg.dialog.sharesettings.write");
			if (isOwner())
				return Util.I18N.getString("spshg.dialog.sharesettings.owner");
			
		}
		return null;
	}
	
	public static JComboBox getPermissionComboBox(){
		JComboBox pcb= new JComboBox();
		pcb.addItem(Util.I18N.getString("spshg.dialog.sharesettings.read"));
		pcb.addItem(Util.I18N.getString("spshg.dialog.sharesettings.write"));
		pcb.setSelectedIndex(0);
		return pcb;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Users)) return false;
		if (((Users)obj).getEmail().equalsIgnoreCase(this.email))
			return true;
		return false;
	}
	
	@Override
	public String toString() {
		return "Name:"+name+" E-mail:"+email+" Role:"+permissionType+" scope:"+scope;
	}
	
	public boolean isOwner(){
		return permissionType==GoogleSpreadSheetService.ROLE_OWNER;
	}
}


