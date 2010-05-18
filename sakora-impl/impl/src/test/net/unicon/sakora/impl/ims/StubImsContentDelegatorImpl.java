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
import org.jdom.output.XMLOutputter;

import net.unicon.sakora.api.ims.ImsContentDelegator;


/*
	Dummy ImsContentDelegator implemenation for performance testing
*/

public class StubImsContentDelegatorImpl implements ImsContentDelegator {
	private static final Log log = LogFactory.getLog(StubImsContentDelegatorImpl.class);
	private int count = 0;
	// try to slow down the content handler 
	public synchronized void handleXmlContent(String xmlContent) {
		log.info(String.valueOf(++count) + ": " + xmlContent.substring(0, Math.min(100, xmlContent.length())));
		log.info("message length " + xmlContent.length());
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// ignore everything
		}
	}

	public synchronized void handleDocument(Document document) {
		handleXmlContent(new XMLOutputter().outputString(document));
	}
}
