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
package net.unicon.sakora.impl.queue;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import net.unicon.sakora.api.queue.MessageProducer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

/**
 * A simplified interface to abstract out the details of the
 * implementation from the caller.  This interface is used
 * to place a new XML message chunk on to the persistent
 * queue, without necessarily knowing how that persistent queue
 * is implemented.
 * 
 * This implementation uses JMS, and specifically Spring
 * JMS to accomplish this task.
 */
public class MessageProducerImpl implements MessageProducer {
	private static Log log = LogFactory.getLog(MessageProducerImpl.class);
	private JmsOperations jmsTemplate;
	private String queueName;

	/**
	 * Place a new XML message chunk on to the JMS Queue. 
	 * @param messageText the XML message chunk
	 */
	public void createMessage(String messageText) {
		log.debug("Placing a new message on the persistent queue.");
		log.debug("message text: "+messageText);
		MessageCreator mc = new MessageCreatorImpl(messageText);
		jmsTemplate.send(queueName, mc);
	}

	/**
	 * The JMS connection factory to use for JMS sessions
	 * @param cf The JMS connection factory
	 */
	public void setConnectionFactory(ConnectionFactory cf) {
		jmsTemplate = new JmsTemplate(cf);
	}

	/**
	 * The JMS Queue name to send messages to
	 * @param queue The JMS Queue
	 */
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	/**
	 * A simple inner class implementing the Spring JMS MessageCreator
	 * callback interface.
	 */
	private static class MessageCreatorImpl implements MessageCreator {
		String messageText = "";
		
		/**
		 * Creates an instance of this object
		 * @param text the body of any future JMS message object
		 * created by this object
		 */
		public MessageCreatorImpl(String text) {
			messageText = text;
		}
		
		/** 
		 * Create a JMS message object to be sent
		 */
		public Message createMessage(Session session) throws JMSException {
			return session.createTextMessage(messageText);
		}
	}
}
