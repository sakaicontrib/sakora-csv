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
package net.unicon.sakora.impl.ims;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.exception.IdExistsException;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;

/**
 * Map IMS CourseOffering from Oracle SIS feed to Sakai Course Management.
 * 
 * Note: CourseOffering maps to CourseOffering
 * 
 * @author bsawert
 *
 */
public class ImsCreateCourseOffering extends ImsCreateBase {
	private static final Log log = LogFactory.getLog(ImsCreateCourseOffering.class);
	private static final String XPATH_EXPR = "//*[lower-case(name())='courseoffering']";

	public ImsCreateCourseOffering() {
		super();
	}

	public void init() {
		log.debug("Initializing " + getClass().getName());
		try {
			 xPath = XPath.newInstance(XPATH_EXPR);
		}
		catch (JDOMException jde) {
			log.error("ImsCreateCourseOffering: " + jde.getMessage());
			log.error("XPath object not initialized.");
		}
	}	

	@Override
	public void handleXml(Document doc) {
		CourseOffering co;
		// values from xml
		String eid, title, description, status;
		String action;
		String academicSessionEid;
		String canonicalCourseEid;
		Date startDate = null;
		Date endDate = null;
		
		// make sure we have a valid XPath
		if (xPath == null) {
			log.error("ImsCreateCourseOffering: XPath is empty.");
			return;
		}

		if (doc == null) {
			log.info("ImsCreateCourseOffering: Document is empty.");
		}
		else {
			// try block for xml parsing
			try {
				List<Element> elements = xPath.selectNodes(doc);
				
				if (log.isDebugEnabled()) {
					log.debug("Found " + elements.size() + " course offerings.");
				}
	
				// log in as admin and process the elements
				loginToSakai();

				/**
				 * Oracle to IMS to Sakai mappings:
				 * 
				 * 	course id + course offering number + term code + session code + class section -> sourcedid -> eid
				 * 	course id -> parenttemplateid -> canonicalCourseEid
				 * 	catalog description -> ?
				 * 	catalog description long -> ? -> description
				 * 	status -> status -> status
				 * 	term code -> academicsession -> academicSessionEid
				 * 	start date -> begin -> (not used)
				 * 	end date -> end -> (not used)
				 * 
				 */
					
				Namespace ns;
				// process all the elements in the document
				for (Element element : elements) {
					co = null;
					ns = element.getNamespace();
					
					eid =  element.getChildText("sourcedid",ns);
					
					// get add/update/delete action
					Element extension = element.getChild("extension",ns);
					action = getAction(extension);
					if (action.equals("D")) {
						// delete course offering
						log.debug("ImsCreateCourseOffering: deleting CourseOffering (" + eid + ").");
						
						cmAdmin.removeCourseOffering(eid);
						continue;
					}
					

					// get title from child element
					Element titleNode = element.getChild("title",ns);
					title = (titleNode != null) ? titleNode.getChildText("text",ns) : null;
					// title cannot be null - use eid
					title = (title != null) ? title : eid;
					
					// get description from child element
					Element descNode = element.getChild("catalogdescription",ns);
					description = (descNode != null) ? descNode.getChildText("longdescription",ns) : null;
					// description cannot be null - use eid
					description = (description != null) ? description : eid;

					status = element.getChildText("status",ns);
					
					// get academic session from child element
					academicSessionEid = null;
					Element sessionNode = element.getChild("academicsession",ns);
					if (sessionNode != null) {
						academicSessionEid = sessionNode.getChildText("text",ns);
					}
					// eid can be null, but not blank
					academicSessionEid = (academicSessionEid != null && academicSessionEid.length() > 0) ? academicSessionEid : null;
					
					canonicalCourseEid = element.getChildText("parenttemplateid",ns);
					// eid can be null, but not blank
					canonicalCourseEid = (canonicalCourseEid != null && canonicalCourseEid.length() > 0) ? canonicalCourseEid : null;

					// get dates from child element
					Date[] timeFrame = getTimeframe(element);
					if (timeFrame != null) {
						startDate = timeFrame[0]; 
						endDate =  timeFrame[1];
					} else {
						startDate = endDate = null;
					}
					
					if (eid != null && eid.length() > 0) {
						// try block for course management calls
						try {
							if (cmService.isCourseOfferingDefined(eid)) {
								// course offering already defined - update it
								log.debug("ImsCreateCourseOffering: updating CourseOffering (" + eid + ").");
								
								co = cmService.getCourseOffering(eid);
								co.setTitle(title);
								co.setDescription(description);
								co.setStatus(status);
								co.setStartDate(startDate);
								co.setEndDate(endDate);
		
								if (academicSessionEid != null && academicSessionEid.length() > 0) {
									// check current academic session to see if we need to update it
									String oldSessionEid = null;
									if (co.getAcademicSession() != null) {
										AcademicSession oldSession = co.getAcademicSession();
										oldSessionEid = oldSession.getEid();
										
										if (oldSessionEid != null && !oldSessionEid.equals(academicSessionEid)) {
											// different sessions - must update
											try {
												AcademicSession newSession = cmService.getAcademicSession(academicSessionEid);
												co.setAcademicSession(newSession);
											}
											catch(IdNotFoundException infe) {
												log.error("ImsCreateCourseOffering: " + infe.getMessage());
												// do nothing
											}
										}
										
									}
								}
								
								// missing from update - CanonicalCourseEid - bas
								
								cmAdmin.updateCourseOffering(co);
							}
							else {
								// create a new canonical course
								log.debug("ImsCreateCourseOffering: creating CourseOffering (" + eid + ").");
		
								co = cmAdmin.createCourseOffering(eid, title, description,
									status, academicSessionEid, canonicalCourseEid, startDate, endDate);
							}
						}
						catch(IdExistsException iee) {
							log.error("ImsCreateCourseOffering: " + iee.getMessage());
						}
						catch(IdNotFoundException infe) {
							log.error("ImsCreateCourseOffering: " + infe.getMessage());
						}

					}
					else {
						// no eid retrieved
						log.info("ImsCreateCourseOffering: no eid in xml fragment.");
					}
				}
				
				logoutFromSakai();
			}
			catch (JDOMException jde) {
				log.error("ImsCreateCourseOffering: " + jde.getMessage());
			}
		}
	}

}
