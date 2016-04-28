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

import java.util.HashMap;
import java.util.Map;

import net.unicon.sakora.api.csv.CsvSyncContext;
import net.unicon.sakora.api.csv.CsvSyncService;
import net.unicon.sakora.impl.csv.CsvSyncContextImpl;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

public class CsvLoaderJobBean implements StatefulJob {
	
	private CsvSyncService csvSyncService;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDetail detail = context.getJobDetail();
		JobDataMap map = detail.getJobDataMap();
		Map<String, String> convertedMap = new HashMap( map );
		CsvSyncContext syncContext = new CsvSyncContextImpl();
		syncContext.setProperties( convertedMap );
		csvSyncService.sync(syncContext);
		context.setResult( convertedMap );
	}
	
	public CsvSyncService getCsvSyncService() {
		return csvSyncService;
	}

	public void setCsvSyncService(CsvSyncService csvSyncService) {
		this.csvSyncService = csvSyncService;
	}

}
