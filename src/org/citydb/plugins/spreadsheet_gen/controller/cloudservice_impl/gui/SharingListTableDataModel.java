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
package org.citydb.plugins.spreadsheet_gen.controller.cloudservice_impl.gui;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import org.citydb.plugins.spreadsheet_gen.controller.cloudservice_impl.ShareSettingController;
import org.citydb.plugins.spreadsheet_gen.events.SharingEvent;
import org.citydb.plugins.spreadsheet_gen.util.Util;

import de.tub.citydb.api.event.EventDispatcher;
import de.tub.citydb.api.registry.ObjectRegistry;

@SuppressWarnings("serial")
public class SharingListTableDataModel  extends AbstractTableModel{
	public final static int delColumnNumber=2;
	private String[] columnNames;
	private ArrayList<Users> rows = new ArrayList<Users>() ;
	ImageIcon delIcon;
	ImageIcon blankIcon;
	final EventDispatcher eventDispatcher;
	
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
	
	@Override
	public Class<?> getColumnClass(int arg0) {
		switch(arg0){
		case 0: return String.class;
		case 1: return Object.class;
		case delColumnNumber: return ImageIcon.class;
		}
		return super.getColumnClass(arg0);
	}
	
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return rows.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		// User can not delete the owner.
		if (col==delColumnNumber && rows.get(row).isOwner())
			return blankIcon;
		if (col==delColumnNumber)
			return delIcon;
		return rows.get(row).getValue(col) ;
	}
	
	public String getColumnName(int col) {
		return columnNames[col].toString();
	}
	
	public void addNewRow(Users data){
		rows.add(data);
		fireTableRowsInserted(rows.size()-1, rows.size()-1);
	}
	
	public boolean isCellEditable(int row, int col) {
		// email address and delete icon are not editable
		if (col==delColumnNumber||col==0)
			return false;
		if (rows.get(row).isOwner())
			return false;
		return true;
	}
	
	
	public void removeRow(int rowNum){
		if (rowNum>=rows.size()|| rowNum<0)return;
		rows.remove(rowNum);
		fireTableRowsDeleted(rowNum, rowNum);
	}
	
	public void removeUser(Users user){
		rows.remove(user);
		fireTableRowsDeleted(0, rows.size());
	}
	
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
	
	public Users getRow(int rowNum){
		if (rowNum>=rows.size()|| rowNum<0)return null;
		return rows.get(rowNum);
	}
}
