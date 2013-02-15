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
import java.util.List;

import net.unicon.sakora.api.csv.CsvSyncContext;
import net.unicon.sakora.api.csv.model.Membership;
import net.unicon.sakora.api.csv.model.SakoraLog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;
import org.sakaiproject.genericdao.api.search.Restriction;
import org.sakaiproject.genericdao.api.search.Search;

/**
 * Reads in Enrollment data from csv extracts, expect format is:
 * Enrollment Set Eid, User Eid, Status, Enrollment Credits, Grading Scheme
 * 
 * @author Dan McCallum dmccallum@unicon.net
 * @author Aaron Zeckoski azeckoski@unicon.net
 * @author Joshua Ryan
 */
public class CsvEnrollmentHandler extends CsvHandlerBase {
	static final Log log = LogFactory.getLog(CsvEnrollmentHandler.class);
	private String studentRole = "S";

	public CsvEnrollmentHandler() {
	}

	@Override
	protected void readInputLine(CsvSyncContext context, String[] line) {

		final int minFieldCount = 5;

		if (line != null && line.length >= minFieldCount) {
			line = trimAll(line);

			// for clarity
			String eid = line[0];
			String userEid = line[1];
			String status = line[2];
			String credits = line[3];
			String gradingScheme = line[4];

			if (!isValid(userEid, "User Eid", eid)
					|| !isValid(status, "Status", eid)) {
				log.error("Missing required parameter(s), skipping item " + eid);
			}
			else if (!cmService.isEnrollmentSetDefined(eid)) {
				log.error("Invalid ErollmentSet Eid " + eid);
				dao.create(new SakoraLog(this.getClass().toString(), "Invalid ErollmentSet Eid " + eid));
			}
			else {
				cmAdmin.addOrUpdateEnrollment(userEid, eid, status, credits, gradingScheme);
				dao.save(new Membership(userEid, eid, studentRole, "enrollment", time));
				adds++;
			}
		} else {
			log.error("Skipping short line (expected at least [" + minFieldCount + 
					"] fields): [" + (line == null ? null : Arrays.toString(line)) + "]");
		}

	}
	
	@Override
	protected void processInternal(CsvSyncContext context) {
		loginToSakai();
		// look for all enrollments previously defined but not included in this snapshot
		Search search = new Search();
		search.addRestriction(new Restriction("inputTime", time, Restriction.NOT_EQUALS));
		search.addRestriction(new Restriction("mode", "enrollment", Restriction.EQUALS));
		search.setLimit(searchPageSize);

		boolean done = false;

		// TODO should bail if a stop has been requested
		while (!done) {
			List<Membership> memberships = dao.findBySearch(Membership.class, search);
			for (Membership membership : memberships) {
				try {
					cmAdmin.addOrUpdateEnrollment(membership.getUserEid(), membership.getContainerEid(), "dropped", "0", "");
				}
				catch (IdNotFoundException idfe) {
					dao.create(new SakoraLog(this.getClass().toString(), idfe.getLocalizedMessage()));
				}
			}

			if (memberships == null || memberships.size() == 0)
				done = true;
			else
				search.setStart(search.getStart() + searchPageSize);
		}

		logoutFromSakai();
		dao.create(new SakoraLog(this.getClass().toString(),
				"Finished processing input, added or updated " 
				+ updates + " items and removed " + deletes));
	}

	public String getStudentRole() {
		return studentRole;
	}

	public void setStudentRole(String studentRole) {
		this.studentRole = studentRole;
	}

	
}
