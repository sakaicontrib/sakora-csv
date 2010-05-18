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
package net.unicon.sakora.impl.ws.service;

import javax.xml.transform.Source;

import org.jdom.Namespace;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

@Endpoint
public class CourseManagementServicesEndpoint extends ResponseBuilderEndpoint {
	protected final String NAMESPACE_STRING = "http://xmlns.oracle.com/Enterprise/HCM/services/CourseManagementService.1"; 
	protected final Namespace NAMESPACE = Namespace.getNamespace(NAMESPACE_STRING);
	
	public Source addCourseSectionId(Source request) {
		return null;
	}

	public Source changeCourseSectionIdentifier(Source request) {
		return null;
	}

	public Source changeCourseOfferingIdentifier(Source request) {
		return null;
	}

	public Source changeCourseTemplateIdentifier(Source request) {
		return null;
	}

	public Source changeSectionAssociationIdentifier(Source request) {
		return null;
	}

	public Source createByProxyCourseSection(Source request) {
		return null;
	}

	public Source createByProxyCourseOffering(Source request) {
		return null;
	}

	public Source createByProxyCourseTemplate(Source request) {
		return null;
	}

	public Source createCourseOffering(Source request) {
		return null;
	}

	public Source createCourseSectionFromCourseSection(Source request) {
		return null;
	}

	public Source createCourseSection(Source request) {
		return null;
	}

	public Source createCourseTemplate(Source request) {
		return null;
	}

	public Source createCourseOfferingFromCourseOffering(Source request) {
		return null;
	}

	public Source createSectionAssociation(Source request) {
		return null;
	}

	public Source deleteCourseOffering(Source request) {
		return null;
	}

	public Source deleteCourseSection(Source request) {
		return null;
	}

	public Source deleteCourseTemplate(Source request) {
		return null;
	}

	public Source deleteSectionAssociation(Source request) {
		return null;
	}

	public Source discoverCourseOfferingIds(Source request) {
		return null;
	}

	public Source discoverCourseSectionIds(Source request) {
		return null;
	}

	public Source discoverCourseTemplateIds(Source request) {
		return null;
	}

	public Source discoverSectionAssociationIds(Source request) {
		return null;
	}

	public Source readAllActiveCourseOfferingIdsForAcademicSession(
			Source request) {
		return null;
	}

	public Source readAllCourseOfferingIds(Source request) {
		return null;
	}

	public Source readAllCourseSectionIds(Source request) {
		return null;
	}

	public Source readAllCourseTemplateIds(Source request) {
		return null;
	}

	public Source readAllSectionAssociationIds(Source request) {
		return null;
	}

	public Source READBATCHCOURSESECTION(Source request) {
		return null;
	}

	public Source readCourseOfferingIdsFromSavePoint(Source request) {
		return null;
	}

	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readCourseOfferingsRequest")
	public Source readCourseOfferings(Source request) {
		return getResponseBuilder().buildSnapshotResponse("readCourseOfferingsResponse", "READCOURSEOFFERINGS.xml");
	}

	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readCourseOfferingsFromSavePointRequest")
	public Source readCourseOfferingsFromSavePoint(Source request) {
		return getResponseBuilder().buildIncrementalResponse("readCourseOfferingsFromSavePointResponse", "READCOURSEOFFERINGS.xml","2008-01-01-1");
	}

	public Source readCourseOffering(Source request) {
		return null;
	}

	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readCourseSectionsRequest")
	public Source readCourseSections(Source request) {
		return getResponseBuilder().buildSnapshotResponse("readCourseSectionsResponse", "READCOURSESECTIONS.xml");
	}

	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readCourseSectionsFromSavePointRequest")
	public Source readCourseSectionsFromSavePoint(Source request) {
		return getResponseBuilder().buildIncrementalResponse("readCourseSectionsFromSavePointResponse", "READCOURSESECTIONS.xml","2008-01-01-1");
	}

	public Source readCourseSection(Source request) {
		return null;
	}

	public Source readCourseTemplateIdsFromSavePoint(Source request) {
		return null;
	}

	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readCourseTemplatesRequest")
	public Source readCourseTemplates(Source request) {
		return getResponseBuilder().buildSnapshotResponse("readCourseTemplatesResponse", "READCOURSETEMPLATES.xml");
	}

	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readCourseTemplatesFromSavePointRequest")
	public Source readCourseTemplatesFromSavePoint(Source request) {
		return getResponseBuilder().buildIncrementalResponse("readCourseTemplatesFromSavePointResponse", "READCOURSETEMPLATES.xml","2008-01-01-1");
	}

	public Source readCourseTemplate(Source request) {
		return null;
	}

	public Source readCourseOfferingIdsForCourseTemplate(Source request) {
		return null;
	}

	public Source readCourseSectionIdsForCourseOffering(Source request) {
		return null;
	}

	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readSectionAssociationsRequest")
	public Source readSectionAssociations(Source request) {
		return getResponseBuilder().buildSnapshotResponse("readSectionAssociationsResponse", "READSECTIONASSOCIATIONS.xml");
	}

	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readSectionAssociationsFromSavePointRequest")
	public Source readSectionAssociationsFromSavePoint(Source request) {
		return getResponseBuilder().buildIncrementalResponse("readSectionAssociationsFromSavePointResponse", "READSECTIONASSOCIATIONS.xml","2008-01-01-1");
	}

	public Source readSectionAssociation(Source request) {
		return null;
	}

	public Source readSectionAssociationIdsFromSavePoint(Source request) {
		return null;
	}

	public Source removeCourseSectionId(Source request) {
		return null;
	}

	public Source replaceCourseOffering(Source request) {
		return null;
	}

	public Source replaceCourseSection(Source request) {
		return null;
	}

	public Source replaceCourseTemplate(Source request) {
		return null;
	}

	public Source replaceSectionAssociation(Source request) {
		return null;
	}

	public Source updateCourseOffering(Source request) {
		return null;
	}

	public Source updateCourseSectionStatus(Source request) {
		return null;
	}

	public Source updateCourseSection(Source request) {
		return null;
	}

	public Source updateCourseTemplate(Source request) {
		return null;
	}

	public Source updateCourseOfferingStatus(Source request) {
		return null;
	}

	public Source updateSectionAssociation(Source request) {
		return null;
	}

}
