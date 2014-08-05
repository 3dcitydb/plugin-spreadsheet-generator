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
package org.citydb.plugins.spreadsheet_gen.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="FeatureClassType", propOrder={
		"building",
		"waterBody",
		"landUse",
		"vegetation",
		"transportation",
		"reliefFeature",
		"cityFurniture",
		"genericCityObject",
		"cityObjectGroup",
		"tunnel",
		"bridge"
})
public class FeatureClass {
	@XmlElement(defaultValue="true")
	private Boolean building = true;
	@XmlElement(defaultValue="false")
	private Boolean waterBody = false;
	@XmlElement(defaultValue="false")
	private Boolean landUse = false;
	@XmlElement(defaultValue="false")
	private Boolean vegetation = false;
	@XmlElement(defaultValue="false")
	private Boolean transportation = false;
	@XmlElement(defaultValue="false")
	private Boolean reliefFeature = false;
	@XmlElement(defaultValue="false")
	private Boolean cityFurniture = false;
	@XmlElement(defaultValue="false")
	private Boolean genericCityObject = false;
	@XmlElement(defaultValue="false")
	private Boolean cityObjectGroup = false;
	@XmlElement(defaultValue="false")
	private Boolean tunnel = false;
	@XmlElement(defaultValue="false")
	private Boolean bridge = false;

	public FeatureClass() {
	}

	public boolean isSetBuilding() {
		if (building != null)
			return building.booleanValue();

		return false;
	}

	public Boolean getBuilding() {
		return building;
	}

	public void setBuilding(Boolean building) {
		this.building = building;
	}

	public boolean isSetWaterBody() {
		if (waterBody != null)
			return waterBody.booleanValue();

		return false;
	}

	public Boolean getWaterBody() {
		return waterBody;
	}

	public void setWaterBody(Boolean waterBody) {
		this.waterBody = waterBody;
	}

	public boolean isSetLandUse() {
		if (landUse != null)
			return landUse.booleanValue();

		return false;
	}

	public Boolean getLandUse() {
		return landUse;
	}

	public void setLandUse(Boolean landUse) {
		this.landUse = landUse;
	}

	public boolean isSetVegetation() {
		if (vegetation != null)
			return vegetation.booleanValue();

		return false;
	}

	public Boolean getVegetation() {
		return vegetation;
	}

	public void setVegetation(Boolean vegetation) {
		this.vegetation = vegetation;
	}

	public boolean isSetTransportation() {
		if (transportation != null)
			return transportation.booleanValue();

		return false;
	}

	public Boolean getTransportation() {
		return transportation;
	}

	public void setTransportation(Boolean transportation) {
		this.transportation = transportation;
	}

	public boolean isSetReliefFeature() {
		if (reliefFeature != null)
			return reliefFeature.booleanValue();

		return false;
	}

	public Boolean getReliefFeature() {
		return reliefFeature;
	}

	public void setReliefFeature(Boolean reliefFeature) {
		this.reliefFeature = reliefFeature;
	}

	public boolean isSetCityFurniture() {
		if (cityFurniture != null)
			return cityFurniture.booleanValue();

		return false;
	}

	public Boolean getCityFurniture() {
		return cityFurniture;
	}

	public void setCityFurniture(Boolean cityFurniture) {
		this.cityFurniture = cityFurniture;
	}

	public boolean isSetGenericCityObject() {
		if (genericCityObject != null)
			return genericCityObject.booleanValue();

		return false;
	}

	public Boolean getGenericCityObject() {
		return genericCityObject;
	}

	public void setGenericCityObject(Boolean genericCityObject) {
		this.genericCityObject = genericCityObject;
	}

	public boolean isSetCityObjectGroup() {
		if (cityObjectGroup != null)
			return cityObjectGroup.booleanValue();

		return false;
	}

	public Boolean getCityObjectGroup() {
		return cityObjectGroup;
	}

	public void setCityObjectGroup(Boolean cityObjectGroup) {
		this.cityObjectGroup = cityObjectGroup;
	}

	public boolean isSetTunnel() {
		if (tunnel != null)
			return tunnel.booleanValue();
		return false;
	}

	public Boolean getTunnel() {
		return tunnel;
	}

	public void setTunnel(Boolean tunnel) {
		this.tunnel = tunnel;
	}
	
	public boolean isSetBridge() {
		if (bridge != null)
			return bridge.booleanValue();
		return false;
	}

	public Boolean getBridge() {
		return bridge;
	}

	public void setBridge(Boolean bridge) {
		this.bridge = bridge;
	}
}
