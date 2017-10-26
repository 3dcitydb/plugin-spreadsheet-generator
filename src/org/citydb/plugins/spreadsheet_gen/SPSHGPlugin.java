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
package org.citydb.plugins.spreadsheet_gen;

import java.util.Locale;
import java.util.ResourceBundle;

import org.citydb.plugins.spreadsheet_gen.config.ConfigImpl;
import org.citydb.plugins.spreadsheet_gen.controller.cloudservice.CloudServiceRegistery;
import org.citydb.plugins.spreadsheet_gen.controller.cloudservice_impl.GoogleSpreadSheetService;
import org.citydb.plugins.spreadsheet_gen.gui.view.SPSHGView;
import org.citydb.plugins.spreadsheet_gen.util.Util;

import org.citydb.api.controller.ApplicationStarter;
import org.citydb.api.plugin.Plugin;
import org.citydb.api.plugin.extension.config.ConfigExtension;
import org.citydb.api.plugin.extension.config.PluginConfigEvent;
import org.citydb.api.plugin.extension.view.View;
import org.citydb.api.plugin.extension.view.ViewExtension;

// TODO: Auto-generated Javadoc
/**
 * The Class SPSHGPlugin.
 */
public class SPSHGPlugin implements Plugin, ViewExtension, ConfigExtension<ConfigImpl>  {
	
	/** The config. */
	private ConfigImpl config;
	
	/** The current locale. */
	private Locale currentLocale;
	
	/** The view. */
	private SPSHGView view;

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		// test run
		ApplicationStarter starter = new ApplicationStarter();
		starter.run(args, new SPSHGPlugin());
	}

	/* (non-Javadoc)
	 * @see org.citydb.api.plugin.extension.view.ViewExtension#getView()
	 */
	@Override
	public View getView() {
		return view;
	}

	/* (non-Javadoc)
	 * @see org.citydb.api.plugin.Plugin#init(java.util.Locale)
	 */
	@Override
	public void init(Locale locale) {
		Util.I18N = ResourceBundle.getBundle("org.citydb.plugins.spreadsheet_gen.gui.locale", locale);	
		initCloudServices();
		view = new SPSHGView(this);
		loadSettings();
		switchLocale(locale);
	}

	/**
	 * Inits the cloud services.
	 */
	private void initCloudServices(){
		GoogleSpreadSheetService gsss=new GoogleSpreadSheetService();
		CloudServiceRegistery.getInstance().registerService(gsss,gsss.getServiceName());
	}
	
	/* (non-Javadoc)
	 * @see org.citydb.api.plugin.Plugin#shutdown()
	 */
	@Override
	public void shutdown() {
		saveSettings();
	}

	/* (non-Javadoc)
	 * @see org.citydb.api.plugin.Plugin#switchLocale(java.util.Locale)
	 */
	@Override
	public void switchLocale(Locale locale) {
		if (locale.equals(currentLocale))
			return;
		Util.I18N = ResourceBundle.getBundle("org.citydb.plugins.spreadsheet_gen.gui.locale", locale);
		currentLocale = locale;
		
		view.switchLocale();
	}

	/* (non-Javadoc)
	 * @see org.citydb.api.plugin.extension.config.ConfigExtension#configLoaded(org.citydb.api.plugin.extension.config.PluginConfig)
	 */
	@Override
	public void configLoaded(ConfigImpl config2) {
		boolean reload = this.config != null;		
		setConfig(config2);
		
		if (reload)
			loadSettings();	
	}

	/* (non-Javadoc)
	 * @see org.citydb.api.plugin.extension.config.ConfigExtension#getConfig()
	 */
	@Override
	public ConfigImpl getConfig() {
		return config;
	}
	
	/**
	 * Sets the config.
	 *
	 * @param config the new config
	 */
	public void setConfig(ConfigImpl config) {
		this.config = config;
	}
	
	/* (non-Javadoc)
	 * @see org.citydb.api.plugin.extension.config.ConfigExtension#handleEvent(org.citydb.api.plugin.extension.config.PluginConfigEvent)
	 */
	@Override
	public void handleEvent(PluginConfigEvent event) {
		switch (event) {
		case RESET_DEFAULT_CONFIG:
			this.config = new ConfigImpl();
			loadSettings();
			break;
		case PRE_SAVE_CONFIG:
			saveSettings();
			break;
		}
		
	}
	
	/**
	 * Load settings.
	 */
	public void loadSettings() {
		view.loadSettings();	
	}
	
	/**
	 * Save settings.
	 */
	public void saveSettings() {
		view.saveSettings();
	}
}
