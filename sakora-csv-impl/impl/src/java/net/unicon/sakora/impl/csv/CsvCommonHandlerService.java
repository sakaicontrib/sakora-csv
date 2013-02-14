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

import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.sakaiproject.component.api.ServerConfigurationService;

/**
 * Handled any common processing that needs to be chared between the CSV handlers
 * during processing (or idle time)
 * 
 * @author Aaron Zeckoski azeckoski@unicon.net
 */
public class CsvCommonHandlerService {
    static final Log log = LogFactory.getLog(CsvCommonHandlerService.class);

    protected ServerConfigurationService configurationService = null;

    public void init() {
        // load common config values
        ignoreMissingSessions = configurationService.getBoolean("net.unicon.sakora.csv.ignoreMissingSessions", ignoreMissingSessions);
        if (isIgnoreMissingSessions()) {
            log.info("SakoraCSV ignoreMissingSessions is enabled: all data related to sessions not included in sessions.csv will be left skipped or otherwise unchanged");
        } else {
            log.info("SakoraCSV set to process missing sessions (ignoreMissingSessions=false): all data related to sessions not included in sessions.csv will be processed and REMOVED");
        }
    }

    private int runCounter = 0;
    private static final ThreadLocal<HashMap<String, Object>> runVars = new ThreadLocal<HashMap<String,Object>>() {
        @Override
        protected HashMap<String, Object> initialValue() {
            return new HashMap<String, Object>();
        }
    };

    public synchronized void initRun() {
        // run this before a run starts
        runVars.remove(); // reset
        // setup the run id
        int runId = ++runCounter;
        runVars.get().put("id", runId);
        runVars.get().put("status", "running");
    }

    public synchronized void completeRun(boolean success) {
        // run this after a run completes
        runVars.remove();
    }

    @SuppressWarnings("unchecked")
    public <T> T getCurrentSyncVar(String name, Class<T> type) {
        if (runVars.get().containsKey(name)) {
            return (T) runVars.get().get(name);
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
            runVars.get().remove(name);
        } else {
            runVars.get().put(name, value);
       }
    }

    /**
     * @return the current sync run for this thread OR null if there is not one
     */
    public String getCurrentSyncRunId() {
        return getCurrentSyncVar("id", String.class);
    }

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
        setCurrentSyncVar("currentSessionEids", currentAcademicSessionEids);
        return currentAcademicSessionEids.size();
    }

    protected boolean processAcademicSession(String academicSessionEid) {
        boolean process;
        if (isIgnoreMissingSessions()) {
            // check the list of sessions which are current and if this is not in that set then false
            @SuppressWarnings("unchecked")
            HashSet<String> currentAcademicSessionEids = (HashSet<String>) getCurrentSyncVar("currentSessionEids", HashSet.class);
            if (currentAcademicSessionEids.contains(academicSessionEid)) {
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

    public void setConfigurationService(ServerConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

}
