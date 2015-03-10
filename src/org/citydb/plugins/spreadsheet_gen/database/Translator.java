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
package org.citydb.plugins.spreadsheet_gen.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.citydb.modules.common.balloon.BalloonTemplateHandlerImpl;
import org.citydb.plugins.spreadsheet_gen.gui.datatype.CSVColumns;
import org.citydb.plugins.spreadsheet_gen.gui.datatype.SeparatorPhrase;
import org.citydb.plugins.spreadsheet_gen.gui.view.components.NewCSVColumnDialog;
import org.citydb.plugins.spreadsheet_gen.util.Util;

import org.citydb.api.database.BalloonTemplateHandler;
import org.citydb.api.registry.ObjectRegistry;


public class Translator {
	private StringBuffer wordparser=new StringBuffer();
	private int offset;
	private Set<String> keys;
	private Set<String> aggregations;
	private HashMap<String, Set<String>> _3dcitydbcontent;
	private ArrayList<String> columnTitle;
	private static Translator INSTANCE= new Translator();
	private Map<String, String> templateMap;
	
	public Translator(){
		BalloonTemplateHandler dummy = new BalloonTemplateHandlerImpl("", null);
		aggregations = dummy.getSupportedAggregationFunctions();
		_3dcitydbcontent = dummy.getSupportedTablesAndColumns();
		keys = _3dcitydbcontent.keySet();
		 
	}
	
	public static Translator getInstance(){
		return INSTANCE;
	}
	
	public Map<String, String> getTemplateHashmap() {
		return templateMap;	
	}
	
