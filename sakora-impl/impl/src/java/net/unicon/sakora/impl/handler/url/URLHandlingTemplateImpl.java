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
package net.unicon.sakora.impl.handler.url;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import net.unicon.sakora.api.handler.url.URLHandlingTemplate;
import net.unicon.sakora.api.handler.xml.XMLStreamHandlingCallback;
import net.unicon.sakora.impl.exception.SakoraRuntimeException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.net.ftp.FtpLoginException;
/**
 * Generic implementation of the URLHandlingTemplate. Any valid URI can be handed down
 * to the method. We don not relay on any specific protocol. 
 * The associated test case verifies file:// and http:// and ftp://
 *  
 * @author tbehlau
 */
public class URLHandlingTemplateImpl implements URLHandlingTemplate {
	private static final Log LOG = LogFactory.getLog(URLHandlingTemplateImpl.class);

	private String serverUrl;
	private String userId;
	private String password;

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}
	
	public String getServerUrl() {
		return serverUrl;
	}
	
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.unicon.sakora.api.handler.url.URLHandlingTemplate#handleURL(java.lang.String, net.unicon.sakora.api.handler.xml.XMLStreamHandlingCallback)
	 */
	public void handleURL(String urlString, XMLStreamHandlingCallback xshc) {
		URL url = null;
		InputStream inputStream = null;

		try {
			try {
				url = new URL(urlString);
			} catch (MalformedURLException me) {
				// we did not receive protocol, so we will assume we have file and pull the server information
				// from the properties
				urlString = serverUrl+'/'+urlString;
				url = new URL(urlString); 
			}
		
			try {
				inputStream = url.openStream();
			} catch (IOException ie) {
				if (url.getUserInfo() == null && ie instanceof FtpLoginException)
				{
					String protocol = url.getProtocol();
					StringBuffer newUrl = new StringBuffer(1024);
					newUrl.append(protocol)
						.append("://").append(userId)
						.append(':').append(password)
						.append('@')
						.append(urlString.substring(protocol.length() + 3));
					url = new URL(newUrl.toString());
					inputStream = url.openStream();
				}
			}
			
//			if (inputStream == null) {
//				throw new SakoraRuntimeException("Cannot retrieve URL " + url.toString());
//			}
			xshc.handleStream(inputStream);
		} catch (MalformedURLException mfe) {
			LOG.error("Invalid URL: " + url);
			throw new SakoraRuntimeException(mfe);
		} catch (IOException ioe) {
			LOG.error("Error reading from " + url);
			throw new SakoraRuntimeException(ioe);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException ignore) {
				}
			}
		}

	}
}
