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
package net.unicon.sakora.api.handler.xml;

import java.io.InputStream;

import net.unicon.sakora.api.queue.MessageProducer;

/**
 * The class provides a callback to handle the file content from the IMS system. 
 */
public interface XMLStreamHandlingCallback {
	/**
	 * Set the specific message producer.
	 * 
	 * @param producer - message producer to handle the file segments
	 */
	void setMessageProducer(MessageProducer producer);
	
	/**
	 * Handle the input stream usually by parsing segments of the file and
	 * passing these segments on to the message producer.
	 *  
	 * @param responseStream - file content input stream.
	 */
	void handleStream(InputStream responseStream);
}
