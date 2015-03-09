/*
 * This file is part of the Spreadsheet Generator Plugin
 * developed for the Importer/Exporter v3.0 of the
 * 3D City Database - The Open Source CityGML Database
 * http://www.3dcitydb.org/
 * 
 * (C) 2013 - 2015,
 * Chair of Geoinformatics,
 * Technische Universitaet Muenchen, Germany
 * http://www.gis.bgu.tum.de/
 * 
 * The 3D City Database is jointly developed with the following
 * cooperation partners:
 * 
 * Chair of Geoinformatics, TU Munich, <http://www.gis.bgu.tum.de/>
 * virtualcitySYSTEMS GmbH, Berlin <http://www.virtualcitysystems.de/>
 * M.O.S.S. Computer Grafik Systeme GmbH, Muenchen <http://www.moss.de/>
 * 
 * The Spreadsheet Generator Plugin program is free software:
 * you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 */
package org.citydb.plugins.spreadsheet_gen.config;

import javax.xml.bind.annotation.XmlType;

import org.citydb.api.geometry.BoundingBox;
import org.citydb.api.plugin.extension.config.PluginConfig;
import org.citydb.config.project.database.Workspace;

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
