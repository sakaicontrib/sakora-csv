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
package net.unicon.sakora.impl.jobs;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.sakaiproject.api.app.scheduler.JobBeanWrapper;
import org.sakaiproject.api.app.scheduler.SchedulerManager;


public class ResultMapCopyingJobListener implements JobListener {
	
	private static final Log log = LogFactory.getLog(ResultMapCopyingJobListener.class);
	
	private String name;
	private SchedulerManager schedulerManager;
	private String listenForJobBeanName;
	private String targetJobBeanName;
	private Collection<Object> keysToCopy;
	
	public void init() throws SchedulerException {
		schedulerManager.getScheduler().addGlobalJobListener(this);
	}
	
	public void destroy() throws SchedulerException {
		schedulerManager.getScheduler().removeGlobalJobListener(this);
	}

	public void jobExecutionVetoed(JobExecutionContext context) {
		// nothing to do
	}

	public void jobToBeExecuted(JobExecutionContext context) {
		// nothing to do
	}

	public void jobWasExecuted(JobExecutionContext context,
			JobExecutionException executionException) {
		if ( !(isHandleable(context, executionException)) ) {
			return;
		}
		try {
			JobDetail targetJobDetail = lookupTargetJobDetail(context,executionException);
			if ( targetJobDetail == null ) {
				return;
			}
			copyResult(context, executionException,targetJobDetail);
		} catch ( SchedulerException e ) {
			log.error("Failed to lookup target job or copy results into it (" + targetJobBeanName + ")", e);
		}
		
	}

	protected boolean isHandleable(JobExecutionContext context,
			JobExecutionException executionException) {
		return executionException == null && 
			matchJobDetailBeanName(listenForJobBeanName, context.getJobDetail());
	}
	
	protected boolean matchJobDetailBeanName(String expected, JobDetail candidate) {
		String actual = candidate.getJobDataMap().getString(JobBeanWrapper.SPRING_BEAN_NAME);
		return expected.equals(actual);
	}
	
	protected void copyResult(JobExecutionContext context,
			JobExecutionException executionException, JobDetail targetJobDetail) throws SchedulerException {
		Map<?,?> sourceMap = (Map<?,?>) context.getResult();
		JobDataMap targetMap = targetJobDetail.getJobDataMap();
		for ( Object keyToCopy : keysToCopy ) {
			Object valueToCopy = sourceMap.get(keyToCopy);
			targetMap.put(keyToCopy, valueToCopy);
		}
		schedulerManager.getScheduler().addJob(targetJobDetail, true);
	}

	protected JobDetail lookupTargetJobDetail(JobExecutionContext context,
			JobExecutionException executionException) throws SchedulerException {
		String[] jobDetailNames = 
			schedulerManager.getScheduler().getJobNames(Scheduler.DEFAULT_GROUP);
		for ( String jobDetailName : jobDetailNames ) {
			JobDetail jobDetail =
				schedulerManager.getScheduler().getJobDetail(jobDetailName, Scheduler.DEFAULT_GROUP);
			if ( matchJobDetailBeanName(targetJobBeanName, jobDetail) ) {
				return jobDetail;
			}
		}
		return null;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public SchedulerManager getSchedulerManager() {
		return schedulerManager;
	}

	public void setSchedulerManager(SchedulerManager schedulerManager) {
		this.schedulerManager = schedulerManager;
	}
	
	public String getListenForJobBeanName() {
		return listenForJobBeanName;
	}

	public void setListenForJobBeanName(String listenForJobBeanName) {
		this.listenForJobBeanName = listenForJobBeanName;
	}
	
	public String getTargetJobBeanName() {
		return targetJobBeanName;
	}

	public void setTargetJobBeanName(String targetJobBeanName) {
		this.targetJobBeanName = targetJobBeanName;
	}
	
	public Collection<Object> getKeysToCopy() {
		return keysToCopy;
	}

	public void setKeysToCopy(Collection<Object> keysToCopy) {
		this.keysToCopy = keysToCopy;
	}

}
