/*
 * 3D City Database - The Open Source CityGML Database
 * https://www.3dcitydb.org/
 *
 * Copyright 2013 - 2026
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
package org.citydb.plugins.spreadsheet_gen.database;

import org.citydb.plugins.spreadsheet_gen.gui.datatype.CSVColumns;
import org.citydb.plugins.spreadsheet_gen.gui.view.components.NewCSVColumnDialog;
import org.citydb.plugins.spreadsheet_gen.util.Util;
import org.citydb.util.log.Logger;
import org.citydb.vis.util.BalloonTemplateHandler;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Translator {
    private final Logger LOG = Logger.getInstance();
    private final Set<String> aggregations;
    private final Map<String, Set<String>> cityDbContent;
    private final String balloonToken;

    private List<String> columnTitle;
    private Map<String, String> templateMap;
    private int offset;

    public Translator() {
        BalloonTemplateHandler dummy = new BalloonTemplateHandler("", null);
        aggregations = dummy.getSupportedAggregationFunctions();
        cityDbContent = dummy.getSupportedTablesAndColumns();
        balloonToken = "_$" + System.currentTimeMillis() + "$_";
    }

    public String getBalloonToken() {
        return balloonToken;
    }

    public Map<String, String> getTemplateMap() {
        return templateMap;
    }

    public String translateToBalloonTemplate(File csvTemplate) throws Exception {
        templateMap = new HashMap<>();
        columnTitle = new ArrayList<>();

        FileInputStream fstream = new FileInputStream(csvTemplate);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(fstream, StandardCharsets.UTF_8))) {
            String strLine;

            // Read File Line By Line
            StringBuilder output = new StringBuilder();
            String tmpout, header;

            // Initial values
            output.append("<3DCityDB>CITYOBJECT/GMLID</3DCityDB>");
            columnTitle.add("GMLID");
            templateMap.put("GMLID", "CITYOBJECT__GMLID");
            while ((strLine = br.readLine()) != null) {
                if (!(strLine.startsWith("//") || strLine.startsWith(";")) && strLine.indexOf(':') > 0) {

                    header = strLine.substring(0, strLine.indexOf(':'));
                    tmpout = translateLine(columnTitle, strLine.substring(strLine.indexOf(':') + 1, strLine.length()), false);

                    String templateCSVcolumn = header;
                    String dbTableColumn = null;
                    try {
                        dbTableColumn = getTableColumn(strLine.substring(strLine.indexOf(':') + 1, strLine.length()));
                    } catch (Exception e) {
                        LOG.error(e.getMessage());
                    }

                    templateMap.put(templateCSVcolumn.trim(), dbTableColumn);

                } else {
                    tmpout = translateLine(columnTitle, strLine, true);
                    header = null;
                }

                if (tmpout.length() > 0) {
                    output.append(balloonToken);
                    output.append(tmpout);
                    if (header != null)
                        columnTitle.add(header.trim());
                }

            }
            return output.toString();
        }
    }

    public String translateToBalloonTemplate(ArrayList<CSVColumns> rows) throws IOException {
        templateMap = new HashMap<>();
        columnTitle = new ArrayList<>();

        StringBuilder output = new StringBuilder();
        String tmpout, header;
        // Initial values
        output.append("<3DCityDB>CITYOBJECT/GMLID</3DCityDB>");
        columnTitle.add("GMLID");
        templateMap.put("GMLID", "CITYOBJECT__GMLID");

        for (CSVColumns row : rows) {
            header = row.title.trim();
            tmpout = translateLine(columnTitle, row.textcontent, false);

            String templateCSVcolumn = header;
            String dbTableColumn = null;
            try {
                dbTableColumn = getTableColumn(row.textcontent);
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }

            templateMap.put(templateCSVcolumn.trim(), dbTableColumn);

            if (tmpout.length() > 0) {
                output.append(balloonToken);
                output.append(tmpout);
                columnTitle.add(header);
            }

        }

        return output.toString();
    }

    public String getProperHeader(String content) {
        List<String> columnTitle = new ArrayList<>();
        translateLine(columnTitle, content, true);
        return columnTitle.get(0);
    }

    public String generateHeader(String separator) {
        StringBuilder sb = new StringBuilder();
        boolean firstround = true;
        for (String st : columnTitle) {
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

    public StyledDocument getFormatedDocument(String content) {
        StyleContext context = new StyleContext();
        DefaultStyledDocument document = new DefaultStyledDocument(context);
        try {
            List<String> columnTitle = new ArrayList<>();
            String tagedText = translateLine(columnTitle, content, false);
            int pointer = 0;
            int tagIndex = 0;
            while (tagedText.indexOf(BalloonTemplateHandler.START_TAG, pointer) != -1) {
                tagIndex = tagedText.indexOf(BalloonTemplateHandler.START_TAG, pointer);
                // Is there any character before the strat tag which is not parsed yet?
                if (tagIndex != pointer) {
                    document.insertString(document.getLength(), tagedText.substring(pointer, tagIndex), NewCSVColumnDialog.getDefaultStyle());
                    pointer = tagIndex;
                }
                pointer += BalloonTemplateHandler.START_TAG.length();
                tagIndex = tagedText.indexOf(BalloonTemplateHandler.END_TAG, pointer);
                document.insertString(document.getLength(), tagedText.substring(pointer, tagIndex), NewCSVColumnDialog.getLabelStyle());
                pointer = tagIndex + BalloonTemplateHandler.END_TAG.length();
            }
            if (pointer != tagedText.length() - 1) {
                document.insertString(document.getLength(), tagedText.substring(pointer, tagedText.length()), NewCSVColumnDialog.getDefaultStyle());
            }
            // check for [EOL]
            pointer = 0;
            tagIndex = 0;
            tagedText = document.getText(0, document.getLength());
            String lineSeparator = System.getProperty("line.separator");
            while (tagedText.indexOf(lineSeparator, pointer) != -1) {
                tagIndex = tagedText.indexOf(lineSeparator, pointer);
                document.remove(tagIndex, lineSeparator.length());
                document.insertString(tagIndex, NewCSVColumnDialog.EOL,
                        NewCSVColumnDialog.getEOLStyle());
                pointer = tagIndex + NewCSVColumnDialog.EOL.length();
                tagedText = document.getText(0, document.getLength());
            }
        } catch (Exception e) {
        }
        return document;
    }

    private String translateLine(List<String> columnTitle, String line, boolean generateHeader) {
        StringBuilder sbout = new StringBuilder();
        offset = 0;
        String tmpout = "";
        boolean hadTable = false;
        boolean hadslash = false;
        boolean hadColumn = false;
        boolean hadAggfunc = false;
        boolean hadCondition = false;
        boolean fullTablename = false;
        boolean isClearColumn = false;
        boolean readNewWord = true;
        String extractedName = "";
        String tmpWord;
        boolean hadunknown = true; // anything instead of what mentiend before
        String header = "";
        int startposition = 0, endposition = 0;
        String currentTable = "";
        line = line.replace(NewCSVColumnDialog.EOL, System.getProperty("line.separator"));
        char[] chars = line.toCharArray();
        while (offset < line.length() || !readNewWord) {
            if (readNewWord)
                tmpout = getWord(chars);
            readNewWord = true;
            if (tmpout.equals("//") || tmpout.equals(";"))
                return sbout.toString();

            if (!hadTable && ((fullTablename = cityDbContent.containsKey(tmpout))
                    || (offset < chars.length && chars[offset] == '/'))) {
                if (!fullTablename) {
                    extractedName = null;
                    tmpWord = tmpout.toUpperCase();
                    for (String tableName : cityDbContent.keySet()) {
                        if (tmpWord.contains(tableName)) {
                            extractedName = tableName;
                            break;
                        }
                    }
                    if (extractedName != null) {
                        hadunknown = false;
                        hadTable = true;
                        startposition = sbout.length() + tmpWord.indexOf(extractedName);
                        header = extractedName;
                        sbout.append(tmpout);
                        currentTable = extractedName;
                        continue;
                    }
                } else {

                    hadunknown = false;
                    hadTable = true;
                    startposition = sbout.length();
                    header = tmpout;
                    sbout.append(tmpout);
                    currentTable = tmpout;
                    continue;
                }
            }
            if (hadTable && !hadslash && tmpout.equals("/")) {
                if (hadunknown) {
                    hadTable = false;
                } else {
                    hadunknown = false;
                    hadslash = true;
                    sbout.append(tmpout);
                    continue;
                }
            }
            if (hadTable && hadslash && !hadAggfunc && aggregations.contains(tmpout)) {
                if (hadunknown) {
                    hadTable = false;
                    hadslash = false;
                } else {
                    hadunknown = false;
                    hadAggfunc = true;
                    sbout.append(tmpout);
                    continue;
                }
            }
            // checking for columns
            if (hadTable && hadslash && !hadColumn && cityDbContent.containsKey(currentTable)) {

                if (hadunknown) {
                    hadTable = false;
                    hadslash = false;
                    hadAggfunc = false;
                } else {
                    if ((cityDbContent.get(currentTable)).contains(tmpout)) {
                        hadunknown = false;
                        hadColumn = true;
                        isClearColumn = true;
                        sbout.append(tmpout);
                        header += "_" + tmpout;
                        endposition = sbout.length();
                        continue;
                    } else {
                        Set<String> columns = cityDbContent.get(currentTable);
                        tmpWord = tmpout.toUpperCase();
                        extractedName = null;
                        for (String tmpColumn : columns) {
                            if (tmpWord.startsWith(tmpColumn)) {
                                extractedName = tmpColumn;
                                break;
                            }
                        }
                        if (extractedName != null) {
                            hadunknown = false;
                            hadColumn = true;
                            isClearColumn = false;
                            sbout.append(extractedName);
                            endposition = sbout.length();
                            header += "_" + extractedName;
                            offset -= (tmpout.length() - extractedName.length());
                            continue;
                        }

                    }
                }
            }
            if (hadTable && hadColumn && !hadunknown && tmpout.equals("[")) {
                sbout.append(tmpout);
                hadCondition = true;
                continue;
            }

            if (hadTable && hadColumn && isClearColumn && hadCondition && tmpout.equals("]")) {
                sbout.append(tmpout);
                endposition = sbout.length();
                hadTable = false;
                hadColumn = false;
                hadslash = false;
                hadAggfunc = false;
                hadCondition = false;
                isClearColumn = false;
                sbout.insert(endposition, BalloonTemplateHandler.END_TAG);
                sbout.insert(startposition, BalloonTemplateHandler.START_TAG);
                continue;
            }
            if (tmpout.equals("]") || tmpout.equals("[")) {
                sbout.append(tmpout);
                continue;
            }
            if (hadTable && hadslash && hadColumn && !hadCondition) {
                hadTable = false;
                hadColumn = false;
                hadslash = false;
                hadAggfunc = false;
                hadCondition = false;
                isClearColumn = false;
                sbout.insert(endposition, BalloonTemplateHandler.END_TAG);
                sbout.insert(startposition, BalloonTemplateHandler.START_TAG);
                readNewWord = false;
                continue;
            }
            hadunknown = true;
            sbout.append(tmpout);
        }
        if (hadTable && hadslash && hadColumn) {
            sbout.insert(endposition, BalloonTemplateHandler.END_TAG);
            sbout.insert(startposition, BalloonTemplateHandler.START_TAG);
        }

        if (generateHeader)
            columnTitle.add(header);
        return sbout.toString();
    }

    private String getWord(char[] chararray) {
        StringBuilder wordparser = new StringBuilder();
        wordparser.setLength(0);

        if (Character.isLetterOrDigit(chararray[offset])) {
            while (chararray.length > offset && (Character.isLetterOrDigit(chararray[offset]) ||
                    chararray[offset] == '_')) {
                wordparser.append(chararray[offset]);
                offset++;
            }
            return wordparser.toString();
        }

        if (Character.isWhitespace(chararray[offset])) {
            while (chararray.length > offset && Character.isWhitespace(chararray[offset])) {
                wordparser.append(chararray[offset]);
                offset++;
            }
            return wordparser.toString();
        }
        if (Character.isDefined(chararray[offset])) {
            wordparser.append(chararray[offset]);
            offset++;
            if (chararray.length > offset && chararray[offset] == '/') {
                wordparser.append(chararray[offset]);
                offset++;
            }
            return wordparser.toString();
        }
        return wordparser.toString();
    }

    public String getTableColumn(String rawStatement) throws Exception {

        String aggregateFunction = null;
        String table = null;
        String column = null;
        String unit = null;

        int index = rawStatement.indexOf('/');

        table = rawStatement.substring(0, index).trim();

        index++;

        // beginning of aggregate function
        if (rawStatement.charAt(index) == '[') {
            index++;
            aggregateFunction = rawStatement.substring(index, rawStatement.indexOf(']', index)).trim();
            index = rawStatement.indexOf(']', index) + 1;
        }

        // no condition
        if (rawStatement.indexOf('[', index) == -1) {
            column = rawStatement.substring(index).trim();
            if (column.contains(" ")) {
                String[] tempStr = column.split(" ");
                column = tempStr[0];
                unit = tempStr[1];
            }
        } else {
            column = rawStatement.substring(index, rawStatement.indexOf('[', index)).trim();
            index = rawStatement.indexOf(']');
            index++;
            if (index < rawStatement.length()) {
                if (rawStatement.substring(index).trim().length() > 0) {
                    unit = rawStatement.substring(index).trim();
                }
                ;
            }
        }

        // specific case 1: aggregation functions such as Max, Min, Avg, Sum, and Count, the Output value should have Number-Format
        if (aggregateFunction != null) {
            if (aggregateFunction.equalsIgnoreCase("MAX") ||
                    aggregateFunction.equalsIgnoreCase("MIN") ||
                    aggregateFunction.equalsIgnoreCase("AVG") ||
                    aggregateFunction.equalsIgnoreCase("SUM") ||
                    aggregateFunction.equalsIgnoreCase("COUNT")) {
                return Util.NUMBER_COLUMN_KEY;
            }
        }

        // specific case 2: Mixed columns, the output should have String-Format
        if (rawStatement.contains(NewCSVColumnDialog.EOL)) {
            return Util.STRING_COLUMN_KEY;
        }

        // specific case 3: Number with unit "e.g. EUR", --> String-Format
        if (unit != null) {
            return Util.STRING_COLUMN_KEY;
        }

        return table + "__" + column;
    }
}
