/*
 * 3D City Database - The Open Source CityGML Database
 * http://www.3dcitydb.org/
 * 
 * Copyright 2013 - 2016
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
package org.citydb.plugins.spreadsheet_gen.controller.cloudservice_impl.gui;


import javax.swing.JComboBox;

import org.citydb.plugins.spreadsheet_gen.controller.cloudservice_impl.GoogleSpreadSheetService;
import org.citydb.plugins.spreadsheet_gen.util.Util;


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


