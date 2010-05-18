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
package net.unicon.sakora.impl.handler.xml;

import java.io.InputStream;

import org.jmock.Expectations;
import org.jmock.Mockery;

import junit.framework.TestCase;
import net.unicon.sakora.api.queue.MessageProducer;
import net.unicon.sakora.impl.msg.SystenOutMessageProducerImpl;

public class XMLStreamHandlingCallbackImplSystemOutTest extends TestCase {
	private Mockery context;
	
	@Override
	protected void setUp() throws Exception {
		context = new Mockery();
	}
	
	/**
	 * Use an expected string that contains two lines.  This test should currently
	 * process two messages, one per line, where the values of each 'createMessage'
	 * call are known based on the string that was used.
	 */
	public void testHandleStreamFileStream() {
		// Prepare the test object
		final MessageProducer mockProducer = context.mock(MessageProducer.class);  
		context.checking( new Expectations() {{
			atLeast(1).of(mockProducer).createMessage(with(any(String.class)));
		}});
		XMLStreamHandlingCallbackImpl testObj = new XMLStreamHandlingCallbackImpl();
//		testObj.setMessageProducer(new SystenOutMessageProducerImpl());
		testObj.setMessageProducer(mockProducer);
		testObj.setChildDepth(2);
		InputStream stream = XMLStreamHandlingCallbackImpl.class.getResourceAsStream("/webapp/readCourseTemplates.xml");

		// execute the test
		testObj.handleStream(stream);
		
	}

	@Override
	protected void tearDown() throws Exception {
		// nothing to do yet
	}
}
