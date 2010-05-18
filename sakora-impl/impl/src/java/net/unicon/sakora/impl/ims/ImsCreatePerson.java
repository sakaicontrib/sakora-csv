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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.sakaiproject.user.api.UserAlreadyDefinedException;
import org.sakaiproject.user.api.UserEdit;
import org.sakaiproject.user.api.UserIdInvalidException;
import org.sakaiproject.user.api.UserLockedException;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.api.UserPermissionException;

/**
 * Map IMS Person from Oracle SIS feed to Sakai Course Management.
 * 
 * Note: Person maps to User
 * 
 * @author bsawert
 *
 */
public class ImsCreatePerson extends ImsCreateBase {
	static final Log log = LogFactory.getLog(ImsCreatePerson.class);
	private static final String XPATH_EXPR = "//*[lower-case(name())='person']";
	
	public ImsCreatePerson() {
		super();
	}

	public void init() {
		log.debug("Initializing " + getClass().getName());
		try {
			 xPath = XPath.newInstance(XPATH_EXPR);
		}
		catch (JDOMException jde) {
			log.error("ImsCreatePerson: JDOM exception: " + jde.getMessage());
			log.error("XPath object not initialized.");
		}
	}	

	@Override
	public void handleXml(Document doc) {
		UserEdit ue;		
		// values from XML
		String eid, firstName, lastName, email;
		String action;
		
		// make sure we have a valid XPath
		if (xPath == null) {
			log.error("ImsCreatePerson: XPath is empty.");
			return;
		}
		
		if (doc == null) {
			log.info("ImsCreatePerson: Document is empty.");
		}
		else {
			// try block for XML parsing
			try {
				List<Element> elements = xPath.selectNodes(doc);
				
				if (log.isDebugEnabled()) {
					log.debug("Found " + elements.size() + " persons.");
				}
	
				// log in as admin and process the elements
				loginToSakai();
				
				/**
				 * Oracle to IMS to Sakai mappings:
				 * 
				 * 	emplId -> id -> eid
				 * 	first name -> firstname/givenname -> firstName
				 * 	last name -> familyname/last/surname -> lastName
				 * 	middle name -> middle -> (not used)
				 * 	prefix -> prefix -> (not used)
				 *  suffix -> suffix -> (not used)
				 *  email addr -> email -> email
				 * 
				 */
												
				// process all the elements in the document
				for (Element element : elements) {
					ue = null;
					
					eid = element.getChildText("sourcedid");
					// TB: that has changed again
					// Element sourcedIdNode = element.getChild("sourcedid");
					// eid = (sourcedIdNode != null) ? sourcedIdNode.getChildText("id") : null;
					
					// get name info from child element
					firstName = lastName = null;
					Element nameNode = element.getChild("name");
					if (nameNode != null) {
						Element partNode = nameNode.getChild("partname");
						if (partNode != null) {
							firstName = partNode.getChildText("givenname");
							if (firstName == null || firstName.length() <= 0) {
								firstName = partNode.getChildText("firstname");
							}
							lastName = partNode.getChildText("surname");
							if (lastName == null || lastName.length() <= 0) {
								lastName = partNode.getChildText("last");
							}
						}
					}
					
					// get email from child element
					Element contactNode = element.getChild("contactinfo");
					email = (contactNode != null) ? contactNode.getChildText("email") : null;
					
					// get add/update/delete action
					Element extension = element.getChild("extension");
					action = getAction(extension);

					// translate IMS id to eid if external person handler is defined 
					if (imsExternalHandler != null) {
						eid = imsExternalHandler.getPersonEidFromImsId(eid);
					}

					if (eid != null && eid.length() > 0) {
						String id = null;
						
						try {
							id = userDirService.getUserId(eid);
						}
						catch(UserNotDefinedException unde) {
							log.debug("getUserId: user (" + eid + ") does not exist.");
						}

						// try block for user directory service calls
						try {
							if (id == null) {
							// create a new user
								ue = userDirService.addUser(null, eid);
								// temporary - add eid as password
								ue.setPassword(eid);
							}
							else {
							// user exists - update or delete
								ue = userDirService.editUser(id);						
							}
							
							if (action.equals("D")) {
							// delete user
								log.debug("ImsCreatePerson: deleting User (" + eid + ").");
								
								// call external delete if external person handler is defined
								if (imsExternalHandler != null) {
									imsExternalHandler.externalDeletePerson(eid);
									
									if (imsExternalHandler.mustDeleteLocalPerson(eid)) {
									// finish with local delete
										userDirService.removeUser(ue);
									}
								}
								else {
								// do local delete only
									userDirService.removeUser(ue);
								}
							}
							else {
							// add/update user info
								log.debug("ImsCreatePerson: updating User (" + eid + ").");
								
								ue.setFirstName(firstName);
								ue.setLastName(lastName);
								ue.setEmail(email);
							}
							
							// commit the changes
							userDirService.commitEdit(ue);
						}
						catch(UserIdInvalidException uiie) {
							log.error("ImsCreatePerson: " + uiie.getMessage());
						}
						catch(UserNotDefinedException unde) {
							log.error("ImsCreatePerson: " + unde.getMessage());
						}
						catch(UserAlreadyDefinedException uade) {
							log.error("ImsCreatePerson: " + uade.getMessage());
						}
						catch(UserLockedException ule) {
							log.error("ImsCreatePerson: " + ule.getMessage());
						}
						catch(UserPermissionException upe) {
							// this one should never happen
							log.error("ImsCreatePerson: " + upe.getMessage());
						}

					}
					else {
						// no eid retrieved
						log.info("ImsCreatePerson: no eid in xml fragment.");
					}
				}
				
				logoutFromSakai();
			}
			catch (JDOMException jde) {
				log.error("ImsCreatePerson: " + jde.getMessage());
			}
		}
	}

}
