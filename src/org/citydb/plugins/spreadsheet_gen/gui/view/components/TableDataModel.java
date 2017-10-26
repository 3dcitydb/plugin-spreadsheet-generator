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
package org.citydb.plugins.spreadsheet_gen.gui.view.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


import javax.swing.table.AbstractTableModel;

import org.citydb.plugins.spreadsheet_gen.gui.datatype.CSVColumns;
import org.citydb.plugins.spreadsheet_gen.util.Util;



// TODO: Auto-generated Javadoc
/**
 * The Class TableDataModel.
 */
@SuppressWarnings("serial")
public class TableDataModel extends AbstractTableModel {
//	private String[] columnNames = {"Column's Title",
//			"Column's Content",
/** The column names. */
//		"Comment"};
	private String[] columnNames=null;
	
	/** The rows. */
	private ArrayList<CSVColumns> rows = new ArrayList<CSVColumns>() ;
	
	/**
	 * Instantiates a new table data model.
	 */
	public TableDataModel(){
		updateColumnsTitle();
	}
	
	/**
	 * Update columns title.
	 */
	public void updateColumnsTitle(){
		columnNames= new String[3];
		columnNames[0]=Util.I18N.getString("spshg.csvcolumns.manual.header.title");
		columnNames[1]=Util.I18N.getString("spshg.csvcolumns.manual.header.content");
		columnNames[2]=Util.I18N.getString("spshg.csvcolumns.manual.header.comment");
		fireTableStructureChanged();
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	public String getColumnName(int col) {
		return columnNames[col];
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return rows.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return columnNames.length;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int row, int col) {
		return rows.get(row).getValue(col) ;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int row, int col) {
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
	 */
	public void setValueAt(Object value, int row, int col) {
		 rows.get(row).setValue(col,value);
	}
	
	/**
	 * Adds the new row.
	 *
	 * @param data the data
	 */
	public void addNewRow(CSVColumns data){
		data.rownum=rows.size();
		rows.add(data);
		fireTableRowsInserted(rows.size()-1, rows.size()-1);
	}
	
	/**
	 * Edits the row.
	 *
	 * @param data the data
	 */
	public void editRow(CSVColumns data){
		rows.set(data.rownum, data);
		fireTableRowsUpdated(data.rownum,data.rownum);
	}
	
	/**
	 * Removes the row.
	 *
	 * @param index the index
	 */
	public void removeRow(int[] index){
		if (index==null || index.length<0) return;
		Arrays.sort(index);
		for (int j=index.length-1;j>=0;j--){
			if (index[j]<rows.size()){
				rows.remove(index[j]);
				for (int i=index[j];i<rows.size();i++)
					rows.get(i).rownum=i;
				fireTableRowsDeleted(index[j], index[j]);
			}
		}
	}
	
	/**
	 * Move.
	 *
	 * @param index the index
	 * @param moveUp the move up
	 */
	public void move(int index,boolean moveUp){
		if (index<0 || index>=rows.size()) return;
		if (moveUp &&index==0) return;
		if (!moveUp &&index==rows.size()-1) return;
		int tmp =rows.get(index).rownum;
		rows.get(index).rownum= rows.get(moveUp?(index-1):(index+1)).rownum;
		rows.get(moveUp?(index-1):(index+1)).rownum=tmp;
		Collections.sort(rows);
		fireTableDataChanged();
	}
	
	/**
	 * Gets the CSV column.
	 *
	 * @param index the index
	 * @return the CSV column
	 */
	public CSVColumns getCSVColumn(int index){
		if (index<0 || index>=rows.size()) return null;
		return rows.get(index);
	}
	
	/**
	 * Checks if is separator phrase suitable.
	 *
	 * @param phrase the phrase
	 * @return true, if is separator phrase suitable
	 */
	public boolean isSeparatorPhraseSuitable(String phrase){
		return false;
	}
	
	/**
	 * Gets the rows.
	 *
	 * @return the rows
	 */
	public ArrayList<CSVColumns> getRows(){
		return rows;
	}
	
	/**
	 * Reset.
	 */
	public void reset(){
		int size= rows.size();
		rows = new ArrayList<CSVColumns>();
		if (size!=0)
			fireTableRowsDeleted(0, size-1);
		
	}
	
} 
