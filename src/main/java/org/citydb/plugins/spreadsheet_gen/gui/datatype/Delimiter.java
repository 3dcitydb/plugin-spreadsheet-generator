/*
 * 3D City Database - The Open Source CityGML Database
 * https://www.3dcitydb.org/
 *
 * Copyright 2013 - 2021
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
package org.citydb.plugins.spreadsheet_gen.gui.datatype;

import org.citydb.plugins.spreadsheet_gen.util.Util;

public enum Delimiter {
    COMMA(","),
    SEMICOLON(";"),
    COLON(":"),
    SPACE(" "),
    TAB("\t");

    private final String delimiter;

    Delimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getDelimiter() {
        return delimiter;
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
        switch (this) {
            case COMMA:
                return Util.I18N.getString("spshg.csvPanel.delimiter.comma");
            case SEMICOLON:
                return Util.I18N.getString("spshg.csvPanel.delimiter.semicolon");
            case COLON:
                return Util.I18N.getString("spshg.csvPanel.delimiter.colon");
            case SPACE:
                return Util.I18N.getString("spshg.csvPanel.delimiter.space");
            case TAB:
                return Util.I18N.getString("spshg.csvPanel.delimiter.tab");
            default:
                return "";
        }
    }
}
