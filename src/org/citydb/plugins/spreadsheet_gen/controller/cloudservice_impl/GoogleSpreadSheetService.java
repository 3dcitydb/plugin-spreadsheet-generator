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
package org.citydb.plugins.spreadsheet_gen.controller.cloudservice_impl;

import java.io.File;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.citydb.plugins.spreadsheet_gen.controller.cloudservice.AuthenticationException;
import org.citydb.plugins.spreadsheet_gen.controller.cloudservice.CaptchaRequiredException;
import org.citydb.plugins.spreadsheet_gen.controller.cloudservice.CloudService;
import org.citydb.plugins.spreadsheet_gen.controller.cloudservice.UploadException;
import org.citydb.plugins.spreadsheet_gen.controller.cloudservice_impl.gui.ShareSettingDialog;
import org.citydb.plugins.spreadsheet_gen.controller.cloudservice_impl.gui.Users;
import org.citydb.plugins.spreadsheet_gen.util.Util;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.acl.AclEntry;
import com.google.gdata.data.acl.AclFeed;
import com.google.gdata.data.acl.AclRole;
import com.google.gdata.data.acl.AclScope;
import com.google.gdata.data.acl.AclWithKey;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.data.docs.RevisionEntry;
import com.google.gdata.data.docs.RevisionFeed;
import com.google.gdata.data.media.MediaFileSource;
import com.google.gdata.util.ServiceException;


import org.citydb.api.registry.ObjectRegistry;

// TODO: Auto-generated Javadoc
/**
 * The Class GoogleSpreadSheetService.
 */
public class GoogleSpreadSheetService implements CloudService {
	
	/** The Constant ROLE_OWNER. */
	public final static int ROLE_OWNER = 1;
	
	/** The Constant ROLE_WRITER. */
	public final static int ROLE_WRITER = 2;
	
	/** The Constant ROLE_READER. */
	public final static int ROLE_READER = 3;

	/** The Constant SCOPE_USER. */
	public final static int SCOPE_USER = 10;
	
	/** The Constant SCOPE_GROUP. */
	public final static int SCOPE_GROUP = 11;
	
	/** The Constant SCOPE_DOMAIN. */
	public final static int SCOPE_DOMAIN = 12;
	
	/** The Constant SCOPE_DEFAULT. */
	public final static int SCOPE_DEFAULT = 13;
	
	/** The Constant SCOPE_DEFAULT_WITH_KEY. */
	public final static int SCOPE_DEFAULT_WITH_KEY = 14;
	
	/** The Constant SCOPE_PRIVATE. */
	public final static int SCOPE_PRIVATE = 15;

	/** The service. */
	private DocsService service;
	
	/** The main entry. */
	private DocumentListEntry mainEntry = null;

	/** The use correct proxy. */
	public static boolean useCorrectProxy=true;
	
	/**
	 * Instantiates a new google spread sheet service.
	 */
	public GoogleSpreadSheetService() {
	}

	/**
	 * Gets the service.
	 *
	 * @return the service
	 */
	private DocsService getService() {
		if (service == null){
			service = new DocsService("3dcitydb-spreadsheetplugin-v1.0");
		}
		return service;
	}

	/* (non-Javadoc)
	 * @see org.citydb.plugins.spreadsheet_gen.controller.cloudservice.CloudService#uploadFile(java.lang.String, java.lang.String)
	 */
	@Override
	public synchronized String uploadFile(String filepath, String title) throws Exception {
		Spreadsheet overwriteOn=null;
		final ArrayList<Spreadsheet> similarSpsh=getSpreadsheetWithSameName(title);
		if (similarSpsh.size()!=0){
			MyRunnable myRunnable= new MyRunnable(similarSpsh);
			SwingUtilities.invokeAndWait(myRunnable);
			overwriteOn=myRunnable.getSelectedOne();
			if (overwriteOn==null)
				throw new Exception(Util.I18N.getString("spshg.message.upload.cancel.user"));
		}
		String url;
		if (similarSpsh.size()==0 || overwriteOn.getType()==Spreadsheet.NEW)
			url= createNewFile(filepath, title);
		else
			url= overrideOnFile(filepath, overwriteOn);
		return url;
	}
		
