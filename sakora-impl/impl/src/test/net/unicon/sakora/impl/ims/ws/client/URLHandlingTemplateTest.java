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
package net.unicon.sakora.impl.ims.ws.client;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.startsWith;

import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.TestCase;
import net.unicon.sakora.api.handler.url.URLHandlingTemplate;
import net.unicon.sakora.api.handler.xml.XMLStreamHandlingCallback;
import net.unicon.sakora.api.queue.MessageProducer;
import net.unicon.sakora.impl.exception.SakoraRuntimeException;
import net.unicon.sakora.impl.handler.url.HttpHandlingTemplateImpl;
import net.unicon.sakora.impl.handler.url.URLHandlingTemplateImpl;
import net.unicon.sakora.impl.handler.xml.XMLStreamHandlingCallbackImpl;
import net.unicon.sakora.impl.msg.SystenOutMessageProducerImpl;

import org.jmock.Expectations;
import org.jmock.Mockery;

public class URLHandlingTemplateTest extends TestCase {
	Mockery context;
	
	String fileName = "webapp/readCourseTemplates.xml";
	String ftpURL = "ftp://localhost/test/readCourseTemplates.xml";
	String httpURL = "http://localhost:8888/data/READCOURSETEMPLATES.xml";
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		context = new Mockery();
	}

	public void testFileReadWithURLImplementation() {
		// Prepare the Mock object
		final MessageProducer producer = context.mock(MessageProducer.class);
		context.checking(new Expectations() {{
            atLeast(1).of(producer).createMessage(with(allOf(startsWith("<coursetemplate>"), endsWith("</coursetemplate>"))));
        }});

		// get the file URL and pass it to the URLHandlingTemplate
		URL url = getClass().getResource("/"+fileName);
		
		URLHandlingTemplate template = new URLHandlingTemplateImpl();
		XMLStreamHandlingCallbackImpl xmlImpl = new XMLStreamHandlingCallbackImpl();		
		xmlImpl.setMessageProducer(producer);
		xmlImpl.setChildDepth(2);
		template.handleURL(url.toString(), xmlImpl);
		context.assertIsSatisfied();
	}

	public void testFtpReadFailureWithURLImplementation() {
		URLHandlingTemplate template = new URLHandlingTemplateImpl();
		XMLStreamHandlingCallbackImpl xmlImpl = new XMLStreamHandlingCallbackImpl();		
		xmlImpl.setMessageProducer(new SystenOutMessageProducerImpl());
		xmlImpl.setChildDepth(2);
		try {
			template.handleURL(ftpURL, xmlImpl);
			fail("Should have thrown an exception.");			
		} catch (RuntimeException re) {
			// We are expecting a exception here.
		}
	}
	
	public void testHandleUrlMalformedUrl() {
		URLHandlingTemplate template = new URLHandlingTemplateImpl();
		final XMLStreamHandlingCallback callback = context.mock(XMLStreamHandlingCallback.class);
		try {
			template.handleURL("invalidurl", callback);
			fail("malformedUrlException should have been thrown.");
		} catch (SakoraRuntimeException se) {
			assertEquals("Exception is not a MalformedURLExcpetion", 
					MalformedURLException.class, se.getCause().getClass());
		}
	}


	public void testHttpReadWithURLImplementation() {
		// Prepare the Mock object
		final MessageProducer producer = context.mock(MessageProducer.class);
		context.checking(new Expectations() {{
            atLeast(1).of(producer).createMessage(with(allOf(startsWith("<courseTemplate>"), endsWith("</courseTemplate>"))));
        }});

		URLHandlingTemplate template = new URLHandlingTemplateImpl();
		XMLStreamHandlingCallbackImpl xmlImpl = new XMLStreamHandlingCallbackImpl();		
		xmlImpl.setMessageProducer(producer);
		xmlImpl.setChildDepth(2);
		xmlImpl.setInputEncoding("UTF-8");
		template.handleURL(httpURL, xmlImpl);
		context.assertIsSatisfied();
	}
	
	public void testHttpReadWithHttpImplementation() {
		// Prepare the Mock object
		final MessageProducer producer = context.mock(MessageProducer.class);
		context.checking(new Expectations() {{
            atLeast(1).of(producer).createMessage(with(allOf(startsWith("<courseTemplate>"), endsWith("</courseTemplate>"))));
        }});

		URLHandlingTemplate template = new HttpHandlingTemplateImpl();
		XMLStreamHandlingCallbackImpl xmlImpl = new XMLStreamHandlingCallbackImpl();		
		xmlImpl.setMessageProducer(producer);
		xmlImpl.setChildDepth(2);
		xmlImpl.setInputEncoding("UTF-8");
		try {
			template.handleURL(httpURL, xmlImpl);
			context.assertIsSatisfied();
		} catch (RuntimeException re) {
			fail("Should have not thrown an exception." + re.getMessage());			
		}
	}
	
	public void testFtpReadFailWithHttpImplementation() {
		URLHandlingTemplate template = new HttpHandlingTemplateImpl();
		XMLStreamHandlingCallbackImpl xmlImpl = new XMLStreamHandlingCallbackImpl();		
		xmlImpl.setMessageProducer(new SystenOutMessageProducerImpl());
		xmlImpl.setChildDepth(2);
		try {
			template.handleURL(ftpURL, xmlImpl);
			fail("IllegalStateExcception should have been thrown with a 'unsupported protocol' error message.");			
		} catch (IllegalStateException ie) {
			assertTrue(ie.getMessage().startsWith("unsupported protocol"));
		}
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
