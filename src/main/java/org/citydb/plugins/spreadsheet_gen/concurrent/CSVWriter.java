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
import org.citydb.plugins.spreadsheet_gen.concurrent.work.RowofCSVWork;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

public class CSVWriter extends DefaultWorker<RowofCSVWork> {
	private FileOutputStream fos;

	public CSVWriter(File output) {
		try {
			fos = new FileOutputStream(output);
		} catch (Exception e) {
			//
		}
	}

	@Override
	public void doWork(RowofCSVWork row) {
		try {
			if (fos != null) {
				fos.write(row.getText().getBytes(StandardCharsets.UTF_8));
			}
		} catch (Exception e) {
			// event
		}
	}

	@Override
	public void shutdown() {
		try {
			fos.close();
		} catch (Exception e) {
			//
		}
	}
}
