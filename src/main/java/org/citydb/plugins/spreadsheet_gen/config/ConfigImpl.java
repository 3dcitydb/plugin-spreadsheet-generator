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

import javax.xml.bind.annotation.XmlType;

@XmlType(name="SpreadsheetGeneratorConfigType", propOrder={
		"template",
		"selectedcityobjects",
		"workspace",
		"boundingbox",
		"output",
		"showUnsupportedADEWarning"
})
public class ConfigImpl extends PluginConfig {
	private Template template;
	private FeatureClass selectedcityobjects;
	private Workspace workspace;
	private BoundingBox boundingbox;
	private Output output;
	private boolean showUnsupportedADEWarning = true;
	
	public ConfigImpl(){
		template= new Template();
		selectedcityobjects= new FeatureClass();
		workspace = new Workspace();
		boundingbox = new BoundingBox();
		output=new Output();		
	}
	
	public FeatureClass getSelectedcityobjects() {
		return selectedcityobjects;
	}

	public void setSelectedcityobjects(FeatureClass selectedcityobjects) {
		this.selectedcityobjects = selectedcityobjects;
	}

	public Template getTemplate() {
		return template;
	}
	public void setTemplate(Template template) {
		this.template = template;
	}
	public Workspace getWorkspace() {
		return workspace;
	}
	public void setWorkspace(Workspace workspace) {
		this.workspace = workspace;
	}
	public BoundingBox getBoundingbox() {
		return boundingbox;
	}
	public void setBoundingbox(BoundingBox boundingbox) {
		this.boundingbox = boundingbox;
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
