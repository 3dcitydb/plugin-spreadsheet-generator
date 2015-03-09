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
package org.citydb.plugins.spreadsheet_gen.util;

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
