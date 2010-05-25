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
import java.util.HashMap;
import java.util.Map;

import net.unicon.sakora.api.csv.CsvSyncContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.coursemanagement.api.Section;

/**
 * Reads in Section definitions from csv extracts, expected format is:
 * Eid, Title, Description, Category, Parent Section Eid, Enrollment Set Eid, Course Offering Eid
 * 
 * @author Joshua Ryan
 *
 */
@SuppressWarnings("serial")
public class CsvSectionHandler extends CsvHandlerBase {
	static final Log log = LogFactory.getLog(CsvSectionHandler.class);
	
	private String defaultSectionCategoryCode = "NONE";
	private Map<String,String> sectionCategoryMap = new HashMap<String,String>(){{
		this.put(defaultSectionCategoryCode, "Uncategorized");
	}};

	public CsvSectionHandler() {
	}

	@Override
	protected void readInputLine(CsvSyncContext context, String[] line) {

		final int minFieldCount = 7;

		if (line != null && line.length >= minFieldCount) {
			line = trimAll(line);

			// for clarity
			String eid = line[0];
			String title = line[1];
			String description = line[2];
			String category = line[3];
			String parentSectionEid = line[4];
			String enrollmentSetEid = line[5];
			String courseOfferingEid = line[6];

			if ( category == null || cmService.getSectionCategoryDescription(category) == null ) {
				boolean createCategory = true;
				if ( category == null ) {
					category = defaultSectionCategoryCode;
					createCategory = cmService.getSectionCategoryDescription(category) == null;
				}
				if ( createCategory ) {
					String categoryDescription =  sectionCategoryMap.get(category);
					categoryDescription = categoryDescription == null ? category : categoryDescription;
					if ( log.isDebugEnabled() ) {
						log.debug("Creating section category, code: [" + category + 
								"], desc: [" + categoryDescription + "]");
					}
					cmAdmin.addSectionCategory(category, categoryDescription);
				}
			}

			if (!isValid(title, "Title", eid)
					|| !isValid(description, "Description", eid)
					|| !isValid(courseOfferingEid, "Course Offering Eid", eid)) {
				log.error("Missing required parameter(s), skipping item " + eid);
			}
			else if(cmService.isSectionDefined(eid)) {
				Section section = cmService.getSection(eid);
				section.setTitle(title);
				section.setDescription(description);

				section.setCategory(category);
				Section parent = null;
				if (cmService.isSectionDefined(parentSectionEid))
					parent = cmService.getSection(parentSectionEid);
				section.setParent(parent);
				if (cmService.isEnrollmentSetDefined(enrollmentSetEid))	
					section.setEnrollmentSet(cmService.getEnrollmentSet(enrollmentSetEid));
				cmAdmin.updateSection(section);
				updates++;
			}
			else {
				cmAdmin.createSection(eid, title, description, category, parentSectionEid, courseOfferingEid, enrollmentSetEid);
				adds++;
			}
		} else {
			log.error("Skipping short line (expected at least [" + minFieldCount + 
					"] fields): [" + (line == null ? null : Arrays.toString(line)) + "]");
		}
	}

	@Override
	protected void processInternal(CsvSyncContext context) {
		// TODO: support for conditionally removing sections not in current extract?
	}
	
	public String getDefaultSectionCategoryCode() {
		return defaultSectionCategoryCode;
	}

	/**
	 * Section categories are technically optional, but the WS Setup tool
	 * does not degrade gracefully when section categories are not specified.
	 * Use this property, then, to specify the category code to be associated
	 * with sections when a code does not arrive in a CSV file. The value
	 * set here should be a key in {@link #getSectionCategoryMap()}.
	 * 
	 * @param defaultSectionCategoryCode
	 */
	public void setDefaultSectionCategoryCode(String defaultSectionCategoryCode) {
		this.defaultSectionCategoryCode = defaultSectionCategoryCode;
	}
	
	public Map<String, String> getSectionCategoryMap() {
		return sectionCategoryMap;
	}

	/**
	 * Maps section category codes to descriptions. Descriptions are what
	 * actually show up in the WS Setup tool next to section titles. CSV files
	 * only contain section category codes, so this map is used to lazily
	 * populate the underlying CM API's enum of section categories as
	 * category codes arrive.
	 * 
	 * @see #setDefaultSectionCategoryCode(String)
	 * @param sectionCategoryMap
	 */
	public void setSectionCategoryMap(Map<String, String> sectionCategoryMap) {
		this.sectionCategoryMap = sectionCategoryMap;
	}

}
