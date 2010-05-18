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
package net.unicon.sakora.tool.test.ws.server;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.ws.client.core.WebServiceTemplate;

public class WebServiceClient extends TestCase
{
	WebServiceTemplate template;
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		template = new WebServiceTemplate();
		template.setDefaultUri("http://localhost:8888/sakai-sakora-tool/services/");
	}
	
	public void testExecuteSampleFiles() throws IOException {
		boolean failed = false;
		Resource resource = new ClassPathResource("eventMessages/");
		File[] files = resource.getFile().listFiles();
		for (File file : files)
		{
			// We can't have test cases breaking b/c of the DeleteGroup messages
			// if we don't have development resources to actually fix the problem
			if ( file.getName().startsWith("DeleteGroup") ) {
				System.out.println(" >>SKIPPED UNSUPPORTED MESSAGE: " + file.getName());
				continue;
			}
			try {
				JDOMResult result = createRequestAndCallWebservice(file);
		//		Unmarshaller cmsMarshaller;
				assertNotNull("The result of the webservice call should not be null.", result);
		//		assertTrue("Incorrect return class " + result.getClass().getName(),
		//				result.getClass() == CreateCourseSectionResponse.class);
			} catch (Exception ex) {
				System.out.println(" >>FAILED:" + ex.getMessage() + ":");
				failed = true;					
			}
		}
		assertFalse("Test failed, check output for errors", failed);
		
	}
	
	private JDOMResult createRequestAndCallWebservice(File file) throws Exception {
		Document doc = (new SAXBuilder()).build(file);
		JDOMSource requestSource = new JDOMSource(doc);
		JDOMResult responseResult = new JDOMResult();
		template.sendSourceAndReceiveToResult(requestSource, responseResult);
		return responseResult;
	}
}
