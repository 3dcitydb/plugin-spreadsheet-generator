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
package org.citydb.plugins.spreadsheet_gen.controller;

import java.io.File;
import java.io.FileOutputStream;

import org.citydb.plugins.spreadsheet_gen.gui.datatype.CSVColumns;
import org.citydb.plugins.spreadsheet_gen.gui.view.components.TableDataModel;
import org.citydb.plugins.spreadsheet_gen.util.Util;

import org.citydb.api.controller.LogController;
import org.citydb.api.registry.ObjectRegistry;

public class TemplateWriter implements Runnable {
	private String path;
	private TableDataModel tableDataModel;
	private String separatorPhrase;
	private final LogController logController;
	
	public TemplateWriter(String path, TableDataModel tableDataModel){
		this.path=path;
		this.tableDataModel=tableDataModel;
		this.separatorPhrase=System.getProperty("line.separator");
		logController = ObjectRegistry.getInstance().getLogController();
	}

	@Override
	public void run() {
				File output= new File(path);
				if (!output.exists() &&output.getParent()!=null)
					output.getParentFile().mkdirs();
				int count = tableDataModel.getRowCount();
				CSVColumns column;
				StringBuffer sb = new StringBuffer();
				
				for (int i=0;i<count;i++){
					column= tableDataModel.getCSVColumn(i);
					if (column.comment!=null && column.comment.trim().length()>0){
						if (i>0)sb.append(separatorPhrase);
						sb.append("// ");
						sb.append(column.comment.replaceAll(separatorPhrase, separatorPhrase+"//"));
						sb.append(separatorPhrase);
					}else if (i>0) sb.append(separatorPhrase);
					
					sb.append(column.title);
					sb.append(":");
					sb.append(column.textcontent);		
				}
				try{
					FileOutputStream fos = new FileOutputStream(output);
					fos.write(sb.toString().getBytes("UTF-8"));
					fos.flush();
					fos.close();
					logController.info(Util.I18N.getString("spshg.message.save.template.success")+System.getProperty("line.separator")+path);
				}catch(Exception e){
					logController.error(Util.I18N.getString("spshg.message.save.template.failed")+e.getMessage());
				}
	}

}
