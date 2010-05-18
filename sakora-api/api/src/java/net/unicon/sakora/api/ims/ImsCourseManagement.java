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
package net.unicon.sakora.api.ims;

import com.oracle.xmlns.enterprise.hcm.services.cms.CourseOfferingType;
import com.oracle.xmlns.enterprise.hcm.services.cms.CourseSectionType;
import com.oracle.xmlns.enterprise.hcm.services.cms.CourseTemplateType;
import com.oracle.xmlns.enterprise.hcm.services.cms.CreateCourseOfferingResponse;
import com.oracle.xmlns.enterprise.hcm.services.cms.CreateCourseSectionResponse;
import com.oracle.xmlns.enterprise.hcm.services.cms.CreateCourseTemplateResponse;
import com.oracle.xmlns.enterprise.hcm.services.cms.CreateSectionAssociationResponse;
import com.oracle.xmlns.enterprise.hcm.services.cms.DeleteCourseOfferingResponse;
import com.oracle.xmlns.enterprise.hcm.services.cms.DeleteCourseSectionResponse;
import com.oracle.xmlns.enterprise.hcm.services.cms.DeleteCourseTemplateResponse;
import com.oracle.xmlns.enterprise.hcm.services.cms.DeleteSectionAssociationResponse;
import com.oracle.xmlns.enterprise.hcm.services.cms.ReadCourseOfferingsFromSavePointRequest;
import com.oracle.xmlns.enterprise.hcm.services.cms.ReadCourseOfferingsFromSavePointResponse;
import com.oracle.xmlns.enterprise.hcm.services.cms.ReadCourseOfferingsRequest;
import com.oracle.xmlns.enterprise.hcm.services.cms.ReadCourseOfferingsResponse;
import com.oracle.xmlns.enterprise.hcm.services.cms.ReadCourseSectionsFromSavePointRequest;
import com.oracle.xmlns.enterprise.hcm.services.cms.ReadCourseSectionsFromSavePointResponse;
import com.oracle.xmlns.enterprise.hcm.services.cms.ReadCourseSectionsRequest;
import com.oracle.xmlns.enterprise.hcm.services.cms.ReadCourseSectionsResponse;
import com.oracle.xmlns.enterprise.hcm.services.cms.ReadCourseTemplatesFromSavePointRequest;
import com.oracle.xmlns.enterprise.hcm.services.cms.ReadCourseTemplatesFromSavePointResponse;
import com.oracle.xmlns.enterprise.hcm.services.cms.ReadCourseTemplatesRequest;
import com.oracle.xmlns.enterprise.hcm.services.cms.ReadCourseTemplatesResponse;
import com.oracle.xmlns.enterprise.hcm.services.cms.ReadSectionAssociationsFromSavePointRequest;
import com.oracle.xmlns.enterprise.hcm.services.cms.ReadSectionAssociationsFromSavePointResponse;
import com.oracle.xmlns.enterprise.hcm.services.cms.ReadSectionAssociationsRequest;
import com.oracle.xmlns.enterprise.hcm.services.cms.ReadSectionAssociationsResponse;
import com.oracle.xmlns.enterprise.hcm.services.cms.SectionAssociationType;
import com.oracle.xmlns.enterprise.hcm.services.cms.UpdateCourseOfferingResponse;
import com.oracle.xmlns.enterprise.hcm.services.cms.UpdateCourseSectionResponse;
import com.oracle.xmlns.enterprise.hcm.services.cms.UpdateCourseTemplateResponse;
import com.oracle.xmlns.enterprise.hcm.services.cms.UpdateSectionAssociationResponse;

public interface ImsCourseManagement
{
	// Course Template interfaces
	CreateCourseTemplateResponse createCourseTemplate( CourseTemplateType courseTemplate);
	
	UpdateCourseTemplateResponse updateCourseTemplate( CourseTemplateType courseTemplate);
	
	DeleteCourseTemplateResponse deleteCourseTemplate( CourseTemplateType courseTemplate);
	
    // course offering interfaces		
	CreateCourseOfferingResponse createCourseOffering( CourseOfferingType courseOffering);
	
	UpdateCourseOfferingResponse updateCourseOffering( CourseOfferingType courseOffering);
	
	DeleteCourseOfferingResponse deleteCourseOffering( CourseOfferingType courseOffering);

    // course section interfaces
	CreateCourseSectionResponse createCourseSection( CourseSectionType courseSection);
	
	UpdateCourseSectionResponse updateCourseSection( CourseSectionType courseSection);
	
	DeleteCourseSectionResponse deleteCourseSection( CourseSectionType courseSection);

    // section association interfaces		
	CreateSectionAssociationResponse createSectionAssociation( SectionAssociationType sectionAssociation);
	
	UpdateSectionAssociationResponse updateSectionAssociation( SectionAssociationType sectionAssociation);
	
	DeleteSectionAssociationResponse deleteSectionAssociation( SectionAssociationType sectionAssociation);

	// read course batch information
	ReadCourseTemplatesResponse readCourseTemplates(ReadCourseTemplatesRequest request);
	
	ReadCourseTemplatesFromSavePointResponse readCourseTemplatesFromSavePoint(ReadCourseTemplatesFromSavePointRequest request);
	
	ReadCourseOfferingsResponse readCourseOfferings(ReadCourseOfferingsRequest request);
	
	ReadCourseOfferingsFromSavePointResponse readCourseOfferingsFromSavePoint(ReadCourseOfferingsFromSavePointRequest request);
	
	ReadCourseSectionsResponse readCourseSections(ReadCourseSectionsRequest request);
	
	ReadCourseSectionsFromSavePointResponse readCourseSectionsFromSavePoint(ReadCourseSectionsFromSavePointRequest request);
	
	ReadSectionAssociationsResponse readSectionAssociations(ReadSectionAssociationsRequest request);
	
	ReadSectionAssociationsFromSavePointResponse readSectionAssociationsFromSavePoint(ReadSectionAssociationsFromSavePointRequest request);
	

}
