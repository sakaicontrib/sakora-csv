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
import java.util.Map;

import net.unicon.sakora.api.ims.ImsContentDelegator;
import net.unicon.sakora.api.ims.ImsEntityCreator;
import net.unicon.sakora.impl.exception.SakoraRuntimeException;
import net.unicon.sakora.impl.util.XmlUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.springframework.util.Assert;
/**
 * 
 * 
 * @author tbehlau
 *
 */
public class ImsContentDelegatorImpl implements ImsContentDelegator {
	private static final Log log = LogFactory.getLog(ImsContentDelegatorImpl.class);
	private Map<String, ? extends ImsEntityCreator> elementToCreatorMap;
	public void setElementToCreatorMap(Map<String, ? extends ImsEntityCreator> elememtToCreatorMap) {
		this.elementToCreatorMap = elememtToCreatorMap;
	}
	
	public void handleXmlContent(String xmlContent) {
		Document doc = XmlUtils.documentFromXmlString(XmlUtils.lowerCaseTags(xmlContent));
		if (doc == null) {
			throw new SakoraRuntimeException("XMLContent is not valid. " + xmlContent);
		} else {
			handleDocument(doc);
		}
	}

	public void handleDocument(Document document) {
		Assert.notNull(document, "Document should not be null");
		Element element = document.getRootElement();
		String elementTag = element.getName();
		if (elementTag != null && elementTag.equals("enterprise")) {
			List<?> children = element.getChildren();
			if (children != null && !children.isEmpty()) {
				elementTag = ((Element)children.get(0)).getName();
			} else {
				elementTag = null; 
			}
		}				
		if (elementTag != null) {
			ImsEntityCreator creator = elementToCreatorMap.get(elementTag.toLowerCase());
			if (creator != null) {
				if (creator.canHandle(document)) {
					creator.handleXml(document);
				}
			} else {
				log.error("Cannot find ImsEntityCreator for " + elementTag);
			}
		} else {
			log.error("Cannot find valid start tag in xmlContent");
		}
	}
}
