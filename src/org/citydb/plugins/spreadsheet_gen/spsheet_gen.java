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


public class spsheet_gen implements Plugin, ViewExtension, ConfigExtension<ConfigImpl>  {
	private ConfigImpl config;
	private Locale currentLocale;
	private SPSHGView view;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// test run
		ApplicationStarter starter = new ApplicationStarter();
		starter.run(args, new spsheet_gen());
	}

	@Override
	public View getView() {
		return view;
	}

	@Override
	public void init(Locale locale) {
		Util.I18N = ResourceBundle.getBundle("org.citydb.plugins.spreadsheet_gen.gui.locale", locale);	
		initCloudServices();
		view = new SPSHGView(this);
		loadSettings();
		switchLocale(locale);
	}

	private void initCloudServices(){
		GoogleSpreadSheetService gsss=new GoogleSpreadSheetService();
		CloudServiceRegistery.getInstance().registerService(gsss,gsss.getServiceName());
	}
	
	@Override
	public void shutdown() {
		saveSettings();
	}

	@Override
	public void switchLocale(Locale locale) {
		if (locale.equals(currentLocale))
			return;
		Util.I18N = ResourceBundle.getBundle("org.citydb.plugins.spreadsheet_gen.gui.locale", locale);
		currentLocale = locale;
		
		view.switchLocale();
	}

	@Override
	public void configLoaded(ConfigImpl config2) {
		boolean reload = this.config != null;		
		setConfig(config2);
		
		if (reload)
			loadSettings();	
	}

	@Override
	public ConfigImpl getConfig() {
		return config;
	}
	
	public void setConfig(ConfigImpl config) {
		this.config = config;
	}
	
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
	
	public void loadSettings() {
		view.loadSettings();	
	}
	
	public void saveSettings() {
		view.saveSettings();
	}
}
