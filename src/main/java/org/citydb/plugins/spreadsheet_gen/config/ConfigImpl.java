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

import org.citydb.config.geometry.BoundingBox;
import org.citydb.config.project.database.Workspace;
import org.citydb.config.project.plugin.PluginConfig;
import org.citydb.config.project.query.filter.type.FeatureTypeFilter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "SpreadsheetGeneratorConfigType", propOrder = {
		"template",
		"featureTypeFilter",
		"workspace",
		"boundingbox",
		"output",
		"showUnsupportedADEWarning"
})
public class ConfigImpl extends PluginConfig {
	@XmlAttribute
	private boolean useFeatureTypeFilter = true;
	@XmlAttribute
	private boolean useBoundingBoxFilter;

	private Template template;
	private FeatureTypeFilter featureTypeFilter;
	private Workspace workspace;
	private BoundingBox boundingbox;
	private Output output;
	private boolean showUnsupportedADEWarning = true;

	public ConfigImpl() {
		template = new Template();
		featureTypeFilter = new FeatureTypeFilter();
		workspace = new Workspace();
		boundingbox = new BoundingBox();
		output = new Output();
	}

	public FeatureTypeFilter getFeatureTypeFilter() {
		return featureTypeFilter;
	}

	public void setFeatureTypeFilter(FeatureTypeFilter featureTypeFilter) {
		this.featureTypeFilter = featureTypeFilter;
	}

	public boolean isUseFeatureTypeFilter() {
		return useFeatureTypeFilter;
	}

	public void setUseFeatureTypeFilter(boolean useFeatureTypeFilter) {
		this.useFeatureTypeFilter = useFeatureTypeFilter;
	}

	public Template getTemplate() {
		return template;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}

	public BoundingBox getBoundingbox() {
		return boundingbox;
	}

	public void setBoundingbox(BoundingBox boundingbox) {
		this.boundingbox = boundingbox;
	}

	public boolean isUseBoundingBoxFilter() {
		return useBoundingBoxFilter;
	}

	public void setUseBoundingBoxFilter(boolean useBoundingBoxFilter) {
		this.useBoundingBoxFilter = useBoundingBoxFilter;
	}

	public Output getOutput() {
		return output;
	}

	public void setOutput(Output output) {
		this.output = output;
	}

	public boolean isShowUnsupportedADEWarning() {
		return showUnsupportedADEWarning;
	}

	public void setShowUnsupportedADEWarning(boolean showUnsupportedADEWarning) {
		this.showUnsupportedADEWarning = showUnsupportedADEWarning;
	}
}
