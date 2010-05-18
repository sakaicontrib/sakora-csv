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

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.unicon.sakora.api.ims.ImsEntityCreator;

/**
 * This class provides a factory for IMS to Course Management XML handlers.
 * 
 * @author bsawert
 *
 */
public class ImsEntityCreatorFactory {
	private static final Log log = LogFactory.getLog(ImsEntityCreatorFactory.class);
	
	/**
	 * Don't instantiate this class
	 */
	private ImsEntityCreatorFactory() {
	}
	
	// begin spring injection
	
	private Map<String, ? extends ImsEntityCreator> imsCreatorBeans;
	public void setImsCreatorBeans(Map<String, ? extends ImsEntityCreator> imsCreatorBeans) {
		this.imsCreatorBeans = imsCreatorBeans;
	}
	
	// end spring injection
	
	/**
	 * Return appropriate creator for named element
	 * 
	 * @param	String elementName	IMS element name
	 * @return	ImsEntityCreator	entity creator for named element 
	 */
	public ImsEntityCreator getCreatorInstance(String elementName) {
		log.debug("Getting ImsEntityCreator for: " + elementName);
		return imsCreatorBeans.get(elementName);
	}

}
