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
import org.citydb.database.adapter.AbstractDatabaseAdapter;
import org.citydb.database.connection.DatabaseConnectionPool;
import org.citydb.database.schema.mapping.FeatureType;
import org.citydb.database.schema.mapping.MappingConstants;
import org.citydb.database.schema.mapping.SchemaMapping;
import org.citydb.event.EventDispatcher;
import org.citydb.event.global.ProgressBarEventType;
import org.citydb.event.global.StatusDialogProgressBar;
import org.citydb.log.Logger;
import org.citydb.plugins.spreadsheet_gen.concurrent.work.CityObjectWork;
import org.citydb.query.Query;
import org.citydb.query.builder.QueryBuildException;
import org.citydb.query.builder.sql.BuildProperties;
import org.citydb.query.builder.sql.SQLQueryBuilder;
import org.citydb.sqlbuilder.schema.Table;
import org.citydb.sqlbuilder.select.Select;
import org.citydb.sqlbuilder.select.projection.Function;
import org.citydb.sqlbuilder.select.projection.WildCardColumn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

public class DBManager {
	private final Logger log = Logger.getInstance();
	private final Query query;
	private final SchemaMapping schemaMapping;
	private final WorkerPool<CityObjectWork> workerPool;
	private final EventDispatcher eventDispatcher;

	private final Connection connection;
	private final AbstractDatabaseAdapter databaseAdapter;
	private final AtomicBoolean isInterrupted = new AtomicBoolean(false);
	private final SQLQueryBuilder builder;

	private volatile boolean shouldRun = true;

	public DBManager(
			Query query,
			SchemaMapping schemaMapping,
			DatabaseConnectionPool dbConnectionPool,
			WorkerPool<CityObjectWork> workerPool,
			EventDispatcher eventDispatcher) throws SQLException {
		this.query = query;
		this.schemaMapping = schemaMapping;
		this.workerPool = workerPool;
		this.eventDispatcher = eventDispatcher;

		databaseAdapter = dbConnectionPool.getActiveDatabaseAdapter();
		connection = dbConnectionPool.getConnection();
		connection.setAutoCommit(false);

		builder = new SQLQueryBuilder(
				schemaMapping,
				databaseAdapter,
				BuildProperties.defaults().addProjectionColumn(MappingConstants.GMLID));
	}

	public void startQuery() throws QueryBuildException, SQLException {
		try {
			queryObjects();
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	private void queryObjects() throws QueryBuildException, SQLException {
		// create query statement
		Select select = builder.buildQuery(query);

		// calculate matched feature number
		Select hitsQuery = new Select().addProjection(new Function("count", new WildCardColumn(new Table(select), false)));
		try (PreparedStatement stmt = databaseAdapter.getSQLAdapter().prepareStatement(hitsQuery, connection);
		     ResultSet rs = stmt.executeQuery()) {
			long numCityObjects = rs.next() ? rs.getLong(1) : 0;
			log.info("Found " + numCityObjects + " top-level feature(s) matching the request.");
			eventDispatcher.triggerEvent(new StatusDialogProgressBar(ProgressBarEventType.INIT, (int) numCityObjects, this));
		}

		// do database query
		try (PreparedStatement psSelect = databaseAdapter.getSQLAdapter().prepareStatement(select, connection);
		     ResultSet rs = psSelect.executeQuery()) {
			while (rs.next() && shouldRun) {
				String gmlId = rs.getString(MappingConstants.GMLID);
				int classId = rs.getInt(MappingConstants.OBJECTCLASS_ID);

				FeatureType featureType = schemaMapping.getFeatureType(classId);
				if (featureType != null && featureType.isTopLevel()) {
					workerPool.addWork(new CityObjectWork(gmlId, classId));
				}
			}
		}
	}

	public void shutdown() {
		if (isInterrupted.compareAndSet(false, true)) {
			shouldRun = false;
		}
	}
}
