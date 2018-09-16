/*
 * 3D City Database - The Open Source CityGML Database
 * http://www.3dcitydb.org/
 *
 * Copyright 2013 - 2018
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
package org.citydb.plugins.spreadsheet_gen.gui.datatype;

import java.util.LinkedHashMap;
import java.util.Set;

import org.citydb.plugins.spreadsheet_gen.util.Util;


public class SeparatorPhrase {
	private LinkedHashMap<String,String> predefiendPhrase= new LinkedHashMap<String,String>();
	private String tempPhrase=""; 
	private static SeparatorPhrase INSTANCE= new SeparatorPhrase();
	
	public static SeparatorPhrase getInstance(){
		return INSTANCE;
	}
	public void load(){
		predefiendPhrase.clear();
		predefiendPhrase.put(Util.I18N.getString("spshg.csvPanel.separator.comma"),",");
		predefiendPhrase.put(Util.I18N.getString("spshg.csvPanel.separator.tab"),"\t");
		predefiendPhrase.put(Util.I18N.getString("spshg.csvPanel.separator.semicolon"),";");
		predefiendPhrase.put(Util.I18N.getString("spshg.csvPanel.separator.space")," ");
	}
	
	public Set<String> getNicknames(){
		return predefiendPhrase.keySet();
	}
	
	public synchronized String decode(String encoded){
		
		for (String key:predefiendPhrase.keySet()){
			encoded=encoded.replace(key, predefiendPhrase.get(key));
		}
		return encoded;
	}
	
	public String getTempPhrase(){
		if (tempPhrase.length()==0)
			renewTempPhrase();
		return tempPhrase;
	}
	
	public void renewTempPhrase(){
		tempPhrase="_$"+System.currentTimeMillis()+"$_";
	}
	
	public synchronized String  getIntoCloudDefaultSeperator(){
		return ",";
	}
	
}
