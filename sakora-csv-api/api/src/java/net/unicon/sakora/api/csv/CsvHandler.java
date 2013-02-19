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
package net.unicon.sakora.api.csv;

import java.util.Map;

public interface CsvHandler {
    /**
     * @return the name of this handler (should be unique)
     */
    public String getName();
    /**
     * @return the map of stats values (lines, errors, adds, updates, deletes, start, seconds, end),
     * start and end are unix timestamps (in seconds), seconds is runtime for this handler processing
     */
    public Map<String, Integer> getStats();
    /**
     * Executed immediately before the sync processing for this handler is complete
     * @param context
     */
    public void before(CsvSyncContext syncContext);
	public void readInput(CsvSyncContext syncContext);
	public void process(CsvSyncContext syncContext);
	public void cleanUp(CsvSyncContext syncContext);
    /**
     * Executed after the sync processing for this handler is complete,
     * NOTE: check the SyncContext IS_BATCH_OK or the current handler state var to see if there was an error
     * @param context
     */
    public void after(CsvSyncContext syncContext);
}
