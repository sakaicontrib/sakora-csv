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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import net.unicon.sakora.api.handler.xml.XMLStreamHandlingCallback;
import net.unicon.sakora.api.queue.MessageProducer;
import net.unicon.sakora.impl.exception.SakoraRuntimeException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

public class XMLStreamHandlingCallbackImpl implements XMLStreamHandlingCallback {
	private static final Log log = LogFactory.getLog(XMLStreamHandlingCallbackImpl.class);
	private MessageProducer messageProducer;
	private int childDepth = -1;			
	private String childName = null;
	private String inputEncoding = null;
	
	public MessageProducer getMessageProducer() {
		return messageProducer;
	}

	public void setMessageProducer(MessageProducer messageProducer) {
		this.messageProducer = messageProducer;
	}
	
	public int getChildDepth() {
		return childDepth;
	}

	public void setChildDepth(int childDepth) {
		this.childDepth = childDepth;
	}

	public String getChildName() {
		return childName;
	}

	public void setChildName(String childName) {
		this.childName = childName;
	}

	public void setInputEncoding(String inputEncoding) {
		this.inputEncoding = inputEncoding;
	}

	/*
	 * (non-Javadoc)
	 * @see net.unicon.sakora.api.handler.xml.XMLStreamHandlingCallback#handleStream(java.io.InputStream)
	 */
	public void handleStream(InputStream responseStream) {
		XmlPullParser parser = null;
		XmlSerializer serializer = null;
		checkConfiguration();
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			// auto-detect does not work, so we have to scan the first 4 bytes and switch to to correct encoding manually
			// factory.setFeature("detect-encoding", true);  
			parser = factory.newPullParser();
			serializer = factory.newSerializer();
			if (parser != null) { 
				parser.setInput(responseStream, inputEncoding);
				int eventType = parser.getEventType();
				do {
					if (eventType == XmlPullParser.START_DOCUMENT) {
						log.debug("Started parsing stream.");
					} else if (eventType == XmlPullParser.START_TAG) {
						String name = parser.getName();
						String uri = parser.getNamespace();
						if ((getChildName() != null && getChildName().equals(name)) ||
								getChildDepth() != -1 && getChildDepth() == parser.getDepth()){
							processElement(parser,serializer,name, uri);
						}
					} // ignore all other events.
					eventType = parser.next();
				} while (eventType != XmlPullParser.END_DOCUMENT);
				log.debug("Finished parsing stream.");
			} else {
				log.error("Unable to create pull parser from XMLPullParserFactory.");
			}
		} catch (XmlPullParserException xpe) {
			log.error("Error parsing inputstream ", xpe);
			if (parser != null) {
				log.error(parser.getPositionDescription());
			}
		} catch (IOException ioe) {
			log.error("Error parsing inputstream ", ioe);			
			if (parser != null) {
				log.error(parser.getPositionDescription());
			}
		}
	}

	private void checkConfiguration() {
		if (getMessageProducer() == null) {
			throw new SakoraRuntimeException("MessageProducer has to be specified!");			
		}
		if (getChildDepth() == -1 && getChildName() == null) {
			throw new SakoraRuntimeException("Child name or child depth had to be specify for message split operation");			
		}
	}

	private void processElement(XmlPullParser parser, XmlSerializer serializer, String name, String uri) throws XmlPullParserException, IOException {
		StringWriter writer = new StringWriter();
		serializer.setOutput(writer);
		boolean readElement = true;
		int eventType = XmlPullParser.START_TAG;
		while (readElement) {
			if (eventType == XmlPullParser.TEXT) {
				serializer.text(parser.getText());
			} else {
				writer.write(parser.getText());
			}
			eventType = parser.next();
			String childName = parser.getName();
			if (eventType == XmlPullParser.END_TAG && name.equals(childName)) {
				readElement = false;
				writer.write(parser.getText());
			}
			
		}
		messageProducer.createMessage(writer.getBuffer().toString());
		serializer.setOutput(null);
	}

}
