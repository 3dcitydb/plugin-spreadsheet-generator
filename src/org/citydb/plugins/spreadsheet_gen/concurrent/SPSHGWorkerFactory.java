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
package org.citydb.plugins.spreadsheet_gen.concurrent;


import org.citydb.api.concurrent.Worker;
import org.citydb.api.concurrent.WorkerFactory;
import org.citydb.api.concurrent.WorkerPool;
import org.citydb.database.DatabaseConnectionPool;
import org.citydb.plugins.spreadsheet_gen.concurrent.work.CityObjectWork;
import org.citydb.plugins.spreadsheet_gen.concurrent.work.RowofCSVWork;
import org.citydb.plugins.spreadsheet_gen.config.ConfigImpl;

// TODO: Auto-generated Javadoc
/**
 * A factory for creating SPSHGWorker objects.
 */
public class SPSHGWorkerFactory implements WorkerFactory<CityObjectWork> {
	
	/** The db pool. */
	private final DatabaseConnectionPool dbPool;
	
	/** The io writer pool. */
	private final WorkerPool<RowofCSVWork> ioWriterPool;
	
	/** The config. */
	private final ConfigImpl config;
	
	/** The template. */
	private String template;
	
	/**
	 * Instantiates a new SPSHG worker factory.
	 *
	 * @param dbPool the db pool
	 * @param ioWriterPool the io writer pool
	 * @param config the config
	 * @param template the template
	 */
	public SPSHGWorkerFactory(DatabaseConnectionPool dbPool,
			WorkerPool<RowofCSVWork> ioWriterPool,
			ConfigImpl config, String template){
		
		this.dbPool=dbPool;
		this.ioWriterPool=ioWriterPool;
		this.config=config;
		this.template=template;
	}
	
	/* (non-Javadoc)
	 * @see org.citydb.api.concurrent.WorkerFactory#createWorker()
	 */
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
