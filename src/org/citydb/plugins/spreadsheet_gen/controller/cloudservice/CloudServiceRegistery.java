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

import java.util.LinkedHashMap;
import java.util.Set;

import org.citydb.plugins.spreadsheet_gen.controller.cloudservice.CloudService;


// TODO: Auto-generated Javadoc
/**
 * The Class CloudServiceRegistery.
 */
public class CloudServiceRegistery {
	
	/** The instance. */
	private static CloudServiceRegistery INSTANCE=new CloudServiceRegistery();
	
	/** The servicelist. */
	private LinkedHashMap<String, CloudService> servicelist= new LinkedHashMap<String, CloudService>();
	
	/** The current service. */
	private String currentService="";
	
	/** The is service selected. */
	private boolean isServiceSelected=false;
	
	/**
	 * Instantiates a new cloud service registery.
	 */
	private CloudServiceRegistery(){
		isServiceSelected=false;
	}
	
	/**
	 * Gets the single instance of CloudServiceRegistery.
	 *
	 * @return single instance of CloudServiceRegistery
	 */
	public static CloudServiceRegistery getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Register service.
	 *
	 * @param service the service
	 * @param name the name
	 */
	public void registerService(CloudService service, String name){
		servicelist.put(name, service);
	}
	
	/**
	 * Gets the listof services.
	 *
	 * @return the listof services
	 */
	public Set<String> getListofServices(){
		return servicelist.keySet();
	}
	
	/**
	 * Select service.
	 *
	 * @param name the name
	 * @return true, if successful
	 */
	public boolean selectService(String name){
		if (servicelist.containsKey(name)){
			currentService=name;
			isServiceSelected=true;
			return true;
		}
		isServiceSelected=false;
		return false;
	}
	
	/**
	 * Gets the selected service.
	 *
	 * @return the selected service
	 */
	public CloudService getSelectedService(){
		return servicelist.get(currentService);
	}
	
	/**
	 * Checks if is service selected.
	 *
	 * @return true, if is service selected
	 */
	public boolean isServiceSelected(){
		return isServiceSelected;
	}
	
}
