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
package net.unicon.sakora.impl.ims.job;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import junit.framework.TestCase;
import net.unicon.sakora.api.SakoraService;
import net.unicon.sakora.api.ws.ImsEsSyncService;
import net.unicon.sakora.api.ws.ImsSyncContext;
import net.unicon.sakora.impl.jobs.DataLoaderJobBean;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DataLoaderJobTest extends TestCase
{

	private DataLoaderJobBean jobBean;
	private Mockery context = new Mockery() {{
		setImposteriser(ClassImposteriser.INSTANCE);
	}};
	ApplicationContext appContext;
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		appContext = new ClassPathXmlApplicationContext("components.xml");
		jobBean = (DataLoaderJobBean) appContext.getBean("dataLoaderJobBean");
		
	}
	public void testExecuteSnapshot()
	{	
		final ImsEsSyncService syncServiceMock = context.mock(ImsEsSyncService.class);
		final SakoraService sakoraServiceMock = context.mock(SakoraService.class);
		final JobExecutionContext jobContext = context.mock(JobExecutionContext.class);
		final JobDetail detail = context.mock(JobDetail.class);
		final JobDataMap map = new JobDataMap();
		context.checking(new Expectations() {
		  {
			one(syncServiceMock).readCourseTemplates(with(any(ImsSyncContext.class)));
			one(syncServiceMock).readCourseOfferings(with(any(ImsSyncContext.class)));
			one(syncServiceMock).readCourseSections(with(any(ImsSyncContext.class)));
			one(syncServiceMock).readSectionAssociations(with(any(ImsSyncContext.class)));
			one(syncServiceMock).readPersons(with(any(ImsSyncContext.class)));
			one(syncServiceMock).readMemberships(with(any(ImsSyncContext.class)));
			one(jobContext).getJobDetail(); will(returnValue(detail));
			one(detail).getJobDataMap(); will(returnValue(map));
			exactly(2).of(detail).getName(); will(returnValue("Name"));
			
			
			one(syncServiceMock).readGroups(with(any(ImsSyncContext.class)));
			one(syncServiceMock).readCourseTemplates(with(any(ImsSyncContext.class)));
			one(syncServiceMock).readCourseOfferings(with(any(ImsSyncContext.class)));
			one(syncServiceMock).readCourseSections(with(any(ImsSyncContext.class)));
			one(syncServiceMock).readSectionAssociations(with(any(ImsSyncContext.class)));
			one(syncServiceMock).readPersons(with(any(ImsSyncContext.class)));
			one(syncServiceMock).readMemberships(with(any(ImsSyncContext.class)));
			one(jobContext).setResult(with(any(Map.class))); // TODO needs to be more precise
		  }
		});
		
		jobBean.setImsEsSyncService(syncServiceMock);
		jobBean.setSakoraService(sakoraServiceMock);
		jobBean.setFeedType(ImsSyncContext.FeedType.SNAPSHOT);

		try {
			jobBean.execute(jobContext);
		} catch (JobExecutionException ex) {
			
		}
	}
	
	public void testExecuteIncremental()
	{	
		final String lastRun = String.valueOf(new Date().getTime());
		
		final ImsEsSyncService syncServiceMock = context.mock(ImsEsSyncService.class);
		final SakoraService sakoraServiceMock = context.mock(SakoraService.class);
		final JobExecutionContext jobContext = context.mock(JobExecutionContext.class);
		final JobDetail detail = context.mock(JobDetail.class);
		final JobDataMap map = new JobDataMap();
		context.checking(new Expectations() {
		  {
			one(syncServiceMock).readCourseTemplates(with(any(ImsSyncContext.class)));
			one(syncServiceMock).readCourseOfferings(with(any(ImsSyncContext.class)));
			one(syncServiceMock).readCourseSections(with(any(ImsSyncContext.class)));
			one(syncServiceMock).readSectionAssociations(with(any(ImsSyncContext.class)));
			one(syncServiceMock).readPersons(with(any(ImsSyncContext.class)));
			one(syncServiceMock).readMemberships(with(any(ImsSyncContext.class)));
			one(jobContext).getJobDetail(); will(returnValue(detail));
			one(detail).getJobDataMap(); will(returnValue(map));
			exactly(2).of(detail).getName(); will(returnValue("Name"));
			
			one(syncServiceMock).readGroups(with(any(ImsSyncContext.class)));
			one(jobContext).setResult(with(any(Map.class))); // TODO needs to be more precise
		  }
		});
		
		jobBean.setImsEsSyncService(syncServiceMock);
		jobBean.setSakoraService(sakoraServiceMock);
		jobBean.setFeedType(ImsSyncContext.FeedType.INCREMENTAL);

		try {
			jobBean.execute(jobContext);
		} catch (JobExecutionException ex) {
			
		}
	}
}
