/*
 * Licensed to the Sakai Foundation under one or more contributor
 * license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership.
 * The Sakai Foundation licenses this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package net.unicon.sakora.impl;

import java.io.File;
import java.io.FilenameFilter;

import net.unicon.sakora.api.handler.url.URLHandlingTemplate;
import net.unicon.sakora.api.handler.xml.XMLStreamHandlingCallback;
import net.unicon.sakora.api.ws.ImsEsSyncService;
import net.unicon.sakora.api.ws.ImsSyncContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.api.ServerConfigurationService;

public class ImsEsSyncServiceFileImpl implements ImsEsSyncService {
	private static final Log log = LogFactory.getLog(ImsEsSyncServiceFileImpl.class);
	
	private static final String DEFAULT_DATA_FOLDER = "data";
	/** 
	 * When a response is received from the web service call it contains a URL to an XML doucment with the information we seek.
	 * This class will handle the URL and retrieve the documment and send it onto the XMLStreamHandlingCallback for processing.
	 */		
	private URLHandlingTemplate urlHandlingTemplate;	

	public XMLStreamHandlingCallback getXmlStreamHandlingCallback() {
		return xmlStreamHandlingCallback;
	}

	public void setXmlStreamHandlingCallback(
			XMLStreamHandlingCallback xmlStreamHandlingCallback) {
		this.xmlStreamHandlingCallback = xmlStreamHandlingCallback;
	}	

	/** 
	 * Processes the XML stream retrieved by URLHandlingTemplate.
	 */		
	private XMLStreamHandlingCallback xmlStreamHandlingCallback;

	public URLHandlingTemplate getUrlHandlingTemplate() {
		return urlHandlingTemplate;
	}

	public void setUrlHandlingTemplate(URLHandlingTemplate urlHandlingTemplate) {
		this.urlHandlingTemplate = urlHandlingTemplate;
	}

	/** 
	 * Access to the server configuration service
	 */		
	private ServerConfigurationService serverConfigurationService;
	public void  setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
		this.serverConfigurationService = serverConfigurationService;
	}
	
	/** 
	 * Location of the data to be sync'ed
	 */		
	private File dataFolder;
	public File getDataFolder() {
		return dataFolder;
	}

	public void setDataFolder(File dataFolder) {
		this.dataFolder = dataFolder;
	}
	
	private boolean cleanupData;
	public void setCleanupData(String cleanupDataString) {
		this.cleanupData = Boolean.getBoolean(cleanupDataString);
	}
	
	public void setCleanupData(boolean cleanupData) {
		this.cleanupData = cleanupData;
	}
	
	
	/**
	 * Initialization of data folder and cleanup date
	 */
	public void init() {
		if (dataFolder == null) {
			String sakaiHomePath = serverConfigurationService.getSakaiHomePath();
			setDataFolder(new File(sakaiHomePath,DEFAULT_DATA_FOLDER));
			setCleanupData(true);
		}
	}
	
	public void readCourseOfferings(ImsSyncContext syncContext) {
		retrieveAndProcessData("READCOURSEOFFERINGS", syncContext);
	}

	public void readCourseSections(ImsSyncContext syncContext) {
		retrieveAndProcessData("READCOURSESECTIONS", syncContext);
	}

	public void readCourseTemplates(ImsSyncContext syncContext) {
		retrieveAndProcessData("READCOURSETEMPLATES", syncContext);
	}

	public void readGroups(ImsSyncContext syncContext) {
		retrieveAndProcessData("READGROUPSMGMT", syncContext);
	}

	public void readMemberships(ImsSyncContext syncContext) {
		retrieveAndProcessData("READMEMBERSHIPS", syncContext);
	}

	public void readPersons(ImsSyncContext syncContext) {
		retrieveAndProcessData("READPERSONS", syncContext);
	}

	public void readSectionAssociations(ImsSyncContext syncContext) {
		retrieveAndProcessData("READSECTIONASSOCIATION", syncContext);
	}

	private void retrieveAndProcessData(final String filePattern, ImsSyncContext syncContext) {
		log.info("Reading file from " + dataFolder.getPath() + " cleanup after read: " + cleanupData);
		if (dataFolder.exists()) {
			File[] fileList = dataFolder.listFiles(new FilenameFilter(){
				public boolean accept(File dir, String name) {
					return name.toLowerCase().startsWith(filePattern.toLowerCase());
				}});
			
			for (File file : fileList) {
				urlHandlingTemplate.handleURL(file.toURI().toString(), xmlStreamHandlingCallback);
				if (cleanupData)
					file.delete();
			}
		} else {
			log.error("Data folder " + dataFolder.getAbsolutePath() + " does not exist.");
		}
	}
}
