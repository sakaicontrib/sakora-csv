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

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import net.unicon.sakora.api.ims.ImsContentDelegator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MessageListenerImpl implements MessageListener {
	private final static Log log = LogFactory.getLog(MessageListenerImpl.class);

	private ImsContentDelegator imsContentDelegator;

	public void setImsContentDelegator(ImsContentDelegator imsContentDelegator) {
		this.imsContentDelegator = imsContentDelegator;
	}
	
	public void onMessage(Message message) {

		try {
			// I don't like instanceof, but don't know how to make the type case save without it. 
			if (message instanceof TextMessage) {
				TextMessage txtMessage = (TextMessage)message;
				String xmlContent = txtMessage.getText();			
				if (log.isDebugEnabled()) {
					log.debug("Received message id: " +  message.getJMSMessageID() + " content: " + xmlContent);					
				}
				imsContentDelegator.handleXmlContent(xmlContent);
			}
		} catch (JMSException jme) {
			log.error(jme.getMessage(), jme);
		}
	}
}
