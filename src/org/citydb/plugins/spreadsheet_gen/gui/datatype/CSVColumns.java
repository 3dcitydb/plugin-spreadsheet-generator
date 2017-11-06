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

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.citydb.plugins.spreadsheet_gen.gui.view.components.NewCSVColumnDialog;



public class CSVColumns implements Comparable<CSVColumns>{
	public int rownum;
	public String title="";
	public String textcontent="";
	public String comment="";
	public StyledDocument document;
	
	public CSVColumns(){
		this.rownum=-1;
		this.textcontent="";	
		this.title="";
		this.comment="";
		
		StyleContext context = new StyleContext();
		document = new DefaultStyledDocument(context);
	}
	
	public CSVColumns( String title, String textcontent, String comment,StyledDocument document ){
		this.title=title;
		this.textcontent=textcontent;
		this.comment=comment;
		this.document=document;
	}
	
	public CSVColumns( String title, String textcontent, String comment){
		this.title=title;
		this.textcontent=textcontent;
		this.comment=comment;
		StyleContext context = new StyleContext();
		document = new DefaultStyledDocument(context);
		try {
			document.insertString(0, textcontent, NewCSVColumnDialog.getDefaultStyle());
		} catch (BadLocationException e) {}
	}
	
	public String getValue(int col){
		switch(col){
			case 0: return title;
			case 1: return textcontent;
			case 2: return comment;
			default: return "";
		}
	}
	
	public void setValues(String title, String textcontent, String comment,StyledDocument document){
		this.title=title;
		this.textcontent=textcontent;
		this.comment=comment;
		this.document=document;
	}
	public void setValue(int col, Object obj){
		
		switch(col){
			case 0: title=(String)obj;return;
			case 1: textcontent=(String)obj;return;
			case 2: comment=(String)obj;return;
		}
		
	}

	@Override
	public int compareTo(CSVColumns o) {
		if (!(o instanceof CSVColumns  )) return 0;
		if (((CSVColumns)o).rownum >this.rownum) return -1;
		if (((CSVColumns)o).rownum <this.rownum) return 1;
		return 0;
		
	}

	
}
