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
package org.citydb.plugins.spreadsheet_gen;

import org.citydb.ImpExpLauncher;
import org.citydb.cli.ImpExpCli;
import org.citydb.config.Config;
import org.citydb.config.geometry.BoundingBox;
import org.citydb.config.project.database.DatabaseConnection;
import org.citydb.database.DatabaseController;
import org.citydb.log.Logger;
import org.citydb.plugin.CliCommand;
import org.citydb.plugin.cli.CliOptionBuilder;
import org.citydb.plugin.cli.DatabaseOption;
import org.citydb.plugin.cli.TypeNamesOption;
import org.citydb.plugins.spreadsheet_gen.config.ConfigImpl;
import org.citydb.plugins.spreadsheet_gen.config.Output;
import org.citydb.plugins.spreadsheet_gen.config.Template;
import org.citydb.plugins.spreadsheet_gen.controller.SpreadsheetExporter;
import org.citydb.registry.ObjectRegistry;
import org.citydb.util.Util;
import picocli.CommandLine;

import java.nio.file.Path;
import java.util.Locale;
import java.util.ResourceBundle;

@CommandLine.Command(
		name = "spreadsheet-generate",
		description = "Generate spreadsheet from the database.",
		versionProvider = ImpExpCli.class
)
public class SPSHGPluginCli extends CliCommand {
	@CommandLine.Option(names = {"--template"}, required = true,
			description = "Name of the template file.")
	private Path templateFile;

	@CommandLine.ArgGroup (exclusive = false)
	private TypeNamesOption typeNamesOption;

	@CommandLine.Option(names = {"-b", "--bbox"}, paramLabel = "<minx,miny,maxx,maxy[,srid]>",
			description = "Bounding box to use as spatial filter.")
	private String bbox;

	@CommandLine.Option(names = {"-o", "--output"}, required = true,
			description = "Name of the output spreadsheet file with the extension .csv or .xlsx")
	private Path outputFile;

	@CommandLine.Option(names = {"-D", "--delimiter"}, paramLabel = "<char>", defaultValue = ",",
			description = "Delimiter to use for splitting lines in CSV file (default: '${DEFAULT-VALUE}').")
	private String delimiter;

	@CommandLine.ArgGroup(exclusive = false, heading = "Database connection options:%n")
	private DatabaseOption databaseOption;

	private BoundingBox boundingBox;
	private final Logger log = Logger.getInstance();

	public static void main(String[] args) {
		// test run
		ImpExpLauncher launcher = new ImpExpLauncher()
				.withArgs(args)
				.withCliCommand(new SPSHGPluginCli());

		launcher.start();
	}

	@Override
	public Integer call() throws Exception {
		// prepare configs
		Config config = ObjectRegistry.getInstance().getConfig();
		ConfigImpl pluginConfig = config.getPluginConfig(ConfigImpl.class);
		if (pluginConfig == null) {
			pluginConfig = new ConfigImpl();
			config.registerPluginConfig(pluginConfig);
		}

		// connect to database
		DatabaseController database = ObjectRegistry.getInstance().getDatabaseController();
		DatabaseConnection connection = databaseOption != null ?
				databaseOption.toDatabaseConnection() :
				config.getDatabaseConfig().getActiveConnection();

		if (!database.connect(connection)) {
			log.warn("Database export aborted.");
			return 1;
		}

		// set template
		Template templateConfig = pluginConfig.getTemplate();
		templateConfig.setManualTemplate(false);
		templateConfig.setPath(templateFile.toAbsolutePath().toString());

		// set feature types
		if (typeNamesOption != null) {
			pluginConfig.setFeatureTypeFilter(typeNamesOption.toFeatureTypeFilter());
		}

		// set bounding box
		if (boundingBox != null) {
			if (boundingBox.getSrs() == null) {
				boundingBox.setSrs(database.getActiveDatabaseAdapter().getConnectionMetaData().getReferenceSystem());
			}
			pluginConfig.setBoundingbox(boundingBox);
		}

		// set xlsx/csv output
		String outputFileName = outputFile.toAbsolutePath().toString();
		if ("xlsx".equalsIgnoreCase(Util.getFileExtension(outputFileName))) {
			pluginConfig.getOutput().setType(Output.XLSX_FILE_CONFIG);
			pluginConfig.getOutput().getXlsxfile().setOutputPath(outputFileName);
		} else {
			pluginConfig.getOutput().setType(Output.CSV_FILE_CONFIG);
			pluginConfig.getOutput().getCsvfile().setOutputPath(outputFileName);
			pluginConfig.getOutput().getCsvfile().setSeparator(delimiter);
		}

		// run spreadsheet export
		org.citydb.plugins.spreadsheet_gen.util.Util.I18N = ResourceBundle.getBundle(
				"org.citydb.plugins.spreadsheet_gen.i18n.language",
				Locale.ENGLISH);
		boolean success = new SpreadsheetExporter(pluginConfig).doProcess();
		database.disconnect(true);
		if (success) {
			log.info("Spreadsheet export successfully finished.");
			return 0;
		} else {
			log.warn("Spreadsheet export aborted.");
			return 1;
		}
	}

	@Override
	public void preprocess(CommandLine commandLine) throws Exception {
		boundingBox = CliOptionBuilder.boundingBox(bbox, commandLine);
	}
}
