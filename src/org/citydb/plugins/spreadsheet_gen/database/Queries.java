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
package org.citydb.plugins.spreadsheet_gen.database;

import de.tub.citydb.api.database.DatabaseType;

public class Queries {
	// not in used.
    public static final String QUERY_GET_GMLIDS =
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
	}
}
