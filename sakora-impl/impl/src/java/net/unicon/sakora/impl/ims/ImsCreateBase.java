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

import net.unicon.sakora.api.ims.ImsEntityCreator;
import net.unicon.sakora.api.ims.extern.ImsExternalHandler;
import net.unicon.sakora.impl.util.XmlUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.coursemanagement.api.CourseManagementAdministration;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.event.api.UsageSessionService;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.UserDirectoryService;

/**
 * This is the base class for IMS elements defined in the corresponding
 * XML schemas.
 * 
 * @author bsawert
 *
 */

public abstract class ImsCreateBase implements ImsEntityCreator {
	private static final Log log = LogFactory.getLog(ImsCreateBase.class);
	
	protected static final String DEFAULT_ACTION = "C";

	protected XPath xPath;

	// xml element to Sakai entity
	public abstract void handleXml(Document doc);
	
	// begin spring injection

	protected CourseManagementAdministration cmAdmin;
	public void setCmAdmin(CourseManagementAdministration cmAdmin) {
		this.cmAdmin = cmAdmin;
	}

	protected CourseManagementService cmService;
	public void setCmService(CourseManagementService cmService) {
		this.cmService = cmService;
	}

	protected UserDirectoryService userDirService;
	public void setUserDirService(UserDirectoryService userDirService) {
		this.userDirService = userDirService;
	}

	protected SessionManager sessionManager;
	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	protected UsageSessionService usageSessionService;
	public void setUsageSessionService(UsageSessionService usageSessionService) {
		this.usageSessionService = usageSessionService;
	}

	protected AuthzGroupService authzGroupService;
	public void setAuthzGroupService(AuthzGroupService authzGroupService) {
		this.authzGroupService = authzGroupService;
	}

	protected EventTrackingService eventTrackingService;
	public void setEventTrackingService(EventTrackingService eventTrackingService) {
		this.eventTrackingService = eventTrackingService;
	}
	
	protected ImsExternalHandler imsExternalHandler;

	public void setImsExternalHandler(ImsExternalHandler imsExternalHandler) {
		this.imsExternalHandler = imsExternalHandler;
	}	
	
	// end spring injection
	
	protected void loginToSakai() {
	    Session sakaiSession = sessionManager.getCurrentSession();
		sakaiSession.setUserId("admin");
		sakaiSession.setUserEid("admin");

		// establish the user's session
		usageSessionService.startSession("admin", "127.0.0.1", "SakoraLoader");

		// update the user's externally provided realm definitions
		authzGroupService.refreshUser("admin");

		// post the login event
		eventTrackingService.post(eventTrackingService.newEvent(UsageSessionService.EVENT_LOGIN, null, true));
	}

	protected void logoutFromSakai() {
	    Session sakaiSession = sessionManager.getCurrentSession();
		sakaiSession.invalidate();

		// post the logout event
		eventTrackingService.post(eventTrackingService.newEvent(UsageSessionService.EVENT_LOGOUT, null, true));
	}

	/**
	 * Check XML fragment to see if it contains elements on a specific XPath
	 * 
	 * @param xml 		XML fragment to parse
	 * @param xPath		XPath expression to search for
	 * @return			true if elements found, false if not
	 */
	protected boolean canHandle(String xml, XPath xPath) {
		// parse xml doc to see if it contains our XPath
		Document doc = documentFromXmlString(xml);
		return canHandle(doc, xPath);
	}
	
	/**
	 * Check XML Document to see if it contains elements on a specific XPath
	 * 
	 * @param xml 		XML Document to parse
	 * @param xPath		XPath expression to search for
	 * @return			true if elements found, false if not
	 */
	protected boolean canHandle(Document doc, XPath xPath) {
		// parse xml doc to see if it contains our XPath
		if (doc == null) {
			log.info("Document is empty.");
		}
		else {
			try {
				List<?> elements = xPath.selectNodes(doc);
				
				if (elements != null && elements.size() > 0) {
					return true;
				}
			}
			catch (JDOMException jde) {
				log.error("JDOM exception: " + jde.getMessage());
			}
		}
	
		return false;
	}
	
	/**
	 * Create Document from XML fragment
	 * 
	 * @param xml		XML fragment to parse
	 * @return			Document on success, null otherwise
	 */
	protected Document documentFromXmlString(String xml) {
		return XmlUtils.documentFromXmlString(xml);
	}
	
	/**
	 * Routine to convert tags in XML fragment to lower case for consistent handling.
	 * 
	 * @param xml	XML String to convert
	 * @return		XML with tags converted
	 */
	protected String lowerCaseTags(String xml) {
		return XmlUtils.lowerCaseTags(xml);
	}


	/**
	 * Routine to get time frame information from sub element
	 * 
	 * @param extNode	IMS element to search for time frame
	 * @return			null : not time frame found or time frame invalid
	 * 					Date[] containing start and end date of time frame.
	 */
	protected Date[] getTimeframe(Element element) {
		Namespace ns = element.getNamespace();
		Date startDate = null;
		Date endDate = null;
		// get dates from child element
		startDate = endDate = null;
		Element dateNode = element.getChild("timeframe",ns);
		if (dateNode != null) {
			Element restrictDate = dateNode.getChild("begin",ns);
			String startDateStr = restrictDate.getChildText("date",ns);
			restrictDate = dateNode.getChild("end",ns);	
			String endDateStr = restrictDate.getChildText("date",ns);
			
			// date strings look like "2007-09-06", "2007-11-14"
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			
			try {
				if (startDateStr != null) {
					startDate = df.parse(startDateStr);
				}
				if (endDateStr != null) {
					endDate = df.parse(endDateStr);
				}
				
				return new Date[] {startDate, endDate};
			}
			catch(ParseException pe) {
				log.error("ImsCreateBase: " + pe.getMessage());
			}
		}
		return null;
	}

	/**
	 * Routine to get action field from sub element
	 * 
	 * @param extNode	IMS extension element
	 * @return			"C", "U", "D" (Create/Update/Delete)
	 */
	protected String getAction(Element extNode) {
		String fieldName = null;
		String fieldValue = null;
		
		if (extNode != null) {
			Namespace ns = extNode.getNamespace();
			Element fieldNode = extNode.getChild("extensionfield",ns);
			if (fieldNode != null) {
				fieldName = fieldNode.getChildText("fieldname",ns);
				if (fieldName != null && fieldName.equalsIgnoreCase("mode")) {
					fieldValue = fieldNode.getChildText("fieldvalue",ns);	
				}
			}
		}

		// default to Add if not specified
		return (fieldValue == null) ? DEFAULT_ACTION : fieldValue;
	}

	public boolean canHandle(String xml)
	{
		if (xPath == null) {
			log.error(getClass().getName() + ": XPath is empty.");
			return false;
		}
	
		return canHandle(xml, xPath);
	}

	public boolean canHandle(Document doc)
	{
		if (xPath == null) {
			log.error(getClass().getName() +": XPath is empty.");
			return false;
		}
	
		return canHandle(doc, xPath);
	}

	public void handleXml(String xml)
	{
		// make sure we have a valid XPath
		if (xPath == null) {
			log.error(getClass().getName() +": XPath is empty.");
			return;
		}
		
		if (xml == null) {
			log.info(getClass().getName() +": XML string is empty.");
			return;
		}
		
		// convert all XML tags to lower case
		String lcXml = lowerCaseTags(xml);
	
		// parse XML here to extract above fields
		Document doc = documentFromXmlString(lcXml);
	
		handleXml(doc);
	}
	
}
