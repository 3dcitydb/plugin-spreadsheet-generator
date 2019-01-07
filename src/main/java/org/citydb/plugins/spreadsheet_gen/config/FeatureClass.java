/*
 * 3D City Database - The Open Source CityGML Database
 * http://www.3dcitydb.org/
 *
 * Copyright 2013 - 2019
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
