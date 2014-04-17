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
package de.tub.citydb.plugins.spreadsheet_gen.gui.datatype;

import java.util.LinkedHashMap;
import java.util.Set;

import de.tub.citydb.plugins.spreadsheet_gen.util.Util;

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
