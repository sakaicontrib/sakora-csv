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
package net.unicon.sakora.impl.ims;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.startsWith;
import junit.framework.TestCase;
import net.unicon.sakora.api.handler.xml.XMLStreamHandlingCallback;
import net.unicon.sakora.api.queue.MessageProducer;
import net.unicon.sakora.api.ws.ImsEsSyncService;
import net.unicon.sakora.api.ws.ImsSyncContext;
import net.unicon.sakora.impl.ImsEsSyncServiceFileImpl;
import net.unicon.sakora.api.ws.ImsSyncContextImpl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ImsEsSyncFileServiceTest extends TestCase {
	Mockery context;
	ApplicationContext appContext;
	String courseTemplateTag = "courseTemplate";
	String courseOfferingTag = "courseOffering";
	String courseSectionTag = "courseSection";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		appContext = new ClassPathXmlApplicationContext("components.xml");
		context = new Mockery();
	}

	public void testReadCourseTemplates() {
		ImsSyncContext syncContext = new ImsSyncContextImpl();
		ImsEsSyncService syncService = setupSyncServiceCall(courseTemplateTag,
				false, syncContext);
		syncService.readCourseTemplates(syncContext);
		context.assertIsSatisfied();
		assertNull(syncContext.getNewToken());
	}

	public void testReadCourseOfferings() {
		ImsSyncContext syncContext = new ImsSyncContextImpl();
		ImsEsSyncService syncService = setupSyncServiceCall(courseOfferingTag,
				false, syncContext);
		syncService.readCourseOfferings(syncContext);
		context.assertIsSatisfied();
		assertNull(syncContext.getNewToken());
	}

	public void testReadCourseSections() {
		ImsSyncContext syncContext = new ImsSyncContextImpl();
		ImsEsSyncService syncService = setupSyncServiceCall(courseSectionTag,
				false, syncContext);
		syncService.readCourseSections(syncContext);
		context.assertIsSatisfied();
		assertNull(syncContext.getNewToken());
	}

	private ImsEsSyncService setupSyncServiceCall(final String xmlTag,
			boolean checkSnapShot, ImsSyncContext syncContext) {
		// Prepare the Mock object
		final MessageProducer producer = context.mock(MessageProducer.class);
		context.checking(new Expectations() {
			{
				atLeast(1).of(producer).createMessage(
						with(allOf(startsWith("<" + xmlTag + ">"),
								endsWith("</" + xmlTag + ">"))));
			}
		});

		ImsEsSyncServiceFileImpl syncService = (ImsEsSyncServiceFileImpl) appContext
				.getBean("net.unicon.sakora.file.ImsEsSyncService");
		syncService.setCleanupData("false");
		XMLStreamHandlingCallback xmlCallBack = syncService
				.getXmlStreamHandlingCallback();
		xmlCallBack.setMessageProducer(producer);

		if (checkSnapShot) {
			syncContext.setFeedType(ImsSyncContext.FeedType.SNAPSHOT);
		} else {
			syncContext.setFeedType(ImsSyncContext.FeedType.INCREMENTAL);
			syncContext.setLastToken("2008-01-01");
		}
		return syncService;
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
