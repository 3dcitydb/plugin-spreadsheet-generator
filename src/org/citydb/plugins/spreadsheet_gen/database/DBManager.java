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
package org.citydb.plugins.spreadsheet_gen.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.citydb.log.Logger;
import org.citydb.modules.kml.database.Queries;
import org.citydb.plugins.spreadsheet_gen.concurrent.work.CityObjectWork;
import org.citydb.plugins.spreadsheet_gen.config.ConfigImpl;
import org.citygml4j.geometry.Point;
import org.citygml4j.model.citygml.CityGMLClass;
import org.citygml4j.model.gml.geometry.primitives.DirectPosition;
import org.citygml4j.model.gml.geometry.primitives.Envelope;

import org.citydb.api.concurrent.WorkerPool;
import org.citydb.api.database.DatabaseSrs;
import org.citydb.api.geometry.BoundingBox;
import org.citydb.api.geometry.GeometryObject;
import org.citydb.api.geometry.GeometryObject.GeometryType;
import org.citydb.database.DatabaseConnectionPool;
import org.citydb.database.adapter.AbstractDatabaseAdapter;

import org.citydb.util.Util;


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
		
		// check whether we have to transform the bounding box
		if (bbx.getSrs().isSupported() && bbx.getSrs().getSrid() != dbSrs.getSrid()) {			
			try {
				bbx = databaseAdapter.getUtil().transformBoundingBox(bbx, bbx.getSrs(), dbSrs);
			} catch (SQLException sqlEx) {
				LOG.error("Failed to initialize bounding box filter.");
			}
		}
					
		try {				
			spatialQuery = connection.prepareStatement(Queries.GET_IDS(databaseAdapter.getDatabaseType())); 				
			spatialQuery.setObject(1, databaseAdapter.getGeometryConverter().getDatabaseObject(GeometryObject.createEnvelope(bbx), connection));

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
		if (centroidX >= bbx.getLowerLeftCorner().getX() &&
				centroidY > bbx.getLowerLeftCorner().getY() &&
				centroidX < bbx.getUpperRightCorner().getX() &&
				centroidY <= bbx.getUpperRightCorner().getY())
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
