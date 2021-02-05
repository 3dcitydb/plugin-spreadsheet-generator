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
package org.citydb.plugins.spreadsheet_gen.concurrent;

import org.citydb.concurrent.DefaultWorker;
import org.citydb.concurrent.WorkerPool;
import org.citydb.config.project.global.LogLevel;
import org.citydb.database.adapter.AbstractDatabaseAdapter;
import org.citydb.event.EventDispatcher;
import org.citydb.event.global.CounterEvent;
import org.citydb.event.global.CounterType;
import org.citydb.event.global.InterruptEvent;
import org.citydb.event.global.ObjectCounterEvent;
import org.citydb.modules.kml.util.BalloonTemplateHandler;
import org.citydb.plugins.spreadsheet_gen.concurrent.work.CityObjectWork;
import org.citydb.plugins.spreadsheet_gen.concurrent.work.RowofCSVWork;
import org.citydb.plugins.spreadsheet_gen.config.ConfigImpl;
import org.citydb.plugins.spreadsheet_gen.config.Output;
import org.citydb.plugins.spreadsheet_gen.config.OutputFileType;
import org.citydb.plugins.spreadsheet_gen.gui.datatype.SeparatorPhrase;
import org.citydb.registry.ObjectRegistry;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SPSHGWorker extends DefaultWorker<CityObjectWork> {
	private final WorkerPool<RowofCSVWork> ioWriterPool;
	private final EventDispatcher eventDispatcher;
	private final BalloonTemplateHandler bth;
	private Map<Integer, Long> featureCounter;
	private final String schema;
	private final String separatorCharacter;
	private final int lod;

	private Connection connection;
	private boolean shouldRun = true;

	public SPSHGWorker(Connection connection, AbstractDatabaseAdapter databaseAdapter, WorkerPool<RowofCSVWork> ioWriterPool, ConfigImpl config, String template) throws SQLException {
		this.connection = connection;
		this.ioWriterPool = ioWriterPool;

		separatorCharacter = config.getOutput().getType() == OutputFileType.CSV ?
				SeparatorPhrase.getInstance().decode(config.getOutput().getCsvfile().getSeparator().trim()) :
				SeparatorPhrase.getInstance().getExcelSeparator();

		bth = new BalloonTemplateHandler(template, databaseAdapter);
		featureCounter = new HashMap<>();
		schema = databaseAdapter.getSchemaManager().getDefaultSchema();
		lod = 2;

		eventDispatcher = ObjectRegistry.getInstance().getEventDispatcher();
	}

	@Override
	public void run() {
		super.run();
		eventDispatcher.triggerEvent(new ObjectCounterEvent(featureCounter, this));
	}

	@Override
	public void doWork(CityObjectWork work) {
		try {
			if (!shouldRun)
				return;

			String data = bth.getBalloonContent(work.getGmlid(), lod, connection, schema);
			String[] cells = data.split("\\Q" + SeparatorPhrase.getInstance().getTempPhrase() + "\\E");

			StringBuilder sb = new StringBuilder();
			boolean firstround = true;

			for (String st : cells) {
				if (!firstround) {
					sb.append(separatorCharacter);
					sb.append("\"");
				} else {
					sb.append('"');
					firstround = false;
				}
				sb.append(st);
				sb.append("\"");
			}
			sb.append("\n");

			ioWriterPool.addWork(new RowofCSVWork(sb.toString(), work.getClassid()));
			featureCounter.merge(work.getClassid(), 1L, Long::sum);
			eventDispatcher.triggerEvent(new CounterEvent(CounterType.TOPLEVEL_FEATURE, 1, this));
		} catch (Exception e) {
			eventDispatcher.triggerSyncEvent(new InterruptEvent("A fatal error occurred during export of " +
					"feature with gml:id " + work.getGmlid() + ".", LogLevel.ERROR, e, eventChannel, this));
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
		StringBuilder sb = new StringBuilder();
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
		sb.append("\n");
		return sb.toString();
	}
}
