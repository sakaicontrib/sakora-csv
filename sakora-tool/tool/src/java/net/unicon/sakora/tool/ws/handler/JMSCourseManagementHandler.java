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


import java.io.IOException;

import javax.xml.transform.Source;

import org.springframework.oxm.support.MarshallingSource;
import org.springframework.ws.soap.SoapFaultException;

import com.oracle.xmlns.enterprise.hcm.services.cms.CourseOfferingType;
import com.oracle.xmlns.enterprise.hcm.services.cms.CourseSectionType;
import com.oracle.xmlns.enterprise.hcm.services.cms.CourseTemplateType;
import com.oracle.xmlns.enterprise.hcm.services.cms.CreateCourseOfferingResponse;
import com.oracle.xmlns.enterprise.hcm.services.cms.CreateCourseSectionResponse;
import com.oracle.xmlns.enterprise.hcm.services.cms.CreateCourseTemplateResponse;
import com.oracle.xmlns.enterprise.hcm.services.cms.CreateSectionAssociationResponse;
import com.oracle.xmlns.enterprise.hcm.services.cms.DeleteCourseOfferingRequest;
import com.oracle.xmlns.enterprise.hcm.services.cms.DeleteCourseOfferingResponse;
import com.oracle.xmlns.enterprise.hcm.services.cms.DeleteCourseSectionRequest;
import com.oracle.xmlns.enterprise.hcm.services.cms.DeleteCourseSectionResponse;
import com.oracle.xmlns.enterprise.hcm.services.cms.DeleteCourseTemplateRequest;
import com.oracle.xmlns.enterprise.hcm.services.cms.DeleteCourseTemplateResponse;
import com.oracle.xmlns.enterprise.hcm.services.cms.DeleteSectionAssociationRequest;
import com.oracle.xmlns.enterprise.hcm.services.cms.DeleteSectionAssociationResponse;
import com.oracle.xmlns.enterprise.hcm.services.cms.ExtensionFieldType;
import com.oracle.xmlns.enterprise.hcm.services.cms.IMSExtensionType;
import com.oracle.xmlns.enterprise.hcm.services.cms.SectionAssociationType;
import com.oracle.xmlns.enterprise.hcm.services.cms.UpdateCourseOfferingRequest;
import com.oracle.xmlns.enterprise.hcm.services.cms.UpdateCourseOfferingResponse;
import com.oracle.xmlns.enterprise.hcm.services.cms.UpdateCourseSectionRequest;
import com.oracle.xmlns.enterprise.hcm.services.cms.UpdateCourseSectionResponse;
import com.oracle.xmlns.enterprise.hcm.services.cms.UpdateCourseTemplateRequest;
import com.oracle.xmlns.enterprise.hcm.services.cms.UpdateCourseTemplateResponse;
import com.oracle.xmlns.enterprise.hcm.services.cms.UpdateSectionAssociationRequest;
import com.oracle.xmlns.enterprise.hcm.services.cms.UpdateSectionAssociationResponse;

