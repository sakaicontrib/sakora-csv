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
package net.unicon.sakora.tool.ws.handler;

import javax.xml.transform.Source;

public interface CourseManagementHandler
{
	Source createCourseOffering(Source createCourseOfferingRequest);
	Source createCourseSection(Source createCourseSectionRequest);
	Source createSectionAssociation(Source createSectionAssociationRequest);
	Source deleteCourseOffering(Source deleteCourseOfferingRequest);
	Source deleteCourseSection(Source deleteCourseSectionRequest);
	Source deleteCourseTemplate(Source deleteCourseTemplateRequest);
	Source deleteSectionAssociation(Source deleteSectionAssociationRequest);
	Source createCourseTemplate(Source createCourseTemplateRequest);
	Source addCourseSectionId(Source requestSource);
	Source changeCourseSectionIdentifier(Source requestSource);
	Source readCourseSection(Source requestSource);
	Source readCourseTemplateIdsFromSavePoint(Source requestSource);
	Source readCourseTemplates(Source requestSource);
	Source readCourseSectionsFromSavePoint(Source requestSource);
	Source readCourseSections(Source requestSource);
	Source readCourseOffering(Source requestSource);
	Source readCourseOfferingsFromSavePoint(Source requestSource);
	Source readCourseOfferings(Source requestSource);
	Source readCourseOfferingIdsFromSavePoint(Source requestSource);
	Source readAllSectionAssociationIds(Source requestSource);
	Source readAllCourseTemplateIds(Source requestSource);
	Source readAllCourseSectionIds(Source requestSource);
	Source readAllCourseOfferingIds(Source requestSource);
	Source readAllActiveCourseOfferingIdsForAcademicSession(Source requestSource);
	Source discoverSectionAssociationIds(Source requestSource);
	Source discoverCourseTemplateIds(Source requestSource);
	Source discoverCourseSectionIds(Source requestSource);
	Source discoverCourseOfferingIds(Source requestSource);
	Source createCourseOfferingFromCourseOffering(Source requestSource);
	Source createCourseSectionFromCourseSection(Source requestSource);
	Source createByProxySectionAssociation(Source requestSource);
	Source createByProxyCourseTemplate(Source requestSource);
	Source createByProxyCourseOffering(Source requestSource);
	Source createByProxyCourseSection(Source requestSource);
	Source changeSectionAssociationIdentifier(Source requestSource);
	Source changeCourseTemplateIdentifier(Source requestSource);
	Source changeCourseOfferingIdentifier(Source requestSource);
	Source updateCourseOfferingStatus(Source requestSource);
	Source updateSectionAssociation(Source requestSource);
	Source updateCourseTemplate(Source requestSource);
	Source updateCourseSection(Source requestSource);
	Source updateCourseSectionStatus(Source requestSource);
	Source updateCourseOffering(Source requestSource);
	Source replaceCourseSection(Source requestSource);
	Source replaceCourseTemplate(Source requestSource);
	Source replaceSectionAssociation(Source requestSource);
	Source replaceCourseOffering(Source requestSource);
	Source removeCourseSectionId(Source requestSource);
	Source readSectionAssociationIdsFromSavePoint(Source requestSource);
	Source readSectionAssociation(Source requestSource);
	Source readSectionAssociationsFromSavePoint(Source requestSource);
	Source readSectionAssociations(Source requestSource);
	Source readCourseSectionIdsForCourseOffering(Source requestSource);
	Source readCourseOfferingIdsForCourseTemplate(Source requestSource);
	Source readCourseTemplate(Source requestSource);
	Source readCourseTemplatesFromSavePoint(Source requestSource);
}
