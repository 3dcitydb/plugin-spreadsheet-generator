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
package org.citydb.plugins.spreadsheet_gen.controller.cloudservice_impl.gui;


import javax.swing.JComboBox;

import org.citydb.plugins.spreadsheet_gen.controller.cloudservice_impl.GoogleSpreadSheetService;
import org.citydb.plugins.spreadsheet_gen.util.Util;


// TODO: Auto-generated Javadoc
/**
 * The Class Users.
 */
public class Users {
	
	/** The name. */
	private String name;
	
	/** The email. */
	private String email;
	
	/** The permission type. */
	private int permissionType=GoogleSpreadSheetService.ROLE_READER;
	
	/** The scope. */
	private int scope=GoogleSpreadSheetService.SCOPE_USER;
	
	
	/**
	 * Instantiates a new users.
	 *
	 * @param name the name
	 * @param email the email
	 * @param type the type
	 * @param scope the scope
	 */
	public Users(String name,String email, String type,int scope){
		setEmail(email);
		setName(name);
		this.scope=scope;
		setPermissionType(type);
	}
	
	/**
	 * Instantiates a new users.
	 *
	 * @param name the name
	 * @param email the email
	 * @param type the type
	 * @param scope the scope
	 */
	public Users(String name,String email, int type,int scope){
		setEmail(email);
		setName(name);
		this.permissionType=type;
		this.scope=scope;
	}

	/**
	 * Gets the email.
	 *
	 * @return the email
	 */
	public String getEmail() {
		if (email!=null)
			return this.email; 
		return "";
	}
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Checks if is visibility description.
	 *
	 * @return true, if is visibility description
	 */
	public boolean isVisibilityDescription(){
		return (this.email==null);
	}
	
	/**
	 * Sets the email.
	 *
	 * @param email the new email
	 */
	public void setEmail(String email) {
		if (email!=null)
			this.email = email.trim();
		else
			this.email =null;
	}
	
	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name){
		if (name!=null)
			this.name=name.trim();
		else
			this.name="";
	}

	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	public int getScope() {
		return scope;
	}

	/**
	 * Sets the scope.
	 *
	 * @param scope the new scope
	 */
	public void setScope(int scope) {
		this.scope = scope;
	}

	/**
	 * Gets the permission type.
	 *
	 * @return the permission type
	 */
	public int getPermissionType() {
		return permissionType;
	}

	/**
	 * Sets the permission type.
	 *
	 * @param type the new permission type
	 */
	public void setPermissionType(String type) {
		if (type.equalsIgnoreCase(Util.I18N.getString("spshg.dialog.sharesettings.read")))
			this.permissionType = GoogleSpreadSheetService.ROLE_READER;
		else
			this.permissionType = GoogleSpreadSheetService.ROLE_WRITER;
	}

	/**
	 * Gets the value.
	 *
	 * @param col the col
	 * @return the value
	 */
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
	
	/**
	 * Gets the permission combo box.
	 *
	 * @return the permission combo box
	 */
	public static JComboBox getPermissionComboBox(){
		JComboBox pcb= new JComboBox();
		pcb.addItem(Util.I18N.getString("spshg.dialog.sharesettings.read"));
		pcb.addItem(Util.I18N.getString("spshg.dialog.sharesettings.write"));
		pcb.setSelectedIndex(0);
		return pcb;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Users)) return false;
		if (((Users)obj).getEmail().equalsIgnoreCase(this.email))
			return true;
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Name:"+name+" E-mail:"+email+" Role:"+permissionType+" scope:"+scope;
	}
	
	/**
	 * Checks if is owner.
	 *
	 * @return true, if is owner
	 */
	public boolean isOwner(){
		return permissionType==GoogleSpreadSheetService.ROLE_OWNER;
	}
}


