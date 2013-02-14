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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import net.unicon.sakora.api.csv.CsvSyncContext;
import net.unicon.sakora.api.csv.model.Session;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.genericdao.api.search.Restriction;
import org.sakaiproject.genericdao.api.search.Search;

/**
 * Reads in Academic Session info from csv extracts. The expected csv format is:
 * EID, Title, Description, StartDate, EndDate	 
 * 
 * @author Dan McCallum dmccallum@unicon.net
 * @author Aaron Zeckoski azeckoski@unicon.net
 * @author Joshua Ryan
 */
public class CsvAcademicSessionHandler extends CsvHandlerBase {
	static final Log log = LogFactory.getLog(CsvAcademicSessionHandler.class);

	/**
	 * @deprecated UNUSED - this should be removed entirely
	 */
	private boolean deleteSession = false;

	public void init() {
	    super.init();
        if (isIgnoreMissingSessions()) {
            log.info("SakoraCSV ignoreMissingSessions is enabled: all data related to sessions not included in sessions.csv will be left skipped or otherwise unchanged");
        } else {
            log.info("SakoraCSV set to process missing sessions (ignoreMissingSessions=false): all data related to sessions not included in sessions.csv will be processed and REMOVED");
        }
	    //log.info("SakoraCSV AcademicSession handler: deleteSession="+(deleteSession?"true (academic sessions will be removed when they are not included in sessions.csv)":"false (no academic sessions will be removed)"));
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
			Date startDate = parseDate(line[3]);
			Date endDate = parseDate(line[4]);

			if (!isValid(title, "Title", eid)
					|| !isValid(description, "Description", eid)
					|| !isValid(startDate, "Start Date", eid)
					|| !isValid(endDate, "End Date", eid)) {
				log.error("SakoraCSV Missing required parameter(s), skipping item " + eid);
			}
			else if (cmService.isAcademicSessionDefined(eid)) {
				AcademicSession session = cmService.getAcademicSession(eid);
				session.setTitle(title);
				session.setDescription(description);
				session.setStartDate(startDate);
				session.setEndDate(endDate);
				cmAdmin.updateAcademicSession(session);
				updates++;
			}
			else {
				cmAdmin.createAcademicSession(eid, title, description, startDate, endDate);
				adds++;
			}
			Search search = new Search();
			search.addRestriction(new Restriction("eid", eid));
			Session existing = dao.findOneBySearch(Session.class, search);
			if (existing != null) {
				existing.setInputTime(time);
				dao.update(existing);
			}
			else
				dao.save(new Session(eid, time));
		} else {
			log.error("SakoraCSV Skipping short line (expected at least [" + minFieldCount + 
					"] fields): [" + (line == null ? null : Arrays.toString(line)) + "]");
		}
	}
	
	@Override
	protected void processInternal(CsvSyncContext context) {
		loginToSakai();

		ArrayList<String> currentSessions = new ArrayList<String>();

		Search search = new Search();
		search.addRestriction(new Restriction("inputTime", time, Restriction.EQUALS));

		boolean done = false;

		while (!done) {
			List<Session> sessions = dao.findBySearch(Session.class, search);
			for (Session session : sessions) {
				currentSessions.add(session.getEid());
			}
			if (sessions == null || sessions.size() == 0) {
				done = true;
			} else {
				search.setStart(search.getStart() + searchPageSize);
			}
		}
        if (currentSessions.isEmpty() && !isIgnoreMissingSessions()) {
            // TODO should we die here? -AZ
            log.error("SakoraCSV has no current academic sessions to process and session processing is on, this is an invalid state as it would cause all data (courses, memberships, etc.) to be disabled or removed, please check your sessions.csv file");
        }
        // set the current academic sessions according to the incoming sessions
		cmAdmin.setCurrentAcademicSessions(currentSessions);
		setCurrentAcademicSessions( currentSessions.toArray(new String[currentSessions.size()]) );
		logoutFromSakai();
	}

	public boolean isDeleteSession() {
		return deleteSession;
	}

	public void setDeleteSession(boolean deleteSession) {
		this.deleteSession = deleteSession;
	}

}
