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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.Membership;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;
import org.sakaiproject.user.api.UserEdit;

/**
 * Map IMS Membership from Oracle SIS feed to Sakai Course Management.
 * 
 * Note: Membership maps to Membership and EnrollmentSet
 * 
 * @author bsawert
 *
 */
public class ImsCreateMembership extends ImsCreateBase {
	private static final Log log = LogFactory.getLog(ImsCreateMembership.class);
	private static final String XPATH_EXPR = "//*[lower-case(name())='membership']";
	private static final String DEFAULT_CREDIT_HOURS = "0";
	private static final String DEFAULT_GRADE_SCHEME = "Letter Grade";
	private static final String DEFAULT_STATUS = "Active";
	
	Map<String, Membership> memberships = new HashMap<String, Membership>();

	public ImsCreateMembership() {
		super();
	}

	public void init() {
		log.debug("Initializing " + getClass().getName());
		try {
			 xPath = XPath.newInstance(XPATH_EXPR);
		}
		catch (JDOMException jde) {
			log.error("ImsCreateMembership: " + jde.getMessage());
			log.error("XPath object not initialized.");
		}
	}	

	// begin spring injection
	
	private Map<String, String> roleMap;
	public void setRoleMap(Map<String, String> roleMap) {
		this.roleMap = roleMap;
	}

	private Map<String, String> statusMap;
	public void setStatusMap(Map<String, String> statusMap) {
		this.statusMap = statusMap;
	}
	
	private Map<String, String> roleUserTypeMap;
	public void setRoleUserTypeMap(Map<String, String> roleUserTypeMap) {
		this.roleUserTypeMap = roleUserTypeMap;
	}
	
	private String defaultCredits = DEFAULT_CREDIT_HOURS;
	public void setDefaultCredits(String defaultCredits) {
		this.defaultCredits = defaultCredits;
	}
	
	private String gradingScheme = DEFAULT_GRADE_SCHEME;
	public void setGradingScheme(String gradingScheme) {
		this.gradingScheme = gradingScheme;
	}
	
	// end spring injection
	
	@Override
	public void handleXml(Document doc) {
		// values from xml
		String eid, sectionEid, userId, status;
		String action;
		String membershipIdType, idType, roleType;

		// make sure we have a valid XPath
		if (xPath == null) {
			log.error("ImsCreateMembership: XPath is empty.");
			return;
		}

		if (doc == null) {
			log.info("ImsCreateMembership: Document is empty.");
		}
		else {
			// try block for xml parsing
			try {
				List<Element> elements = xPath.selectNodes(doc);
				
				if (log.isDebugEnabled()) {
					log.debug("Found " + elements.size() + " memberships.");
				}
	
				// log in as admin and process the elements
				loginToSakai();
				
				/**
				 * Oracle to IMS to Sakai mappings:
				 * 
				 * 	course id + "-" + term code + "-" + session code + "-" + class section -> sourcedid -> eid
				 * 	"courseSection" -> membershipidtype
				 * 	
				 * 	Member
				 * 		emplId -> sourcedid -> userId
				 * 		"person" -> idtype
				 * 		"Instructor", "TeachingAssistant", "Learner -> roletype -> roleType
				 * 		"Active" -> status -> status
				 * 
				 * 	roleType is mapped through Spring config to Sakai CM provider roles.
				 * 	status is mapped through Spring config to Sakai CM provider status.
				 */
												
				// process all the elements in the document
				for (Element element : elements) {
					eid = element.getChildText("sourcedid");
					sectionEid = element.getChildText("collectionsourcedid");
					membershipIdType = element.getChildText("membershipidtype");
					
					if (eid == null || eid.length() <= 0) {
						// course offering or section is missing
						log.info("ImsCreateMembership: no CourseOffering or Section eid in xml fragment.");
						
						// skip to next element
						continue;						
					}
					else if (membershipIdType == null || membershipIdType.length() <= 0) {
						// membership id type is missing
						log.info("ImsCreateMembership: no membership id type in xml fragment.");
						
						// skip to next element
						continue;						
					}

					// get members child elements
					List<Element> members = element.getChildren("members");
					for (Element member : members) {
						action = DEFAULT_ACTION;
						userId = member.getChildText("sourcedid");
						idType = member.getChildText("idtype");
						
						if (userId == null || userId.length() <= 0 ||
							idType == null || !idType.equalsIgnoreCase("person")) {
							// not a valid user id
							log.info("ImsCreateMembership: member id (" + userId + "/" + idType + ") is not valid.");
							
							// skip to next member
							continue;
						}
						else {
							// translate IMS id to eid if external person handler is defined 
							if (imsExternalHandler != null) {
								userId = imsExternalHandler.getPersonEidFromImsId(userId);
							}

							// get role and status from child node
							roleType = status = null;
							Element roleNode = member.getChild("role");
							if (roleNode != null) {
								roleType = roleNode.getChildText("roletype");
								status = roleNode.getChildText("status");
								
								// get add/update/delete action
								Element extension = element.getChild("extension");
								action = getAction(extension);
							}
							// status cannot be null
							status = (status != null) ? status : DEFAULT_STATUS;
							
							try {
								if (membershipIdType.equalsIgnoreCase("coursesection")) {
									// add member to course section
									// instructor role added as instructor of record
									// student role added to enrollment set
									// other role added as member
									addMemberToSection(userId, roleType, sectionEid, status, action);
								}
								else if (membershipIdType.equalsIgnoreCase("courseoffering")) {
									// add member to course offering
									// no enrollment set - all roles added as members
									addMemberToCourseOffering(userId, roleType, sectionEid, status, action);
								}
								else {
									log.info("ImsCreateMembership: membership type (" + membershipIdType + ") not supported.");
								}
							}
							catch(IdNotFoundException infe) {
								log.info("ImsCreateMembership: " + infe.getMessage());
							}
							catch(Exception e) {
								log.info("ImsCreateMembership: " + e.getMessage());
							}
						}
					}
				}
				
				logoutFromSakai();
			}
			catch (JDOMException jde) {
				log.error("ImsCreateMembership: " + jde.getMessage());
			}
		}		
	}
	
