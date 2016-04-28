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
 * Reads in Enrollment data from csv extracts, expected format is:
 * Enrollment Set Eid, User Eid, Status, Enrollment Credits, Grading Scheme
 * 
 * @author Dan McCallum dmccallum@unicon.net
 * @author Aaron Zeckoski azeckoski@unicon.net
 * @author Joshua Ryan
 */
public class CsvEnrollmentHandler extends CsvHandlerBase {
    static final Log LOG = LogFactory.getLog(CsvEnrollmentHandler.class);
    private String studentRole = "S";

    public CsvEnrollmentHandler() {}

    @Override
    public String getName() {
        return "Enrollment";
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
                LOG.error("Missing required parameter(s), skipping item " + eid);
                errors++;
            } else {
                if (commonHandlerService.processEnrollmentSet(eid)) {
                    if (!cmService.isEnrollmentSetDefined(eid)) {
                        LOG.error("Invalid EnrollmentSet Eid " + eid);
                        dao.create(new SakoraLog(this.getClass().toString(), "Invalid EnrollmentSet Eid " + eid));
                    } else {
                        cmAdmin.addOrUpdateEnrollment(userEid, eid, status, credits, gradingScheme);
                        // NOTE: this next line is likely to cause a hibernate exception
                        dao.save( new Membership(userEid, eid, studentRole, "enrollment", time) );
                        adds++;
                    }
                } else {
                    if (LOG.isDebugEnabled()) LOG.debug("Skipped processing enrollment for user ("+userEid+") because it is in enrollment set ("+eid+") which is part of an academic session which is being skipped");
                }
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
            if (LOG.isDebugEnabled()) LOG.debug("SakoraCSV skipping enrollment membership processing, ignoreMembershipRemovals=true");
        } else {
            // do removal processing
            loginToSakai();

            boolean done = false;

            List<String> enrollmentSetEids = new ArrayList<String>();
            boolean ignoreMissingSessions = commonHandlerService.ignoreMissingSessions();
            // filter out anything which is not part of the current set of enrollment sets
            if (ignoreMissingSessions) {
                enrollmentSetEids.addAll(commonHandlerService.getCurrentEnrollmentSets());
                if (enrollmentSetEids.isEmpty()) {
                    // no sets are current so we skip everything
                    done = true;
                    LOG.warn("SakoraCSV EnrollmentHandler processInternal: No current enrollment sets so we are skipping all internal enrollment post CSV read processing");
                } else {
                    LOG.info("SakoraCSV limiting enrollment membership removals to "+enrollmentSetEids.size()+" enrollment sets: "+enrollmentSetEids);
                }
            }

            if (!done)
            {
                // if we are ignoring missing sessions, we have to add a restriction on containerEid which could result
                // in over 1000 entries in the IN clause. This is a problem for Oracle so we need to loop the search.
                // if we are not ignoring missing sessions, we do not add a restriction, and the loop will execute exactly once. 
                int max = 1000;
                int containerCount = max;
                int startIndex = 0;
                int endIndex = max;
                if (ignoreMissingSessions)
                {
                    containerCount = enrollmentSetEids.size();
                    endIndex = Math.min(containerCount, max);
                }

                while (startIndex < containerCount)
                {
                    // look for all enrollments previously defined but not included in this snapshot
                    Search search = new Search();
                    search.addRestriction(new Restriction("inputTime", time, Restriction.NOT_EQUALS));
                    search.addRestriction(new Restriction("mode", "enrollment", Restriction.EQUALS));
                    search.setLimit(searchPageSize);
                    if (ignoreMissingSessions)
                    {
                        search.addRestriction(new Restriction("containerEid", enrollmentSetEids.subList(startIndex, endIndex)));
                    }

                    boolean paging = true;
                    while (paging) {
                        List<Membership> memberships = dao.findBySearch(Membership.class, search);
                        if (LOG.isDebugEnabled()) LOG.debug("SakoraCSV processing "+memberships.size()+" enrollment membership removals");
                        for (Membership membership : memberships) {
                            try {
                                cmAdmin.addOrUpdateEnrollment(membership.getUserEid(), membership.getContainerEid(), "dropped", "0", "");
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

    public String getStudentRole() {
        return studentRole;
    }

    public void setStudentRole(String studentRole) {
        this.studentRole = studentRole;
    }
}
