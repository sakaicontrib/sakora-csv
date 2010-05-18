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

/*
 * Data Loader Job Bean
 * 
 * Implements a StatefulJob as a Spring Bean for loading course management data from files.
 * 
 * Author:	Brian Sawert
 * Company: Unicon, Inc.
 * Email:	bsawert@unicon.net
 * 
 * Date:	12 November 2007
 * 
 * Note:
 * 	This class is wrapped by a singleton instance of
 * org.sakaiproject.component.app.scheduler.jobs.SpringStatefulJobBeanWrapper,
 * so local data values (taskList) persist between executions. Because it implements
 * StatefulJob, the JobDataMap also persists between calls.
 */

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.unicon.sakora.api.SakoraService;
import net.unicon.sakora.api.ws.ImsEsSyncService;
import net.unicon.sakora.api.ws.ImsSyncContext;
import net.unicon.sakora.api.ws.ImsSyncContext.FeedType;
import net.unicon.sakora.api.ws.ImsSyncContextImpl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

/**
 * Author:	Brian Sawert
 * Company: Unicon, Inc.
 * Email:	bsawert@unicon.net
 * 
 * Date:	12 November 2007
 */
public class DataLoaderJobBean implements StatefulJob {
	private static final String LAST_RUN = "LAST_RUN";
	
	protected SakoraService sakoraService;
	protected ImsEsSyncService imsEsSyncService;
	protected List<String> taskList;
	private FeedType feedType;
	
	private static final Log log = LogFactory.getLog(DataLoaderJobBean.class);

	public DataLoaderJobBean() {
	}

	public void init() {
		log.info("Initializing " + getClass().getName());
	}
	
	public void destroy() {
		log.info("Destroying " + getClass().getName());
	}
	
	// begin spring injection

	public void setSakoraService(SakoraService sakoraService) {
		this.sakoraService = sakoraService;
	}

	public void setImsEsSyncService(ImsEsSyncService imsEsSyncService) {
		this.imsEsSyncService = imsEsSyncService;
	}

	public void setTaskList(List<String> taskList) {
		// set the ordered master task list
		this.taskList = taskList;
	}
	
	/**
	 * Access the {@Link FeedType} configured for this job instance.
	 * 
	 * @return
	 */
	public FeedType getFeedType() {
		return feedType;
	}

	/**
	 * Set the {@link FeedType} for this job instance. Necessary since
	 * we have no good way to pre-configure job state maps. And depending
	 * on trigger names is fragile, e.g. manually fired triggers get
	 * effectively randomized strings as names.
	 * 
	 * <p>This field has no default value since behaviors differ
	 * significantly for each feed type and we would rarely intend to
	 * have multiple copies of the job sharing the same feed type.</p>
	 * 
	 * @param feedType
	 */
	public void setFeedType(FeedType feedType) {
		this.feedType = feedType;
	}

	// end spring injection
	
	
	/**
	 * Quartz execute routine - this does not try to determine feed state or success
	 * of last load. It just calls the web service to start things off.
	 */
	@SuppressWarnings("unchecked")
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// get persistent data map from JobDetail
		JobDetail detail = context.getJobDetail();
		JobDataMap map = detail.getJobDataMap();
		
		if ( feedType == null ) {
			throw new JobExecutionException("Must specify a FeedType");
		}
		
		// get time stamp in millis
		Date thisRun = new Date();
		
		// get last run time
		Date lastRun = null;
		if (map.get(LAST_RUN) != null) {
			lastRun = new Date(Long.parseLong((String)map.get(LAST_RUN)));
		}

		// save time stamp for next run
		map.put(LAST_RUN, String.valueOf(thisRun.getTime()));
		
		// here is where we'll do the actual processing
		for (String taskName : taskList) {
			log.info("running " + taskName);
			ImsSyncContext syncContext = new ImsSyncContextImpl();
			syncContext.setContextName(taskName);
			syncContext.setFeedType(feedType);
			syncContext.setFinished(false);
			syncContext.setLastToken(map.getString(taskName));
			
			
			// there must be a better way to do this - bas
			if (taskName.equalsIgnoreCase("Group")) {
				//succeeded = imsEsSyncService.readGroups(syncContext);
				imsEsSyncService.readGroups(syncContext);
			}
			else if (taskName.equalsIgnoreCase("CourseTemplate")) {
				//succeeded = imsEsSyncService.readCourseTemplates(syncContext);
				imsEsSyncService.readCourseTemplates(syncContext);
			}
			else if (taskName.equalsIgnoreCase("CourseOffering")) {
				//succeeded = imsEsSyncService.readCourseOfferings(syncContext);
				imsEsSyncService.readCourseOfferings(syncContext);
			}
			else if (taskName.equalsIgnoreCase("CourseSection")) {
				//succeeded = imsEsSyncService.readCourseSections(syncContext);
				imsEsSyncService.readCourseSections(syncContext);
			}
			else if (taskName.equalsIgnoreCase("SectionAssociation")) {
				//succeeded = imsEsSyncService.readSectionAssociations(syncContext);
				imsEsSyncService.readSectionAssociations(syncContext);
			}
			else if (taskName.equalsIgnoreCase("Person")) {
				//succeeded = imsEsSyncService.readPersons(syncContext);
				imsEsSyncService.readPersons(syncContext);
			}
			else if (taskName.equalsIgnoreCase("Membership")) {
				//succeeded = imsEsSyncService.readMemberships(syncContext);
				imsEsSyncService.readMemberships(syncContext);
			}
			else {
				log.error("Unknown sync service call: " + taskName);
			}
			
			
			// store the last sync date in the map, so it can be persisted
			if (syncContext.getNewToken() != null) {
				map.put(taskName, syncContext.getNewToken());
			}
		}
		
		context.setResult(new HashMap(map));
		
	}

	

}
