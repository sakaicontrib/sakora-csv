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
package net.unicon.sakora.tool.ws.handler;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import net.unicon.sakora.api.queue.MessageProducer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.xml.transform.StringResult;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

public class JMSManagementHandler
{
	static final Log log = LogFactory.getLog(JMSManagementHandler.class);

	private Marshaller marshaller;
	private MessageProducer messageProducer;

	public void setMarshaller(Marshaller marshaller)
	{
		this.marshaller = marshaller;
	}

	public Marshaller getMarshaller()
	{
		return marshaller;
	}

	public Unmarshaller getUnmarshaller()
	{
		return (Unmarshaller) marshaller;
	}

	public void setMessageProducer(MessageProducer messageProducer)
	{
		this.messageProducer = messageProducer;
	}

	/**
	 * @param requestSource
	 * @return
	 */
	protected void parseSourceAndProcess(Source requestSource, String elementName)
	{
		XmlPullParser parser = null;
		XmlSerializer serializer = null;
		try
		{
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(false);
			// auto-detect does not work, so we have to scan the first 4 bytes
			// and switch to to correct encoding manually
			// factory.setFeature("detect-encoding", true);
			parser = factory.newPullParser();
			serializer = factory.newSerializer();
			Transformer trans = TransformerFactory.newInstance().newTransformer();
			StringResult result = new StringResult();
			trans.transform(requestSource, result);
			if (parser != null)
			{
				parser.setInput(new StringReader(result.toString()));
				int eventType = parser.getEventType();
				do
				{
					if (eventType == XmlPullParser.START_DOCUMENT)
					{
						log.debug("Started parsing stream.");
					}
					else if (eventType == XmlPullParser.START_TAG)
					{
						String name = parser.getName();
						String uri = parser.getNamespace();
						if (name.equalsIgnoreCase(elementName))
						{
							// process the found element
							processElement(parser, serializer, name, uri);
						}
					} // ignore all other events.
					eventType = parser.next();
				}
				while (eventType != XmlPullParser.END_DOCUMENT);
				log.debug("Finished parsing stream.");
			}
			else
			{
				log.error("Unable to create pull parser from XMLPullParserFactory.");
			}
		}
		catch (XmlPullParserException xpe)
		{
			log.error("Error parsing inputstream ", xpe);
			if (parser != null)
			{
				log.error(parser.getPositionDescription());
			}
		}
		catch (IOException ioe)
		{
			log.error("Error parsing inputstream ", ioe);
			if (parser != null)
			{
				log.error(parser.getPositionDescription());
			}
		}
		catch (TransformerException te)
		{
			log.error("Error transforming source ", te);
		}
	}

	private void processElement(XmlPullParser parser, XmlSerializer serializer, String name, String uri)
			throws XmlPullParserException, IOException
	{
		StringWriter writer = new StringWriter();
		serializer.setOutput(writer);
		boolean readElement = true;
		int eventType = XmlPullParser.START_TAG;
		while (readElement)
		{
			if (eventType == XmlPullParser.TEXT)
			{
				serializer.text(parser.getText());
			}
			else
			{
				writer.write(parser.getText());
			}
			eventType = parser.next();
			String childName = parser.getName();
			if (eventType == XmlPullParser.END_TAG && name.equalsIgnoreCase(childName))
			{
				readElement = false;
				writer.write(parser.getText());
			}
	
		}
		messageProducer.createMessage(writer.getBuffer().toString());
		serializer.setOutput(null);
	}

}
