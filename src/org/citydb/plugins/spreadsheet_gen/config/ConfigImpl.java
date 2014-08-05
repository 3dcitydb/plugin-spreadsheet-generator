/*
 * This file is part of the Spreadsheet Generator Plugin
 * developed for the 3D City Database Importer/Exporter v1.4.0
 * Copyright (c) 2012
 * Institute for Geodesy and Geoinformation Science
 * Technische Universitaet Berlin, Germany
 * http://www.gis.tu-berlin.de/
 * 
 * The Spreadsheet Generator Plugin program is free software:
 * you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 */
package org.citydb.plugins.spreadsheet_gen.config;

import javax.xml.bind.annotation.XmlType;

//import de.tub.citydb.api.config.BoundingBox;
import de.tub.citydb.api.geometry.BoundingBox;
import de.tub.citydb.api.plugin.extension.config.PluginConfig;
import de.tub.citydb.config.project.database.Workspace;

@XmlType(name="SpreadsheetGeneratorConfigType", propOrder={
		"template",
		"selectedcityobjects",
		"workspace",
		"boundingbox",
		"output"
})
public class ConfigImpl extends PluginConfig{
	private Template template;
	private FeatureClass selectedcityobjects;
	private Workspace workspace;
	private BoundingBox boundingbox;
	private Output output;
	
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

}
