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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.sakaiproject.coursemanagement.api.CanonicalCourse;
import org.sakaiproject.coursemanagement.api.exception.IdExistsException;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;

/**
 * Map IMS CourseTemplate from Oracle SIS feed to Sakai Course Management.
 * 
 * Note: CourseTemplate maps to CanonicalCourse
 * 
 * @author bsawert
 *
 */
public class ImsCreateCourseTemplate extends ImsCreateBase {
	private static final Log log = LogFactory.getLog(ImsCreateCourseTemplate.class);
	private static final String XPATH_EXPR = "//*[lower-case(name())='coursetemplate']";

	public ImsCreateCourseTemplate() {
		super();
	}
	
	public void init() {
		log.debug("Initializing " + getClass().getName());
		try {
			 xPath = XPath.newInstance(XPATH_EXPR);
		}
		catch (JDOMException jde) {
			log.error("ImsCreateCourseTemplate: " + jde.getMessage());
			log.error("XPath object not initialized.");
		}
	}	

	@Override
	public void handleXml(Document doc) {
		CanonicalCourse cc;		
		// values from xml
		String eid, title, description;
		String action;

		// make sure we have a valid XPath
		if (xPath == null) {
			log.error("ImsCreateCourseTemplate: XPath is empty.");
			return;
		}
		
		if (doc == null) {
			log.info("ImsCreateCourseTemplate: Document is empty.");
		}
		else {
			// try block for xml parsing
			try {
				List<Element> elements = xPath.selectNodes(doc);
				
				if (log.isDebugEnabled()) {
					log.debug("Found " + elements.size() + " course templates.");
				}
	
				// log in as admin and process the elements
				loginToSakai();
				
				/**
				 * Oracle to IMS to Sakai mappings:
				 * 
				 * 	course id -> sourcedid / label -> eid
				 * 	course title long -> title -> title
				 * 	catalog desc long -> longdescription -> description
				 * 
				 */
				
				// process all the elements in the document
				for (Element element : elements) {
					cc = null;
					eid = element.getChildText("sourcedid");
					
					// get add/update/delete action
					Element extension = element.getChild("extension");
					action = getAction(extension);
					
					if (action.equals("D")) {
						// delete canonical course
						log.debug("ImsCreateCourseTemplate: deleting CanonicalCourse (" + eid + ").");
						
						cmAdmin.removeCanonicalCourse(eid);
						continue;
					}

					// get title from child element
					Element titleNode = element.getChild("title");
					title = (titleNode != null) ? titleNode.getChildText("text") : null;
					// title cannot be null - use eid
					title = (title != null) ? title : eid;

					// get description from child element
					Element descNode = element.getChild("catalogdescription");
					description = (descNode != null) ? descNode.getChildText("longdescription") : null;
					// description cannot be null - use eid
					description = (description != null) ? description : eid;
					
					if (eid != null && eid.length() > 0) {
						// try block for course management calls
						try {
							if (cmService.isCanonicalCourseDefined(eid)) {
								// canonical course already defined - update it
								log.debug("ImsCreateCourseTemplate: updating CanonicalCourse (" + eid + ").");
									
								cc = cmService.getCanonicalCourse(eid);
								cc.setTitle(title);
								cc.setDescription(description);
								
								cmAdmin.updateCanonicalCourse(cc);
							}
							else {
								// create a new canonical course
								log.debug("ImsCreateCourseTemplate: creating CanonicalCourse (" + eid + ").");
			
								cc = cmAdmin.createCanonicalCourse(eid, title, description);
							}
						}
						catch(IdExistsException iee) {
							log.error("ImsCreateCourseTemplate: " + iee.getMessage());
						}
						catch(IdNotFoundException infe) {
							log.error("ImsCreateCourseTemplate: " + infe.getMessage());
						}
					}
					else {
						// no eid retrieved
						log.info("ImsCreateCourseTemplate: no eid in xml fragment.");
					}
				}
				
				logoutFromSakai();
			}
			catch (JDOMException jde) {
				log.error("ImsCreateCourseTemplate: " + jde.getMessage());
			}
			
			log.info("");
		}
	}

}
