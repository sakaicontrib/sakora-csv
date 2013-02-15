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
package net.unicon.sakora.impl.csv;

import java.util.Arrays;

import net.unicon.sakora.api.csv.CsvSyncContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;

/**
 * Reads in Enrollment Set data from csv extracts, expect format is:
 * Eid, Title, Description, Category, Course Offering Eid, Default Enrollment Credits
 * 
 * @author Dan McCallum dmccallum@unicon.net
 * @author Aaron Zeckoski azeckoski@unicon.net
 * @author Joshua Ryan
 */
public class CsvEnrollmentSetHandler extends CsvHandlerBase {
	static final Log log = LogFactory.getLog(CsvEnrollmentSetHandler.class);

	public CsvEnrollmentSetHandler() {
	}

	@Override
	protected void readInputLine(CsvSyncContext context, String[] line) {

		final int minFieldCount = 6;

		if (line != null && line.length >= minFieldCount) {
			line = trimAll(line);

			// for clarity
			String eid = line[0];
			String title = line[1];
			String description = line[2];
			String category = line[3];
			String courseOfferingEid = line[4];
			String defaultEnrollmentCredits = line[5];

			if (!isValid(title, "Title", eid)
					|| !isValid(description, "Description", eid)
					|| !isValid(courseOfferingEid, "Course Offering Eid", eid)) {
				log.error("Missing required parameter(s), skipping item " + eid);
			} else {
			    if (commonHandlerService.processCourseOffering(courseOfferingEid)) {
			        if (cmService.isEnrollmentSetDefined(eid)) {
			            EnrollmentSet enrollment = cmService.getEnrollmentSet(eid);
			            enrollment.setTitle(title);
			            enrollment.setDescription(description);
			            enrollment.setDefaultEnrollmentCredits(defaultEnrollmentCredits);
			            enrollment.setCategory(category);
			            updates++;
			        } else {
			            cmAdmin.createEnrollmentSet(eid, title, description, category, defaultEnrollmentCredits, courseOfferingEid, null);
			            adds++;
			        }
			        int total = commonHandlerService.addCurrentEnrollmentSet(eid);
			        if (log.isDebugEnabled()) log.debug("Added enrollment set ("+eid+") to the current list: "+total);
			    } else {
			        if (log.isDebugEnabled()) log.debug("Skipped processing enrollment set ("+eid+") because it is in course offering ("+courseOfferingEid+") which is part of an academic session which is being skipped");
			    }
			}
		} else {
			log.error("Skipping short line (expected at least [" + minFieldCount + 
					"] fields): [" + (line == null ? null : Arrays.toString(line)) + "]");
		}
	}

	@Override
	protected void processInternal(CsvSyncContext context) {
		// TODO: support for conditionally removing enrollment sets not in current extract?
	}
}
