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
import java.util.Date;

import net.unicon.sakora.api.csv.CsvSyncContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.coursemanagement.api.CourseOffering;

/**
 * Reads in Course Offerings from csv extracts, expected format is:
 * Eid, Session Eid, Title, Description, Status, Start Date, End Date, Canonical Course Eid
 * 
 * @author Dan McCallum dmccallum@unicon.net
 * @author Aaron Zeckoski azeckoski@unicon.net
 * @author Joshua Ryan
 */
public class CsvCourseOfferingHandler extends CsvHandlerBase {
	static final Log log = LogFactory.getLog(CsvCourseOfferingHandler.class);
	
	private boolean handleCanonicalCourseReferences = true;

	@Override
	protected void readInputLine(CsvSyncContext context, String[] line) {

		final int minFieldCount = 7;

		if (line != null && line.length >= minFieldCount) {
			line = trimAll(line);

			// for clarity
			String eid = line[0];
			String sessionEid = line[1];
			String title = line[2];
			String description = line[3];
			String status = line[4];
			Date startDate = parseDate(line[5]);
			Date endDate = parseDate(line[6]);
			String canonicalCourseEid = null;
			if ( line.length > 7 ) {
				if ( handleCanonicalCourseReferences ) {
					canonicalCourseEid = line[7];
				} else {
					if ( log.isDebugEnabled() ) {
						log.debug("Course offering arrived with a canonical course reference of [" + 
								line[7] + "] but canonical course reference handling has been disabled");
					}
				}
			}
			String courseSet = null;
			if (line.length > 8) {
				courseSet = line[8];
			}

			if (!isValid(startDate, "Start Date", eid)
					|| !isValid(endDate, "End Date", eid)
					|| !isValid(sessionEid, "Session Eid", eid)) {
				log.error("Missing required parameter(s), skipping item " + eid);
				errors++;

			} else {
			    if (commonHandlerService.processAcademicSession(sessionEid)) {
			        if (cmService.isCourseOfferingDefined(eid)) {
			            CourseOffering courseOffering = cmService.getCourseOffering(eid);
			            courseOffering.setTitle(title);
			            courseOffering.setDescription(description);
			            courseOffering.setStatus(status);
			            courseOffering.setAcademicSession(cmService.getAcademicSession(sessionEid));
			            courseOffering.setStartDate(startDate);
			            courseOffering.setEndDate(endDate);
			            cmAdmin.updateCourseOffering(courseOffering);
			            updates++;
			        } else {
			            cmAdmin.createCourseOffering(eid, title, description, status, sessionEid, canonicalCourseEid, startDate, endDate);
			            adds++;
			        }
			        int total = commonHandlerService.addCurrentCourseOffering(eid);
			        if (log.isDebugEnabled()) log.debug("Added course offering ("+eid+") to the current list: "+total);
			        if (courseSet != null && cmService.isCourseSetDefined(courseSet)) {
			            cmAdmin.addCourseOfferingToCourseSet(courseSet, eid);
			        }
			    } else {
			        if (log.isDebugEnabled()) log.debug("Skipped processing course offering ("+eid+") because it is in an academic session ("+sessionEid+") which is being skipped");
			    }
			}
		} else {
		    log.error("Skipping short line (expected at least [" + minFieldCount + 
		            "] fields): [" + (line == null ? null : Arrays.toString(line)) + "]");
		    errors++;
		}
	}

	@Override
	protected void processInternal(CsvSyncContext context) {
		// TODO: support for conditionally removing course offerings not in current extract?
	}
	
	/**
	 * @see #setHandleCanonicalCourseReferences(boolean)
	 * @return true if references should be handled
	 */
	public boolean isHandleCanonicalCourseReferences() {
		return handleCanonicalCourseReferences;
	}

	/**
	 * <strong>Does not actually work at this time if set to 
	 * <code>false</code> because the default CM API impl happens to 
	 * require canonical course references on course offerings. Unclear
	 * if that is correct behavior.<strong>
	 * 
	 * <p>Turn canonical course reference handling on and off. This helps support 
	 * batches where for historical reasons course offering files happen to 
	 * arrive with canonical course references, but where those canonical 
	 * courses aren't defined and won't ever be defined, e.g. b/c the SIS 
	 * doesn't actually have a canonical course concept such that generating 
	 * a courses.csv file is onerous.</p>
	 * 
	 * <p>Defaults to true</p>
	 * 
	 * @param handleCanonicalCourseReferences
	 */
	public void setHandleCanonicalCourseReferences(
			boolean handleCanonicalCourseReferences) {
		this.handleCanonicalCourseReferences = handleCanonicalCourseReferences;
	}

	
}
