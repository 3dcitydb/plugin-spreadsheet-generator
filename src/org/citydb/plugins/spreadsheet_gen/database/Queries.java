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
package org.citydb.plugins.spreadsheet_gen.database;

import org.citydb.api.database.DatabaseType;

// TODO: Auto-generated Javadoc
/**
 * The Class Queries.
 */
public class Queries {
	// not in used.
/**
	 * Gets the ids.
	 *
	 * @param type the type
	 * @return the string
	 */
	/*    public static final String QUERY_GET_GMLIDS =
		"SELECT co.gmlid " +
		"FROM CITYOBJECT co " +
		"WHERE " +
		  "SDO_RELATE(co.envelope, MDSYS.SDO_GEOMETRY(2003, ?, null, MDSYS.SDO_ELEM_INFO_ARRAY(1,1003,3), " +
					  "MDSYS.SDO_ORDINATE_ARRAY(?,?,?,?)), 'mask=ANYINTERACT') ='TRUE' "+ 
		"ORDER BY co.gmlid";
    // not in used.
    public static final String QUERY_GET_NUM_GMLIDS =
		"SELECT count(co.gmlid) " +
		"FROM CITYOBJECT co " +
		"WHERE " +
		  "SDO_RELATE(co.envelope, MDSYS.SDO_GEOMETRY(2003, ?, null, MDSYS.SDO_ELEM_INFO_ARRAY(1,1003,3), " +
					  "MDSYS.SDO_ORDINATE_ARRAY(?,?,?,?)), 'mask=ANYINTERACT') ='TRUE' ";
    
    public static final String QUERY_GET_GMLIDS_2 =
    		"SELECT co.gmlid, co.class_id " +
    		"FROM CITYOBJECT co " +
    		"WHERE " +
    		  "SDO_RELATE(co.envelope, MDSYS.SDO_GEOMETRY(2003, ?, null, MDSYS.SDO_ELEM_INFO_ARRAY(1,1003,3), " +
    					  "MDSYS.SDO_ORDINATE_ARRAY(?,?,?,?)), 'mask=ANYINTERACT') ='TRUE' " ;
    
	public static final String QUERY_GET_GMLIDS_3(DatabaseType type) {
		String query = "SELECT co.gmlid, co.objectclass_id FROM CITYOBJECT co WHERE ";

		switch (type) {
		case ORACLE:
			query += "(SDO_RELATE(co.envelope, ?, 'mask=ANYINTERACT') = 'TRUE') ";
			break;
		case POSTGIS:
			query += "ST_Intersects(co.envelope, ?) = 'TRUE' ";
			break;
		}
		return query;
	}*/
	public static final String GET_IDS(DatabaseType type) {
		StringBuilder query = new StringBuilder()
		.append("SELECT co.id, co.gmlid, co.objectclass_id, co.envelope FROM CITYOBJECT co WHERE ");
		
		switch (type) {
		case ORACLE:
			query.append("SDO_ANYINTERACT(co.envelope, ?) = 'TRUE'");
			break;
		case POSTGIS:
			query.append("co.envelope && ?");
			break;
		}		
		
		return query.toString();
	}
}
