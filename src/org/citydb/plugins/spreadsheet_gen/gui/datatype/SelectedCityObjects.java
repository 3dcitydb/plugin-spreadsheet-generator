/*
 * 3D City Database - The Open Source CityGML Database
 * http://www.3dcitydb.org/
 * 
 * Copyright 2013 - 2016
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
package org.citydb.plugins.spreadsheet_gen.gui.datatype;

import java.util.HashSet;
import java.util.LinkedHashMap;

import org.citydb.plugins.spreadsheet_gen.config.ConfigImpl;
import org.citydb.plugins.spreadsheet_gen.config.FeatureClass;
import org.citygml4j.model.citygml.CityGMLClass;


public class SelectedCityObjects {
	private  final int Cityobject=1;
	private  final int Building=2;
	private  final int WaterBody=3;
	private  final int LandUse=4;
	private  final int Vegetation=5;
	private  final int Transportation=6;
	private  final int ReliefFeature=7;
	private  final int CityFurniture=8;
	private  final int GenericCityObject=9;
	private  final int CityObjectGroup=10;		
	private  final int Tunnel=11;
	private  final int Bridge=12;
	
	private HashSet<CityGMLClass> desireCityObjects= new HashSet<CityGMLClass>();
	private LinkedHashMap<String, Integer> allitems= new LinkedHashMap<String, Integer>();
	private static final SelectedCityObjects instance= new SelectedCityObjects();
	private FeatureClass fc;
	SelectedCityObjects(){
		allitems.put(getName(Building), new Integer(this.Building));
		allitems.put(getName(WaterBody), new Integer(this.WaterBody));
		allitems.put(getName(LandUse), new Integer(this.LandUse));
		allitems.put(getName(Vegetation), new Integer(this.Vegetation));
		allitems.put(getName(Transportation), new Integer(this.Transportation));
		allitems.put(getName(ReliefFeature),new Integer(this.ReliefFeature));
		allitems.put(getName(CityFurniture), new Integer(this.CityFurniture));
		allitems.put(getName(GenericCityObject), new Integer(this.GenericCityObject));
		allitems.put(getName(CityObjectGroup), new Integer(this.CityObjectGroup));
		allitems.put(getName(Tunnel), new Integer(this.Tunnel));
		allitems.put(getName(Bridge), new Integer(this.Bridge));		
	}
	
	
	public static SelectedCityObjects getInstance(){
		return instance;
	}
	
	public void initialize(ConfigImpl config){
		fc= config.getSelectedcityobjects();
		if (fc.isSetBuilding()) selectCityObject(Building); else removeCityObject(Building);
		if (fc.isSetWaterBody()) selectCityObject(WaterBody); else removeCityObject(WaterBody);
		if (fc.isSetLandUse()) selectCityObject(LandUse); else removeCityObject(LandUse);
		if (fc.isSetVegetation()) selectCityObject(Vegetation); else removeCityObject(Vegetation);
		if (fc.isSetTransportation()) selectCityObject(Transportation); else removeCityObject(Transportation);
		if (fc.isSetReliefFeature()) selectCityObject(ReliefFeature); else removeCityObject(ReliefFeature);
		if (fc.isSetCityFurniture()) selectCityObject(CityFurniture); else removeCityObject(CityFurniture);
		if (fc.isSetGenericCityObject()) selectCityObject(GenericCityObject); else removeCityObject(GenericCityObject);
		if (fc.isSetBuilding()) selectCityObject(Building); else removeCityObject(Building);
		if (fc.isSetTunnel()) selectCityObject(Tunnel); else removeCityObject(Tunnel);
		if (fc.isSetBridge()) selectCityObject(Bridge); else removeCityObject(Bridge);
	}
	
	public HashSet<CityGMLClass> getSelectedCityObjects(){
		return desireCityObjects;
	} 
	
	public String getSelectedObjectsString(){
		StringBuffer sb= new StringBuffer();
		boolean isfirst=true;
		for (String name:allitems.keySet()){
			if (isCityObjectSelected(allitems.get(name))){
				if (!isfirst){
					sb.append(", ");
				}else 
					isfirst=false;
				sb.append(name.trim());
			}
		}
		
		return sb.toString();
	}
	
	public void selectCityObject(Integer code){
		selectCityObject(code.intValue());
		saveSetting(code.intValue(),true);
	}
	
	public void removeCityObject(Integer code){
		removeCityObject(code.intValue());
		saveSetting(code.intValue(),false);
	}
	
	public String getRoot(){
		return getName(Cityobject);
	}
	
	public int getRootID(){
		return Cityobject;
	}
	
	public LinkedHashMap<String, Integer> getChilds(){
		return allitems;
	}
	
	public boolean isCityObjectSelected(Integer code){
		switch (code) {
		case Cityobject:
			return fc.isSetBuilding() && 
			fc.isSetCityFurniture()&&
			fc.isSetLandUse()&&
			fc.isSetWaterBody()&&
			fc.isSetVegetation()&&
			fc.isSetTransportation()&&
			fc.isSetReliefFeature()&&
			fc.isSetGenericCityObject()&&
			fc.isSetCityObjectGroup()&&
			fc.isSetTunnel()&&
			fc.isSetBridge();
		case Building:
			return fc.isSetBuilding();
		case CityFurniture:
			return fc.isSetCityFurniture();
		case LandUse:
			return fc.isSetLandUse();
		case WaterBody:
			return fc.isSetWaterBody();
		case Vegetation:
			return fc.isSetVegetation();
		case Transportation:
			return fc.isSetTransportation();
		case ReliefFeature:
			return fc.isSetReliefFeature();
		case GenericCityObject:
			return fc.isSetGenericCityObject();
		case CityObjectGroup:
			return fc.isSetCityObjectGroup();
		case Tunnel:
			return fc.isSetTunnel();
		case Bridge:
			return fc.isSetBridge();
		}
		return false;
	}
	
	private void removeCityObject(int code){
		switch (code) {
		case Cityobject:
			desireCityObjects.remove(CityGMLClass.BUILDING);
			desireCityObjects.remove(CityGMLClass.CITY_FURNITURE);
			desireCityObjects.remove(CityGMLClass.LAND_USE);
			desireCityObjects.remove(CityGMLClass.WATER_BODY);
			desireCityObjects.remove(CityGMLClass.PLANT_COVER);
			desireCityObjects.remove(CityGMLClass.SOLITARY_VEGETATION_OBJECT);
			desireCityObjects.remove(CityGMLClass.TRANSPORTATION_COMPLEX);
			desireCityObjects.remove(CityGMLClass.ROAD);
			desireCityObjects.remove(CityGMLClass.RAILWAY);
			desireCityObjects.remove(CityGMLClass.TRACK);
			desireCityObjects.remove(CityGMLClass.SQUARE);
			desireCityObjects.remove(CityGMLClass.RELIEF_FEATURE);
			desireCityObjects.remove(CityGMLClass.GENERIC_CITY_OBJECT);
			desireCityObjects.remove(CityGMLClass.CITY_OBJECT_GROUP);
			desireCityObjects.remove(CityGMLClass.TUNNEL);
			desireCityObjects.remove(CityGMLClass.BRIDGE);
			return;
		case Building:
			desireCityObjects.remove(CityGMLClass.BUILDING);
			return;
		case CityFurniture:
			desireCityObjects.remove(CityGMLClass.CITY_FURNITURE);
			return;
		case LandUse:
			desireCityObjects.remove(CityGMLClass.LAND_USE);
			return ;
		case WaterBody:
			desireCityObjects.remove(CityGMLClass.WATER_BODY);
			return ;
		case Vegetation:
			desireCityObjects.remove(CityGMLClass.PLANT_COVER);
			desireCityObjects.remove(CityGMLClass.SOLITARY_VEGETATION_OBJECT);
			return ;
		case Transportation:
			desireCityObjects.remove(CityGMLClass.TRANSPORTATION_COMPLEX);
			desireCityObjects.remove(CityGMLClass.ROAD);
			desireCityObjects.remove(CityGMLClass.RAILWAY);
			desireCityObjects.remove(CityGMLClass.TRACK);
			desireCityObjects.remove(CityGMLClass.SQUARE);
			
			return;
		case ReliefFeature:
			desireCityObjects.remove(CityGMLClass.RELIEF_FEATURE);
			return ;
		case GenericCityObject:
			desireCityObjects.remove(CityGMLClass.GENERIC_CITY_OBJECT);
			return ;
		case CityObjectGroup:
			desireCityObjects.remove(CityGMLClass.CITY_OBJECT_GROUP);
			return ;
		case Tunnel:
			desireCityObjects.remove(CityGMLClass.TUNNEL);
			return ;
		case Bridge:
			desireCityObjects.remove(CityGMLClass.BRIDGE);
			return ;
		}
	}
	
	private void saveSetting(int code, boolean isselected){
		switch (code) {
		case Cityobject:
			fc.setBuilding(isselected);
			fc.setCityFurniture(isselected);
			fc.setLandUse(isselected);
			fc.setWaterBody(isselected);
			fc.setVegetation(isselected);
			fc.setTransportation(isselected);
			fc.setReliefFeature(isselected);
			fc.setGenericCityObject(isselected);
			fc.setCityObjectGroup(isselected);
			fc.setTunnel(isselected);
			fc.setBridge(isselected);
			return;
		case Building:
			fc.setBuilding(isselected);
			return;
		case CityFurniture:
			fc.setCityFurniture(isselected);
			return;
		case LandUse:
			fc.setLandUse(isselected);
			return ;
		case WaterBody:
			fc.setWaterBody(isselected);
			return ;
		case Vegetation:
			fc.setVegetation(isselected);
			return ;
		case Transportation:
			fc.setTransportation(isselected);
			return;
		case ReliefFeature:
			fc.setReliefFeature(isselected);
			return ;
		case GenericCityObject:
			fc.setGenericCityObject(isselected);
			return ;
		case CityObjectGroup:
			fc.setCityObjectGroup(isselected);
			return ;
		case Tunnel:
			fc.setTunnel(isselected);
			return ;
		case Bridge:
			fc.setBridge(isselected);
			return ;
		}
	}
	
	public String getName(int code){
		String space="    ";
		switch (code) {
		case Cityobject:return "City Object";
		case Building: return space+"Building";
		case CityFurniture:return space+"City Furniture";
		case LandUse: return space+"Land Use";
		case WaterBody:return space+"Water Body";
		case Vegetation: return space+"Vegetation";
		case Transportation: return space+"Transportation";
		case ReliefFeature:return space+"Relief Feature";
		case GenericCityObject:return space+"Generic City Object";
		case CityObjectGroup:return space+"City Object Group";
		case Tunnel:return space+"Tunnel";
		case Bridge:return space+"Bridge";
		}
		return "";
	}
	
	private void selectCityObject(int code){
		switch (code) {
		case Cityobject:
			desireCityObjects.add(CityGMLClass.BUILDING);
			desireCityObjects.add(CityGMLClass.CITY_FURNITURE);
			desireCityObjects.add(CityGMLClass.LAND_USE);
			desireCityObjects.add(CityGMLClass.WATER_BODY);
			desireCityObjects.add(CityGMLClass.PLANT_COVER);
			desireCityObjects.add(CityGMLClass.SOLITARY_VEGETATION_OBJECT);
			desireCityObjects.add(CityGMLClass.TRANSPORTATION_COMPLEX);
			desireCityObjects.add(CityGMLClass.ROAD);
			desireCityObjects.add(CityGMLClass.RAILWAY);
			desireCityObjects.add(CityGMLClass.TRACK);
			desireCityObjects.add(CityGMLClass.SQUARE);
			desireCityObjects.add(CityGMLClass.RELIEF_FEATURE);
			desireCityObjects.add(CityGMLClass.GENERIC_CITY_OBJECT);
			desireCityObjects.add(CityGMLClass.CITY_OBJECT_GROUP);
			desireCityObjects.add(CityGMLClass.TUNNEL);
			desireCityObjects.add(CityGMLClass.BRIDGE);
			return;
		case Building:
			desireCityObjects.add(CityGMLClass.BUILDING);
			return;
		case CityFurniture:
			desireCityObjects.add(CityGMLClass.CITY_FURNITURE);
			return;
		case LandUse:
			desireCityObjects.add(CityGMLClass.LAND_USE);
			return ;
		case WaterBody:
			desireCityObjects.add(CityGMLClass.WATER_BODY);
			return ;
		case Vegetation:
			desireCityObjects.add(CityGMLClass.PLANT_COVER);
			desireCityObjects.add(CityGMLClass.SOLITARY_VEGETATION_OBJECT);
			return ;
		case Transportation:
			desireCityObjects.add(CityGMLClass.TRANSPORTATION_COMPLEX);
			desireCityObjects.add(CityGMLClass.ROAD);
			desireCityObjects.add(CityGMLClass.RAILWAY);
			desireCityObjects.add(CityGMLClass.TRACK);
			desireCityObjects.add(CityGMLClass.SQUARE);
			return;
		case ReliefFeature:
			desireCityObjects.add(CityGMLClass.RELIEF_FEATURE);
			return ;
		case GenericCityObject:
			desireCityObjects.add(CityGMLClass.GENERIC_CITY_OBJECT);
			return ;
		case CityObjectGroup:
			desireCityObjects.add(CityGMLClass.CITY_OBJECT_GROUP);
			return ;
		case Tunnel:
			desireCityObjects.add(CityGMLClass.TUNNEL);
			return ;
		case Bridge:
			desireCityObjects.add(CityGMLClass.BRIDGE);
			return ;	
		}		
	}
	
	public String getCityObjectName(CityGMLClass c ){
		if (CityGMLClass.BUILDING == c){
			return getName(Building);
		}
		if (CityGMLClass.CITY_FURNITURE == c){
			return getName(CityFurniture);
		}
		if (CityGMLClass.LAND_USE == c){
			return getName(LandUse);
		}
		if (CityGMLClass.CITY_OBJECT_GROUP == c)
			return getName(CityObjectGroup);
		
		if (CityGMLClass.GENERIC_CITY_OBJECT == c)
			return getName(GenericCityObject);
		
		if (CityGMLClass.RELIEF_FEATURE == c){
			return getName(ReliefFeature);
		}
		if (CityGMLClass.TRANSPORTATION_COMPLEX == c||
				c==CityGMLClass.ROAD||
				c==CityGMLClass.RAILWAY||
				c==CityGMLClass.TRACK||
				c==CityGMLClass.SQUARE){
			return getName(Transportation);
		}
		if (CityGMLClass.PLANT_COVER == c||
				c== CityGMLClass.SOLITARY_VEGETATION_OBJECT){
			return getName(Vegetation);
		}
		if (CityGMLClass.WATER_BODY == c){
			return getName(WaterBody);
		}
		if (CityGMLClass.TUNNEL == c){
			return getName(Tunnel);
		}
		if (CityGMLClass.BRIDGE == c){
			return getName(Bridge);
		}
		return "";

	}
}
