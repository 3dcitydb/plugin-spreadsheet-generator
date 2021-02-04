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
import org.citydb.config.project.plugin.PluginConfig;
import org.citydb.config.project.query.filter.type.FeatureTypeFilter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "SpreadsheetGeneratorConfigType", propOrder = {
		"template",
		"featureTypeFilter",
		"boundingBox",
		"output",
		"showUnsupportedADEWarning",
		"collapseBoundingBoxFilter",
		"collapseFeatureTypeFilter"
})
public class ConfigImpl extends PluginConfig {
	@XmlAttribute
	private boolean useFeatureTypeFilter;
	@XmlAttribute
	private boolean useBoundingBoxFilter;

	private Template template;
	private FeatureTypeFilter featureTypeFilter;
	private BoundingBox boundingBox;
	private Output output;
	private boolean showUnsupportedADEWarning = true;
	private boolean collapseBoundingBoxFilter = true;
	private boolean collapseFeatureTypeFilter = true;

	public ConfigImpl() {
		template = new Template();
		featureTypeFilter = new FeatureTypeFilter();
		boundingBox = new BoundingBox();
		output = new Output();
	}

	public FeatureTypeFilter getFeatureTypeFilter() {
		return featureTypeFilter;
	}

	public void setFeatureTypeFilter(FeatureTypeFilter featureTypeFilter) {
		if (featureTypeFilter != null) {
			this.featureTypeFilter = featureTypeFilter;
		}
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
		if (template != null) {
			this.template = template;
		}
	}

	public BoundingBox getBoundingBox() {
		return boundingBox;
	}

	public void setBoundingBox(BoundingBox boundingBox) {
		if (boundingBox != null) {
			this.boundingBox = boundingBox;
		}
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
		if (output != null) {
			this.output = output;
		}
	}

	public boolean isShowUnsupportedADEWarning() {
		return showUnsupportedADEWarning;
	}

	public void setShowUnsupportedADEWarning(boolean showUnsupportedADEWarning) {
		this.showUnsupportedADEWarning = showUnsupportedADEWarning;
	}

	public boolean isCollapseBoundingBoxFilter() {
		return collapseBoundingBoxFilter;
	}

	public void setCollapseBoundingBoxFilter(boolean collapseBoundingBoxFilter) {
		this.collapseBoundingBoxFilter = collapseBoundingBoxFilter;
	}

	public boolean isCollapseFeatureTypeFilter() {
		return collapseFeatureTypeFilter;
	}

	public void setCollapseFeatureTypeFilter(boolean collapseFeatureTypeFilter) {
		this.collapseFeatureTypeFilter = collapseFeatureTypeFilter;
	}
}
