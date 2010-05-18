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
package net.unicon.sakora.impl;

import java.util.List;

import net.unicon.sakora.api.ws.ImsEsSyncService;
import net.unicon.sakora.api.ws.ImsSyncContext;

public class ImsEsSyncServiceFederatedImpl implements ImsEsSyncService {
    private List<ImsEsSyncService> syncServiceList;

    public void setSyncServiceList(List<ImsEsSyncService> implList)
    {
        this.syncServiceList = implList;
    }

    public List<ImsEsSyncService> getSyncServiceList() 
    {
        return syncServiceList;
    }

	
	public void readCourseOfferings(ImsSyncContext syncContext) {
		if (syncServiceList != null) {
			for (ImsEsSyncService syncService : syncServiceList) {
				syncService.readCourseOfferings(syncContext);
			}
		}
	}

	public void readCourseSections(ImsSyncContext syncContext) {
		if (syncServiceList != null) {
			for (ImsEsSyncService syncService : syncServiceList) {
				syncService.readCourseSections(syncContext);
			}
		}
	}

	public void readCourseTemplates(ImsSyncContext syncContext) {
		if (syncServiceList != null) {
			for (ImsEsSyncService syncService : syncServiceList) {
				syncService.readCourseTemplates(syncContext);
			}
		}
	}

	public void readGroups(ImsSyncContext syncContext) {
		if (syncServiceList != null) {
			for (ImsEsSyncService syncService : syncServiceList) {
				syncService.readGroups(syncContext);
			}
		}
	}

	public void readMemberships(ImsSyncContext syncContext) {
		if (syncServiceList != null) {
			for (ImsEsSyncService syncService : syncServiceList) {
				syncService.readMemberships(syncContext);
			}
		}
	}

	public void readPersons(ImsSyncContext syncContext) {
		if (syncServiceList != null) {
			for (ImsEsSyncService syncService : syncServiceList) {
				syncService.readPersons(syncContext);
			}
		}
	}

	public void readSectionAssociations(ImsSyncContext syncContext) {
		if (syncServiceList != null) {
			for (ImsEsSyncService syncService : syncServiceList) {
				syncService.readSectionAssociations(syncContext);
			}
		}
	}

}
