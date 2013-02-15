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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.unicon.sakora.api.csv.CsvHandler;
import net.unicon.sakora.api.csv.CsvSyncContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.coursemanagement.api.CourseManagementAdministration;
import org.sakaiproject.coursemanagement.api.CourseManagementService;

/**
 * Handled any common processing that needs to be chared between the CSV handlers
 * during processing (or idle time)
 * 
 * @author Aaron Zeckoski azeckoski@unicon.net
 */
public class CsvCommonHandlerService {
    static final Log log = LogFactory.getLog(CsvCommonHandlerService.class);

    public static final String SYNC_VAR_ID = "id";
    public static final String SYNC_VAR_CONTEXT = "context";
    public static final String SYNC_VAR_STATUS = "status";
    public static final String SYNC_VAR_HANDLER = "handler";
    public static final String SYNC_VAR_HANDLER_STATE = "handler_state";

    private static final String CURRENT_ENROLLMENT_SET_EIDS = "currentEnrollmentSetEids";
    private static final String CURRENT_SECTION_EIDS = "currentSectionEids";
    private static final String CURRENT_COURSE_OFFERING_EIDS = "currentCourseOfferingEids";
    private static final String CURRENT_SESSION_EIDS = "currentSessionEids";

    protected ServerConfigurationService configurationService;
    protected CourseManagementAdministration cmAdmin;
    protected CourseManagementService cmService;

    private volatile int runCounter = 0;
    private ConcurrentHashMap<String, Object> syncVars = new ConcurrentHashMap<String, Object>();

    public void init() {
        // load common config values
        ignoreMissingSessions = configurationService.getBoolean("net.unicon.sakora.csv.ignoreMissingSessions", ignoreMissingSessions);
        if (isIgnoreMissingSessions()) {
            log.info("SakoraCSV ignoreMissingSessions is enabled: all data related to sessions not included in sessions.csv will be left skipped or otherwise unchanged");
        } else {
            log.info("SakoraCSV set to process missing sessions (ignoreMissingSessions=false): all data related to sessions not included in sessions.csv will be processed and REMOVED");
        }
    }

    public void destroy() {
        syncVars = null;
    }

    /* We won't allow multiple syncs to run so no need for the thread safety -AZ
    private static final ThreadLocal<HashMap<String, Object>> runVars = new ThreadLocal<HashMap<String,Object>>() {
        @Override
        protected HashMap<String, Object> initialValue() {
            return new HashMap<String, Object>();
        }
    };*/

    public synchronized String initRun(CsvSyncContext context) {
        // run this before a run starts
        syncVars.clear(); //runVars.remove(); // reset the run vars
        // setup the run id
        String runId = ++runCounter + ":" + (new Date().getTime() / 1000);
        syncVars.put(SYNC_VAR_ID, runId);
        syncVars.put(SYNC_VAR_STATUS, "running");
        syncVars.put(SYNC_VAR_CONTEXT, context);
        return runId;
    }

    public synchronized void completeRun(boolean success) {
        // run this after a run completes
        syncVars.put(SYNC_VAR_STATUS, success?"complete":"failed");
    }

    @SuppressWarnings("unchecked")
    public <T> T getCurrentSyncVar(String name, Class<T> type) {
        if (syncVars.containsKey(name)) {
            return (T) syncVars.get(name);
        }
        return null;
    }

    /**
     * Add a variable and value to the thread for this sync run,
     * using null value will remove the variable,
     * adding existing will replace
     * 
     * @param name any string name
     * @param value any object, if null then the var is removed
     */
    public void setCurrentSyncVar(String name, Object value) {
        if (value == null) {
            syncVars.remove(name);
        } else {
            syncVars.put(name, value);
       }
    }

    /**
     * @return the current sync run for this thread OR null if there is not one (no sync is running)
     */
    public String getCurrentSyncRunId() {
        return getCurrentSyncVar(SYNC_VAR_ID, String.class);
    }

    /**
     * @return the current sync context for this thread OR null if there is not one (no sync is running)
     */
    public CsvSyncContext getCurrentSyncRunContext() {
        return getCurrentSyncVar(SYNC_VAR_CONTEXT, CsvSyncContext.class);
    }

