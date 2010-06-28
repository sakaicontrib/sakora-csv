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
import java.util.HashSet;
import java.util.List;

import net.unicon.sakora.api.csv.CsvSyncContext;
import net.unicon.sakora.api.csv.model.Membership;
import net.unicon.sakora.api.csv.model.SakoraLog;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;
import org.sakaiproject.genericdao.api.search.Restriction;
import org.sakaiproject.genericdao.api.search.Search;

/**
 * Reads in membership data from csv extracts, expected format:
 * Section or Course Eid, User Eid, Role, Status, *Credits, *Grading Scheme
 * 
 * * denotes completely optional fields, blank or missing values are fine, default values will be used.
 * 
 * @author Joshua Ryan
 *
 */
public class CsvMembershipHandler extends CsvHandlerBase {
	static final Log log = LogFactory.getLog(CsvMembershipHandler.class);

	private String defaultCredits = "0";
	private String defaultGradingScheme = "Letter Grade";
    private String taRole;
    private String studentRole;
    private String instructorRole;
    private String mode = "section";
    private boolean lazyEnrollmentMode = false;   // Allows simple flat enrollment handling without sets or sections
    private String defaultEnrollmentSetCategory = "NONE";

	@Override
	protected void readInputLine(CsvSyncContext context, String[] line) {
		
		final int minFieldCount = 4;

		if (line != null && line.length >= minFieldCount) {
			line = trimAll(line);

			// for clarity
			String eid = line[0];
			String userEid = line[1];
			String role = line[2];
			String status = line[3];
			String credits = defaultCredits;
			if (line.length > 4 && line[4] != null)
				credits = line[4];
			String gradingScheme = defaultGradingScheme;
			if (line.length > 5 && line[5] != null)
				gradingScheme = line[5];

			try {
				if (!isValid(userEid, "User Eid", eid)
						|| !isValid(role, "Role", eid)
						|| !isValid(status, "Status", eid)) {
					log.error("Missing required parameter(s), skipping item " + eid);
				}
				else if ("section".equals(mode)) {
					Section section = cmService.getSection(eid);
					EnrollmentSet enrolled = section.getEnrollmentSet();

					if (enrolled == null) {
						// no enrollment set yet - create one
						String esEid = section.getEid() + "_ES";

						if ( log.isDebugEnabled() ) {
							log.debug("Section [" + section.getEid() + 
									"] has no enrollment set, creating one with eid [" + esEid + "]");
						}

						enrolled = cmAdmin.createEnrollmentSet(esEid, section.getTitle(), section.getDescription(),
								(section.getCategory() == null ? defaultEnrollmentSetCategory : section.getCategory()), 
								defaultCredits, section.getCourseOfferingEid(), null);
						section.setEnrollmentSet(enrolled);
						cmAdmin.updateSection(section);
					}
					if (role.equalsIgnoreCase(instructorRole)) {
						if (enrolled.getOfficialInstructors() == null) {
							enrolled.setOfficialInstructors(new HashSet<String>());
						}
						enrolled.getOfficialInstructors().add(userEid);
					}
					cmAdmin.addOrUpdateSectionMembership(userEid, role, eid, status);
					if (role.equalsIgnoreCase(studentRole)) {
						if (credits == null || defaultCredits.equals(credits))
							credits = enrolled.getDefaultEnrollmentCredits();
						cmAdmin.addOrUpdateEnrollment(userEid, enrolled.getEid(), status, credits, gradingScheme);
					}
					updates++; // hard to say if it was an add or an update
				}
				else {
					cmAdmin.addOrUpdateCourseOfferingMembership(userEid, role, eid, status);
					updates++; // hard to say if it was an add or an update
				}

				// Update or add Sakora membership entry (used for tracking deltas)
				Search search = new Search();
				search.addRestriction(new Restriction("mode", mode, Restriction.EQUALS));
				search.addRestriction(new Restriction("userEid", userEid));
				search.addRestriction(new Restriction("containerEid", eid));
				List<Membership> existing = dao.findBySearch(Membership.class, search);
				if ( existing == null || existing.isEmpty() ) {
					dao.save(new Membership(userEid, eid, role, mode, time));
				} else {
					for ( int i = 0 ; i < existing.size() ; i++ ) {
						// guard against dupl records, which can lead
						// to inadvertent CM membership deletion
						if ( i == existing.size() - 1 ) {
							existing.get(i).setInputTime(time);
							existing.get(i).setRole(role);
							dao.update(existing.get(i));
						} else {
							// Not in transaction so can't use delete(Object).
							// Should really be fixed.
							dao.delete(Membership.class, existing.get(i).getId());
						}
					}
				}
			} catch (IdNotFoundException idfe) {
				dao.create(new SakoraLog(this.getClass().toString(), idfe.getLocalizedMessage()));
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
		search.addRestriction(new Restriction("mode", mode, Restriction.EQUALS));
		search.setLimit(searchPageSize);

		boolean done = false;

		while (!done) {
			List<Membership> memberships = dao.findBySearch(Membership.class, search);
			for (Membership membership : memberships) {
				try {
					if ("section".equals(mode)) {
						cmAdmin.removeSectionMembership(membership.getUserEid(), membership.getContainerEid());
						Section section = cmService.getSection(membership.getContainerEid());
						if (section != null) {
							EnrollmentSet enrolled = section.getEnrollmentSet();
							cmAdmin.removeEnrollment(membership.getUserEid(), enrolled.getEid());
							deletes++;
						}
					}
					else {
						cmAdmin.removeCourseOfferingMembership(membership.getUserEid(), membership.getContainerEid());
					}
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

	public String getTaRole() {
		return taRole;
	}

	public void setTaRole(String taRole) {
		this.taRole = taRole;
	}

	public String getStudentRole() {
		return studentRole;
	}

	public void setStudentRole(String studentRole) {
		this.studentRole = studentRole;
	}

	public String getDefaultCredits() {
		return defaultCredits;
	}

	public void setDefaultCredits(String defaultCredits) {
		this.defaultCredits = defaultCredits;
	}

	public String getDefaultGradingScheme() {
		return defaultGradingScheme;
	}

	public void setDefaultGradingScheme(String defaultGradingScheme) {
		this.defaultGradingScheme = defaultGradingScheme;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getInstructorRole() {
		return instructorRole;
	}

	public void setInstructorRole(String instructorRole) {
		this.instructorRole = instructorRole;
	}

	public boolean isLazyEnrollmentMode() {
		return lazyEnrollmentMode;
	}

	public void setLazyEnrollmentMode(boolean lazyEnrollmentMode) {
		this.lazyEnrollmentMode = lazyEnrollmentMode;
	}
	
	public String getDefaultEnrollmentSetCategory() {
		return defaultEnrollmentSetCategory;
	}

    /**
     * Set the string to be assigned to enrollment set categories if the
     * associated section does not have a category. Although category
     * is optional on sections, it is non-optional on enrollment sets
     * under the default CM API implementation. (So this field should
     * not be set to null.)
     * 
     * @param defaultEnrollmentSetCategory
     */
	public void setDefaultEnrollmentSetCategory(String defaultEnrollmentSetCategory) {
		this.defaultEnrollmentSetCategory = defaultEnrollmentSetCategory;
	}
}
