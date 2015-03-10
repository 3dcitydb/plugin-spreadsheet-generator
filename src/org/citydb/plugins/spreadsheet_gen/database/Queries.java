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
package org.citydb.plugins.spreadsheet_gen.database;

import org.citydb.api.database.DatabaseType;

public class Queries {
	// not in used.
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