    public String getCurrentSyncState() {
        String status = getCurrentSyncVar(SYNC_VAR_STATUS, String.class);
        if (status.equals("running")) {
            String state = getCurrentSyncVar(SYNC_VAR_HANDLER_STATE, String.class);
            CsvHandler handler = getCurrentSyncVar(SYNC_VAR_HANDLER, CsvHandler.class);
            if (handler != null) {
                String handlerName = handler.getClass().getSimpleName().replace("Handler", "").replace("Csv", "");
                status = "Sync ("+getCurrentSyncRunId()+"): "+handlerName+" state is: "+state;
            }
        }
        return status;
    }

    public void setCurrentHandlerState(String state, CsvHandler handler) {
        /* Allows us to take actions when the state changes (like logging for example)
         */
        setCurrentSyncVar(SYNC_VAR_HANDLER_STATE, state);
        setCurrentSyncVar(SYNC_VAR_HANDLER, handler);
        String handlerName = handler.getClass().getSimpleName().replace("Handler", "").replace("Csv", "");
        log.info("SakoraCSV: Sync ("+getCurrentSyncRunId()+"): "+handlerName+" state is: "+state);
    }

    /* 
     * Academic Session Skip handling:
     * If the flag is set then all data processing for anything not in the current (i.e. included in the feed) academic sessions
     * will be skipped. This includes the sessions themselves (they will not be disabled or removed or flagged), 
     * the course offerings, the enrollment sets, and the enrollments.
     */

    /**
     * controlled by net.unicon.sakora.csv.ignoreMissingSessions config, Default: false
     */
    protected boolean ignoreMissingSessions = false;
    public void setIgnoreMissingSessions(boolean ignoreMissingSessions) {
        this.ignoreMissingSessions = ignoreMissingSessions;
    }
    public boolean isIgnoreMissingSessions() {
        return ignoreMissingSessions;
    }

    // ACADEMIC SESSIONS
    protected int setCurrentAcademicSessions(String[] sessions) {
        HashSet<String> currentAcademicSessionEids;
        if (sessions != null) {
            currentAcademicSessionEids = new HashSet<String>(sessions.length);
            for (int i = 0; i < sessions.length; i++) {
                currentAcademicSessionEids.add(sessions[i]);
            }
        } else {
            currentAcademicSessionEids = new HashSet<String>(0);
        }
        if (currentAcademicSessionEids.isEmpty()) {
            log.warn("SakoraCSV has no current academic sessions, this is typically not a valid state, please check your sessions.csv file");
        }
        setCurrentSyncVar(CURRENT_SESSION_EIDS, currentAcademicSessionEids);
        return currentAcademicSessionEids.size();
    }

    protected boolean processAcademicSession(String academicSessionEid) {
        boolean process;
        if (isIgnoreMissingSessions()) {
            // check the list of sessions which are current and if this is not in that set then false
            @SuppressWarnings("unchecked")
            Set<String> currentAcademicSessionEids = (Set<String>) getCurrentSyncVar(CURRENT_SESSION_EIDS, Set.class);
            if (currentAcademicSessionEids != null && currentAcademicSessionEids.contains(academicSessionEid)) {
                process = true;
            } else {
                process = false;
            }
        } else {
            // standard processing, process all sessions
            process = true;
        }
        return process;
    }

    protected Set<String> getCurrentAcademicSessionEids() {
        @SuppressWarnings("unchecked")
        Set<String> currentSessionEids = (Set<String>) getCurrentSyncVar(CURRENT_SESSION_EIDS, Set.class);
        if (currentSessionEids == null) {
            currentSessionEids = new HashSet<String>(0);
        }
        return currentSessionEids;
    }

    // COURSE OFFERINGS
    protected int addCurrentCourseOffering(String courseOfferingEid) {
        @SuppressWarnings("unchecked")
        Set<String> currentCourseOfferingEids = (Set<String>) getCurrentSyncVar(CURRENT_COURSE_OFFERING_EIDS, Set.class);
        if (currentCourseOfferingEids == null) {
            currentCourseOfferingEids = new HashSet<String>();
            setCurrentSyncVar(CURRENT_COURSE_OFFERING_EIDS, currentCourseOfferingEids);
        }
        if (courseOfferingEid != null) {
            currentCourseOfferingEids.add(courseOfferingEid);
        }
        return currentCourseOfferingEids.size();
    }

