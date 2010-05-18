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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CourseSet;
import org.sakaiproject.coursemanagement.api.exception.IdExistsException;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;

/**
 * Map IMS Group from Oracle SIS feed to Sakai Course Management.
 * 
 * Note: Term maps to Academic Session, Department maps to CourseSet.
 * Institution does not map to anything yet.
 * 
 * @author bsawert
 *
 */
public class ImsCreateGroup extends ImsCreateBase {
	private static final Log log = LogFactory.getLog(ImsCreateGroup.class);
	private static final String XPATH_EXPR = "//*[lower-case(name())='group']";

	public ImsCreateGroup() {
		super();
	}

	public void init() {
		log.debug("Initializing " + getClass().getName());
		try {
			 xPath = XPath.newInstance(XPATH_EXPR);
		}
		catch (JDOMException jde) {
			log.error("ImsCreateGroup: " + jde.getMessage());
			log.error("XPath object not initialized.");
		}
	}	

	@Override
	public void handleXml(Document doc) {
		// values from xml
		String eid, title, description;
		String action;
		String level, typeDesc;
		Date startDate = null;
		Date endDate = null;
		
		// make sure we have a valid XPath
		if (xPath == null) {
			log.error("ImsCreateGroup: XPath is empty.");
			return;
		}

		if (doc == null) {
			log.info("ImsCreateGroup: Document is empty.");
		}
		else {
			// try block for xml parsing
			try {
				List<Element> elements = xPath.selectNodes(doc);
				
				if (log.isDebugEnabled()) {
					log.debug("Found " + elements.size() + " groups.");
				}
	
				// log in as admin and process the elements
				loginToSakai();
				
				/**
				 * Oracle to IMS to Sakai mappings:
				 * 
				 * 	"INSTITUTE" (level 1) -> CourseSet
				 * 	"DEPARTMENT" (level 2) -> CourseSet
				 * 	"TERM" (level 3) -> AcademicSession
				 * 
				 * 	institution + "-" + term code -> sourcedid -> eid
				 * 	course id + course offering number + term code + session code + class section -> parentofferingid -> courseOfferingEid
				 * 	description short -> shortdescription -> title
				 * 	description -> longdescription -> description
				 * 
				 * For term only:
				 * 	start date -> begin -> startDate
				 * 	end date -> end -> endDate
				 * 
				 */
												
				// process all the elements in the document
				for (Element element : elements) {
					eid = element.getChildText("sourcedid");

					// get group type from child element
					level = typeDesc = null;
					Element groupType = element.getChild("grouptype");
					if (groupType != null) {
						Element typeValue = groupType.getChild("typevalue");
						if (typeValue != null) {
							Element typeLevel = typeValue.getChild("level");
							if (typeLevel != null) {
								level = typeLevel.getChildText("text");
							}
							
							Element typeNode = typeValue.getChild("type");
							if (typeNode != null) {
								typeDesc = typeNode.getChildText("text");
							}
						}

					}
					
					if (level == null || level.length() <= 0 ||
						typeDesc == null || typeDesc.length() <= 0) {
						// level number or type description is missing
						log.info("ImsCreateGroup: no type information in xml fragment.");
						
						// skip to next element
						continue;
					}
					
					// get title and description from child element
					title = description = null;
					Element desc = element.getChild("description");
					if (desc != null) {
						title = desc.getChildText("shortdescription");
						description = desc.getChildText("longdescription");
					}
					
					// get dates from child element (only for term element)
					startDate = endDate = null;
					Element dateNode = element.getChild("timeframe");
					if (dateNode != null) {
						String startDateStr = null;
						String endDateStr = null;
						Element beginNode = dateNode.getChild("begin");
						if (beginNode != null) {
							startDateStr = beginNode.getChildText("date");
						}
						Element endNode = dateNode.getChild("end");
						if (endNode != null) {
							endDateStr = endNode.getChildText("date");
						}
						
						// date strings look like "2007-09-06", "2007-11-14"
						DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
						try {
							if (startDateStr != null) {
								startDate = df.parse(startDateStr);
							}
							if (endDateStr != null) {							
								endDate = df.parse(endDateStr);
							}
						}
						catch(ParseException pe) {
							log.error("ImsCreateGroup: " + pe.getMessage());
						}
					}
					
					// get add/update/delete action
					Element extension = element.getChild("extension");
					action = getAction(extension);
					
					if (level.equals("3") && typeDesc.equalsIgnoreCase("TERM")) {
						// process as academic session
						createOrUpdateAcademicSession(eid, title, description, startDate, endDate, action);
					}
					else if (level.equals("2") && typeDesc.equalsIgnoreCase("DEPARTMENT")) {
						// process as course set
						createOrUpdateCourseSet(eid, title, description, typeDesc, action);
					}
					else if (level.equals("1") && typeDesc.equalsIgnoreCase("INSTITUTE")) {
						// process as course set
						createOrUpdateCourseSet(eid, title, description, typeDesc, action);					}
					else {
						// unknown type
						log.error("ImsCreateGroup: unknown level/type (" + level + "/" + typeDesc + ")");
					}
				}
				
				logoutFromSakai();
			}
			catch (JDOMException jde) {
				log.error("ImsCreateGroup: " + jde.getMessage());
			}
		}		
	}

	
	/**
	 * Create or update AcademicSession from IMS info.
	 * 
	 * @param eid
	 * @param title
	 * @param description
	 * @param startDate
	 * @param endDate
	 * @param action
	 * 
	 * @return nothing
	 */
	public void createOrUpdateAcademicSession(String eid, String title, String description, Date startDate, Date endDate, String action) {
		AcademicSession sess = null;
		
		if (eid != null && eid.length() > 0) {
			// try block for course management calls
			try {
				if (action.equals("D")) {
					// delete course section
					log.debug("ImsCreateGroup: deleting AcademicSession (" + eid + ").");
					
					cmAdmin.removeAcademicSession(eid);
				}
				else if (cmService.isAcademicSessionDefined(eid)) {
					// academic session already defined - update it
					log.debug("ImsCreateGroup: updating AcademicSession (" + eid + ").");
					
					sess = cmService.getAcademicSession(eid);
					sess.setTitle(title);
					sess.setDescription(description);
					sess.setStartDate(startDate);
					sess.setEndDate(endDate);
					
					// TODO missing from update - Authority - bas
					
					cmAdmin.updateAcademicSession(sess);
				}
				else {
					// create a new academic session
					log.debug("ImsCreateGroup: creating AcademicSession (" + eid + ").");

					sess = cmAdmin.createAcademicSession(eid, title, description, startDate, endDate);
				}
			}
			catch(IdExistsException iee) {
				log.error("ImsCreateGroup: " + iee.getMessage());
			}
			catch(IdNotFoundException infe) {
				log.error("ImsCreateGroup: " + infe.getMessage());
			}

		}
		else {
			// no eid retrieved
			log.info("ImsCreateGroup: no eid in xml fragment.");
		}

		return;
	}
	
	
	/**
	 * Create or update CourseSet from IMS info.
	 * 
	 * @param eid
	 * @param title
	 * @param description
	 * @param category
	 * @param action
	 * 
	 * @return nothing
	 */
	public void createOrUpdateCourseSet(String eid, String title, String description, String category, String action) {
		CourseSet cs = null;
		
		if (eid != null && eid.length() > 0) {
			// try block for course management calls
			try {
				if (action.equals("D")) {
					// delete course section
					log.debug("ImsCreateGroup: deleting CourseSet (" + eid + ").");
					
					cmAdmin.removeCourseSet(eid);
				}
				else if (cmService.isCourseSetDefined(eid)) {
					// course set already defined - update it
					log.debug("ImsCreateGroup: updating CourseSet (" + eid + ").");
					
					cs = cmService.getCourseSet(eid);
					cs.setTitle(title);
					cs.setDescription(description);
					cs.setCategory(category);
					
					// TODO missing from update - Parent ID - bas
					
					cmAdmin.updateCourseSet(cs);
				}
				else {
					// create a new course set
					log.debug("ImsCreateGroup: creating CourseSet (" + eid + ").");

					cs = cmAdmin.createCourseSet(eid, title, description, category, null);
				}
			}
			catch(IdExistsException iee) {
				log.error("ImsCreateGroup: " + iee.getMessage());
			}
			catch(IdNotFoundException infe) {
				log.error("ImsCreateGroup: " + infe.getMessage());
			}

		}
		else {
			// no eid retrieved
			log.info("ImsCreateGroup: no eid in xml fragment.");
		}

		return;
	}
	
}
