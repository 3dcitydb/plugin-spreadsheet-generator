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
