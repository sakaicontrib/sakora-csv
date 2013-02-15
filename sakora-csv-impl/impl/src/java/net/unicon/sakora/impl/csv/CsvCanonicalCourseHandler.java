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
import org.sakaiproject.coursemanagement.api.CanonicalCourse;

/**
 * Reads in Canonical Courses from csv extracts, expected format is:
 * EID, Title, Description
 * 
 * @author Joshua Ryan
 *
 */
public class CsvCanonicalCourseHandler extends CsvHandlerBase {
	static final Log log = LogFactory.getLog(CsvCanonicalCourseHandler.class);

	@Override
	protected void readInputLine(CsvSyncContext context, String[] line) {

		final int minFieldCount = 3;

		if (line != null && line.length >= minFieldCount) {
			line = trimAll(line);

			// for clarity
			String eid = line[0];
			String title = line[1];
			String description = line[2];
			String courseSet = null;
			if (line.length > 3)
				courseSet = line[3];

			if (!isValid(title, "Title", eid)
					|| !isValid(description, "Description", eid)) {
				log.error("Missing required parameter(s), skipping item " + eid);
			}
			else if (!cmService.isCanonicalCourseDefined(eid)) {
				cmAdmin.createCanonicalCourse(eid, title, description);
				adds++;
			}
			else {
				CanonicalCourse canonicalCourse = cmService.getCanonicalCourse(eid);
				canonicalCourse.setTitle(title);
				canonicalCourse.setDescription(description);
				cmAdmin.updateCanonicalCourse(canonicalCourse);
				updates++;
			}
			if (courseSet != null && cmService.isCourseSetDefined(courseSet))
				cmAdmin.addCanonicalCourseToCourseSet(courseSet, eid);
		} else {
			log.error("Skipping short line (expected at least [" + minFieldCount + 
					"] fields): [" + (line == null ? null : Arrays.toString(line)) + "]");
		}
	}

	@Override
	protected void processInternal(CsvSyncContext context) {
		// TODO: support for removing canonical courses not in current extract?
	}
}
