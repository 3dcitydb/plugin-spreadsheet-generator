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
package org.citydb.plugins.spreadsheet_gen.controller.cloudservice_impl;

import java.util.ArrayList;
import java.util.List;

import org.citydb.plugins.spreadsheet_gen.controller.cloudservice_impl.gui.Users;
import org.citydb.plugins.spreadsheet_gen.events.SharingEvent;
import org.citydb.plugins.spreadsheet_gen.util.Util;

import com.google.gdata.data.acl.AclEntry;
import com.google.gdata.util.VersionConflictException;

import org.citydb.api.event.EventDispatcher;
import org.citydb.api.registry.ObjectRegistry;

// TODO: Auto-generated Javadoc
/**
 * The Class ShareSettingController.
 */
public class ShareSettingController implements Runnable{

	/** The Constant TASK_ADD_NEW. */
	public final static int TASK_ADD_NEW=1;
	
	/** The Constant TASK_UPDATE_PERMISSION. */
	public final static int TASK_UPDATE_PERMISSION=2;
	
	/** The Constant TASK_REMOVE. */
	public final static int TASK_REMOVE=3;
	
	/** The Constant TASK_LOAD_CURRENT_STATE. */
	public final static int TASK_LOAD_CURRENT_STATE=4;
	
	/** The Constant TASK_VALUE_CHANGED. */
	public final static int TASK_VALUE_CHANGED=5;
	
	/** The Constant FINISH_UPDATE. */
	public final static int FINISH_UPDATE=6;
	
	/** The Constant TASK_CANCELD. */
	// may be by exception
	public final static int TASK_CANCELD=7;
	
	/** The Acl entty list. */
	public static volatile List<AclEntry> AclEnttyList= new ArrayList<AclEntry>();
	
	/** The type. */
	private int type=0;
	
	/** The gsss. */
	private GoogleSpreadSheetService gsss=null;
	
	/** The user. */
	private Users user=null;
	
	/** The scope. */
	private int scope;
	
	/** The event dispatcher. */
	final EventDispatcher eventDispatcher;
	
	/**
	 * Instantiates a new share setting controller.
	 *
	 * @param gsss the gsss
	 */
	public ShareSettingController(GoogleSpreadSheetService gsss){
		this.gsss=gsss;
		eventDispatcher = ObjectRegistry.getInstance().getEventDispatcher();
	}
	
	/**
	 * Adds the new ACLE ntry.
	 *
	 * @param newUser the new user
	 */
	public void addNewACLENtry(Users newUser ){
		type= TASK_ADD_NEW;
		this.user=newUser;
		this.scope=GoogleSpreadSheetService.SCOPE_USER;
	}
	
	/**
	 * Load initial state.
	 */
	public void loadInitialState(){
		type= TASK_LOAD_CURRENT_STATE;
	}
	
	/**
	 * Update permission.
	 *
	 * @param user the user
	 */
	public void updatePermission(Users user ){
		this.user=user;
		type= TASK_UPDATE_PERMISSION;
	}
	
	/**
	 * Removes the ACL entrl.
	 *
	 * @param user the user
	 */
	public void removeACLEntrl(Users user){
		this.user=user;
		type= TASK_REMOVE;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		AclEntry acle;
		Users newuser;
		try{
		switch(type){
		case TASK_ADD_NEW:
			if (user==null) return;
			acle= gsss.shareDocument(user.getPermissionType(), scope, user.getEmail());
			AclEnttyList.add(acle);
			user.setName(acle.getScope().getName());
			eventDispatcher.triggerEvent(new SharingEvent(TASK_ADD_NEW, this, user));
			return;
		case TASK_LOAD_CURRENT_STATE:
			AclEnttyList=gsss.updateAclList();
			for(AclEntry entry:AclEnttyList){
				if (entry.getWithKey()!=null){ // anybody who has the link!
					newuser= new Users(null,null,GoogleSpreadSheetService.toLocalCode (entry.getWithKey().getRole()),
							GoogleSpreadSheetService.SCOPE_DEFAULT_WITH_KEY);
				}else	// all the others!		
					newuser= new Users(entry.getScope().getName(),entry.getScope().getValue(),
						GoogleSpreadSheetService.toLocalCode (entry.getRole()),GoogleSpreadSheetService.toLocalCode(entry.getScope().getType()));
				eventDispatcher.triggerEvent(new SharingEvent(TASK_LOAD_CURRENT_STATE, this, newuser));
			}
			eventDispatcher.triggerEvent(new SharingEvent(FINISH_UPDATE, this, null));
			return;
		case TASK_UPDATE_PERMISSION: 
			if (user==null) return;
			AclEntry tmpentry;
			if (user.isVisibilityDescription()){ // sharing permission
				for(AclEntry entry:AclEnttyList){
					if (entry.getScope().getValue()==null){
						if (user.getScope()==GoogleSpreadSheetService.SCOPE_PRIVATE){
							AclEnttyList.remove(entry);
							entry.delete();
							eventDispatcher.triggerEvent(new SharingEvent(TASK_UPDATE_PERMISSION, this, user));
							return;
						}else{
							entry.delete();
							tmpentry=gsss.updatePermission(null, user);
							AclEnttyList.remove(entry);
							AclEnttyList.add(tmpentry);
							eventDispatcher.triggerEvent(new SharingEvent(TASK_UPDATE_PERMISSION, this, user));
							return;
						}
					}
				}
				// previous it was just private, so there is not any AclEntry.
				tmpentry=gsss.updatePermission(null, user);
				AclEnttyList.add(tmpentry);
				eventDispatcher.triggerEvent(new SharingEvent(TASK_UPDATE_PERMISSION, this, user));
				return;
			}else{ // users
				for(AclEntry entry:AclEnttyList)
					if (entry.getScope().getValue()!=null &&
							entry.getScope().getValue().equalsIgnoreCase(user.getEmail())){
						tmpentry=gsss.updatePermission(entry, user);
						AclEnttyList.remove(entry);
						AclEnttyList.add(tmpentry);
						eventDispatcher.triggerEvent(new SharingEvent(TASK_UPDATE_PERMISSION, this, user));
						return;
					}
			}
			
			return;
		case TASK_REMOVE:
			if (user==null) return;
			for(AclEntry entry:AclEnttyList){
				if (entry.getScope().getValue()!=null && 
						entry.getScope().getValue().equalsIgnoreCase(user.getEmail())){
					entry.delete();
					AclEnttyList.remove(entry);
					eventDispatcher.triggerEvent(new SharingEvent(TASK_REMOVE, this, user));
					return;
				}
			}
			
			return;
		}
		}catch (Exception e){
			if (e instanceof VersionConflictException){
				eventDispatcher.triggerEvent(new SharingEvent(TASK_CANCELD, this,
						Util.I18N.getString("spshg.message.export.sharesettings.email.exist")
						,e.getLocalizedMessage()));
				return;
			}	
		}
		eventDispatcher.triggerEvent(new SharingEvent(TASK_CANCELD, this, user));
	}

}
