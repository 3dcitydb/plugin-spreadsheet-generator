/*
 * 3D City Database - The Open Source CityGML Database
 * http://www.3dcitydb.org/
 * 
 * Copyright 2013 - 2017
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

// TODO: Auto-generated Javadoc
/**
 * The Class FeatureClass.
 */
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
	
	/** The building. */
	@XmlElement(defaultValue="true")
	private Boolean building = true;
	
	/** The water body. */
	@XmlElement(defaultValue="false")
	private Boolean waterBody = false;
	
	/** The land use. */
	@XmlElement(defaultValue="false")
	private Boolean landUse = false;
	
	/** The vegetation. */
	@XmlElement(defaultValue="false")
	private Boolean vegetation = false;
	
	/** The transportation. */
	@XmlElement(defaultValue="false")
	private Boolean transportation = false;
	
	/** The relief feature. */
	@XmlElement(defaultValue="false")
	private Boolean reliefFeature = false;
	
	/** The city furniture. */
	@XmlElement(defaultValue="false")
	private Boolean cityFurniture = false;
	
	/** The generic city object. */
	@XmlElement(defaultValue="false")
	private Boolean genericCityObject = false;
	
	/** The city object group. */
	@XmlElement(defaultValue="false")
	private Boolean cityObjectGroup = false;
	
	/** The tunnel. */
	@XmlElement(defaultValue="false")
	private Boolean tunnel = false;
	
	/** The bridge. */
	@XmlElement(defaultValue="false")
	private Boolean bridge = false;

	/**
	 * Instantiates a new feature class.
	 */
	public FeatureClass() {
	}

	/**
	 * Checks if is sets the building.
	 *
	 * @return true, if is sets the building
	 */
	public boolean isSetBuilding() {
		if (building != null)
			return building.booleanValue();

		return false;
	}

	/**
	 * Gets the building.
	 *
	 * @return the building
	 */
	public Boolean getBuilding() {
		return building;
	}

	/**
	 * Sets the building.
	 *
	 * @param building the new building
	 */
	public void setBuilding(Boolean building) {
		this.building = building;
	}

	/**
	 * Checks if is sets the water body.
	 *
	 * @return true, if is sets the water body
	 */
	public boolean isSetWaterBody() {
		if (waterBody != null)
			return waterBody.booleanValue();

		return false;
	}

	/**
	 * Gets the water body.
	 *
	 * @return the water body
	 */
	public Boolean getWaterBody() {
		return waterBody;
	}

	/**
	 * Sets the water body.
	 *
	 * @param waterBody the new water body
	 */
	public void setWaterBody(Boolean waterBody) {
		this.waterBody = waterBody;
	}

	/**
	 * Checks if is sets the land use.
	 *
	 * @return true, if is sets the land use
	 */
	public boolean isSetLandUse() {
		if (landUse != null)
			return landUse.booleanValue();

		return false;
	}

	/**
	 * Gets the land use.
	 *
	 * @return the land use
	 */
	public Boolean getLandUse() {
		return landUse;
	}

	/**
	 * Sets the land use.
	 *
	 * @param landUse the new land use
	 */
	public void setLandUse(Boolean landUse) {
		this.landUse = landUse;
	}

	/**
	 * Checks if is sets the vegetation.
	 *
	 * @return true, if is sets the vegetation
	 */
	public boolean isSetVegetation() {
		if (vegetation != null)
			return vegetation.booleanValue();

		return false;
	}

	/**
	 * Gets the vegetation.
	 *
	 * @return the vegetation
	 */
	public Boolean getVegetation() {
		return vegetation;
	}

	/**
	 * Sets the vegetation.
	 *
	 * @param vegetation the new vegetation
	 */
	public void setVegetation(Boolean vegetation) {
		this.vegetation = vegetation;
	}

	/**
	 * Checks if is sets the transportation.
	 *
	 * @return true, if is sets the transportation
	 */
	public boolean isSetTransportation() {
		if (transportation != null)
			return transportation.booleanValue();

		return false;
	}

	/**
	 * Gets the transportation.
	 *
	 * @return the transportation
	 */
	public Boolean getTransportation() {
		return transportation;
	}

	/**
	 * Sets the transportation.
	 *
	 * @param transportation the new transportation
	 */
	public void setTransportation(Boolean transportation) {
		this.transportation = transportation;
	}

	/**
	 * Checks if is sets the relief feature.
	 *
	 * @return true, if is sets the relief feature
	 */
	public boolean isSetReliefFeature() {
		if (reliefFeature != null)
			return reliefFeature.booleanValue();

		return false;
	}

	/**
	 * Gets the relief feature.
	 *
	 * @return the relief feature
	 */
	public Boolean getReliefFeature() {
		return reliefFeature;
	}

	/**
	 * Sets the relief feature.
	 *
	 * @param reliefFeature the new relief feature
	 */
	public void setReliefFeature(Boolean reliefFeature) {
		this.reliefFeature = reliefFeature;
	}

	/**
	 * Checks if is sets the city furniture.
	 *
	 * @return true, if is sets the city furniture
	 */
	public boolean isSetCityFurniture() {
		if (cityFurniture != null)
			return cityFurniture.booleanValue();

		return false;
	}

	/**
	 * Gets the city furniture.
	 *
	 * @return the city furniture
	 */
	public Boolean getCityFurniture() {
		return cityFurniture;
	}

	/**
	 * Sets the city furniture.
	 *
	 * @param cityFurniture the new city furniture
	 */
	public void setCityFurniture(Boolean cityFurniture) {
		this.cityFurniture = cityFurniture;
	}

	/**
	 * Checks if is sets the generic city object.
	 *
	 * @return true, if is sets the generic city object
	 */
	public boolean isSetGenericCityObject() {
		if (genericCityObject != null)
			return genericCityObject.booleanValue();

		return false;
	}

	/**
	 * Gets the generic city object.
	 *
	 * @return the generic city object
	 */
	public Boolean getGenericCityObject() {
		return genericCityObject;
	}

	/**
	 * Sets the generic city object.
	 *
	 * @param genericCityObject the new generic city object
	 */
	public void setGenericCityObject(Boolean genericCityObject) {
		this.genericCityObject = genericCityObject;
	}

	/**
	 * Checks if is sets the city object group.
	 *
	 * @return true, if is sets the city object group
	 */
	public boolean isSetCityObjectGroup() {
		if (cityObjectGroup != null)
			return cityObjectGroup.booleanValue();

		return false;
	}

	/**
	 * Gets the city object group.
	 *
	 * @return the city object group
	 */
	public Boolean getCityObjectGroup() {
		return cityObjectGroup;
	}

	/**
	 * Sets the city object group.
	 *
	 * @param cityObjectGroup the new city object group
	 */
	public void setCityObjectGroup(Boolean cityObjectGroup) {
		this.cityObjectGroup = cityObjectGroup;
	}

	/**
	 * Checks if is sets the tunnel.
	 *
	 * @return true, if is sets the tunnel
	 */
	public boolean isSetTunnel() {
		if (tunnel != null)
			return tunnel.booleanValue();
		return false;
	}

	/**
	 * Gets the tunnel.
	 *
	 * @return the tunnel
	 */
	public Boolean getTunnel() {
		return tunnel;
	}

	/**
	 * Sets the tunnel.
	 *
	 * @param tunnel the new tunnel
	 */
	public void setTunnel(Boolean tunnel) {
		this.tunnel = tunnel;
	}
	
	/**
	 * Checks if is sets the bridge.
	 *
	 * @return true, if is sets the bridge
	 */
	public boolean isSetBridge() {
		if (bridge != null)
			return bridge.booleanValue();
		return false;
	}

	/**
	 * Gets the bridge.
	 *
	 * @return the bridge
	 */
	public Boolean getBridge() {
		return bridge;
	}

	/**
	 * Sets the bridge.
	 *
	 * @param bridge the new bridge
	 */
	public void setBridge(Boolean bridge) {
		this.bridge = bridge;
	}
}
