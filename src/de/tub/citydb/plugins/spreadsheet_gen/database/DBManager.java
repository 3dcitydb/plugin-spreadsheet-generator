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
package de.tub.citydb.plugins.spreadsheet_gen.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.citygml4j.model.citygml.CityGMLClass;

import oracle.jdbc.OracleResultSet;

import de.tub.citydb.api.concurrent.WorkerPool;
import de.tub.citydb.api.controller.DatabaseController;
import de.tub.citydb.api.database.DatabaseSrs;
import de.tub.citydb.api.geometry.BoundingBox;
import de.tub.citydb.api.geometry.GeometryObject;
import de.tub.citydb.config.project.database.Database;
import de.tub.citydb.database.DatabaseConnectionPool;
import de.tub.citydb.database.adapter.AbstractDatabaseAdapter;

import de.tub.citydb.plugins.spreadsheet_gen.concurrent.work.CityObjectWork;
import de.tub.citydb.plugins.spreadsheet_gen.config.ConfigImpl;
import de.tub.citydb.util.Util;


public class DBManager {
	private final WorkerPool<CityObjectWork> workerpool;
	private ConfigImpl config;	
	
	private volatile boolean shouldRun = true;
	private AtomicBoolean isInterrupted = new AtomicBoolean(false);
	
	private Connection connection;
	private AbstractDatabaseAdapter databaseAdapter;
	private DatabaseSrs dbSrs;
	private DatabaseConnectionPool dbConnectionPool;

	
	public DBManager(DatabaseConnectionPool dbConnectionPool,
			ConfigImpl config,
			WorkerPool<CityObjectWork> workerpool)throws SQLException{
		this.dbConnectionPool =dbConnectionPool;
		this.config=config;
		this.workerpool=workerpool;
		init();
	}
	
	public void init() throws SQLException{
		databaseAdapter = dbConnectionPool.getActiveDatabaseAdapter();
		connection = dbConnectionPool.getConnection();
		dbSrs = databaseAdapter.getConnectionMetaData().getReferenceSystem();

		// try and change workspace for connection if needed
		if (dbConnectionPool.getActiveDatabaseAdapter().hasVersioningSupport()) {
			dbConnectionPool.getActiveDatabaseAdapter().getWorkspaceManager().gotoWorkspace(connection, 
					config.getWorkspace());
		}
	}

	public void queryObjects( HashSet<CityGMLClass> desirableCityObject) throws SQLException {
			
			BoundingBox bbx = config.getBoundingbox();
			ResultSet rs = null;
			PreparedStatement spatialQuery = null;
			HashMap<CityGMLClass,AtomicInteger> countingStorage= new HashMap<CityGMLClass, AtomicInteger>();
			for (CityGMLClass mClass:desirableCityObject)
				countingStorage.put(mClass, new AtomicInteger(0));
			
			
			try {
				spatialQuery = connection.prepareStatement(Queries.QUERY_GET_GMLIDS_3(databaseAdapter.getDatabaseType()));

				Object envelope = databaseAdapter.getGeometryConverter().getDatabaseObject(GeometryObject.createEnvelope(bbx), connection);
				
				// set spatial objects for query
				spatialQuery.setObject(1, envelope);
				
				rs = spatialQuery.executeQuery();
				while (rs.next() && shouldRun) {
					String gmlId = rs.getString("gmlId");
					int classId = rs.getInt("objectclass_id");
					if (desirableCityObject.contains(Util.classId2cityObject(classId))){
						countingStorage.get(Util.classId2cityObject(classId)).incrementAndGet();
						CityObjectWork cow =new CityObjectWork(gmlId,classId);
						workerpool.addWork(cow);
						
					}
				}
				

			}
			catch (SQLException sqlEx) {
				throw sqlEx;
			}
			finally {
				if (rs != null) {
					try {
						rs.close();
					}
					catch (SQLException sqlEx) {
						throw sqlEx;
					}

					rs = null;
				}

				if (spatialQuery != null) {
					try {
						spatialQuery.close();
					}
					catch (SQLException sqlEx) {
						throw sqlEx;
					}

					spatialQuery = null;
				}
//				generateReport(countingStorage);
				
			}
		

	}
	
	public void startQuery(HashSet<CityGMLClass> desirableCityObjects) throws SQLException {
		try {
			queryObjects(desirableCityObjects);
		}
		finally {
			if (connection != null) {
				try {
					connection.close();
				}
				catch (SQLException sqlEx) {}

				connection = null;
			}
		}
	}

	public void shutdown() {
		if (isInterrupted.compareAndSet(false, true)) {
			shouldRun = false;
		}
	}


}
