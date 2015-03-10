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
package org.citydb.plugins.spreadsheet_gen.gui.view;

import java.awt.Component;

import javax.swing.Icon;

import org.citydb.plugins.spreadsheet_gen.spsheet_gen;
import org.citydb.plugins.spreadsheet_gen.util.Util;


import org.citydb.api.event.global.ViewEvent;
import org.citydb.api.plugin.extension.view.View;
import org.citydb.api.plugin.extension.view.ViewListener;

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