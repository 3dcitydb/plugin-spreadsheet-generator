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
package org.citydb.plugins.spreadsheet_gen.controller.cloudservice;

import javax.swing.JDialog;
import javax.swing.JFrame;

// TODO: Auto-generated Javadoc
/**
 * The Interface CloudService.
 */
public interface CloudService {
	//public abstract Object getService();
	
	/**
	 * Upload file.
	 *
	 * @param filepath the filepath
	 * @param title the title
	 * @return the string
	 * @throws Exception the exception
	 */
	public abstract String uploadFile(String filepath,String title)throws Exception;
	
	/**
	 * After upload.
	 *
	 * @throws Exception the exception
	 */
	public abstract void afterUpload()throws Exception;
	
	/**
	 * Sets the user credentials.
	 *
	 * @param userName the user name
	 * @param password the password
	 * @throws AuthenticationException the authentication exception
	 * @throws CaptchaRequiredException the captcha required exception
	 */
	public abstract void setUserCredentials(String userName, String password)throws AuthenticationException,CaptchaRequiredException;
	
	/**
	 * Sets the user credentials.
	 *
	 * @param userName the user name
	 * @param password the password
	 * @param captchaToken the captcha token
	 * @param answer the answer
	 * @throws AuthenticationException the authentication exception
	 * @throws CaptchaRequiredException the captcha required exception
	 */
	public abstract void setUserCredentials(String userName, String password, String captchaToken, String answer)throws AuthenticationException,CaptchaRequiredException;
	
	/**
	 * Gets the service name.
	 *
	 * @return the service name
	 */
	public abstract String getServiceName();
	
	/**
	 * Gets the service description.
	 *
	 * @return the service description
	 */
	public abstract String getServiceDescription();
	
	/**
	 * Checks if is policy changeable.
	 *
	 * @return true, if is policy changeable
	 */
	// just for Google!?
	public abstract boolean isPolicyChangeable();
	
	/**
	 * Gets the policy editor UI.
	 *
	 * @param mainFrame the main frame
	 * @return the policy editor UI
	 */
	public abstract JDialog getPolicyEditorUI(JFrame mainFrame);
	
	/**
	 * Format text.
	 *
	 * @param in the in
	 * @return the string
	 */
	public  abstract String formatText(String in);
	
}
