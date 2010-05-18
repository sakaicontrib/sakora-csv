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

import java.io.ByteArrayInputStream;

import net.unicon.sakora.api.handler.xml.XMLStreamHandlingCallback;
import net.unicon.sakora.api.ims.ImsContentDelegator;

import org.jdom.Document;
import org.jdom.output.XMLOutputter;

public class JmsContentDelegatorImpl implements ImsContentDelegator
{
	private XMLStreamHandlingCallback xmlHandlerCallback;
	
	public void setXMLStreamHandlingCallback(XMLStreamHandlingCallback xmlHandlerCallback) {
		this.xmlHandlerCallback = xmlHandlerCallback;
	}
	
	public void handleDocument(Document document)
	{
		// the idea is that we strip of any header information, 
		// get to the core and pass it on to the xmlHandlerCallback, 
		// which puts the object on the message queue. 
		XMLOutputter outputter = new XMLOutputter();
		String output = outputter.outputString(document);
		ByteArrayInputStream bis = new ByteArrayInputStream(output.getBytes());
		xmlHandlerCallback.handleStream(bis);
	}

	public void handleXmlContent(String xmlContent)
	{
		ByteArrayInputStream bis = new ByteArrayInputStream(xmlContent.getBytes());
		xmlHandlerCallback.handleStream(bis);
	}

}
