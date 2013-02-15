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
import org.sakaiproject.coursemanagement.api.CourseSet;

/**
 * Reads in Course Set info from csv extracts. The expected csv format is:
 * EID, Title, Description, Category, ParentEid
 * 
 * @author Joshua Ryan
 *
 */
public class CsvCourseSetHandler extends CsvHandlerBase {
	static final Log log = LogFactory.getLog(CsvCourseSetHandler.class);

	public CsvCourseSetHandler() {
	}

	@Override
	protected void readInputLine(CsvSyncContext context, String[] line) {

		final int minFieldCount = 5;

		if (line != null && line.length >= minFieldCount) {
			line = trimAll(line);

			// for clarity
			String eid = line[0];
			String title = line[1];
			String description = line[2];
			String category = line[3];
			String parentEid = line[4];

			if (!isValid(title, "Title", eid)
					|| !isValid(description, "Description", eid)) {
				log.error("Missing required parameter(s), skipping item " + eid);
			}
			else if(cmService.isCourseSetDefined(eid)) {
				CourseSet courseSet = cmService.getCourseSet(eid);
				courseSet.setTitle(title);
				courseSet.setDescription(description);
				courseSet.setCategory(category);
				if (parentEid != null && !"".equals(parentEid) && cmService.isCourseSetDefined(parentEid))
					courseSet.setParent(cmService.getCourseSet(parentEid));
				cmAdmin.updateCourseSet(courseSet);
				updates++;
			}
			else {
				cmAdmin.createCourseSet(eid, title, description, category, parentEid);
				adds++;
			}
		} else {
			log.error("Skipping short line (expected at least [" + minFieldCount + 
					"] fields): [" + (line == null ? null : Arrays.toString(line)) + "]");
		}
	}

	@Override
	protected void processInternal(CsvSyncContext context) {
		// TODO: support for conditionally removing course sets not in current extract?
	}
}