public class JMSCourseManagementHandler extends JMSManagementHandler 
	implements CourseManagementHandler
{
	private static final String COURSE_TEMPLATE_ELEMENT = "courseTemplate";

	private static final String COURSE_OFFERING_ELEMENT = "courseOffering";

	private static final String COURSE_SECTION_ELEMENT = "courseSection";

	private static final String SECTION_ASSOCIATION_ELEMENT = "sectionAssociation";

	public Source createCourseOffering(Source createCourseOfferingRequest)
	{
		parseSourceAndProcess(createCourseOfferingRequest, COURSE_OFFERING_ELEMENT);

		CreateCourseOfferingResponse response = new CreateCourseOfferingResponse();
		return new MarshallingSource(getMarshaller(), response);
	}

	public Source createCourseSection(Source createCourseSectionRequest)
	{
		parseSourceAndProcess(createCourseSectionRequest, COURSE_SECTION_ELEMENT);

		CreateCourseSectionResponse response = new CreateCourseSectionResponse();
		return new MarshallingSource(getMarshaller(), response);
	}

	public Source createSectionAssociation(Source createSectionAssociationRequest)
	{
		parseSourceAndProcess(createSectionAssociationRequest, SECTION_ASSOCIATION_ELEMENT);

		CreateSectionAssociationResponse response = new CreateSectionAssociationResponse();
		return new MarshallingSource(getMarshaller(), response);
	}

	
	public Source deleteCourseOffering(Source deleteCourseOfferingRequest)
	{
		// that really sucks, but the JMS implementation expects a D mode entry
		try {
			DeleteCourseOfferingRequest deleteRequest = (DeleteCourseOfferingRequest)getUnmarshaller().unmarshal(deleteCourseOfferingRequest);
			UpdateCourseOfferingRequest request = new UpdateCourseOfferingRequest();
			request.setSourcedId(deleteRequest.getSourcedId());

			// create a new course section type based on the sourcedId and the 'D' mode
			CourseOfferingType courseOfferingType = new CourseOfferingType();
			courseOfferingType.setSourcedId(deleteRequest.getSourcedId());
			
			IMSExtensionType extension = new IMSExtensionType();
			extension.getExtensionField().add(createDeleteMode());
			courseOfferingType.setExtension(extension);
			request.setCourseOffering(courseOfferingType);
			
			parseSourceAndProcess(new MarshallingSource(getMarshaller(),deleteRequest), COURSE_OFFERING_ELEMENT);
	
			DeleteCourseOfferingResponse response = new DeleteCourseOfferingResponse();
			return new MarshallingSource(getMarshaller(), response);
		} catch (IOException ioe) {
			throw new SoapFaultException(ioe);
		}

	}

	public Source deleteCourseSection(Source deleteCourseSectionRequest)
	{
		// that really sucks, but the JMS implementation expects a D mode entry
		try {
			DeleteCourseSectionRequest deleteRequest = (DeleteCourseSectionRequest)getUnmarshaller().unmarshal(deleteCourseSectionRequest);
			UpdateCourseSectionRequest request = new UpdateCourseSectionRequest();
			request.setSourcedId(deleteRequest.getSourcedId());
			
			// create a new course section type based on the sourcedId and the 'D' mode
			CourseSectionType courseSectionType = new CourseSectionType();
			courseSectionType.setSourcedId(deleteRequest.getSourcedId());
			
			IMSExtensionType extension = new IMSExtensionType();
			extension.getExtensionField().add(createDeleteMode());
			courseSectionType.setExtension(extension);
			request.setCourseSection(courseSectionType);
			
			parseSourceAndProcess(new MarshallingSource(getMarshaller(),deleteRequest), COURSE_SECTION_ELEMENT);
	
			DeleteCourseSectionResponse response = new DeleteCourseSectionResponse();
			return new MarshallingSource(getMarshaller(), response);
		} catch (IOException ioe) {
			throw new SoapFaultException(ioe);
		}
	}

	public Source deleteCourseTemplate(Source deleteCourseTemplateRequest)
	{
		// that really sucks, but the JMS implementation expects a D mode entry
		try {
			DeleteCourseTemplateRequest deleteRequest = (DeleteCourseTemplateRequest)getUnmarshaller().unmarshal(deleteCourseTemplateRequest);
			UpdateCourseTemplateRequest request = new UpdateCourseTemplateRequest();
			request.setSourcedId(deleteRequest.getSourcedId());
			
			// create a new course section type based on the sourcedId and the 'D' mode
			CourseTemplateType courseTemplateType = new CourseTemplateType();
			courseTemplateType.setSourcedId(deleteRequest.getSourcedId());
			
			IMSExtensionType extension = new IMSExtensionType();
			extension.getExtensionField().add(createDeleteMode());
			courseTemplateType.setExtension(extension);
			request.setCourseTemplate(courseTemplateType);
			
			parseSourceAndProcess(deleteCourseTemplateRequest, COURSE_TEMPLATE_ELEMENT);
	
			DeleteCourseTemplateResponse response = new DeleteCourseTemplateResponse();
			return new MarshallingSource(getMarshaller(), response);
		} catch (IOException ioe) {
			throw new SoapFaultException(ioe);
		}
	}

	public Source deleteSectionAssociation(Source deleteSectionAssociationRequest)
	{
		// that really sucks, but the JMS implementation expects a D mode entry
		try {
			DeleteSectionAssociationRequest deleteRequest = (DeleteSectionAssociationRequest)getUnmarshaller().unmarshal(deleteSectionAssociationRequest);
			UpdateSectionAssociationRequest request = new UpdateSectionAssociationRequest();
			request.setSourcedId(deleteRequest.getSourcedId());
			
			// create a new course section type based on the sourcedId and the 'D' mode
			SectionAssociationType sectionAssociationType = new SectionAssociationType();
			sectionAssociationType.setSourcedId(deleteRequest.getSourcedId());
			
			IMSExtensionType extension = new IMSExtensionType();
			extension.getExtensionField().add(createDeleteMode());
			sectionAssociationType.setExtension(extension);
			request.setSectionAssociation(sectionAssociationType);
			
			parseSourceAndProcess(deleteSectionAssociationRequest, SECTION_ASSOCIATION_ELEMENT);
	
			DeleteSectionAssociationResponse response = new DeleteSectionAssociationResponse();
			return new MarshallingSource(getMarshaller(), response);
		} catch (IOException ioe) {
			throw new SoapFaultException(ioe);
		}

	}

	public Source addCourseSectionId(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source changeCourseOfferingIdentifier(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source changeCourseSectionIdentifier(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source changeCourseTemplateIdentifier(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source changeSectionAssociationIdentifier(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source createByProxyCourseOffering(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source createByProxyCourseSection(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source createByProxyCourseTemplate(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source createByProxySectionAssociation(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source createCourseOfferingFromCourseOffering(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source createCourseSectionFromCourseSection(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source createCourseTemplate(Source requestSource)
	{
		parseSourceAndProcess(requestSource, COURSE_TEMPLATE_ELEMENT);

		CreateCourseTemplateResponse response = new CreateCourseTemplateResponse();
		return new MarshallingSource(getMarshaller(), response);
	}

	public Source discoverCourseOfferingIds(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source discoverCourseSectionIds(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source discoverCourseTemplateIds(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source discoverSectionAssociationIds(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readAllActiveCourseOfferingIdsForAcademicSession(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readAllCourseOfferingIds(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readAllCourseSectionIds(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readAllCourseTemplateIds(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readAllSectionAssociationIds(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readCourseOffering(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readCourseOfferingIdsForCourseTemplate(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readCourseOfferingIdsFromSavePoint(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readCourseOfferings(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readCourseOfferingsFromSavePoint(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readCourseSection(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readCourseSectionIdsForCourseOffering(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readCourseSections(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readCourseSectionsFromSavePoint(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readCourseTemplate(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readCourseTemplateIdsFromSavePoint(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readCourseTemplates(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readCourseTemplatesFromSavePoint(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readSectionAssociation(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readSectionAssociationIdsFromSavePoint(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readSectionAssociations(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readSectionAssociationsFromSavePoint(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source removeCourseSectionId(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source replaceCourseOffering(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source replaceCourseSection(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source replaceCourseTemplate(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source replaceSectionAssociation(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source updateCourseOffering(Source requestSource)
	{
		parseSourceAndProcess(requestSource, COURSE_OFFERING_ELEMENT);

		UpdateCourseOfferingResponse response = new UpdateCourseOfferingResponse();
		return new MarshallingSource(getMarshaller(), response);
	}

	public Source updateCourseOfferingStatus(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source updateCourseSection(Source requestSource)
	{
		parseSourceAndProcess(requestSource, COURSE_SECTION_ELEMENT);

		UpdateCourseSectionResponse response = new UpdateCourseSectionResponse();
		return new MarshallingSource(getMarshaller(), response);

	}

	public Source updateCourseSectionStatus(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source updateCourseTemplate(Source requestSource)
	{
		parseSourceAndProcess(requestSource, COURSE_TEMPLATE_ELEMENT);

		UpdateCourseTemplateResponse response = new UpdateCourseTemplateResponse();
		return new MarshallingSource(getMarshaller(), response);

	}

	public Source updateSectionAssociation(Source requestSource)
	{
		parseSourceAndProcess(requestSource, SECTION_ASSOCIATION_ELEMENT);

		UpdateSectionAssociationResponse response = new UpdateSectionAssociationResponse();
		return new MarshallingSource(getMarshaller(), response);

	}

	private ExtensionFieldType createDeleteMode()  {
		ExtensionFieldType extType = new ExtensionFieldType();
		extType.setFieldName("Mode");
		extType.setFieldValue("D");
		return extType;
	}
}
