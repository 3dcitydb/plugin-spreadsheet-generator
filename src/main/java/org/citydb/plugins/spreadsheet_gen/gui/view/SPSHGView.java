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
package org.citydb.plugins.spreadsheet_gen.gui.view;

import org.citydb.plugin.extension.view.View;
import org.citydb.plugin.extension.view.ViewController;
import org.citydb.plugin.extension.view.ViewEvent;
import org.citydb.plugin.extension.view.ViewListener;
import org.citydb.plugins.spreadsheet_gen.SPSHGPlugin;
import org.citydb.plugins.spreadsheet_gen.util.Util;

import javax.swing.*;
import java.awt.*;

public class SPSHGView  extends View implements ViewListener {
	private final SPSHGPanel component;
	
	public SPSHGView(ViewController viewController, SPSHGPlugin spshg){
		component = new SPSHGPanel(viewController, spshg);
	}
	
	@Override
	public Icon getIcon() {
		return null;
	}

	@Override
	public String getLocalizedTitle() {
		return Util.I18N.getString("spshg.general.title");
	}

	@Override
	public String getToolTip() {
		return null;
	}

	@Override
	public Component getViewComponent() {
		return component;
	}
	
	public void switchLocale() {
		component.switchLocale();
	}
	
	public void saveSettings(){
		component.saveSettings();
	}
	
	public void loadSettings(){
		component.loadSettings();
	}

	@Override
	public void viewActivated(ViewEvent e) {
		// nothing to do
	}

	@Override
	public void viewDeactivated(ViewEvent e) {
		// nothing to do
	}

}
