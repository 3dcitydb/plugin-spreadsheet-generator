/*
 * 3D City Database - The Open Source CityGML Database
 * https://www.3dcitydb.org/
 *
 * Copyright 2013 - 2024
 * Chair of Geoinformatics
 * Technical University of Munich, Germany
 * https://www.lrg.tum.de/gis/
 *
 * The 3D City Database is jointly developed with the following
 * cooperation partners:
 *
 * Virtual City Systems, Berlin <https://vc.systems/>
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


import org.citydb.core.database.adapter.AbstractDatabaseAdapter;
import org.citydb.core.database.connection.DatabaseConnectionPool;
import org.citydb.plugins.spreadsheet_gen.concurrent.work.CityObjectWork;
import org.citydb.plugins.spreadsheet_gen.concurrent.work.RowofCSVWork;
import org.citydb.plugins.spreadsheet_gen.config.ExportConfig;
import org.citydb.plugins.spreadsheet_gen.database.Translator;
import org.citydb.util.concurrent.Worker;
import org.citydb.util.concurrent.WorkerFactory;
import org.citydb.util.concurrent.WorkerPool;
import org.citydb.util.log.Logger;

import java.sql.Connection;

public class SPSHGWorkerFactory implements WorkerFactory<CityObjectWork> {
    private final Logger log = Logger.getInstance();
    private final WorkerPool<RowofCSVWork> writerPool;
    private final Translator translator;
    private final String template;
    private final ExportConfig config;

    public SPSHGWorkerFactory(
            WorkerPool<RowofCSVWork> writerPool,
            Translator translator,
            String template,
            ExportConfig config) {
        this.writerPool = writerPool;
        this.translator = translator;
        this.template = template;
        this.config = config;
    }

    @Override
    public Worker<CityObjectWork> createWorker() {
        SPSHGWorker worker = null;

        try {
            AbstractDatabaseAdapter databaseAdapter = DatabaseConnectionPool.getInstance().getActiveDatabaseAdapter();
            Connection connection = DatabaseConnectionPool.getInstance().getConnection();
            connection.setAutoCommit(false);

            worker = new SPSHGWorker(
                    connection,
                    databaseAdapter,
                    writerPool,
                    translator,
                    template,
                    config);
        } catch (Exception e) {
            log.error("Failed to create export worker.", e);
        }

        return worker;
    }

}
