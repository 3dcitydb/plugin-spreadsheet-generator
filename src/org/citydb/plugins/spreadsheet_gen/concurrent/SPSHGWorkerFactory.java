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


import org.citydb.plugins.spreadsheet_gen.concurrent.work.CityObjectWork;
import org.citydb.plugins.spreadsheet_gen.concurrent.work.RowofCSVWork;
import org.citydb.plugins.spreadsheet_gen.config.ConfigImpl;

import org.citydb.api.concurrent.Worker;
import org.citydb.api.concurrent.WorkerFactory;
import org.citydb.api.concurrent.WorkerPool;
import org.citydb.api.controller.DatabaseController;
import org.citydb.database.DatabaseConnectionPool;

public class SPSHGWorkerFactory implements WorkerFactory<CityObjectWork> {
	private final DatabaseConnectionPool dbPool;
	private final WorkerPool<RowofCSVWork> ioWriterPool;
	private final ConfigImpl config;
	private String template;
	public SPSHGWorkerFactory(DatabaseConnectionPool dbPool,
			WorkerPool<RowofCSVWork> ioWriterPool,
			ConfigImpl config, String template){
		
		this.dbPool=dbPool;
		this.ioWriterPool=ioWriterPool;
		this.config=config;
		this.template=template;
	}
	@Override
	public Worker<CityObjectWork> createWorker() {
		SPSHGWorker worker =null;

		try {
			worker = new SPSHGWorker(
					dbPool,
					ioWriterPool,
					config,template);
		} catch (Exception e) {
			// could not instantiate DBWorker
		}

		return worker;
	}

}
