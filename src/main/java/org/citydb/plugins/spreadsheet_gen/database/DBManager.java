/*
 * 3D City Database - The Open Source CityGML Database
 * http://www.3dcitydb.org/
 *
 * Copyright 2013 - 2018
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
package org.citydb.plugins.spreadsheet_gen.database;

import org.citydb.concurrent.WorkerPool;
import org.citydb.config.geometry.BoundingBox;
import org.citydb.config.geometry.GeometryObject;
import org.citydb.config.geometry.GeometryType;
import org.citydb.config.project.database.DatabaseSrs;
import org.citydb.database.adapter.AbstractDatabaseAdapter;
import org.citydb.database.connection.DatabaseConnectionPool;
import org.citydb.log.Logger;
import org.citydb.modules.kml.database.Queries;
import org.citydb.plugins.spreadsheet_gen.concurrent.work.CityObjectWork;
import org.citydb.plugins.spreadsheet_gen.config.ConfigImpl;
import org.citydb.util.Util;
import org.citygml4j.geometry.Point;
import org.citygml4j.model.citygml.CityGMLClass;
import org.citygml4j.model.gml.geometry.primitives.DirectPosition;
import org.citygml4j.model.gml.geometry.primitives.Envelope;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class DBManager {
	private final Logger LOG = Logger.getInstance();
	private final WorkerPool<CityObjectWork> workerpool;
	private ConfigImpl config;	
	
	private volatile boolean shouldRun = true;
	private AtomicBoolean isInterrupted = new AtomicBoolean(false);
	
	private Connection connection;
	private AbstractDatabaseAdapter databaseAdapter;
	private DatabaseSrs dbSrs;
	private DatabaseConnectionPool dbConnectionPool;
	private Queries queries;
	
	public static long numCityObjects = 0;
	
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
		if (databaseAdapter.hasVersioningSupport()) {
			databaseAdapter.getWorkspaceManager().gotoWorkspace(connection, 
					config.getWorkspace());
		}
		String schema = databaseAdapter.getConnectionDetails().getSchema();
		queries = new Queries(databaseAdapter, schema);
	}

	public void queryObjects( HashSet<CityGMLClass> desirableCityObject) throws SQLException {		
		BoundingBox bbx = config.getBoundingbox();
		ResultSet rs = null;
		PreparedStatement spatialQuery = null;
		HashMap<CityGMLClass,AtomicInteger> countingStorage= new HashMap<CityGMLClass, AtomicInteger>();
		for (CityGMLClass mClass:desirableCityObject)
			countingStorage.put(mClass, new AtomicInteger(0));
		
		// check whether we have to transform the bounding box
		if (bbx.getSrs().isSupported() && bbx.getSrs().getSrid() != dbSrs.getSrid()) {			
			try {
				bbx = databaseAdapter.getUtil().transformBoundingBox(bbx, bbx.getSrs(), dbSrs);
			} catch (SQLException sqlEx) {
				LOG.error("Failed to initialize bounding box filter.");
			}
		}
					
		try {				
			numCityObjects = 0;
			spatialQuery = connection.prepareStatement(queries.getIds()); 				
			spatialQuery.setObject(1, databaseAdapter.getGeometryConverter().getDatabaseObject(GeometryObject.createEnvelope(bbx), connection));

			ArrayList<CityObjectWork> cows = new ArrayList<>();
			rs = spatialQuery.executeQuery();
			
			while (rs.next() && shouldRun) {
				String gmlId = rs.getString("gmlId");
				int classId = rs.getInt("objectclass_id");
				
				GeometryObject envelope = null;
				Object geomObj = rs.getObject("envelope");
				if (!rs.wasNull() && geomObj != null)
					envelope = databaseAdapter.getGeometryConverter().getEnvelope(geomObj);
				
				// check whether center point of the feature's envelope is within the tile extent
				if (envelope != null && envelope.getGeometryType() == GeometryType.ENVELOPE) {
					double coordinates[] = envelope.getCoordinates(0);
					
					Envelope tmp = new Envelope();
					tmp.setLowerCorner(new Point(coordinates[0], coordinates[1], 0));
					tmp.setUpperCorner(new Point(coordinates[3], coordinates[4], 0));
					
					if (filter(tmp, bbx))
						continue;
				}
				
				if (desirableCityObject.contains(Util.getCityGMLClass(classId))){
					countingStorage.get(Util.getCityGMLClass(classId)).incrementAndGet();
					CityObjectWork cow =new CityObjectWork(gmlId,classId);
					numCityObjects++;
					cows.add(cow);
				}
			}
			
			for (CityObjectWork cow : cows) {
				workerpool.addWork(cow);
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

	private boolean filter(Envelope envelope, BoundingBox bbx) {
		if (!envelope.isSetLowerCorner() || !envelope.isSetUpperCorner())
			return true;

		DirectPosition lowerCorner = envelope.getLowerCorner();
		DirectPosition upperCorner = envelope.getUpperCorner();

		if (!lowerCorner.isSetValue() || !upperCorner.isSetValue())
			return true;

		List<Double> lowerCornerValue = lowerCorner.getValue();
		List<Double> upperCornerValue = upperCorner.getValue();

		if (lowerCornerValue.size() < 2 || upperCornerValue.size() < 2)
			return true;

		Double minX = lowerCornerValue.get(0);
		Double minY = lowerCornerValue.get(1);

		Double maxX = upperCornerValue.get(0);
		Double maxY = upperCornerValue.get(1);

		double centroidX = minX + (maxX - minX) / 2;
		double centroidY = minY + (maxY - minY) / 2;
		if (centroidX >= bbx.getLowerCorner().getX() &&
				centroidY > bbx.getLowerCorner().getY() &&
				centroidX < bbx.getUpperCorner().getX() &&
				centroidY <= bbx.getUpperCorner().getY())
			return false;
		else
			return true;
	}
	
	public void shutdown() {
		if (isInterrupted.compareAndSet(false, true)) {
			shouldRun = false;
		}
	}

}
