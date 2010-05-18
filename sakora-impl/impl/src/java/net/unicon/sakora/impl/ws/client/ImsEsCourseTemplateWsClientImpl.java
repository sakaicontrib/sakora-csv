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
package net.unicon.sakora.impl.ws.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.oracle.xmlns.enterprise.hcm.services.cms.DeleteCourseTemplateRequest;
import com.oracle.xmlns.enterprise.hcm.services.cms.DeleteCourseTemplateResponse;

public class ImsEsCourseTemplateWsClientImpl
{
	private static final Log LOG = LogFactory.getLog(ImsEsCourseTemplateWsClientImpl.class);

	private WebServiceTemplate webServiceTemplate;

	public void setWebServiceTemplate(WebServiceTemplate webServiceTemplate) {
		this.webServiceTemplate = webServiceTemplate;
	}
	// call the delete coursetemplate
	
	public boolean requestDeleteCourseTemplate(String courseTemplateId) {
		DeleteCourseTemplateRequest request = new DeleteCourseTemplateRequest();
		request.setSourcedId(courseTemplateId);
		// send request
		Object response = webServiceTemplate.marshalSendAndReceive(request);
		if (response instanceof DeleteCourseTemplateResponse) {
			DeleteCourseTemplateResponse deleteResponse = (DeleteCourseTemplateResponse)response;
			// process the response, of cause right now the response does not 
			// contain any information at all so we always return true
			return true;			
		} else {
			LOG.error("Should have returned an instance to class DeleteCourseTemplateResponse");
			return false;
		}
	}
}
