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

import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.Meeting;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.coursemanagement.api.exception.IdExistsException;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;

/**
 * Map IMS CourseSection from Oracle SIS feed to Sakai Course Management.
 * 
 * Note: CourseSection maps to Section
 * EnrollmentSet and Meetings are created when creating a Section
 * 
 * @author bsawert
 *
 */
public class ImsCreateCourseSection extends ImsCreateBase {
	private static final Log log = LogFactory.getLog(ImsCreateCourseSection.class);
	private static final String XPATH_EXPR = "//*[lower-case(name())='coursesection']";

	Map<String, Section> courseSections = new HashMap<String, Section>();	
	
	public ImsCreateCourseSection() {
		super();
	}

	public void init() {
		log.debug("Initializing " + getClass().getName());
		try {
			 xPath = XPath.newInstance(XPATH_EXPR);
		}
		catch (JDOMException jde) {
			log.error("ImsCreateCourseSection: " + jde.getMessage());
			log.error("XPath object not initialized.");
		}
	}	

	@Override
	public void handleXml(Document doc) {
		Section sec;
		EnrollmentSet enroll;
		
		// values from xml
		String eid, title, description, category;
		String action;
		String parentSectionEid;
		String courseOfferingEid;
		String enrollmentSetEid;
		String status;
		String credits;
		String maxStudents;
		
		// make sure we have a valid XPath
		if (xPath == null) {
			log.error("ImsCreateCourseSection: XPath is empty.");
			return;
		}
		
		if (doc == null) {
			log.info("ImsCreateCourseSection: Document is empty.");
		}
		else {
			// try block for xml parsing
			try {
				List<Element> elements = xPath.selectNodes(doc);
				
				if (log.isDebugEnabled()) {
					log.debug("Found " + elements.size() + " course sections.");
				}
	
				// log in as admin and process the elements
				loginToSakai();
				
				/**
				 * Oracle to IMS to Sakai mappings:
				 * 
				 * 	course id + "-" + course offering number + "-" + term code + "-" + session code + "-" + class section -> sourcedid -> eid
				 * 	course id + course offering number + term code + session code + class section -> parentofferingid -> courseOfferingEid
				 * 	course title long -> title -> title
				 * 	catalog description long -> longdescription -> description
				 * 	status -> status -> status
				 * 	default credits -> defaultcredits -> (not used - mapped through Membership)
				 * 	component -> category -> category
				 * 	enrollment cap -> maxnumberofstudents -> maxSize
				 * 	start date -> begin -> (not used)
				 * 	end date -> end -> (not used)
				 * 
				 * One or more of the following:
				 *	meeting day -> day -> Meeting day
				 *	meeting start time -> starttime -> Meeting start time
				 *	meeting end time -> endtime -> Meeting end time
				 * 
				 */
												
				// process all the elements in the document
				for (Element element : elements) {
					sec = null;
					int maxSize = 0;
					boolean mustUpdate = false;
					
					eid = element.getChildText("sourcedid");

					// get add/update/delete action
					Element extension = element.getChild("extension");
					action = getAction(extension);
						
					if (action.equals("D") && cmService.isSectionDefined(eid)) {
						// delete course section
						log.debug("ImsCreateCourseSection: deleting Section (" + eid + ").");
						sec = cmService.getSection(eid);
						
						// remove the enrollment set if one exists
						enroll = sec.getEnrollmentSet();
						if (enroll != null) { 
							cmAdmin.removeEnrollmentSet(enroll.getEid());
						}
						cmAdmin.removeSection(eid);
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
					
					// get category from child element
					Element catNode = element.getChild("category");
					category = (catNode != null) ? catNode.getChildText("text") : null;
					
					// add category to title
					if (category != null && eid != null && eid.length() > 0) {
						title = title + " (" + category + ")";
					}
					
					status = element.getChildText("status");
					credits = element.getChildText("defaultcredits");
					maxStudents = element.getChildText("maxnumberofstudents");
					
					if (maxStudents != null && maxStudents.length() > 0) {
						try {
							maxSize = Integer.parseInt(maxStudents);
						}
						catch(NumberFormatException nfe) {
							log.info("ImsCreateCourseSection: " + nfe.getMessage());
						}
					}

					courseOfferingEid = element.getChildText("parentofferingid");
					// eid can be null, but not blank
					courseOfferingEid = (courseOfferingEid != null && courseOfferingEid.length() > 0) ? courseOfferingEid : null;
					
					// these values don't come through in the feed - bas
					parentSectionEid = null;
					enrollmentSetEid = null;
						
					if (eid != null && eid.length() > 0) {
						// try block for course management calls
						try {
							if (category != null && category.length() > 0) {
								if (cmService.getSectionCategoryDescription(category) == null) {
									cmAdmin.addSectionCategory(category, category);
								}
							}
							
							if (cmService.isSectionDefined(eid)) {
								// section already defined - update it
								log.debug("ImsCreateCourseSection: updating Section (" + eid + ").");
								
								sec = cmService.getSection(eid);
								sec.setTitle(title);
								sec.setDescription(description);
								sec.setCategory(category);
								
								// missing from update - ParentSection, CourseOffering - bas
								
							}
							else {
							// create a new section
							log.debug("ImsCreateCourseSection: creating Section (" + eid + ").");

							sec = cmAdmin.createSection(eid, title, description,
								category, parentSectionEid, courseOfferingEid, null);
							}
								
							// set enrollment cap
							if (maxSize > 0) {
								sec.setMaxSize(maxSize);
								mustUpdate = true;
							}

							// create or update enrollment set
							if (credits != null && credits.length() > 0) {
								// update credits in enrollment set
								enroll = sec.getEnrollmentSet();
								try {
									if (enroll == null) {
										// create a new enrollment set
										enrollmentSetEid = eid + "_ES";
										enroll = cmAdmin.createEnrollmentSet(enrollmentSetEid, title, description, category,
											credits, sec.getCourseOfferingEid(), null);
										sec.setEnrollmentSet(enroll);
									}
									else {
										enroll.setDefaultEnrollmentCredits(credits);
										cmAdmin.updateEnrollmentSet(enroll);
									}
									
									mustUpdate = true;
								}
								catch(Exception e) {
									// just log error updating credits
									log.info("ImsCreateCourseSection: error updating EnrollmentSet for Section (" + eid + ")");
								}
							}
							
								// pulling the time frame information and changing the CourseOffering timeframe to cover the sections timeframe. 
//								Date[] timeframe = getTimeframe(element);
//								if (courseOfferingEid != null && courseOfferingEid.length() > 0 && timeframe != null) {
//									CourseOffering co = cmService.getCourseOffering(courseOfferingEid);
//									boolean updateCo = false;
//									if (co.getStartDate() == null || co.getStartDate().after(timeframe[0])) {
//										co.setStartDate(timeframe[0]);
//										updateCo = true;
//									}
//									if (co.getEndDate() == null || co.getEndDate().before(timeframe[1])) {
//										co.setEndDate(timeframe[1]);
//										updateCo = true;
//									}
//									
//									if (updateCo) {
//										cmAdmin.updateCourseOffering(co);
//									}
//										
//								}
//								
								
							// only parse and add meeting set if none exists
							Set<Meeting> meetings = sec.getMeetings();
							
							if (meetings == null || meetings.size() <= 0) {
								// read and create meeting times
								List<Element> meetingNodes = element.getChildren("sectionclass");
								
								if (meetingNodes != null) {
									meetings = new HashSet<Meeting>();
									
									for (Element meetingNode : meetingNodes) {
										String days = meetingNode.getChildText("day");
										
										String startTimeStr = meetingNode.getChildText("starttime");
										Time startTime = timeStringToTime(startTimeStr);
	
										String finishTimeStr = meetingNode.getChildText("finishtime");
										Time finishTime = timeStringToTime(finishTimeStr);
	
										Element locationNode = meetingNode.getChild("location");
										String location = (locationNode != null) ? locationNode.getChildText("text") : null;
										
										Meeting meeting = cmAdmin.newSectionMeeting(eid, location, startTime, finishTime, null);
										setMeetingDays(meeting, days);
										
										meetings.add(meeting);
									}
									
									sec.setMeetings(meetings);
									mustUpdate = true;
								}
							}
							else {
								log.debug("ImsCreateCourseSection: not updating meetings for Section (" + eid + ")");
								Iterator<Meeting> meetingIterator = sec.getMeetings().iterator();
								while (meetingIterator.hasNext())
								{
									Meeting meeting = (Meeting) meetingIterator.next();
//									cmAdmin.removeSectionMeeting(sec, meeting);
									meetingIterator.remove();
								}
								mustUpdate = true;
							}
							
							// update changes to section
							if (mustUpdate) {
								cmAdmin.updateSection(sec);
							}
						}
						catch(IdExistsException iee) {
							log.error("ImsCreateCourseSection: " + iee.getMessage());
						}
						catch(IdNotFoundException infe) {
							log.error("ImsCreateCourseSection: " + infe.getMessage());
						}
						
						if (sec != null) {
							courseSections.put(eid, sec);
						}
					}
					else {
						// no eid retrieved
						log.info("ImsCreateCourseSection: no eid in xml fragment.");
					}
				}
				
				logoutFromSakai();
			}
			catch (JDOMException jde) {
				log.error("ImsCreateCourseSection: " + jde.getMessage());
			}
		}
	}
	
	/**
	 * Parse a time String
	 * 
	 * @param timeStr	time string (hh:mm:ss.0000)
	 * @return Time, or null on error
	 */
	private Time timeStringToTime(String timeStr) {
		Time time = null;

		if (timeStr != null) {
			if (timeStr.lastIndexOf('.') != -1) {
				timeStr = timeStr.substring(0, timeStr.lastIndexOf('.'));
			}
			timeStr = timeStr.replace('.', ':');
			
			try {
				time = Time.valueOf(timeStr);
			}
			catch(Exception e) {
				// number format or illegal argument
				log.error("ImsCreateCourseSection: " + e.getMessage());
			}
		}

		return time;
	}
	
	/**
	 * Set meeting days.
	 * First, check for explicit day name (Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday).
	 * If no match, check for single letter day (MTWRFS).
	 * 
	 * @param meeting	Meeting object
	 * @param days		String of days (MTWRFS)
	 */
	private void setMeetingDays(Meeting meeting, String days) {
		if (days != null && meeting != null) {
			if (days.equalsIgnoreCase("Sunday")) {
				meeting.setSunday(true);
			}
			else if (days.equalsIgnoreCase("Monday")) {
				meeting.setMonday(true);
			}
			else if (days.equalsIgnoreCase("Tuesday")) {
				meeting.setTuesday(true);
			}
			else if (days.equalsIgnoreCase("Wednesday")) {
				meeting.setWednesday(true);
			}
			else if (days.equalsIgnoreCase("Thursday")) {
				meeting.setThursday(true);
			}
			else if (days.equalsIgnoreCase("Friday")) {
				meeting.setFriday(true);
			}
			else if (days.equalsIgnoreCase("Saturday")) {
				meeting.setSaturday(true);	
			}
			else {
				for (int index = 0; index < days.length(); index++) {
					switch (days.charAt(index)) {
						case 'M':
							meeting.setMonday(true);
							break;
						case 'T':
							meeting.setTuesday(true);
							break;
						case 'W':
							meeting.setWednesday(true);
							break;							
						case 'R':
							meeting.setThursday(true);
							break;
						case 'F':
							meeting.setFriday(true);
							break;
						case 'S':
							meeting.setSaturday(true);
							break;
						default:
							break;
					}
				}
			}
		}
		
		return;
	}

}
