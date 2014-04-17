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

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import de.tub.citydb.plugins.spreadsheet_gen.gui.view.components.NewCSVColumnDialog;


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
