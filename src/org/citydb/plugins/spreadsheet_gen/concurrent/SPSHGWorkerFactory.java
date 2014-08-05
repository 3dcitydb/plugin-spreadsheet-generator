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
