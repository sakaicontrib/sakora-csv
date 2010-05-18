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
package net.unicon.sakora.test;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;
import net.unicon.sakora.impl.handler.xml.FtpHandlingImplTest;
import net.unicon.sakora.impl.handler.xml.XMLStreamHandlingCallbackImplSystemOutTest;
import net.unicon.sakora.impl.handler.xml.XMLStreamHandlingCallbackImplTest;
import net.unicon.sakora.impl.ims.ImsContentDelegatorImplTest;
import net.unicon.sakora.impl.ims.ImsCreateImplTest;
import net.unicon.sakora.impl.ims.ImsEsSyncFileServiceTest;
import net.unicon.sakora.impl.ims.job.DataLoaderJobTest;
import net.unicon.sakora.impl.ims.ws.client.URLHandlingTemplateTest;
import net.unicon.sakora.impl.msg.MessageListerTest;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ImsTestSuite extends TestSuite {
	private static ApplicationContext appContext; 
	public static Test suite() {

		TestSuite suite = new TestSuite("Unit tests for SAKORA ImsSyncService");
		suite.addTestSuite(XMLStreamHandlingCallbackImplTest.class);
		suite.addTestSuite(URLHandlingTemplateTest.class);
		//suite.addTestSuite(ImsEsSyncServiceTest.class);
		suite.addTestSuite(ImsEsSyncFileServiceTest.class);
		suite.addTestSuite(ImsCreateImplTest.class);
		suite.addTestSuite(XMLStreamHandlingCallbackImplSystemOutTest.class);
		suite.addTestSuite(MessageListerTest.class);
		suite.addTestSuite(ImsContentDelegatorImplTest.class);
		suite.addTestSuite(DataLoaderJobTest.class);
		suite.addTestSuite(FtpHandlingImplTest.class);
		//suite.addTestSuite(CsvSyncServiceTest.class);

		TestSetup decoratedSuite = new TestSetup(suite) {
			protected void setUp() throws Exception {
				oneTimeSetup();
			}

			protected void tearDown() throws Exception {
				oneTimeTearDown();
			}
		};

		return decoratedSuite;
	}

	private static void oneTimeSetup() {
		appContext = new ClassPathXmlApplicationContext("jetty-components.xml");
		if (appContext.getBean("Server") == null) {
			System.err.println("Cannot find Jetty server");
		}
	}

	private static void oneTimeTearDown() {
	}
}
