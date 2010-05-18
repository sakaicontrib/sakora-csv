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
package net.unicon.sakora.impl.ws.service;

import javax.xml.transform.Source;

import org.jdom.Element;
import org.jdom.transform.JDOMSource;

public class ResponseBuilder {
	private String urlPrefix;
	public void setUrlPrefix(String urlPrefix) {
		this.urlPrefix = urlPrefix;
	}
	
	public Source buildSnapshotResponse(String elementName, String dataFile) {
		Element elem = new Element(elementName);
		Element df = new Element("dataFile");
		df.setText(urlPrefix + dataFile);
		elem.addContent(df);
		Source response = new JDOMSource(elem);
		return response;
	}

	public Source buildIncrementalResponse(String elementName, String dataFile, String savePoint) {
		Element elem = new Element(elementName);
		Element df = new Element("dataFile");
		df.setText(urlPrefix + dataFile);
		Element sp = new Element("savePoint");
		sp.setText(savePoint);
		elem.addContent(df);
		elem.addContent(sp);
		Source response = new JDOMSource(elem);
		return response;
	}
}
