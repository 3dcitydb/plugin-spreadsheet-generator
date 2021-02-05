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
package org.citydb.plugins.spreadsheet_gen.gui.datatype;

import org.citydb.plugins.spreadsheet_gen.util.Util;

public enum Delimiter {
    COMMA(",", "spshg.csvPanel.delimiter.comma"),
    SEMICOLON(";", "spshg.csvPanel.delimiter.semicolon"),
    SPACE(" ", "spshg.csvPanel.delimiter.space"),
    TAB("\t", "spshg.csvPanel.delimiter.tab");

    private String delimiter;
    private String i18n;

    Delimiter(String delimiter, String i18n) {
        this.delimiter = delimiter;
        this.i18n = i18n;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public String getName() {
        return Util.I18N.getString(i18n);
    }

    public static Delimiter fromValue(String delimiter) {
        for (Delimiter value : values()) {
            if (value.delimiter.equals(delimiter)) {
                return value;
            }
        }

        return COMMA;
    }

    @Override
    public String toString() {
        return getName();
    }
}
