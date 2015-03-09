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
package org.citydb.plugins.spreadsheet_gen.concurrent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.citydb.api.concurrent.DefaultWorkerImpl;
import org.citydb.plugins.spreadsheet_gen.concurrent.work.RowofCSVWork;



public class CSVWriter extends DefaultWorkerImpl<RowofCSVWork>{

	BufferedWriter bw;
	OutputStreamWriter osw;
	FileOutputStream fos;
	private static HashMap<Integer,AtomicInteger> countingStorage= new HashMap<Integer, AtomicInteger>();
	public CSVWriter(File output){	
		
		try{
//		FileWriter fw = new FileWriter(output);
//		bw = new BufferedWriter(fw);
		fos = new FileOutputStream(output);
//		bw= new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
//		osw= new OutputStreamWriter(fos, "UTF8");
		
		fos = new FileOutputStream(output);
		
		}catch (Exception e){
//			bw=null;
			osw=null;
			// event
		};
	}
	@Override
	public void doWork(RowofCSVWork row) {
		try{
		if (fos!=null){
			fos.write(row.getText().toString().getBytes("UTF-8"));
			synchronized(countingStorage){
				if (row.getClassid()==RowofCSVWork.UNKNOWN_CLASS_ID) return;
				if (countingStorage.containsKey(new Integer(row.getClassid())))
					countingStorage.get(new Integer(row.getClassid())).incrementAndGet();
				else
					countingStorage.put(new Integer(row.getClassid()), new AtomicInteger(1));
			}
		}
		}catch(Exception e){
			// event
		}
	}

	@Override
	public void shutdown() {
		try{
			fos.flush();
			fos.close();
		}catch(Exception e){}
		
	}
	
	public static void resetLogStorage(){
		countingStorage.clear();
	}

	public static HashMap<Integer,AtomicInteger> getRportStructure(){
		
		return countingStorage;
	}
	
	
}
