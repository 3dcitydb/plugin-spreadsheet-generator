/*
 * 3D City Database - The Open Source CityGML Database
 * http://www.3dcitydb.org/
 * 
 * Copyright 2013 - 2017
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

import javax.xml.bind.annotation.XmlType;

import org.citydb.api.geometry.BoundingBox;
import org.citydb.api.plugin.extension.config.PluginConfig;
import org.citydb.config.project.database.Workspace;

// TODO: Auto-generated Javadoc
/**
 * The Class ConfigImpl.
 */
@XmlType(name="SpreadsheetGeneratorConfigType", propOrder={
		"template",
		"selectedcityobjects",
		"workspace",
		"boundingbox",
		"output"
})
public class ConfigImpl extends PluginConfig{
	
	/** The template. */
	private Template template;
	
	/** The selectedcityobjects. */
	private FeatureClass selectedcityobjects;
	
	/** The workspace. */
	private Workspace workspace;
	
	/** The boundingbox. */
	private BoundingBox boundingbox;
	
	/** The output. */
	private Output output;
	
	/**
	 * Instantiates a new config impl.
	 */
	public ConfigImpl(){
		template= new Template();
		selectedcityobjects= new FeatureClass();
		workspace = new Workspace();
		boundingbox = new BoundingBox();
		output=new Output();		
	}
	
	/**
	 * Gets the selectedcityobjects.
	 *
	 * @return the selectedcityobjects
	 */
	public FeatureClass getSelectedcityobjects() {
		return selectedcityobjects;
	}

	/**
	 * Sets the selectedcityobjects.
	 *
	 * @param selectedcityobjects the new selectedcityobjects
	 */
	public void setSelectedcityobjects(FeatureClass selectedcityobjects) {
		this.selectedcityobjects = selectedcityobjects;
	}

	/**
	 * Gets the template.
	 *
	 * @return the template
	 */
	public Template getTemplate() {
		return template;
	}
	
	/**
	 * Sets the template.
	 *
	 * @param template the new template
	 */
	public void setTemplate(Template template) {
		this.template = template;
	}
	
	/**
	 * Gets the workspace.
	 *
	 * @return the workspace
	 */
	public Workspace getWorkspace() {
		return workspace;
	}
	
	/**
	 * Sets the workspace.
	 *
	 * @param workspace the new workspace
	 */
	public void setWorkspace(Workspace workspace) {
		this.workspace = workspace;
	}
	
	/**
	 * Gets the boundingbox.
	 *
	 * @return the boundingbox
	 */
	public BoundingBox getBoundingbox() {
		return boundingbox;
	}
	
	/**
	 * Sets the boundingbox.
	 *
	 * @param boundingbox the new boundingbox
	 */
	public void setBoundingbox(BoundingBox boundingbox) {
		this.boundingbox = boundingbox;
	}
	
	/**
	 * Gets the output.
	 *
	 * @return the output
	 */
	public Output getOutput() {
		return output;
	}
	
	/**
	 * Sets the output.
	 *
	 * @param output the new output
	 */
	public void setOutput(Output output) {
		this.output = output;
	}

}
