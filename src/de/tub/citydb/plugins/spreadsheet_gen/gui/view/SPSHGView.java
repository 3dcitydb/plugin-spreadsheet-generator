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
package de.tub.citydb.plugins.spreadsheet_gen.gui.view;

import java.awt.Component;

import javax.swing.Icon;

import de.tub.citydb.api.event.global.ViewEvent;
import de.tub.citydb.api.plugin.extension.view.View;
import de.tub.citydb.api.plugin.extension.view.ViewListener;
import de.tub.citydb.plugins.spreadsheet_gen.spsheet_gen;
import de.tub.citydb.plugins.spreadsheet_gen.util.Util;

public class SPSHGView  extends View implements ViewListener{
	private final SPSHGPanel component;
	
	public SPSHGView(spsheet_gen spshg){
		component = new SPSHGPanel(spshg);
	}
	
	@Override
	public Icon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLocalizedTitle() {
		return Util.I18N.getString("spshg.general.title");
	}

	@Override
	public String getToolTip() {
		return Util.I18N.getString("spshg.general.tooltip");
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
		if (component!=null  && e.getView() instanceof SPSHGView)
			component.panelIsVisible(true);
	}

	@Override
	public void viewDeactivated(ViewEvent e) {
		if (component!=null  && e.getView() instanceof SPSHGView)
			component.panelIsVisible(false);
		
	}

}
