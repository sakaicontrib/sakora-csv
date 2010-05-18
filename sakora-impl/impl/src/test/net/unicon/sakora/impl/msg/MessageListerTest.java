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
package net.unicon.sakora.impl.msg;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import junit.framework.TestCase;
import net.unicon.sakora.api.ims.ImsContentDelegator;
import net.unicon.sakora.impl.queue.MessageListenerImpl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MessageListerTest extends TestCase { 
	private Mockery context;
	private ApplicationContext appContext;
	private static final String MESSAGE_TEXT = "messageText";
	@Override
	protected void setUp() throws Exception {
		context = new Mockery();
		appContext = new ClassPathXmlApplicationContext("components.xml");
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testBeanDefinition() {
		assertNotNull("Application Context not loaded", appContext);
		MessageListenerImpl bean = (MessageListenerImpl)appContext.getBean("net.unicon.sakora.impl.queue.MessageListenerImpl"); 
		assertNotNull("Bean definition for net.unicon.sakora.impl.queue.MessageListenerImpl not found", bean);
	}
	
	public void testOnMessage() {
		MessageListenerImpl listener = (MessageListenerImpl) appContext.getBean("net.unicon.sakora.impl.queue.MessageListenerImpl");
		final TextMessage mockMessage = context.mock(TextMessage.class); 
		final ImsContentDelegator imsContentDelegator = context.mock(ImsContentDelegator.class);
		try {
			context.checking( new Expectations() {{
				one(mockMessage).getText(); will(returnValue(MESSAGE_TEXT));
				one(mockMessage).getJMSMessageID(); will(returnValue("1233"));
				one(imsContentDelegator).handleXmlContent(MESSAGE_TEXT);
			}});
			
			listener.setImsContentDelegator(imsContentDelegator);
			listener.onMessage(mockMessage);
			context.assertIsSatisfied();
		} catch (JMSException jme) {
			fail("Exception thrown " + jme.getMessage());
		}
	}
}
