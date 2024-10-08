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
package org.citydb.plugins.spreadsheet_gen.controller;

import org.citydb.plugins.spreadsheet_gen.gui.datatype.CSVColumns;
import org.citydb.plugins.spreadsheet_gen.gui.view.components.TableDataModel;
import org.citydb.util.log.Logger;

import java.io.File;
import java.io.FileOutputStream;

public class TemplateWriter implements Runnable {
    private String path;
    private TableDataModel tableDataModel;
    private String separatorPhrase;
    private final Logger log = Logger.getInstance();

    public TemplateWriter(String path, TableDataModel tableDataModel) {
        this.path = path;
        this.tableDataModel = tableDataModel;
        this.separatorPhrase = System.getProperty("line.separator");
    }

    @Override
    public void run() {
        File output = new File(path);
        if (!output.exists() && output.getParent() != null)
            output.getParentFile().mkdirs();
        int count = tableDataModel.getRowCount();
        CSVColumns column;
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < count; i++) {
            column = tableDataModel.getCSVColumn(i);
            if (column.comment != null && column.comment.trim().length() > 0) {
                if (i > 0) sb.append(separatorPhrase);
                sb.append("// ");
                sb.append(column.comment.replaceAll(separatorPhrase, separatorPhrase + "//"));
                sb.append(separatorPhrase);
            } else if (i > 0) sb.append(separatorPhrase);

            sb.append(column.title);
            sb.append(":");
            sb.append(column.textcontent);
        }
        try {
            FileOutputStream fos = new FileOutputStream(output);
            fos.write(sb.toString().getBytes("UTF-8"));
            fos.flush();
            fos.close();
            log.info("Template file successfully saved as " + path + ".");
        } catch (Exception e) {
            log.error("Failed to save template file.", e);
        }
    }

}
