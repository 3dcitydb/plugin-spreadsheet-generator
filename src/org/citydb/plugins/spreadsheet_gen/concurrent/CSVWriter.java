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
package org.citydb.plugins.spreadsheet_gen.concurrent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.citydb.plugins.spreadsheet_gen.concurrent.work.RowofCSVWork;


import de.tub.citydb.api.concurrent.DefaultWorkerImpl;


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
