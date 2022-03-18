/*
 * 3D City Database - The Open Source CityGML Database
 * https://www.3dcitydb.org/
 *
 * Copyright 2013 - 2021
 * Chair of Geoinformatics
 * Technical University of Munich, Germany
 * https://www.lrg.tum.de/gis/
 *
 * The 3D City Database is jointly developed with the following
 * cooperation partners:
 *
 * Virtual City Systems, Berlin <https://vc.systems/>
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

import org.citydb.core.plugin.Plugin;
import org.citydb.core.plugin.extension.config.ConfigExtension;
import org.citydb.core.plugin.extension.config.PluginConfigEvent;
import org.citydb.gui.ImpExpLauncher;
import org.citydb.gui.plugin.view.View;
import org.citydb.gui.plugin.view.ViewController;
import org.citydb.gui.plugin.view.ViewExtension;
import org.citydb.plugins.spreadsheet_gen.config.ExportConfig;
import org.citydb.plugins.spreadsheet_gen.config.GuiConfig;
import org.citydb.plugins.spreadsheet_gen.gui.view.SPSHGView;
import org.citydb.plugins.spreadsheet_gen.util.Util;

import java.util.Locale;
import java.util.ResourceBundle;

public class SPSHGPlugin extends Plugin implements ViewExtension, ConfigExtension<ExportConfig> {
	private ExportConfig config;
	private SPSHGView view;

	public static void main(String[] args) {
		// test run
		ImpExpLauncher launcher = new ImpExpLauncher()
				.withArgs(args)
				.withPlugin(new SPSHGPlugin());

		launcher.start();
	}

	@Override
	public void initGuiExtension(ViewController viewController, Locale locale) {
		Util.I18N = ResourceBundle.getBundle("org.citydb.plugins.spreadsheet_gen.i18n.language", locale);
		view = new SPSHGView(viewController, this);
		loadSettings();
	}

	@Override
	public View getView() {
		return view;
	}
	
	@Override
	public void shutdownGui() {
		setSettings();
	}

	@Override
	public void switchLocale(Locale locale) {
		Util.I18N = ResourceBundle.getBundle("org.citydb.plugins.spreadsheet_gen.i18n.language", locale);
		view.switchLocale(locale);
	}

	@Override
	public void configLoaded(ExportConfig config) {
		boolean reload = this.config != null;		
		setConfig(config);
		
		if (reload) {
			loadSettings();
		}
	}

	@Override
	public ExportConfig getConfig() {
		return config;
	}
	
	public void setConfig(ExportConfig config) {
		this.config = config;
	}
	
	@Override
	public void handleEvent(PluginConfigEvent event) {
		switch (event) {
			case RESET_DEFAULT_CONFIG:
				this.config = new ExportConfig();
				loadSettings();
				break;
			case PRE_SAVE_CONFIG:
				setSettings();
				break;
			case RESET_GUI_VIEW:
				config.setGuiConfig(new GuiConfig());
				break;
		}
	}
	
	public void loadSettings() {
		view.loadSettings();	
	}
	
	public void setSettings() {
		view.setSettings();
	}
}
