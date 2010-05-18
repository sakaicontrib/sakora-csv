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

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;
import net.unicon.sakora.api.queue.MessageProducer;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;

public class XMLStreamHandlingCallbackImplTest extends TestCase {

	public static final String SIMPLE_TEXT_BLOCK = "<root><child>Hello</child><child>World!</child></root>";
	private Mockery context;
	
	@Override
	protected void setUp() throws Exception {
		context = new Mockery();
	}
	
	/**
	 * Test that the stream handler will fail with some kind of exception
	 * if you try to call "handleStream" with a valid stream, but no
	 * MessageProducer is set (messageProducer == null).
	 */
	public void testHandleStreamWithNullProducer() {
		XMLStreamHandlingCallbackImpl testObj = new XMLStreamHandlingCallbackImpl();
		testObj.setMessageProducer(null);

		ByteArrayInputStream stream = new ByteArrayInputStream(SIMPLE_TEXT_BLOCK.getBytes());

		try {
			testObj.handleStream(stream);
			fail("No exception was thrown, when one was expected.");
		} catch(RuntimeException re) {
			// reason doesn't matter, as long as its a runtime exception
			// therefore no more testing required, this is a success
		}
	}

	/**
	 * Test that the stream handler will fail with some kind of exception
	 * if you try to call "handleStream" with a null stream and
	 * a valid messageProducer.
	 */
	public void testHandleStreamWithNullStream() {
		XMLStreamHandlingCallbackImpl testObj = new XMLStreamHandlingCallbackImpl();
		MessageProducer producer = context.mock(MessageProducer.class);
		testObj.setMessageProducer(producer);

		try {
			testObj.handleStream(null);
			fail("No exception was thrown, when one was expected.");
		} catch(RuntimeException re) {
			// reason doesn't matter, as long as its a runtime exception
			// therefore no more testing required, this is a success
		}
	}

	/**
	 * Use an expected string that contains two lines.  This test should currently
	 * process two messages, one per line, where the values of each 'createMessage'
	 * call are known based on the string that was used.
	 */
	public void testHandleStreamTwoLines() {
		// Prepare the Mock object
		final MessageProducer producer = context.mock(MessageProducer.class);
		final Sequence maintest = context.sequence("maintest");
		context.checking(new Expectations() {{
            one(producer).createMessage("<child>Hello</child>"); inSequence(maintest);
            one(producer).createMessage("<child>World!</child>"); inSequence(maintest);
        }});

		// Prepare the test object
		XMLStreamHandlingCallbackImpl testObj = new XMLStreamHandlingCallbackImpl();		
		testObj.setMessageProducer(producer);
		testObj.setChildName("child");
		ByteArrayInputStream stream = new ByteArrayInputStream(SIMPLE_TEXT_BLOCK.getBytes());

		// execute the test
		testObj.handleStream(stream);
		
		// Validate the expectations have been met
		context.assertIsSatisfied();
	}

	@Override
	protected void tearDown() throws Exception {
		// nothing to do yet
	}
}