	/**
	 * Creates the new file.
	 *
	 * @param filepath the filepath
	 * @param title the title
	 * @return the string
	 * @throws Exception the exception
	 */
	private String createNewFile(String filepath, String title)throws Exception {
		File file = new File(filepath);
		DocumentListEntry newDocument = new DocumentListEntry();
		String mimeType = DocumentListEntry.MediaType.fromFileName(
				file.getName()).getMimeType();
		newDocument.setFile(file, mimeType);
		newDocument.setTitle(new PlainTextConstruct(title));
		
		DocumentListEntry dle = getService().insert(
				new URL("https://docs.google.com/feeds/default/private/full/"),
				newDocument);
		mainEntry = dle;
		if (mainEntry != null) {
			return mainEntry.getHtmlLink().getHref();
		}
		return null;
	}
	
	/**
	 * Override on file.
	 *
	 * @param filepath the filepath
	 * @param spreasheet the spreasheet
	 * @return the string
	 * @throws Exception the exception
	 */
	private String overrideOnFile(String filepath, Spreadsheet spreasheet)throws Exception {
		File file = new File(filepath);
		String mimeType = DocumentListEntry.MediaType.fromFileName(
				file.getName()).getMimeType();
		DocumentListEntry entry=spreasheet.getEntry();
		entry.setMediaSource( new MediaFileSource(file,mimeType));
		entry.setEtag("*");
		
		mainEntry = entry.updateMedia(true);
		return mainEntry.getHtmlLink().getHref();
	}
	
	/**
	 * Gets the spreadsheet with same name.
	 *
	 * @param name the name
	 * @return the spreadsheet with same name
	 */
	public ArrayList<Spreadsheet> getSpreadsheetWithSameName(String name){
		ArrayList<Spreadsheet> list= new ArrayList<Spreadsheet>();
		 URL feedUri;
		try {
			feedUri = new URL("https://docs.google.com/feeds/default/private/full/");
			DocumentListFeed feed = getService().getFeed(feedUri, DocumentListFeed.class);

		  for (DocumentListEntry entry : feed.getEntries()) {
			  if (entry.getType().equalsIgnoreCase("spreadsheet"))
				  if (entry.getTitle().getPlainText().equalsIgnoreCase(name)){
					  if (list.size()==0)
						  list.add(new Spreadsheet());
					  list.add(new Spreadsheet(entry));
				  }
			  
		    
		  }
		} catch (Exception e) {
			e.printStackTrace();
		}
		  return list;
	}
	
	/* (non-Javadoc)
	 * @see org.citydb.plugins.spreadsheet_gen.controller.cloudservice.CloudService#afterUpload()
	 */
	@Override
	public void afterUpload() throws Exception {
		publishDocument();
		
	}
	
