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
package org.citydb.plugins.spreadsheet_gen.util;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import org.citygml4j.model.citygml.CityGMLClass;


public class Util {
	public static ResourceBundle I18N;
	
	public static ClipboardHandler clipboard =new ClipboardHandler();

	public static GridBagConstraints setConstraints(int gridx, int gridy, double weightx, double weighty, int fill,
			int insetTop, int insetLeft, int insetBottom, int insetRight) {
		GridBagConstraints constraint = new GridBagConstraints();
		constraint.gridx = gridx;
		constraint.gridy = gridy;
		constraint.weightx = weightx;
		constraint.weighty = weighty;
		constraint.fill = fill;
		
		constraint.insets = new Insets(insetTop, insetLeft, insetBottom, insetRight);
		return constraint;
	}
	
	public static GridBagConstraints setConstraints(int anchor, int gridx, int gridy, double weightx, double weighty, int fill,
			int insetTop, int insetLeft, int insetBottom, int insetRight) {
		GridBagConstraints constraint = new GridBagConstraints();
		constraint.gridx = gridx;
		constraint.gridy = gridy;
		constraint.weightx = weightx;
		constraint.weighty = weighty;
		constraint.fill = fill;
		constraint.anchor=anchor;
		constraint.insets = new Insets(insetTop, insetLeft, insetBottom, insetRight);
		return constraint;
	}
	
	public static boolean checkWorkspaceTimestamp(String timestamp) {
		boolean success = true;
		if (timestamp.length() > 0) {		
			SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
			format.setLenient(false);
			try {
				format.parse(timestamp);				
			} catch (java.text.ParseException e) {
				success = false;
			}
		}
		return success;
	}

	public static CityGMLClass classId2cityObject(int classId) {
		CityGMLClass cityObjectType = CityGMLClass.UNDEFINED;
		switch (classId) {
		case 4:
//		case 35:
			cityObjectType = CityGMLClass.LAND_USE;
			break;
		case 21:
			cityObjectType = CityGMLClass.CITY_FURNITURE;
			break;
		case 26:
			cityObjectType = CityGMLClass.BUILDING;
			break;
		case 9:
			cityObjectType = CityGMLClass.WATER_BODY;
			break;
		case 8:
			cityObjectType = CityGMLClass.PLANT_COVER;
			break;
		case 7:
			cityObjectType = CityGMLClass.SOLITARY_VEGETATION_OBJECT;
			break;
		case 42:
			cityObjectType = CityGMLClass.TRANSPORTATION_COMPLEX;
			break;
		case 43:
			cityObjectType = CityGMLClass.TRACK;
			break;
		case 44:
			cityObjectType = CityGMLClass.RAILWAY;
			break;
		case 45:
			cityObjectType = CityGMLClass.ROAD;
			break;
		case 46:
			cityObjectType = CityGMLClass.SQUARE;
			break;
		case 5:
			cityObjectType = CityGMLClass.GENERIC_CITY_OBJECT;
			break;
		case 23:
			cityObjectType = CityGMLClass.CITY_OBJECT_GROUP;
			break;
		case 14:
			cityObjectType = CityGMLClass.RELIEF_FEATURE;
			break;
		case 16:
			cityObjectType = CityGMLClass.TIN_RELIEF;
			break;
		case 17:
			cityObjectType = CityGMLClass.MASSPOINT_RELIEF;
			break;
		case 18:
			cityObjectType = CityGMLClass.BREAKLINE_RELIEF;
			break;
		case 19:
			cityObjectType = CityGMLClass.RASTER_RELIEF;
			break;
		}

		return cityObjectType;
	}
}
