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
package net.unicon.sakora.api.ws;

/**
 * API definition for IMS  web service calls.
 * Calls mirror IMS element definitions.
 * 
 * @author bsawert
 *
 */

public interface ImsEsSyncService {
	
	/** 
	 * Read course templates. 
	 * Depending upon the feedtype defined in the ImsSyncContext it will set up the
	 * appropriate WebServiceMessageCallback, read in the appropriate initial request document, make the 
	 * web service call and handle the response accordingly.
	 * 
	 * @param syncContext - specific content information
	 */		
	void readCourseTemplates(ImsSyncContext syncContext);
	
	/** 
	 * Read course offerings. 
	 * Depending upon the feedtype defined in the ImsSyncContext it will set up the
	 * appropriate WebServiceMessageCallback, read in the appropriate initial request document, make the 
	 * web service call and handle the response accordingly.
	 * 
	 * @param syncContext - specific content information
	 */		
	void readCourseOfferings(ImsSyncContext syncContext);

	/** 
	 * Read course sections. 
	 * Depending upon the feedtype defined in the ImsSyncContext it will set up the
	 * appropriate WebServiceMessageCallback, read in the appropriate initial request document, make the 
	 * web service call and handle the response accordingly.
	 * 
	 * @param syncContext - specific content information
	 */		
	void readCourseSections(ImsSyncContext syncContext);

	/** 
	 * Read section associations. 
	 * Depending upon the feedtype defined in the ImsSyncContext it will set up the
	 * appropriate WebServiceMessageCallback, read in the appropriate initial request document, make the 
	 * web service call and handle the response accordingly.
	 * 
	 * @param syncContext - specific content information
	 */		
	void readSectionAssociations(ImsSyncContext syncContext);

	/** 
	 * Read groups. 
	 * Depending upon the feedtype defined in the ImsSyncContext it will set up the
	 * appropriate WebServiceMessageCallback, read in the appropriate initial request document, make the 
	 * web service call and handle the response accordingly.
	 * 
	 * @param syncContext - specific content information
	 */		
	void readGroups(ImsSyncContext syncContext);

	/** 
	 * Read persons. 
	 * Depending upon the feedtype defined in the ImsSyncContext it will set up the
	 * appropriate WebServiceMessageCallback, read in the appropriate initial request document, make the 
	 * web service call and handle the response accordingly.
	 * 
	 * @param syncContext - specific content information
	 */		
	void readPersons(ImsSyncContext syncContext);

	/** 
	 * Read memberships. 
	 * Depending upon the feedtype defined in the ImsSyncContext it will set up the
	 * appropriate WebServiceMessageCallback, read in the appropriate initial request document, make the 
	 * web service call and handle the response accordingly.
	 * 
	 * @param syncContext - specific content information
	 */		
	void readMemberships(ImsSyncContext syncContext);	

}
