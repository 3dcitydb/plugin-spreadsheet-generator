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
package org.citydb.plugins.spreadsheet_gen.gui.datatype;

import java.util.LinkedHashMap;
import java.util.Set;

import org.citydb.plugins.spreadsheet_gen.util.Util;


// TODO: Auto-generated Javadoc
/**
 * The Class SeparatorPhrase.
 */
public class SeparatorPhrase {
	
	/** The predefiend phrase. */
	private LinkedHashMap<String,String> predefiendPhrase= new LinkedHashMap<String,String>();
	
	/** The temp phrase. */
	private String tempPhrase=""; 
	
	/** The instance. */
	private static SeparatorPhrase INSTANCE= new SeparatorPhrase();
	
	/**
	 * Gets the single instance of SeparatorPhrase.
	 *
	 * @return single instance of SeparatorPhrase
	 */
	public static SeparatorPhrase getInstance(){
		return INSTANCE;
	}
	
	/**
	 * Load.
	 */
	public void load(){
		predefiendPhrase.clear();
		predefiendPhrase.put(Util.I18N.getString("spshg.csvPanel.separator.comma"),",");
		predefiendPhrase.put(Util.I18N.getString("spshg.csvPanel.separator.tab"),"\t");
		predefiendPhrase.put(Util.I18N.getString("spshg.csvPanel.separator.semicolon"),";");
		predefiendPhrase.put(Util.I18N.getString("spshg.csvPanel.separator.space")," ");
	}
	
	/**
	 * Gets the nicknames.
	 *
	 * @return the nicknames
	 */
	public Set<String> getNicknames(){
		return predefiendPhrase.keySet();
	}
	
	/**
	 * Decode.
	 *
	 * @param encoded the encoded
	 * @return the string
	 */
	public synchronized String decode(String encoded){
		
		for (String key:predefiendPhrase.keySet()){
			encoded=encoded.replace(key, predefiendPhrase.get(key));
		}
		return encoded;
	}
	
	/**
	 * Gets the temp phrase.
	 *
	 * @return the temp phrase
	 */
	public String getTempPhrase(){
		if (tempPhrase.length()==0)
			renewTempPhrase();
		return tempPhrase;
	}
	
	/**
	 * Renew temp phrase.
	 */
	public void renewTempPhrase(){
		tempPhrase="_$"+System.currentTimeMillis()+"$_";
	}
	
	/**
	 * Gets the into cloud default seperator.
	 *
	 * @return the into cloud default seperator
	 */
	public synchronized String  getIntoCloudDefaultSeperator(){
		return ",";
	}
	
}