    protected boolean processCourseOffering(String courseOfferingEid) {
        boolean process;
        if (isIgnoreMissingSessions()) {
            // check the list of offerings which are current and if course offering is not in that then skip it
            @SuppressWarnings("unchecked")
            Set<String> currentCourseOfferingEids = (Set<String>) getCurrentSyncVar(CURRENT_COURSE_OFFERING_EIDS, Set.class);
            if (currentCourseOfferingEids != null && currentCourseOfferingEids.contains(courseOfferingEid)) {
                process = true;
            } else {
                process = false;
            }
        } else {
            // standard processing, process all course offerings
            process = true;
        }
        return process;
    }

    protected Set<String> getCurrentCourseOfferingEids() {
        @SuppressWarnings("unchecked")
        Set<String> currentCourseOfferingEids = (Set<String>) getCurrentSyncVar(CURRENT_COURSE_OFFERING_EIDS, Set.class);
        if (currentCourseOfferingEids == null) {
            currentCourseOfferingEids = new HashSet<String>(0);
        }
        return currentCourseOfferingEids;
    }

    // SECTIONS
    protected int addCurrentSection(String sectionEid) {
        @SuppressWarnings("unchecked")
        Set<String> currentSectionEids = (Set<String>) getCurrentSyncVar(CURRENT_SECTION_EIDS, Set.class);
        if (currentSectionEids == null) {
            currentSectionEids = new HashSet<String>();
            setCurrentSyncVar(CURRENT_SECTION_EIDS, currentSectionEids);
        }
        if (sectionEid != null) {
            currentSectionEids.add(sectionEid);
        }
        return currentSectionEids.size();
    }

    protected boolean processSection(String sectionEid) {
        boolean process;
        if (isIgnoreMissingSessions()) {
            // check the list of sections which are current and if section is not in that then skip it
            @SuppressWarnings("unchecked")
            Set<String> currentSectionEids = (Set<String>) getCurrentSyncVar(CURRENT_SECTION_EIDS, Set.class);
            if (currentSectionEids != null && currentSectionEids.contains(sectionEid)) {
                process = true;
            } else {
                process = false;
            }
        } else {
            // standard processing, process all course offerings
            process = true;
        }
        return process;
    }

    protected Set<String> getCurrentSectionEids() {
        @SuppressWarnings("unchecked")
        Set<String> currentSectionEids = (Set<String>) getCurrentSyncVar(CURRENT_SECTION_EIDS, Set.class);
        if (currentSectionEids == null) {
            currentSectionEids = new HashSet<String>(0);
        }
        return currentSectionEids;
    }

    // ENROLLMENT SETS
    protected int addCurrentEnrollmentSet(String enrollmentSetEid) {
        @SuppressWarnings("unchecked")
        Set<String> currentEnrollmentSetEids = (Set<String>) getCurrentSyncVar(CURRENT_ENROLLMENT_SET_EIDS, Set.class);
        if (currentEnrollmentSetEids == null) {
            currentEnrollmentSetEids = new HashSet<String>();
            setCurrentSyncVar(CURRENT_ENROLLMENT_SET_EIDS, currentEnrollmentSetEids);
        }
        if (enrollmentSetEid != null) {
            currentEnrollmentSetEids.add(enrollmentSetEid);
        }
        return currentEnrollmentSetEids.size();
    }

    protected boolean processEnrollmentSet(String enrollmentSetEid) {
        boolean process;
        if (isIgnoreMissingSessions()) {
            // check the list of offerings which are current and if course offering is not in that then skip it
            @SuppressWarnings("unchecked")
            Set<String> currentEnrollmentSetEids = (Set<String>) getCurrentSyncVar(CURRENT_ENROLLMENT_SET_EIDS, Set.class);
            if (currentEnrollmentSetEids != null && currentEnrollmentSetEids.contains(enrollmentSetEid)) {
                process = true;
            } else {
                process = false;
            }
        } else {
            // standard processing, process all course offerings
            process = true;
        }
        return process;
    }

    protected Set<String> getCurrentEnrollmentSets() {
        @SuppressWarnings("unchecked")
        Set<String> currentEnrollmentSetEids = (Set<String>) getCurrentSyncVar(CURRENT_ENROLLMENT_SET_EIDS, Set.class);
        if (currentEnrollmentSetEids == null) {
            currentEnrollmentSetEids = new HashSet<String>(0);
        }
        return currentEnrollmentSetEids;
    }



    public void setConfigurationService(ServerConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public void setCmAdmin(CourseManagementAdministration cmAdmin) {
        this.cmAdmin = cmAdmin;
    }

    public void setCmService(CourseManagementService cmService) {
        this.cmService = cmService;
    }

}