	/**
	 * Add user to section. Instructor role added as instructor of record, Student role added as memmber and to enrollment,
	 * TeachingAssistant or other role added as member.
	 *  
	 * @param userId
	 * @param roleType
	 * @param eid
	 * @param status
	 * @param action
	 * 
	 * @throws Exception
	 */
	private void addMemberToSection(String userId, String roleType, String eid, String status, String action) throws Exception {
		EnrollmentSet enrolled = null;
		String role = roleMap.get(roleType.toLowerCase());
		String enrollStatus = statusMap.get(status.toLowerCase());
		
		if (role != null) {
			Section section = cmService.getSection(eid);
			enrolled = section.getEnrollmentSet();

			if (enrolled == null) {
				// no enrollment set yet - create one
				String esEid = section.getEid() + "_ES";
				
				enrolled = cmAdmin.createEnrollmentSet(esEid, section.getTitle(), section.getDescription(),
					section.getCategory(), defaultCredits, section.getCourseOfferingEid(), null);
				section.setEnrollmentSet(enrolled);
				cmAdmin.updateSection(section);
			}
			
			if (roleType.equalsIgnoreCase("Instructor")) {
				// add as instructor of record
				Set<String> instructors = enrolled.getOfficialInstructors();

				if (instructors == null) {
					instructors = new HashSet();
					enrolled.setOfficialInstructors(instructors);
				}
				
				if (action.equals("D")) {
					// delete course section instructor
					log.debug("ImsCreateMembership: deleting instructors (" + userId + ").");
					
					instructors.remove(userId);
				}
				else {
					instructors.add(userId);
					
				}
				cmAdmin.updateEnrollmentSet(enrolled);
			}
			else if (roleType.equalsIgnoreCase("Learner")) {
				if (action.equals("D")) {
					// delete from enrollment
					log.debug("ImsCreateMembership: deleting enrollment (" + userId + ").");
					
					// check whether to mark inactive or delete
					if (imsExternalHandler != null && imsExternalHandler.makeMemberInactiveToDelete(eid)) {
						// just mark inactive
						String inactiveStatus = statusMap.get("inactive");
						cmAdmin.addOrUpdateEnrollment(userId, enrolled.getEid(), inactiveStatus, defaultCredits, gradingScheme);
					}
					else {
						// do a true delete from enrollment
						cmAdmin.removeEnrollment(userId, enrolled.getEid());
					}
				}
				else {
					// add to enrollment
					cmAdmin.addOrUpdateEnrollment(userId, enrolled.getEid(), enrollStatus, defaultCredits, gradingScheme);
				}
			}
			else {
				// TeachingAssistant or other
				if (action.equals("D")) {
					// delete from membership
					log.debug("ImsCreateMembership: deleting membership (" + userId + ").");
					
					// check whether to mark inactive or delete
					if (imsExternalHandler != null && imsExternalHandler.makeMemberInactiveToDelete(eid)) {
						// just mark inactive
						String inactiveStatus = statusMap.get("inactive");
						cmAdmin.addOrUpdateSectionMembership(userId, role, eid, inactiveStatus);
					}
					else {
						// do a true delete from membership
						cmAdmin.removeSectionMembership(userId, eid);
					}
				}
				else {
					// add as member
					cmAdmin.addOrUpdateSectionMembership(userId, role, eid, status);
				}
			}

			
			if (roleUserTypeMap != null && roleUserTypeMap.containsKey(roleType.toLowerCase())) {
				String userType = roleUserTypeMap.get(roleType.toLowerCase());
				UserEdit ue = userDirService.editUser(userDirService.getUserId(userId));
				ue.setType(userType);
				userDirService.commitEdit(ue);
			}
		}
		else {
			log.warn("ImsCreateMembership: no role defined for roleType (" + roleType + ")");
		}
	}
	
	/**
	 * Add user to course offering. All user roles added as members.
	 * 
	 * @param userId
	 * @param roleType
	 * @param eid
	 * @param status
	 * @param action
	 * 
	 * @throws Exception
	 */
	private void addMemberToCourseOffering(String userId, String roleType, String eid, String status, String action) throws Exception {
		if (action.equals("D")) {
			// delete course offering membership
			log.debug("ImsCreateMembership: deleting CourseOfferingMembership (" + userId + ").");
			
			cmAdmin.removeCourseOfferingMembership(userId, eid);
		}
		else {
			cmAdmin.addOrUpdateCourseOfferingMembership(userId, roleType, eid, status);
		}
	}

}