	public String translateToBalloonTemplate(File csvTemplate) throws Exception {
		
		templateMap = new HashMap<String, String>();
		
		String cellSeparator= SeparatorPhrase.getInstance().getTempPhrase();
		columnTitle = new ArrayList<String>();
		
		FileInputStream fstream = new FileInputStream(csvTemplate);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream,"UTF-8"));
		String strLine;
		
		// Read File Line By Line
		StringBuffer output = new StringBuffer();
		String tmpout, header;
		
		// Initial values
		output.append("<3DCityDB>CITYOBJECT/GMLID</3DCityDB>");
		columnTitle.add("GMLID");		
		templateMap.put("GMLID", "CITYOBJECT__GMLID");
		while ((strLine = br.readLine()) != null) {
			if (!(strLine.startsWith("//")||strLine.startsWith(";")) &&strLine.indexOf(':') > 0) {
				
				header = strLine.substring(0, strLine.indexOf(':'));
				tmpout = translateLine(
						strLine.substring(strLine.indexOf(':')+1,
								strLine.length()), false);
				
				String templateCSVcolumn = header;
				String dbTableColumn = null;
				try {
					dbTableColumn = getTableColumn(strLine.substring(strLine.indexOf(':')+1,strLine.length()));	
				}catch (Exception e) {
					System.out.println(e.getMessage());
				}
				
				templateMap.put(templateCSVcolumn.trim(), dbTableColumn);
						
			} else {
				tmpout = translateLine(strLine, true);
				header = null;
			}

			if (tmpout != null && tmpout.length() > 0) {
				output.append(cellSeparator);
				output.append(tmpout);
				if (header != null)
					columnTitle.add(header);
			}
			
		}
		return output.toString();
	}
	
	public String translateToBalloonTemplate( ArrayList<CSVColumns> rows) throws IOException {

		templateMap = new HashMap<String, String>();
		
		String cellSeparator= SeparatorPhrase.getInstance().getTempPhrase();
		columnTitle = new ArrayList<String>();
		StringBuffer output = new StringBuffer();
		String tmpout, header;
		// Initial values
		output.append("<3DCityDB>CITYOBJECT/GMLID</3DCityDB>");
		columnTitle.add("GMLID");
		templateMap.put("GMLID", "CITYOBJECT__GMLID");
		
		for (CSVColumns row:rows){
			header= row.title;
			tmpout= translateLine(row.textcontent,false);
			
			String templateCSVcolumn = header;
			String dbTableColumn = null;
			try {
				dbTableColumn = getTableColumn(row.textcontent);	
			}catch (Exception e) {
				System.out.println(e.getMessage());
			}
			
			templateMap.put(templateCSVcolumn.trim(), dbTableColumn);
			
			if (tmpout != null && tmpout.length() > 0) {
				output.append(cellSeparator);
				output.append(tmpout);
				columnTitle.add(header);
			}
			
		}
		
		return output.toString();
	}
	
	public String getProperHeader(String content){
		columnTitle = new ArrayList<String>();
		translateLine(content,true);
		return columnTitle.get(0);
	}
	
	public ArrayList<String> getColumnTitle(){
		return columnTitle;
	}
	
	public StyledDocument getFormatedDocument(String content){
		StyleContext context = new StyleContext();
		DefaultStyledDocument document = new DefaultStyledDocument(context);
		try{
		String tagedText= translateLine(content, false);
		int pointer=0;
		int tagIndex=0;
		while(tagedText.indexOf(BalloonTemplateHandler.START_TAG,pointer)!=-1){
			tagIndex=tagedText.indexOf(BalloonTemplateHandler.START_TAG,pointer);
			// Is there any character before the strat tag which is not parsed yet?
			if (tagIndex!=pointer){
				document.insertString(document.getLength(), tagedText.substring(pointer, tagIndex), NewCSVColumnDialog.getDefaultStyle());
				pointer = tagIndex;
			}
			pointer+=BalloonTemplateHandler.START_TAG.length();
			tagIndex=tagedText.indexOf(BalloonTemplateHandler.END_TAG,pointer);
			document.insertString(document.getLength(), tagedText.substring(pointer, tagIndex), NewCSVColumnDialog.getLabelStyle());
			pointer=tagIndex+BalloonTemplateHandler.END_TAG.length();
		}
		if (pointer!=tagedText.length()-1){
			document.insertString(document.getLength(), tagedText.substring(pointer,tagedText.length()), NewCSVColumnDialog.getDefaultStyle());
		}
		// check for [EOL]
		 pointer=0;
		 tagIndex=0;
		 tagedText= document.getText(0, document.getLength());
		 String lineSeparator=System.getProperty("line.separator");
		 while(tagedText.indexOf(lineSeparator,pointer)!=-1){
				tagIndex=tagedText.indexOf(lineSeparator,pointer);
				document.remove(tagIndex,lineSeparator.length());
				document.insertString(tagIndex,NewCSVColumnDialog.EOL,
					NewCSVColumnDialog.getEOLStyle());
				pointer=tagIndex+NewCSVColumnDialog.EOL.length();
				tagedText= document.getText(0, document.getLength());
			}		
		}catch(Exception e){}
		return document;
	}
	
	private String translateLine(String line, boolean generateHeader){
		StringBuffer sbout= new StringBuffer();
		offset=0;
		String tmpout="";
		boolean hadTable=false;
		boolean hadslash=false;
		boolean hadColumn=false;
		boolean hadAggfunc=false;
		boolean hadCondition=false;
		boolean fullTablename=false;
		boolean isClearColumn=false;
		boolean readNewWord=true;
		String extractedName="";
		String tmpWord;
		boolean hadunknown=true; // anything instead of what mentiend before
		String header="";
		int startposition=0,endposition=0;
		String currentTable="";
		line=line.replace(NewCSVColumnDialog.EOL, System.getProperty("line.separator"));
		char[] chars=line.toCharArray();
		while(offset<line.length()||!readNewWord){
			if (readNewWord)
				tmpout= getWord(chars);
			readNewWord=true;
			if (tmpout.equals("//")||tmpout.equals(";"))
				return sbout.toString();
						
			if (!hadTable && ((fullTablename=_3dcitydbcontent.containsKey(tmpout))
					|| (offset<chars.length && chars[offset]=='/'))){
				if (!fullTablename){
					extractedName=null;
					tmpWord=tmpout.toUpperCase();
					for (String tableName:keys){
						if (tmpWord.contains(tableName)){
							extractedName=tableName;
							break;
						}
					}
					if (extractedName!=null){
						hadunknown=false;
						hadTable=true;
						startposition= sbout.length()+tmpWord.indexOf(extractedName);
						header = extractedName;
						sbout.append(tmpout);
						currentTable= extractedName;
						continue;
					}
				}else{
				
					hadunknown=false;
					hadTable=true;
					startposition= sbout.length();
					header = tmpout;
					sbout.append(tmpout);
					currentTable= tmpout;
					continue;
				}
			}
			if (hadTable && !hadslash && tmpout.equals("/")){
				if (hadunknown){hadTable=false;}
				else{
					hadunknown=false;
					hadslash=true;
					sbout.append(tmpout);
					continue;
				}
			}
			if (hadTable&& hadslash && ! hadAggfunc && aggregations.contains(tmpout)){
				if (hadunknown){hadTable=false;hadslash=false;}
				else{
					hadunknown=false;
					hadAggfunc=true;
					sbout.append(tmpout);
					continue;
				}
			}
			// checking for columns 
			if (hadTable&& hadslash &&! hadColumn&& _3dcitydbcontent.containsKey(currentTable)){
				
				if (hadunknown){hadTable=false;hadslash=false;hadAggfunc=false;}
				else{
					if ((_3dcitydbcontent.get(currentTable)).contains(tmpout)){
						hadunknown=false;
						hadColumn=true;
						isClearColumn=true;
						sbout.append(tmpout);
						header+="_"+tmpout;
						endposition= sbout.length();
						continue;
					}else {
						Set<String> columns= _3dcitydbcontent.get(currentTable);
						tmpWord=tmpout.toUpperCase();
						extractedName=null;
						for (String tmpColumn:columns){
							if (tmpWord.startsWith(tmpColumn)){
								extractedName=tmpColumn;
								break;
							}
						}
						if (extractedName!=null){
							hadunknown=false;
							hadColumn=true;
							isClearColumn=false;
							sbout.append(extractedName);
							endposition= sbout.length();
							header+="_"+extractedName;
							offset-=(tmpout.length()-extractedName.length());
							continue;
						}

					}
				}
			}
			if (hadTable && hadColumn && !hadunknown&& tmpout.equals("[")){
				sbout.append(tmpout);
				hadCondition=true;
				continue;
			}
			
			if (hadTable && hadColumn &&isClearColumn&& hadCondition&& tmpout.equals("]")){
				sbout.append(tmpout);
				endposition=sbout.length();
				hadTable=false;hadColumn=false;hadslash=false;hadAggfunc=false;hadCondition=false;isClearColumn=false;
				sbout.insert(endposition, BalloonTemplateHandler.END_TAG);
				sbout.insert(startposition, BalloonTemplateHandler.START_TAG);
				continue;
			}
			if (tmpout.equals("]") ||tmpout.equals("[")){
				sbout.append(tmpout);
				continue;
			}
			if (hadTable && hadslash&& hadColumn&& !hadCondition){
				hadTable=false;hadColumn=false;hadslash=false;hadAggfunc=false;hadCondition=false;isClearColumn=false;
				sbout.insert(endposition, BalloonTemplateHandler.END_TAG);
				sbout.insert(startposition, BalloonTemplateHandler.START_TAG);	
				readNewWord=false;
				continue;
			}
			hadunknown=true;
			sbout.append(tmpout);
		}
		if (hadTable && hadslash&& hadColumn){
			sbout.insert(endposition, BalloonTemplateHandler.END_TAG);
			sbout.insert(startposition, BalloonTemplateHandler.START_TAG);				
		}
		
		if (generateHeader)
			columnTitle.add(header);
		return sbout.toString();
	}
	private String getWord(char[] chararray){
		wordparser.setLength(0);
		
		if (Character.isLetterOrDigit(chararray[offset]) ){
			while(chararray.length>offset && (Character.isLetterOrDigit(chararray[offset])||
					chararray[offset]=='_')){
				wordparser.append(chararray[offset]);
				offset++;
			}
			return wordparser.toString();
		}
		
		if (Character.isWhitespace(chararray[offset])){
			while(chararray.length>offset && Character.isWhitespace(chararray[offset])){
				wordparser.append(chararray[offset]);
				offset++;
			}
			return wordparser.toString();
		}	
		if (Character.isDefined(chararray[offset])){
				wordparser.append(chararray[offset]);
				offset++;
				if(chararray.length>offset &&chararray[offset]=='/'){
					wordparser.append(chararray[offset]);
					offset++;
				}
			return wordparser.toString();
		}
		return wordparser.toString();
	}
	
	public String getTableColumn(String rawStatement) throws Exception {	
		
		String aggregateFunction = null;		
		String table = null;
		String column = null;
		String unit = null;
		
		int index = rawStatement.indexOf('/');

		table = rawStatement.substring(0, index).trim();

		index++;
		
		// beginning of aggregate function
		if (rawStatement.charAt(index) == '[') { 
			index++;
			aggregateFunction = rawStatement.substring(index, rawStatement.indexOf(']', index)).trim();
			index = rawStatement.indexOf(']', index) + 1;
		}		
		
		// no condition
		if (rawStatement.indexOf('[', index) == -1) { 
			column = rawStatement.substring(index).trim();
			if (column.indexOf(" ") != -1 ) {					
				String[] tempStr = column.split(" ");
				column = tempStr[0];
				unit = tempStr[1];							
			}
		}
		else {
			column = rawStatement.substring(index, rawStatement.indexOf('[', index)).trim();
			index = rawStatement.indexOf(']');
			index++;
			if (index < rawStatement.length()) {
				if (rawStatement.substring(index).trim().length()>0) {
					unit = rawStatement.substring(index).trim();
				};
			}
		}
		
		// specific case 1: aggregation functions such as Max, Min, Avg, Sum, and Count, the Output value should have Number-Format
		if (aggregateFunction != null) {
			if (aggregateFunction.equalsIgnoreCase("MAX") ||
					aggregateFunction.equalsIgnoreCase("MIN") ||
					aggregateFunction.equalsIgnoreCase("AVG") ||
					aggregateFunction.equalsIgnoreCase("SUM") ||
					aggregateFunction.equalsIgnoreCase("COUNT")) {
				return Util.NUMBER_COLUMN_KEY;
			}
		}
		
		// specific case 2: Mixed columns, the output should have String-Format
		if (rawStatement.indexOf(NewCSVColumnDialog.EOL) > -1 ) {
			return Util.STRING_COLUMN_KEY;
		}
		
		// specific case 3: Number with unit "e.g. EUR", --> String-Format
		if (unit != null) {
			return Util.STRING_COLUMN_KEY;
		}
		
		return table + "__" + column;
	}
}