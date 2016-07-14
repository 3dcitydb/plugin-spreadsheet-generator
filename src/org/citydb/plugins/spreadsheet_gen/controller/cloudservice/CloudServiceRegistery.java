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
package org.citydb.plugins.spreadsheet_gen.controller.cloudservice;

import java.util.LinkedHashMap;
import java.util.Set;

import org.citydb.plugins.spreadsheet_gen.controller.cloudservice.CloudService;


public class CloudServiceRegistery {
	private static CloudServiceRegistery INSTANCE=new CloudServiceRegistery();
	private LinkedHashMap<String, CloudService> servicelist= new LinkedHashMap<String, CloudService>();
	private String currentService="";
	private boolean isServiceSelected=false;
	private CloudServiceRegistery(){
		isServiceSelected=false;
	}
	public static CloudServiceRegistery getInstance() {
		return INSTANCE;
	}
	
	public void registerService(CloudService service, String name){
		servicelist.put(name, service);
	}
	
	public Set<String> getListofServices(){
		return servicelist.keySet();
	}
	
	public boolean selectService(String name){
		if (servicelist.containsKey(name)){
			currentService=name;
			isServiceSelected=true;
			return true;
		}
		isServiceSelected=false;
		return false;
	}
	
	public CloudService getSelectedService(){
		return servicelist.get(currentService);
	}
	
	public boolean isServiceSelected(){
		return isServiceSelected;
	}
	
}
