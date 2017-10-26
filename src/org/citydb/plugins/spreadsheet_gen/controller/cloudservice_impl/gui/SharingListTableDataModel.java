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
package org.citydb.plugins.spreadsheet_gen.controller.cloudservice_impl.gui;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import org.citydb.plugins.spreadsheet_gen.controller.cloudservice_impl.ShareSettingController;
import org.citydb.plugins.spreadsheet_gen.events.SharingEvent;
import org.citydb.plugins.spreadsheet_gen.util.Util;

import org.citydb.api.event.EventDispatcher;
import org.citydb.api.registry.ObjectRegistry;

// TODO: Auto-generated Javadoc
/**
 * The Class SharingListTableDataModel.
 */
@SuppressWarnings("serial")
public class SharingListTableDataModel  extends AbstractTableModel{
	
	/** The Constant delColumnNumber. */
	public final static int delColumnNumber=2;
	
	/** The column names. */
	private String[] columnNames;
	
	/** The rows. */
	private ArrayList<Users> rows = new ArrayList<Users>() ;
	
	/** The del icon. */
	ImageIcon delIcon;
	
	/** The blank icon. */
	ImageIcon blankIcon;
	
	/** The event dispatcher. */
	final EventDispatcher eventDispatcher;
	
	/**
	 * Instantiates a new sharing list table data model.
	 */
	SharingListTableDataModel(){
		columnNames = new String[3];
		columnNames[0]=Util.I18N.getString("spshg.sharing.column.email");
		columnNames[1]=Util.I18N.getString("spshg.sharing.column.permission");
		columnNames[2]="";
		// "delCOlumn" is a column that shows an Icon corresponding to delete action.
		java.net.URL imgURL =getClass().getResource("img/delete.png");
		if (imgURL != null) {
			delIcon= new ImageIcon(imgURL, "delete");
		}
		BufferedImage bi = new BufferedImage(12, 12, BufferedImage.TYPE_INT_ARGB);
		blankIcon = new ImageIcon(bi);
		eventDispatcher = ObjectRegistry.getInstance().getEventDispatcher();
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int arg0) {
		switch(arg0){
		case 0: return String.class;
		case 1: return Object.class;
		case delColumnNumber: return ImageIcon.class;
		}
		return super.getColumnClass(arg0);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return rows.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int row, int col) {
		// User can not delete the owner.
		if (col==delColumnNumber && rows.get(row).isOwner())
			return blankIcon;
		if (col==delColumnNumber)
			return delIcon;
		return rows.get(row).getValue(col) ;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	public String getColumnName(int col) {
		return columnNames[col].toString();
	}
	
	/**
	 * Adds the new row.
	 *
	 * @param data the data
	 */
	public void addNewRow(Users data){
		rows.add(data);
		fireTableRowsInserted(rows.size()-1, rows.size()-1);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int row, int col) {
		// email address and delete icon are not editable
		if (col==delColumnNumber||col==0)
			return false;
		if (rows.get(row).isOwner())
			return false;
		return true;
	}
	
	
	/**
	 * Removes the row.
	 *
	 * @param rowNum the row num
	 */
	public void removeRow(int rowNum){
		if (rowNum>=rows.size()|| rowNum<0)return;
		rows.remove(rowNum);
		fireTableRowsDeleted(rowNum, rowNum);
	}
	
	/**
	 * Removes the user.
	 *
	 * @param user the user
	 */
	public void removeUser(Users user){
		rows.remove(user);
		fireTableRowsDeleted(0, rows.size());
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		switch(columnIndex){
		case 0: rows.get(rowIndex).setEmail((String)aValue); break;
		case 1: rows.get(rowIndex).setPermissionType((String)aValue);
			eventDispatcher.triggerEvent(new SharingEvent(ShareSettingController.TASK_VALUE_CHANGED, this, rows.get(rowIndex)));
		break;
		}
		fireTableRowsUpdated(rowIndex, rowIndex);
	}
	
	/**
	 * Gets the row.
	 *
	 * @param rowNum the row num
	 * @return the row
	 */
	public Users getRow(int rowNum){
		if (rowNum>=rows.size()|| rowNum<0)return null;
		return rows.get(rowNum);
	}
}
