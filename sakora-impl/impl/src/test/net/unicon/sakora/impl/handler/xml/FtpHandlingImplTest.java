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
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import junit.framework.TestCase;
import net.unicon.sakora.api.handler.url.URLHandlingTemplate;
import net.unicon.sakora.api.handler.xml.XMLStreamHandlingCallback;
import net.unicon.sakora.impl.exception.SakoraRuntimeException;

import org.apache.commons.net.ftp.FTPClientConfig;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class FtpHandlingImplTest extends TestCase
{
	ApplicationContext appContext;
	ApplicationContext ftpContext;
	private Mockery context = new Mockery() {{
		setImposteriser(ClassImposteriser.INSTANCE);
	}};

	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		appContext = new ClassPathXmlApplicationContext("components.xml");
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}
	
	public void testHandleUrlMalformedUrl() {
		URLHandlingTemplate ftpHandlering = (URLHandlingTemplate)appContext.getBean("FtpHandlingTemplateImpl"); 
		final XMLStreamHandlingCallback callback = context.mock(XMLStreamHandlingCallback.class);
//		final InputStream responseStream = context.mock(InputStream.class);
//		final FTPClientConfig client = context.mock(FTPClientConfig.class);
//		
//		context.checking(new Expectations() {{
//            one(callback).handleStream(responseStream);
//        }});
//		
		try {
			ftpHandlering.handleURL("invalidurl", callback);
			fail("malformedUrlException should have been thrown.");
		} catch (SakoraRuntimeException se) {
			assertEquals("Exception is not a MalformedURLExcpetion", 
					MalformedURLException.class, se.getCause().getClass());
		}
	}

	public void testHandleUrlConnectFailed() {
			URLHandlingTemplate ftpHandlering = (URLHandlingTemplate)appContext.getBean("FtpHandlingTemplateImpl"); 
			final XMLStreamHandlingCallback callback = context.mock(XMLStreamHandlingCallback.class);
//			final InputStream responseStream = context.mock(InputStream.class);
//			final FTPClientConfig client = context.mock(FTPClientConfig.class);
//			
//			context.checking(new Expectations() {{
//	            one(callback).handleStream(responseStream);
//	        }});
//			
			try {
				ftpHandlering.handleURL("ftp://bogus.ftpserver.com/invalidurl", callback);
				fail("SocketException should have been thrown.");
			} catch (SakoraRuntimeException se) {
				assertEquals("Exception is not a MalformedURLExcpetion", 
						UnknownHostException.class, se.getCause().getClass());
			}
	}

	public void testHandleUrlRead() {
		ftpContext = new ClassPathXmlApplicationContext("ftpserver-components.xml");
		if (ftpContext.getBean("stubFtpServer") == null) {
			System.err.println("Cannot find FTP Stub server");			
		}
		URLHandlingTemplate ftpHandlering = (URLHandlingTemplate)appContext.getBean("FtpHandlingTemplateImpl"); 
		final XMLStreamHandlingCallback callback = context.mock(XMLStreamHandlingCallback.class);
		
		context.checking(new Expectations() {{
            one(callback).handleStream(with(any(InputStream.class)));
        }});
		
		ftpHandlering.handleURL("ftp://localhost:10021/invalidurl", callback);
	}
}