	/* (non-Javadoc)
	 * @see org.citydb.plugins.spreadsheet_gen.controller.cloudservice.CloudService#setUserCredentials(java.lang.String, java.lang.String)
	 */
	@Override
	public void setUserCredentials(String userName, String password)
			throws AuthenticationException, CaptchaRequiredException {
		try {
			getService().setUserCredentials(userName, password);
		} catch (com.google.gdata.client.GoogleService.CaptchaRequiredException cre) {
			throw new CaptchaRequiredException(cre.getCaptchaToken(),
					cre.getCaptchaUrl());
		} catch (com.google.gdata.util.AuthenticationException e) {
			throw new AuthenticationException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.citydb.plugins.spreadsheet_gen.controller.cloudservice.CloudService#setUserCredentials(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void setUserCredentials(String userName, String password,
			String captchaToken, String answer) throws AuthenticationException,
			CaptchaRequiredException {
		try {
			getService().setUserCredentials(userName, password, captchaToken,
					answer);
		} catch (com.google.gdata.client.GoogleService.CaptchaRequiredException cre) {
			throw new CaptchaRequiredException(cre.getCaptchaToken(),
					cre.getCaptchaUrl());
		} catch (com.google.gdata.util.AuthenticationException e) {
			throw new AuthenticationException(e.getMessage());
		}

	}

	/* (non-Javadoc)
	 * @see org.citydb.plugins.spreadsheet_gen.controller.cloudservice.CloudService#getServiceName()
	 */
	@Override
	public String getServiceName() {
		return "Google Spreadsheet Service";
	}

	/* (non-Javadoc)
	 * @see org.citydb.plugins.spreadsheet_gen.controller.cloudservice.CloudService#getServiceDescription()
	 */
	@Override
	public String getServiceDescription() {
		return "spshg.cls.google.description";
	}

	/* (non-Javadoc)
	 * @see org.citydb.plugins.spreadsheet_gen.controller.cloudservice.CloudService#isPolicyChangeable()
	 */
	@Override
	public boolean isPolicyChangeable() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.citydb.plugins.spreadsheet_gen.controller.cloudservice.CloudService#getPolicyEditorUI(javax.swing.JFrame)
	 */
	@Override
	public JDialog getPolicyEditorUI(JFrame mainFrame) {
		return new ShareSettingDialog(mainFrame);
	}

	/* (non-Javadoc)
	 * @see org.citydb.plugins.spreadsheet_gen.controller.cloudservice.CloudService#formatText(java.lang.String)
	 */
	@Override
	public String formatText(String in) {
		if (in.matches("\\s*\\d+\\Qe\\E\\d+\\s*"))
			return "=TO_TEXT(\"\"" + in + "\"\")";
		return in;
	}

	/**
	 * Share document.
	 *
	 * @param role the role
	 * @param scopeType the scope type
	 * @param contact the contact
	 * @return the acl entry
	 * @throws Exception the exception
	 */
	public synchronized AclEntry shareDocument(int role, int scopeType, String contact)
			throws Exception {
		if (mainEntry == null)
			throw new Exception();

		AclRole aclRole = null;
		switch (role) {
		case ROLE_OWNER:
			aclRole = AclRole.OWNER;
			break;
		case ROLE_READER:
			aclRole = AclRole.READER;
			break;
		case ROLE_WRITER:
			aclRole = AclRole.WRITER;
			break;

		}
		AclScope scope = null;
		
		switch (scopeType) {
		case SCOPE_DEFAULT:
			scope = new AclScope(AclScope.Type.DEFAULT, null);
			break;
		case SCOPE_USER:
			scope = new AclScope(AclScope.Type.USER, contact);
			break;
		case SCOPE_GROUP:
			scope = new AclScope(AclScope.Type.GROUP, contact);
			break;
		case SCOPE_DOMAIN:
			scope = new AclScope(AclScope.Type.DOMAIN, contact);
			break;
		default:
			throw new Exception("Scope Type is not correctly selected.");
		}
		return addAclRole(aclRole, scope, mainEntry);

	}

	/**
	 * Publish document.
	 *
	 * @throws UploadException the upload exception
	 */
	public synchronized void publishDocument() throws UploadException{
		try{
			RevisionFeed revisionFeed = getRevisionFeed();
			for (RevisionEntry rentry : revisionFeed.getEntries()) {
				rentry.setPublishAuto(true);
				rentry.setPublish(true);
				rentry=rentry.update();
			}
		} catch(Exception e){
			throw new UploadException(UploadException.PUBLISH_ERROR ,
					Util.I18N.getString("spshg.message.error.publish")+
					e.getLocalizedMessage());
		}		
	}

	/**
	 * Update acl list.
	 *
	 * @return the list
	 * @throws MalformedURLException the malformed URL exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ServiceException the service exception
	 */
	public synchronized List<AclEntry> updateAclList() throws MalformedURLException, IOException, ServiceException{
		AclFeed aclFeed = getService().getFeed(new URL(mainEntry.getAclFeedLink().getHref()), AclFeed.class);
		return aclFeed.getEntries();
	}
	
	/**
	 * Update permission.
	 *
	 * @param oldPermission the old permission
	 * @param user the user
	 * @return the acl entry
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ServiceException the service exception
	 */
	public AclEntry updatePermission(AclEntry oldPermission, Users user) throws IOException, ServiceException{
		if (user.isVisibilityDescription()){ // general permission
				AclEntry aclEntry = new AclEntry();
				aclEntry.setScope(new AclScope(toGoogleScope(user.getScope()),null));
				if (user.getScope()!= SCOPE_DEFAULT_WITH_KEY)
					aclEntry.setRole(toGoogleRole(user.getPermissionType()));
				else
					aclEntry.setWithKey(new AclWithKey("with link", toGoogleRole(user.getPermissionType())));
				
				aclEntry.setPublished(DateTime.now());
				return getService().insert(new URL(mainEntry.getAclFeedLink().getHref()),
						aclEntry);
		}
		// user's permission update
		oldPermission.setRole(toGoogleRole(user.getPermissionType()));
		return oldPermission.update(); 
	}
	

	
	
	/**
	 * Adds the acl role.
	 *
	 * @param role the role
	 * @param scope the scope
	 * @param entry the entry
	 * @return the acl entry
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws MalformedURLException the malformed URL exception
	 * @throws ServiceException the service exception
	 */
	private synchronized AclEntry addAclRole(AclRole role, AclScope scope,
			DocumentListEntry entry) throws IOException, MalformedURLException,
			ServiceException {
		AclEntry aclEntry = new AclEntry();
		aclEntry.setRole(role);
		aclEntry.setScope(scope);
		aclEntry.setPublished(DateTime.now());
		return getService().insert(new URL(entry.getAclFeedLink().getHref()),
				aclEntry);
	}

	/**
	 * Gets the revision feed.
	 *
	 * @return the revision feed
	 * @throws Exception the exception
	 */
	private RevisionFeed getRevisionFeed() throws Exception {
		if (mainEntry == null)
			throw new Exception();
		
		URL url = new URL(mainEntry.getSelfLink().getHref() + "/revisions");
		RevisionFeed rf = getService().getFeed(url, RevisionFeed.class);
		return rf;
	}
	
	/**
	 * To local code.
	 *
	 * @param aclscopeType the aclscope type
	 * @return the int
	 */
	public static int toLocalCode(AclScope.Type aclscopeType){
		if (aclscopeType.equals(AclScope.Type.DEFAULT))
			return SCOPE_DEFAULT;
		if (aclscopeType.equals(AclScope.Type.USER))
			return SCOPE_USER;
		if (aclscopeType.equals(AclScope.Type.GROUP))
			return SCOPE_GROUP;
		if (aclscopeType.equals(AclScope.Type.DOMAIN))
			return SCOPE_DOMAIN;
		return 0;
	}
	
	/**
	 * To local code.
	 *
	 * @param aclRole the acl role
	 * @return the int
	 */
	public static int toLocalCode(AclRole aclRole){
		if (aclRole.equals(AclRole.OWNER))
			return ROLE_OWNER;
		if (aclRole.equals(AclRole.READER))
			return ROLE_READER;
		if (aclRole.equals(AclRole.WRITER))
			return ROLE_WRITER;
		return 0;
	}
	
	/**
	 * To google scope.
	 *
	 * @param scope the scope
	 * @return the acl scope. type
	 */
	public static AclScope.Type toGoogleScope(int scope){
		switch(scope){
			case SCOPE_DEFAULT: return AclScope.Type.DEFAULT;
			case SCOPE_USER: 	return AclScope.Type.USER;
			case SCOPE_GROUP: 	return AclScope.Type.GROUP;
			case SCOPE_DOMAIN: 	return AclScope.Type.DOMAIN;
			case SCOPE_DEFAULT_WITH_KEY: return AclScope.Type.DEFAULT;
		}
		return null;
	}
	
	/**
	 * To google role.
	 *
	 * @param role the role
	 * @return the acl role
	 */
	public static AclRole toGoogleRole(int role){
		switch(role){
			case ROLE_OWNER: 	return AclRole.OWNER;
			case ROLE_READER: 	return AclRole.READER;
			case ROLE_WRITER: 	return AclRole.WRITER;
		}
		return null;
	}


}

class MyRunnable implements Runnable {
	Spreadsheet overwriteOn=null;
	ArrayList<Spreadsheet> similarSpsh;
	MyRunnable(ArrayList<Spreadsheet> similarSpsh){
		this.similarSpsh=similarSpsh;
	}
	@Override
	public void run() {
		overwriteOn = (Spreadsheet)JOptionPane.showInputDialog(
				ObjectRegistry.getInstance().getViewController().getTopFrame(),
				Util.I18N.getString("spshg.message.overwrite.on.message"),
				Util.I18N.getString("spshg.message.overwrite.on.title"),
                JOptionPane.QUESTION_MESSAGE,
                null,
                similarSpsh.toArray(),
                similarSpsh.get(0));
	}
	public Spreadsheet getSelectedOne(){
		return overwriteOn;
	}
};
