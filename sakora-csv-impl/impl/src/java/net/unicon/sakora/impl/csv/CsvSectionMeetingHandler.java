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

import java.sql.Time;
import java.util.Arrays;
import java.util.Date;

import net.unicon.sakora.api.csv.CsvSyncContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.coursemanagement.api.Meeting;
import org.sakaiproject.coursemanagement.api.Section;

/**
 * Processes section meeting times from csv exports
 * 
 * Eid, location, notes, *startTime, *endTime
 * (times are optional)
 * 
 * @author Joshua Ryan
 *
 */
public class CsvSectionMeetingHandler extends CsvHandlerBase {
	static final Log log = LogFactory.getLog(CsvSectionMeetingHandler.class);

	@Override
	protected void readInputLine(CsvSyncContext context, String[] line) {
		
		final int minFieldCount = 6;

		if (line != null && line.length >= minFieldCount) {
			line = trimAll(line);

			// for clarity
			String eid = line[0];
			String location = line[1];
			String notes = line[2];
			Time startTime = null;
			Time endTime = null;
			if (line.length == 5) {
				startTime = Time.valueOf(line[3]);
				endTime = Time.valueOf(line[4]);
			}

			if (!isValid(location, "Location", eid)
					|| !isValid(startTime, "Start Time", eid)
					|| !isValid(endTime, "End Time", eid)) {
				log.error("Missing required parameter(s), skipping item " + eid);
			}
			else if(cmService.isSectionDefined(eid)) {
				Section section = cmService.getSection(eid);
				Meeting meeting = cmAdmin.newSectionMeeting(eid, location, startTime, endTime, notes);
				if (!section.getMeetings().contains(meeting)) {
					section.getMeetings().add(meeting);
					adds++;
				}
				cmAdmin.updateSection(section);
			}
			else {
				log.error("CsvSectionMeetingHandler :: can't add meeting to invalid section");
			}
		} else {
			log.error("Skipping short line (expected at least [" + minFieldCount + 
					"] fields): [" + (line == null ? null : Arrays.toString(line)) + "]");
		}
	}

	@Override
	protected void processInternal(CsvSyncContext context) {
		// TODO: support for conditionally removing course offerings not in current extract?
	}
}
