/*
 * 3D City Database - The Open Source CityGML Database
 * http://www.3dcitydb.org/
 *
 * Copyright 2013 - 2018
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

import org.citydb.plugin.ApplicationStarter;
import org.citydb.plugin.Plugin;
import org.citydb.plugin.extension.config.ConfigExtension;
import org.citydb.plugin.extension.config.PluginConfigEvent;
import org.citydb.plugin.extension.view.View;
import org.citydb.plugin.extension.view.ViewController;
import org.citydb.plugin.extension.view.ViewExtension;
import org.citydb.plugins.spreadsheet_gen.config.ConfigImpl;
import org.citydb.plugins.spreadsheet_gen.gui.view.SPSHGView;
import org.citydb.plugins.spreadsheet_gen.util.Util;

import java.util.Locale;
import java.util.ResourceBundle;

public class SPSHGPlugin implements Plugin, ViewExtension, ConfigExtension<ConfigImpl> {
	private ConfigImpl config;
	private Locale currentLocale;
	private SPSHGView view;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// test run
		ApplicationStarter starter = new ApplicationStarter();
		starter.run(args, new SPSHGPlugin());
	}

	@Override
	public View getView() {
		return view;
	}

	@Override
	public void init(ViewController viewController, Locale locale) {
		Util.I18N = ResourceBundle.getBundle("org.citydb.plugins.spreadsheet_gen.i18n.language", locale);
		view = new SPSHGView(viewController, this);
		loadSettings();
		switchLocale(locale);
	}
	
	@Override
	public void shutdown() {
		saveSettings();
	}

	@Override
	public void switchLocale(Locale locale) {
		if (locale.equals(currentLocale))
			return;
		Util.I18N = ResourceBundle.getBundle("org.citydb.plugins.spreadsheet_gen.i18n.language", locale);
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
