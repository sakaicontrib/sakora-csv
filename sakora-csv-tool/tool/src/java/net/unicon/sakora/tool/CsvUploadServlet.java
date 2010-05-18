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
package net.unicon.sakora.tool;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.unicon.sakora.api.csv.CsvSyncService;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.sakaiproject.api.app.scheduler.JobBeanWrapper;
import org.sakaiproject.api.app.scheduler.SchedulerManager;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;

import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.ParamPart;
import com.oreilly.servlet.multipart.Part;

/**
 * Simple POST only REST webservice for uploading csv data files for the Sakora
 * CsvSyncService.
 * 
 * Supports uploading large files, the default max upload size is 1GB which can be set with
 * net.unicon.sakora.csv.maxFileSize in sakai.properties, value in MBs
 * 
 * The path to upload the files to is configurable via:
 * net.unicon.sakora.csv.basePath in sakai.properties, it defaults to /tmp
 * 
 * If 'runJob' is passed in equal to 'true' the quartz sync job will be fired
 * off at the end of the request cycle.
 * 
 * Example: Calling this service to upload some of the unittest test data and run the sync
 * from a unix shell would look like:
 * 
 * curl -F "username=admin" -F "password=admin" \
 * -F "sessions=@sakora-impl/impl/resources/test/webapp/data/sessions.csv" \
 * -F "courseSections=@sakora-impl/impl/resources/test/webapp/data/courseSections.csv" \
 * http://localhost:8080/sakai-sakora-tool/csv -F "runJob=true"
 * 
 * The only thing about calling this service that possibly isn't obvious is that you need to
 * pass in the username and password before the files. This is because we are streaming in
 * the input and need to verify your authentication and authorization before using up 
 * potentially significant amounts of system disk and bandwidth. So if you are calling this
 * from an html for, just put the 'username' and 'password' fields first in your form.
 * 
 * @author Joshua Ryan
 *
 */
@SuppressWarnings("serial")
public class CsvUploadServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
	throws IOException {
    	ServerConfigurationService serverConfigurationService 
			= (ServerConfigurationService) ComponentManager.get("org.sakaiproject.component.api.ServerConfigurationService");

		String basePath = ((CsvSyncService)ComponentManager.get("net.unicon.sakora.api.csv.CsvSyncService")).getBatchUploadDir();

		// 1GB, should be enough, but it can be configured via net.unicon.sakora.csv.maxFileSize
		int maxFileSize = 1000;
		String username = "";
		String password = "";
		boolean runJob = false;
		PrintWriter out = resp.getWriter();

		maxFileSize = serverConfigurationService.getInt("net.unicon.sakora.csv.maxFileSize", maxFileSize);
		MultipartParser parser = new MultipartParser(req, maxFileSize*1024*1024, true, true, "utf-8");
		Part part;
		while ((part = parser.readNextPart()) != null) {
			if ("username".equals(part.getName()) && part.isParam()) {
				ParamPart paramPart = (ParamPart) part;
				username = paramPart.getStringValue();
			}
			else if ("password".equals(part.getName()) && part.isParam()) {
				ParamPart paramPart = (ParamPart) part;
				password = paramPart.getStringValue();
			}
			else if ("runJob".equals(part.getName()) && part.isParam()) {
				ParamPart paramPart = (ParamPart) part;
				if ("true".equals(paramPart.getStringValue()))
						runJob = true;
			}
			else if (part.isFile()) {
				UserDirectoryService userDirectoryService
					= (UserDirectoryService) ComponentManager.get("org.sakaiproject.user.api.UserDirectoryService");
				SecurityService securityService
					= (SecurityService) ComponentManager.get("org.sakaiproject.authz.api.SecurityService");
				SessionManager sessionManager
					= (SessionManager) ComponentManager.get("org.sakaiproject.tool.api.SessionManager");

		    	User user = userDirectoryService.authenticate(username, password);
		        Session s = null;

		        if ( user != null ) {
		            s = sessionManager.startSession();
		            sessionManager.setCurrentSession(s);   
		        }
		        if (s == null) {
					out.println("You are not authorized to use this service. (Unable to allocate a Sakai session for username: [" + 
							username + "].) \n* Please ensure that parameters are passed before files");
				} else if (user == null) {
					out.println("You are not authorized to use this service. (Authentication failed for username: [" + 
							username + "]. Password was received: [" + (password != null && password.length() > 0) + 
							"]) \n* Please ensure that parameters are passed before files");
				} else if ( !securityService.isSuperUser(user.getId()) ) {
					out.println("You are not authorized to use this service. (Specified username: [" + 
							username + "] did not resolve to a super user). \n* Please ensure that parameters are passed before files");
				}
		        else {
	        		File baseFile = new File(basePath);
	        		if (!baseFile.exists()) {
	        			baseFile.mkdir();
	        		}

		        	/* 
		        	 * stream the uploaded file to disk, it may be very large 
		        	 * and we don't want it in memory any longer then necessary.
		        	 */
		    	
		        	// TODO: support archives, extract as we write
		        	FilePart filePart = (FilePart) part;

		        	BufferedOutputStream bOutputStream
						= new BufferedOutputStream(new FileOutputStream(new File(basePath 
								+ File.separator + part.getName() + ".csv")));
					BufferedInputStream bInputStream
						= new BufferedInputStream(filePart.getInputStream());

		        	int inputByte;
		        	while ((inputByte = bInputStream.read()) != -1){
		        		bOutputStream.write(inputByte);
		        	}
		        	bOutputStream.close();
		        	bInputStream.close();
		        }
			}
		}
		if (runJob)
			runSyncJob();
		out.flush();
	}
	
	/**
	 * Fires off the quartz job for running the Sakara csv sync
	 */
	private void runSyncJob() {
		String jobName 
			= (String) ComponentManager.get("net.unicon.sakora.impl.jobs.DataLoaderJobBean.csv.jobName");
		if (jobName == null || "".equals(jobName))
			jobName = "SIS CSV Data Loader";
		SchedulerManager schedulerManager 
			= (SchedulerManager) ComponentManager.get("org.sakaiproject.api.app.scheduler.SchedulerManager");
		Scheduler scheduler = schedulerManager.getScheduler();
	
		if (scheduler != null) {
			JobBeanWrapper jobWrapper = schedulerManager.getJobBeanWrapper(jobName);

			if (jobWrapper != null) {
				try {
					JobDetail jd = scheduler.getJobDetail(jobName, Scheduler.DEFAULT_GROUP);
					if (jd == null) {
						jd = new JobDetail(jobName, Scheduler.DEFAULT_GROUP, jobWrapper.getJobClass(), false, true, true);
						jd.getJobDataMap().put(JobBeanWrapper.SPRING_BEAN_NAME, jobWrapper.getBeanId());
						jd.getJobDataMap().put(JobBeanWrapper.JOB_TYPE, jobWrapper.getJobType());
						scheduler.addJob(jd, false);
					}
				} catch (SchedulerException e) {
					e.printStackTrace();
				}
			}
			else {
				System.out.println("No Job Wrapper by that name, giving up :: " + jobName);
				return;
			}

			try {
				scheduler.triggerJob(jobName, Scheduler.DEFAULT_GROUP);
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		}
	}
}
