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
package net.unicon.sakora.tool.test;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;
import net.unicon.sakora.tool.test.ws.server.CourseEndpointTest;
import net.unicon.sakora.tool.test.ws.server.GroupEndpointTest;
import net.unicon.sakora.tool.test.ws.server.MembershipEndpointTest;
import net.unicon.sakora.tool.test.ws.server.PersonEndpointTest;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ImsSampleEventTestSuite extends TestSuite {
	private static ApplicationContext appContext; 
	public static Test suite() {

		TestSuite suite = new TestSuite("Unit tests for SAKORA ImsSyncService");
		suite.addTestSuite(CourseEndpointTest.class);
		suite.addTestSuite(GroupEndpointTest.class);
		suite.addTestSuite(PersonEndpointTest.class);
		suite.addTestSuite(MembershipEndpointTest.class);

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
		((ClassPathXmlApplicationContext)appContext).close();
	}
}
