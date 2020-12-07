/*
 * 3D City Database - The Open Source CityGML Database
 * http://www.3dcitydb.org/
 *
 * Copyright 2013 - 2020
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
import org.citydb.database.adapter.AbstractDatabaseAdapter;
import org.citydb.database.connection.DatabaseConnectionPool;
import org.citydb.database.schema.TableEnum;
import org.citydb.database.schema.mapping.FeatureType;
import org.citydb.database.schema.mapping.MappingConstants;
import org.citydb.plugins.spreadsheet_gen.concurrent.work.CityObjectWork;
import org.citydb.plugins.spreadsheet_gen.config.ConfigImpl;
import org.citydb.query.filter.FilterException;
import org.citydb.query.filter.selection.operator.spatial.SpatialOperatorName;
import org.citydb.query.filter.tiling.Tile;
import org.citydb.registry.ObjectRegistry;
import org.citydb.sqlbuilder.expression.IntegerLiteral;
import org.citydb.sqlbuilder.expression.LiteralList;
import org.citydb.sqlbuilder.schema.Column;
import org.citydb.sqlbuilder.schema.Table;
import org.citydb.sqlbuilder.select.Select;
import org.citydb.sqlbuilder.select.operator.comparison.ComparisonFactory;
import org.citydb.sqlbuilder.select.projection.Function;
import org.citydb.sqlbuilder.select.projection.WildCardColumn;

import javax.xml.namespace.QName;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class DBManager {
	private final WorkerPool<CityObjectWork> workerPool;
	private final AtomicBoolean isInterrupted = new AtomicBoolean(false);
	private final Connection connection;
	private final AbstractDatabaseAdapter databaseAdapter;
	private final ConfigImpl config;

	private volatile boolean shouldRun = true;
	public static long numCityObjects = 0;

	public DBManager(DatabaseConnectionPool dbConnectionPool,
			ConfigImpl config,
			WorkerPool<CityObjectWork> workerPool)throws SQLException{
		this.config = config;
		this.workerPool = workerPool;
		this.databaseAdapter = dbConnectionPool.getActiveDatabaseAdapter();
		this.connection = dbConnectionPool.getConnection();
	}

	public void queryObjects() throws SQLException {
		// init feature counter
		HashMap<Integer, AtomicInteger> countingStorage= new HashMap<>();
		if (!config.isUseFeatureTypeFilter()) {
			for (FeatureType featureType : ObjectRegistry.getInstance().getSchemaMapping().listTopLevelFeatureTypes(true)) {
				countingStorage.put(featureType.getObjectClassId(), new AtomicInteger(0));
			}
		} else {
			for (QName typeQName : config.getFeatureTypeFilter().getTypeNames()) {
				int objectClassId = ObjectRegistry.getInstance().getSchemaMapping().getFeatureType(typeQName).getObjectClassId();
				countingStorage.put(objectClassId, new AtomicInteger(0));
			}
		}

		// build query
		Table cityObjectTable = new Table(TableEnum.CITYOBJECT.getName(), databaseAdapter.getConnectionDetails().getSchema());
		Column gmlIdColumn = cityObjectTable.getColumn(MappingConstants.GMLID);
		Column objectClassIdColumn = cityObjectTable.getColumn(MappingConstants.OBJECTCLASS_ID);
		Column envelopeColumn = cityObjectTable.getColumn(MappingConstants.ENVELOPE);
		Select select = new Select().addProjection(gmlIdColumn, objectClassIdColumn, envelopeColumn);

		// feature type filter
		Set<Integer> ids = countingStorage.keySet();
		if (ids.size() == 1) {
			select.addSelection(ComparisonFactory.equalTo(objectClassIdColumn, new IntegerLiteral(ids.iterator().next())));
		} else if (ids.size() > 1) {
			select.addSelection(ComparisonFactory.in(objectClassIdColumn, new LiteralList(ids.toArray(new Integer[ids.size()]))));
		}

		// bbox filter
		if (config.isUseBoundingBoxFilter()) {
			BoundingBox bbox = config.getBoundingbox();
			if (!bbox.isValid()) {
				throw new SQLException("Invalid bounding box for database query.");
			}

			GeometryObject bboxGeometryObject;
			try {
				bboxGeometryObject = new Tile(bbox, 1, 1).getFilterGeometry(databaseAdapter);
			} catch (FilterException e) {
				throw new SQLException("Failed to build bounding box geometry for spatial query.", e);
			}
			select.addSelection(databaseAdapter.getSQLAdapter().getBinarySpatialPredicate(SpatialOperatorName.INTERSECTS, envelopeColumn, bboxGeometryObject, false));
		}

		// calculate matched feature number
		Select hitsQuery = new Select().addProjection(new Function("count", new WildCardColumn(new Table(select), false)));
		try (PreparedStatement stmt = databaseAdapter.getSQLAdapter().prepareStatement(hitsQuery, connection);
		     ResultSet rs = stmt.executeQuery()) {
			numCityObjects = rs.next() ? rs.getLong(1) : 0;
		}

		// do database query
		try (PreparedStatement psSelect = databaseAdapter.getSQLAdapter().prepareStatement(select, connection);
		     ResultSet rs = psSelect.executeQuery()) {
			while (rs.next() && shouldRun) {
				String gmlId = rs.getString(1);
				int classId = rs.getInt(2);
				FeatureType featureType = ObjectRegistry.getInstance().getSchemaMapping().getFeatureType(classId);
				if (countingStorage.keySet().contains(classId) && featureType != null && featureType.isTopLevel()){
					countingStorage.get(classId).incrementAndGet();
					CityObjectWork cow =new CityObjectWork(gmlId,classId);
					workerPool.addWork(cow);
				}
			}
		}
	}

	public void startQuery() throws SQLException {
		try {
			queryObjects();
		}
		finally {
			if (connection != null) {
				try {
					connection.close();
				}
				catch (SQLException sqlEx) {}
			}
		}
	}

	public void shutdown() {
		if (isInterrupted.compareAndSet(false, true)) {
			shouldRun = false;
		}
	}

}
