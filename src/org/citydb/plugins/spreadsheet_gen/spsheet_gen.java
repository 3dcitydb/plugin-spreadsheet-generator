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
package org.citydb.plugins.spreadsheet_gen;

import java.util.Locale;
import java.util.ResourceBundle;

import org.citydb.plugins.spreadsheet_gen.config.ConfigImpl;
import org.citydb.plugins.spreadsheet_gen.controller.cloudservice.CloudServiceRegistery;
import org.citydb.plugins.spreadsheet_gen.controller.cloudservice_impl.GoogleSpreadSheetService;
import org.citydb.plugins.spreadsheet_gen.gui.view.SPSHGView;
import org.citydb.plugins.spreadsheet_gen.util.Util;

import de.tub.citydb.api.controller.ApplicationStarter;
import de.tub.citydb.api.plugin.Plugin;
import de.tub.citydb.api.plugin.extension.config.ConfigExtension;
import de.tub.citydb.api.plugin.extension.config.PluginConfigEvent;
import de.tub.citydb.api.plugin.extension.view.View;
import de.tub.citydb.api.plugin.extension.view.ViewExtension;


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
		Util.I18N = ResourceBundle.getBundle("de.tub.citydb.plugins.spreadsheet_gen.gui.locale", locale);	
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
		Util.I18N = ResourceBundle.getBundle("de.tub.citydb.plugins.spreadsheet_gen.gui.locale", locale);
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
