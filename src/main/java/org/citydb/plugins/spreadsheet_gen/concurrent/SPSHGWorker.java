/*
 * 3D City Database - The Open Source CityGML Database
 * http://www.3dcitydb.org/
 *
 * Copyright 2013 - 2019
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

import org.citydb.concurrent.DefaultWorker;
import org.citydb.concurrent.WorkerPool;
import org.citydb.database.connection.DatabaseConnectionPool;
import org.citydb.event.EventDispatcher;
import org.citydb.modules.kml.util.BalloonTemplateHandler;
import org.citydb.plugins.spreadsheet_gen.concurrent.work.CityObjectWork;
import org.citydb.plugins.spreadsheet_gen.concurrent.work.RowofCSVWork;
import org.citydb.plugins.spreadsheet_gen.config.ConfigImpl;
import org.citydb.plugins.spreadsheet_gen.database.DBManager;
import org.citydb.plugins.spreadsheet_gen.events.StatusDialogMessage;
import org.citydb.plugins.spreadsheet_gen.gui.datatype.SeparatorPhrase;
import org.citydb.registry.ObjectRegistry;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class SPSHGWorker extends DefaultWorker<CityObjectWork> {
	private final WorkerPool<RowofCSVWork> ioWriterPool;

	private BalloonTemplateHandler bth;
	private Connection connection;
	private String seperatorCharacter;
	private int lod;
	public static long counter = 0;

	private final EventDispatcher eventDispatcher;
	private boolean shouldRun;
	private String schema;

	public SPSHGWorker(DatabaseConnectionPool dbConnectionPool, WorkerPool<RowofCSVWork> ioWriterPool, ConfigImpl config, String template) throws SQLException {
		eventDispatcher = ObjectRegistry.getInstance().getEventDispatcher();
		this.ioWriterPool = ioWriterPool;

		connection = dbConnectionPool.getConnection();

		// try and change workspace if needed
		if (dbConnectionPool.getActiveDatabaseAdapter().hasVersioningSupport()) {
			dbConnectionPool.getActiveDatabaseAdapter().getWorkspaceManager().gotoWorkspace(connection, config.getWorkspace());
		}

		seperatorCharacter = SeparatorPhrase.getInstance().decode(config.getOutput().getCsvfile().getSeparator().trim());
		shouldRun = true;
		bth = new BalloonTemplateHandler(template, dbConnectionPool.getActiveDatabaseAdapter());
		lod = 2;
		schema = dbConnectionPool.getActiveDatabaseAdapter().getSchemaManager().getDefaultSchema();
	}

	@Override
	public void doWork(CityObjectWork cityobj) {
		try {
			if (!this.shouldRun)
				return;
			String data = bth.getBalloonContent(cityobj.getGmlid(), lod, connection, schema);
			String[] cells = data.split("\\Q" + SeparatorPhrase.getInstance().getTempPhrase() + "\\E");

			StringBuffer sb = new StringBuffer();
			boolean firstround = true;

			for (String st : cells) {
				if (!firstround) {
					sb.append(seperatorCharacter);
					sb.append("\"");
				} else {
					sb.append('"');
					firstround = false;
				}
				sb.append(st);
				sb.append("\"");
			}
			sb.append("\r\n");
			ioWriterPool.addWork(new RowofCSVWork(sb.toString(), cityobj.getClassid()));
			counter++;
			eventDispatcher.triggerEvent(new StatusDialogMessage(" " + counter + " / " + DBManager.numCityObjects, this));
		} catch (Exception e) {
		}
	}

	@Override
	public void shutdown() {
		this.shouldRun = false;
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException sqlEx) {
			}
			connection = null;
		}
	}

	public static String generateHeader(ArrayList<String> header, String separator) {
		StringBuffer sb = new StringBuffer();
		boolean firstround = true;
		for (String st : header) {
			if (!firstround) {
				sb.append(separator);
				sb.append("\"");
			} else {
				sb.append('"');
				firstround = false;
			}
			sb.append(st);
			sb.append("\"");
		}
		sb.append("\r\n");
		return sb.toString();
	}
}
