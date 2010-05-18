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

import net.unicon.sakora.api.handler.xml.XMLStreamHandlingCallback;
import net.unicon.sakora.impl.exception.SakoraRuntimeException;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * Implementation of the URLHandlingTemplate specific for http requests.
 * All other protocols will fail!
 * 
 * @author tbehlau
 */

public class HttpHandlingTemplateImpl extends URLHandlingTemplateImpl {
	private static final Log log = LogFactory.getLog(URLHandlingTemplateImpl.class);

	/*
	 * (non-Javadoc)
	 * @see net.unicon.sakora.api.handler.url.URLHandlingTemplate#handleURL(java.lang.String, net.unicon.sakora.api.handler.xml.XMLStreamHandlingCallback)
	 */
	public void handleURL(String url, XMLStreamHandlingCallback xshc) {
		HttpClient client = new HttpClient();
		GetMethod method = new GetMethod(url);

		client.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
			
		try {
			int statusCode = client.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK) {
				log.error("executeMethod failed: " + method.getStatusLine());
			}
				
			xshc.handleStream(method.getResponseBodyAsStream());
		} catch (HttpException he) {
			log.error("HttpException thrown while calling executeMethod.",he);
			throw new SakoraRuntimeException(he);
		} catch (IOException ioe) {
			log.error("IOException thrown while calling executeMethod.",ioe);
			throw new SakoraRuntimeException(ioe);
		} finally {
			method.releaseConnection();
		}			
	}
}
