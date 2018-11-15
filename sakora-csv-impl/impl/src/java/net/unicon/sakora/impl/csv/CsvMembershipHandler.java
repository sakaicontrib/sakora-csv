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
 * @author Aaron Zeckoski azeckoski@unicon.net
 * @author Joshua Ryan
 */
public class CsvMembershipHandler extends CsvHandlerBase {

    static final Log LOG = LogFactory.getLog(CsvMembershipHandler.class);

    static final String MODE_SECTION = "section";

    private String defaultCredits = "0";
    private String defaultGradingScheme = "Letter Grade";
    private String taRole;
    private String studentRole;
    private String instructorRole;
    private String mode = MODE_SECTION; // set by the spring config (course or section)
    private String defaultEnrollmentSetCategory = "NONE";

    @Override
    public String getName() {
        return MODE_SECTION.equals(mode) ? "SectionMembership" : "CourseMembership";
    }

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
            if (line.length > 4 && line[4] != null) {
                credits = line[4];
            }
            String gradingScheme = defaultGradingScheme;
            if (line.length > 5 && line[5] != null) {
                gradingScheme = line[5];
            }

            try {
                if (!isValid(userEid, "User Eid", eid)
                        || !isValid(role, "Role", eid)
                        || !isValid(status, "Status", eid)) {
                    LOG.error("Missing required parameter(s), skipping item " + eid);
                    errors++;
                } else if (MODE_SECTION.equals(mode)) {
                    // SECTION MEMBERSHIPS
                    if (commonHandlerService.processSection(eid)) {
                        Section section = cmService.getSection(eid);
                        EnrollmentSet enrolled = section.getEnrollmentSet();

                        if (enrolled == null) {
                            // no enrollment set yet - create one
                            String esEid = section.getEid() + "_ES";

                            if ( LOG.isDebugEnabled() ) {
                                LOG.debug("Section [" + section.getEid() + "] has no enrollment set, creating one with eid [" + esEid + "]");
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
                            if (credits == null || defaultCredits.equals(credits)) {
                                credits = enrolled.getDefaultEnrollmentCredits();
                            }
                            cmAdmin.addOrUpdateEnrollment(userEid, enrolled.getEid(), status, credits, gradingScheme);
                        }
                        updates++; // hard to say if it was an add or an update
                    } else {
                        if (LOG.isDebugEnabled()) LOG.debug("Skipped processing section membership for user ("+userEid+") in section ("+eid+") because it is part of an academic session which is being skipped");
                    }
                } else {
                    // COURSE MEMBERSHIPS
                    if (commonHandlerService.processCourseOffering(eid)) {
                        cmAdmin.addOrUpdateCourseOfferingMembership(userEid, role, eid, status);
                        updates++; // hard to say if it was an add or an update
                    } else {
                        if (LOG.isDebugEnabled()) LOG.debug("Skipped processing membership for user ("+userEid+") in course offering ("+eid+") because it is part of an academic session which is being skipped");
                    }
                }

                if (commonHandlerService.ignoreMembershipRemovals()) {
                    if (LOG.isDebugEnabled()) LOG.debug("SakoraCSV skipping sakora membership table update for user ("+userEid+") and "+mode+" ("+eid+") because ignoreMembershipRemovals=true");
                } else {
                    // Update or add Sakora membership entry (used for tracking deltas)
                    Search search = new Search();
                    search.addRestriction(new Restriction("mode", mode, Restriction.EQUALS));
                    search.addRestriction(new Restriction("userEid", userEid));
                    search.addRestriction(new Restriction("containerEid", eid));
                    List<Membership> existing = dao.findBySearch(Membership.class, search);
                    if ( existing == null || existing.isEmpty() ) {
                        dao.create( new Membership(userEid, eid, role, mode, time) );
                    } else {
                        for ( int i = 0 ; i < existing.size() ; i++ ) {
                            // guard against dupl records, which can lead to inadvertent CM membership deletion
                            if ( i == existing.size() - 1 ) {
                                // only update the last one found
                                existing.get(i).setInputTime(time);
                                existing.get(i).setRole(role);
                                dao.update(existing.get(i));
                            } else {
                                // Remove all duplicates
                                // Not in transaction so can't use delete(Object).
                                dao.delete(Membership.class, existing.get(i).getId());
                            }
                        }
                    }
                }
            } catch (IdNotFoundException idfe) {
                dao.create(new SakoraLog(this.getClass().toString(), idfe.getLocalizedMessage()));
            }
        } else {
            LOG.error("Skipping short line (expected at least [" + minFieldCount + 
                    "] fields): [" + (line == null ? null : Arrays.toString(line)) + "]");
            errors++;
        }
    }

    @Override
    protected void processInternal(CsvSyncContext context) {
        if (commonHandlerService.ignoreMembershipRemovals()) {
            if (LOG.isDebugEnabled()) LOG.debug("SakoraCSV skipping "+mode+" membership processing, ignoreMembershipRemovals=true");
        } else {
            // do removal processing
            loginToSakai();

            boolean done = false;

            List<String> enrollmentContainerEids = new ArrayList<String>();
            final boolean ignoreMissingSessions = commonHandlerService.ignoreMissingSessions();
            // filter out anything which is not part of the current set of offerings/sections
            if (ignoreMissingSessions) {
                if (MODE_SECTION.equals(mode)) {
                    enrollmentContainerEids.addAll(commonHandlerService.getCurrentSectionEids());
                } else {
                    // course enrollments
                    enrollmentContainerEids.addAll(commonHandlerService.getCurrentCourseOfferingEids());
                }
                if (enrollmentContainerEids.isEmpty()) {
                    // no offerings or sections are current so we skip everything
                    done = true;
                    String handler = MODE_SECTION.equals(mode) ? "SectionMembershipHandler" : "CourseMembershipHandler";
                    LOG.warn("SakoraCSV "+handler+" processInternal: No current containers so we are skipping all internal memberships post CSV read processing");
                }
            }

            if (!done)
            {
                // if we are ignoring missing sessions, we have to add a restriction on containerEid which could result
                // in over 1000 entries in the IN clause. This is a problem for Oracle so we need to loop the search.
                // if we are not ignoring missing sessions, we do not add a restriction, and the loop will execute exactly once. 
                final int max = 1000;
                int containerCount = max;
                int startIndex = 0;
                int endIndex = max;
                if (ignoreMissingSessions)
                {
                    containerCount = enrollmentContainerEids.size();
                    endIndex = Math.min(containerCount, max);
                }

                while (startIndex < containerCount)
                {
                    Search search = new Search();
                    search.addRestriction(new Restriction("inputTime", time, Restriction.NOT_EQUALS));
                    search.addRestriction(new Restriction("mode", mode, Restriction.EQUALS));
                    search.setLimit(searchPageSize);
                    LOG.debug("Searching for memberships to drop where inputTime != " + time + " and mode = " + mode);
                    if (ignoreMissingSessions)
                    {
                        List<String> subList = enrollmentContainerEids.subList(startIndex, endIndex);
                        search.addRestriction( new Restriction("containerEid", subList.toArray(new String[subList.size()])) );
                        LOG.info("SakoraCSV limiting "+mode+" membership removals to "+subList.size()+" "+mode+" containers: "+subList);
                    }

                    boolean paging = true;
                    while (paging) {
                        List<Membership> memberships = dao.findBySearch(Membership.class, search);
                        if (LOG.isDebugEnabled()) LOG.debug("SakoraCSV processing "+memberships.size()+" "+mode+" membership removals");
                        for (Membership membership : memberships) {
                            try {
                                if (MODE_SECTION.equals(mode)) {
                                    cmAdmin.removeSectionMembership(membership.getUserEid(), membership.getContainerEid());
                                    Section section = cmService.getSection(membership.getContainerEid());
                                    if (section != null) {
                                        EnrollmentSet enrolled = section.getEnrollmentSet();
                                        if (enrolled != null) {
                                            cmAdmin.removeEnrollment(membership.getUserEid(), enrolled.getEid());
                                            if (LOG.isDebugEnabled()) LOG.debug("SakoraCSV removed "+mode+" membership for "+membership.getUserEid()+": "+enrolled.getEid());
                                            deletes++;
                                        } else {
                                            LOG.info("Null EnrollmentSet found for section EID " + section.getEid() + ", enrollments for this set can't be removed...");
                                        }
                                    }
                                } else {
                                    cmAdmin.removeCourseOfferingMembership(membership.getUserEid(), membership.getContainerEid());
                                    if (LOG.isDebugEnabled()) LOG.debug("SakoraCSV removed "+mode+" membership for "+membership.getUserEid()+": "+membership.getContainerEid());
                                    deletes++;
                                }
                            } catch (IdNotFoundException idfe) {
                                dao.create(new SakoraLog(this.getClass().toString(), idfe.getLocalizedMessage()));
                            }
                        }

                        if (memberships == null || memberships.isEmpty()) {
                            paging = false;
                        } else {
                            search.setStart(search.getStart() + searchPageSize);
                        }
                        // should we halt if a stop was requested via pleaseStop?
                    }

                    startIndex = endIndex; // if not ignoring missing sessions, startIndex == containerCount after a single iteration and the loop exits
                    endIndex = Math.min(endIndex + max, containerCount);
                }
            }
            logoutFromSakai();
        }
        dao.create(new SakoraLog(this.getClass().toString(),
                "Finished processing input, added or updated " + updates + " items and removed " + deletes));
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
