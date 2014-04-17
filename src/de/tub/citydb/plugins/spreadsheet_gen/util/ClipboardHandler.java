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
package de.tub.citydb.plugins.spreadsheet_gen.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;


public class ClipboardHandler implements ClipboardOwner{
	
	ClipboardHandler(){
		
	}
	
	public boolean containsBBX(){
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) 
	    	return true;
		return false;
	}
	
	/**
	 * put following string in clipboard: BBOX=xmin,ymin,xmax,ymax
	 * @param bbx order list of xmin,ymin,xmax,ymax
	 */
	public void copy(double[] bbx){
		if (bbx==null || bbx.length!=4) return;
		String content = "BBOX="+Double.toString(bbx[0])+","+
			Double.toString(bbx[1])+","+
			Double.toString(bbx[2])+","+
			Double.toString(bbx[3]);
	    StringSelection stringSelection = new StringSelection( content );
	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    clipboard.setContents(stringSelection, this );
	}
	
	public void copy(String content){
	    StringSelection stringSelection = new StringSelection( content );
	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    clipboard.setContents(stringSelection, this );
	}
	
	/**
	 * 
	 * @return: 4 values:BBOX=xmin,ymin,xmax,ymax
	 */
	public double[] paste(){
		String result = "";
	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
	      try {
	    	Transferable contents = clipboard.getContents(null);
	        result = (String)contents.getTransferData(DataFlavor.stringFlavor);
	        if (result.lastIndexOf('=')>-1)
	        	result = result.substring(result.lastIndexOf('=')+1, result.length()).trim();
	        else 
	        	result =result.trim();
	        String[] list = result.split(",");
	        
	        if (list.length!=4) return null;
	        double[] bbx = new double[4];
	        bbx[0]= Double.parseDouble(list[0].trim());
	        bbx[1]= Double.parseDouble(list[1].trim());
	        bbx[2]= Double.parseDouble(list[2].trim());
	        bbx[3]= Double.parseDouble(list[3].trim());
	        return bbx;
	      	}
	      	catch (Exception ex){}
	      }
	    return null;
	}

	@Override
	public void lostOwnership(Clipboard arg0, Transferable arg1) {
		// TODO Auto-generated method stub
		
	}
	
}
