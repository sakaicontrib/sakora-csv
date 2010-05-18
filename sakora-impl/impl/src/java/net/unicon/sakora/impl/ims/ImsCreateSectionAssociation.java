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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 * Map IMS SectionAssociation from Oracle SIS feed to Sakai Course Management.
 * 
 * Note: SectionAssociation maps to ?
 * 
 * @author bsawert
 *
 */
public class ImsCreateSectionAssociation extends ImsCreateBase {
	private static final Log log = LogFactory.getLog(ImsCreateSectionAssociation.class);
	private static final String XPATH_EXPR = "//*[lower-case(name())='sectionassociation']";
	
	public ImsCreateSectionAssociation() {
		super();
	}

	public void init() {
		log.debug("Initializing " + getClass().getName());
		try {
			 xPath = XPath.newInstance(XPATH_EXPR);
		}
		catch (JDOMException jde) {
			log.error("ImsCreateSectionAssociation: " + jde.getMessage());
			log.error("XPath object not initialized.");
		}
	}	

	@Override
	public void handleXml(Document doc) {
		// TODO Auto-generated method stub

	}

}
