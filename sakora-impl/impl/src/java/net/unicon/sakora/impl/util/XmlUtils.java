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
package net.unicon.sakora.impl.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.unicon.sakora.impl.ims.ImsContentDelegatorImpl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.SAXParseException;

/**
 * XmlUtils class to handle common tasks for XML transformation and creation.
 * 
 * @author tbehlau
 *
 */
public class XmlUtils {
	private static final Log log = LogFactory.getLog(ImsContentDelegatorImpl.class);

	/**
	 * Create Document from XML fragment
	 * 
	 * @param xml		XML fragment to parse
	 * @return			Document on success, null otherwise
	 */
	public static Document documentFromXmlString(String xml) {
		Reader reader = null;
		Document doc = null;
	
		if (xml != null) {
			try {
				reader = new StringReader(xml);
				doc = new SAXBuilder().build(reader);
				
				if (log.isDebugEnabled()) {
					log.debug("XML Document built from input string.");
				}
			}
			catch(JDOMException jde) {
				String location = "";
				if (jde.getCause() instanceof SAXParseException) {
					SAXParseException spe = (SAXParseException) jde.getCause();
					location = "("+ spe.getLineNumber() + "," + spe.getColumnNumber()+")";
				}
				log.error("Error parsing XML input string " + location + ": " + jde.getMessage());
			}
			catch(IOException ioe) {
				log.error("I/O error parsing XML input string: " + ioe.getMessage());
			}
			finally {
				if (reader != null) {
					try {
						reader.close();
					}
					catch(Exception e) {
						log.error("Unable to close string reader.");
					}
				}
			}
		}
		
		return doc;
	}
	
	/**
	 * Routine to convert tags in XML fragment to lower case for consistent handling.
	 * 
	 * @param xml	XML String to convert
	 * @return		XML with tags converted
	 */
	public static String lowerCaseTags(String xml) {
		Pattern pattern = Pattern.compile("(</?[^/]+/?>)");
		Matcher matcher = pattern.matcher(xml);
		StringBuffer sb = new StringBuffer();
		
		while (matcher.find()) {
			matcher.appendReplacement(sb, matcher.group().toLowerCase());
		}
		matcher.appendTail(sb);
		
		return sb.toString();
	}


}
