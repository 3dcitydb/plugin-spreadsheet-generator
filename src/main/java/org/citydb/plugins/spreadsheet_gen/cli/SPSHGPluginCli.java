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
package org.citydb.plugins.spreadsheet_gen.cli;

import org.citydb.cli.option.DatabaseOption;
import org.citydb.config.Config;
import org.citydb.config.project.database.DatabaseConnection;
import org.citydb.core.database.DatabaseController;
import org.citydb.core.plugin.CliCommand;
import org.citydb.core.registry.ObjectRegistry;
import org.citydb.core.util.Util;
import org.citydb.gui.ImpExpLauncher;
import org.citydb.plugins.spreadsheet_gen.config.ExportConfig;
import org.citydb.plugins.spreadsheet_gen.config.OutputFileType;
import org.citydb.plugins.spreadsheet_gen.config.Template;
import org.citydb.plugins.spreadsheet_gen.controller.SpreadsheetExporter;
import org.citydb.plugins.spreadsheet_gen.controller.TableExportException;
import org.citydb.util.log.Logger;
import picocli.CommandLine;

import java.nio.file.Path;
import java.util.Locale;
import java.util.ResourceBundle;

@CommandLine.Command(
		name = "export-table",
		description = "Exports attribute data in tabular form as CSV or XLSX file."
)
public class SPSHGPluginCli extends CliCommand {
	@CommandLine.Option(names = {"-o", "--output"}, required = true, paramLabel = "<file>",
			description = "Name of the output file with the extension .csv or .xlsx")
	private Path outputFile;

	@CommandLine.Option(names = {"-l", "--template"}, required = true, paramLabel = "<file>",
			description = "Name of the template file.")
	private Path templateFile;

	@CommandLine.Option(names = {"-D", "--delimiter"}, paramLabel = "<char>",
			description = "Column delimiter to use for CSV file (default: ',').")
	private String delimiter;

	@CommandLine.ArgGroup(exclusive = false, heading = "Query and filter options:%n")
	private QueryOption queryOption;

	@CommandLine.ArgGroup(exclusive = false, heading = "Database connection options:%n")
	private final DatabaseOption databaseOption = new DatabaseOption();

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
		ExportConfig pluginConfig = config.getPluginConfig(ExportConfig.class);
		if (pluginConfig == null) {
			pluginConfig = new ExportConfig();
			config.registerPluginConfig(pluginConfig);
		}

		// connect to database
		DatabaseController database = ObjectRegistry.getInstance().getDatabaseController();
		DatabaseConnection connection = databaseOption.hasUserInput() ?
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

		// set xlsx/csv output
		String outputFileName = outputFile.toAbsolutePath().toString();
		if ("xlsx".equalsIgnoreCase(Util.getFileExtension(outputFileName))) {
			pluginConfig.getOutput().setType(OutputFileType.XLSX);
			pluginConfig.getOutput().getXlsxFile().setOutputPath(outputFileName);
		} else {
			pluginConfig.getOutput().setType(OutputFileType.CSV);
			pluginConfig.getOutput().getCsvFile().setOutputPath(outputFileName);

			if (delimiter != null) {
				pluginConfig.getOutput().getCsvFile().setDelimiter(delimiter);
			}
		}

		// set user-defined query options
		if (queryOption != null) {
			pluginConfig.setQuery(queryOption.toQuery());
		}

		// run spreadsheet export
		org.citydb.plugins.spreadsheet_gen.util.Util.I18N = ResourceBundle.getBundle(
				"org.citydb.plugins.spreadsheet_gen.i18n.language",
				Locale.ENGLISH);

		try {
			new SpreadsheetExporter(pluginConfig).doProcess();
			log.info("Table data export successfully finished.");
		} catch (TableExportException e) {
			log.error(e.getMessage(), e.getCause());
			log.warn("Table data export aborted.");
			return 1;
		} finally {
			database.disconnect(true);
		}

		return 0;
	}
}
