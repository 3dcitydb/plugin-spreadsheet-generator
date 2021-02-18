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
package org.citydb.plugins.spreadsheet_gen.config;

import org.citydb.config.project.plugin.PluginConfig;

import javax.xml.bind.annotation.XmlType;

@XmlType(name = "TableExportConfigType", propOrder = {
		"template",
		"output",
		"query",
		"guiConfig"
})
public class ExportConfig extends PluginConfig {
	private Template template;
	private Output output;
	private SimpleQuery query;
	private GuiConfig guiConfig;

	public ExportConfig() {
		template = new Template();
		output = new Output();
		query = new SimpleQuery();
		guiConfig = new GuiConfig();
	}

	public Template getTemplate() {
		return template;
	}

	public void setTemplate(Template template) {
		if (template != null) {
			this.template = template;
		}
	}

	public Output getOutput() {
		return output;
	}

	public void setOutput(Output output) {
		if (output != null) {
			this.output = output;
		}
	}

	public SimpleQuery getQuery() {
		return query;
	}

	public void setQuery(SimpleQuery query) {
		if (query != null) {
			this.query = query;
		}
	}

	public GuiConfig getGuiConfig() {
		return guiConfig;
	}

	public void setGuiConfig(GuiConfig guiConfig) {
		if (guiConfig != null) {
			this.guiConfig = guiConfig;
		}
	}
}
