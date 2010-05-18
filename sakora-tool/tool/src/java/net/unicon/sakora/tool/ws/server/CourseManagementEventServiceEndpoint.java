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
package net.unicon.sakora.tool.ws.server;

import javax.xml.transform.Source;

import net.unicon.sakora.tool.ws.handler.CourseManagementHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.soap.SoapFaultException;

@Endpoint
public class CourseManagementEventServiceEndpoint extends ImsEventServiceEndpoint
{
	private static final Log log = LogFactory
			.getLog(CourseManagementEventServiceEndpoint.class);

	private static final String NAMESPACE_STRING = "http://www.imsglobal.org/services/es/cms2p0/xsd/imscms_v2p0";

	//"http://xmlns.oracle.com/Enterprise/HCM/services/CourseManagementService";

	// private static final Namespace NAMESPACE =
	// Namespace.getNamespace(NAMESPACE_STRING);

	private CourseManagementHandler courseManagementHandler;

	public void setCourseManagementHandler(CourseManagementHandler courseManagementHandler)
	{
		this.courseManagementHandler = courseManagementHandler;
	}

	/*
	 * Definition of endpoints dealing with Course Templates
	 */
	/* Add CourseSections */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "addCourseSectionIdRequest")
    public Source addCourseSectionId(Source requestSource) {
		try {
			return courseManagementHandler.addCourseSectionId(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* changeCourseSectionIdentifier */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "changeCourseSectionIdentifierRequest")
    public Source changeCourseSectionIdentifier(Source requestSource) {
		try {
			return courseManagementHandler.changeCourseSectionIdentifier(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* Change CourseOffering */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "changeCourseOfferingIdentifierRequest")
    public Source changeCourseOfferingIdentifier(Source requestSource) {
		try {
			return courseManagementHandler.changeCourseOfferingIdentifier(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* Change CourseTemplate ID */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "changeCourseTemplateIdentifierRequest")
    public Source changeCourseTemplateIdentifier(Source requestSource) {
		try {
			return courseManagementHandler.changeCourseTemplateIdentifier(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* Change SectionAssociation Id */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "changeSectionAssociationIdentifierRequest")
    public Source changeSectionAssociationIdentifier(Source requestSource) {
		try {
			return courseManagementHandler.changeSectionAssociationIdentifier(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* createByProxyCourseSection */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "createByProxyCourseSectionRequest")
    public Source createByProxyCourseSection(Source requestSource) {
		try {
			return courseManagementHandler.createByProxyCourseSection(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* CourseOffering By Proxy */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "createByProxyCourseOfferingRequest")
    public Source createByProxyCourseOffering(Source requestSource) {
		try {
			return courseManagementHandler.createByProxyCourseOffering(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* Course Template By Proxy */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "createByProxyCourseTemplateRequest")
    public Source createByProxyCourseTemplate(Source requestSource) {
		try {
			return courseManagementHandler.createByProxyCourseTemplate(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* Section Association By Proxy */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "createByProxySectionAssociationRequest")
    public Source createByProxySectionAssociation(Source requestSource) {
		try {
			return courseManagementHandler.createByProxySectionAssociation(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* Create CourseOffering */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "createCourseOfferingRequest")
    public Source createCourseOffering(Source requestSource) {
		try {
			return courseManagementHandler.createCourseOffering(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* createCourseSecFromCourseSec */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "createCourseSectionFromCourseSectionRequest")
    public Source createCourseSectionFromCourseSection(Source requestSource) {
		try {
			return courseManagementHandler.createCourseSectionFromCourseSection(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* createCourseSection */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "createCourseSectionRequest")
    public Source createCourseSection(Source requestSource) {
		try {
			return courseManagementHandler.createCourseSection(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* Create CourseTemplate */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "createCourseTemplateRequest")
    public Source createCourseTemplate(Source requestSource) {
		try {
			return courseManagementHandler.createCourseTemplate(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* Create Offering from Offering */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "createCourseOfferingFromCourseOfferingRequest")
    public Source createCourseOfferingFromCourseOffering(Source requestSource) {
		try {
			return courseManagementHandler.createCourseOfferingFromCourseOffering(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* Create SectionAssociation */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "createSectionAssociationRequest")
    public Source createSectionAssociation(Source requestSource) {
		try {
			return courseManagementHandler.createSectionAssociation(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* Delete CourseOffering */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "deleteCourseOfferingRequest")
    public Source deleteCourseOffering(Source requestSource) {
		try {
			return courseManagementHandler.deleteCourseOffering(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* deleteCourseSection */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "deleteCourseSectionRequest")
    public Source deleteCourseSection(Source requestSource) {
		try {
			return courseManagementHandler.deleteCourseSection(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* Delete CourseTemplate */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "deleteCourseTemplateRequest")
    public Source deleteCourseTemplate(Source requestSource) {
		try {
			return courseManagementHandler.deleteCourseTemplate(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* Delete Section Association */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "deleteSectionAssociationRequest")
    public Source deleteSectionAssociation(Source requestSource) {
		try {
			return courseManagementHandler.deleteSectionAssociation(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* Discover CourseOfferings */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "discoverCourseOfferingIdsRequest")
    public Source discoverCourseOfferingIds(Source requestSource) {
		try {
			return courseManagementHandler.discoverCourseOfferingIds(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* discoverCourseSectionIds */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "discoverCourseSectionIdsRequest")
    public Source discoverCourseSectionIds(Source requestSource) {
		try {
			return courseManagementHandler.discoverCourseSectionIds(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* Discover CourseTemplate IDS */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "discoverCourseTemplateIdsRequest")
    public Source discoverCourseTemplateIds(Source requestSource) {
		try {
			return courseManagementHandler.discoverCourseTemplateIds(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* Discover SectionAssociations */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "discoverSectionAssociationIdsRequest")
    public Source discoverSectionAssociationIds(Source requestSource) {
		try {
			return courseManagementHandler.discoverSectionAssociationIds(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* All Active Offerings */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readAllActiveCourseOfferingIdsForAcademicSessionRequest")
    public Source readAllActiveCourseOfferingIdsForAcademicSession(Source requestSource) {
		try {
			return courseManagementHandler.readAllActiveCourseOfferingIdsForAcademicSession(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* Read All CourseOfferings */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readAllCourseOfferingIdsRequest")
    public Source readAllCourseOfferingIds(Source requestSource) {
		try {
			return courseManagementHandler.readAllCourseOfferingIds(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* readAllCourseSectionIds */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readAllCourseSectionIdsRequest")
    public Source readAllCourseSectionIds(Source requestSource) {
		try {
			return courseManagementHandler.readAllCourseSectionIds(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* Read All CourseTemplateIds */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readAllCourseTemplateIdsRequest")
    public Source readAllCourseTemplateIds(Source requestSource) {
		try {
			return courseManagementHandler.readAllCourseTemplateIds(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* Read All Section Associations */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readAllSectionAssociationIdsRequest")
    public Source readAllSectionAssociationIds(Source requestSource) {
		try {
			return courseManagementHandler.readAllSectionAssociationIds(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* Read CourseOffering Save Point */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readCourseOfferingIdsFromSavePointRequest")
    public Source readCourseOfferingIdsFromSavePoint(Source requestSource) {
		try {
			return courseManagementHandler.readCourseOfferingIdsFromSavePoint(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* Service Operation for IMS V2 */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readCourseOfferingsRequest")
    public Source readCourseOfferings(Source requestSource) {
		try {
			return courseManagementHandler.readCourseOfferings(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* CourseOfferings Save Point */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readCourseOfferingsFromSavePointRequest")
    public Source readCourseOfferingsFromSavePoint(Source requestSource) {
		try {
			return courseManagementHandler.readCourseOfferingsFromSavePoint(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* Read CourseOffering */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readCourseOfferingRequest")
    public Source readCourseOffering(Source requestSource) {
		try {
			return courseManagementHandler.readCourseOffering(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* Read All the Coursesection */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readCourseSectionsRequest")
	public Source readCourseSections(Source requestSource) {
		try {
			return courseManagementHandler.readCourseSections(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

      /* Coursesection Incremental      */
    @PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readCourseSectionsFromSavePointRequest")
    public Source readCourseSectionsFromSavePoint(Source requestSource) {
		try {
			return courseManagementHandler.readCourseSectionsFromSavePoint(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* readCourseSection */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readCourseSectionRequest")
    public Source readCourseSection(Source requestSource) {
		try {
			return courseManagementHandler.readCourseSection(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* CourseTemplate Ids FSP */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readCourseTemplateIdsFromSavePointRequest")
    public Source readCourseTemplateIdsFromSavePoint(Source requestSource) {
		try {
			return courseManagementHandler.readCourseTemplateIdsFromSavePoint(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* ServiceOperation for IMS V2 */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readCourseTemplatesRequest")
    public Source readCourseTemplates(Source requestSource) {
		try {
			return courseManagementHandler.readCourseTemplates(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* CourseTemplates Save point */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readCourseTemplatesFromSavePointRequest")
    public Source readCourseTemplatesFromSavePoint(Source requestSource) {
		try {
			return courseManagementHandler.readCourseTemplatesFromSavePoint(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* Read CourseTemplate */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readCourseTemplateRequest")
    public Source readCourseTemplate(Source requestSource) {
		try {
			return courseManagementHandler.readCourseTemplate(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* Offerings for Templates */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readCourseOfferingIdsForCourseTemplateRequest")
    public Source readCourseOfferingIdsForCourseTemplate(Source requestSource) {
		try {
			return courseManagementHandler.readCourseOfferingIdsForCourseTemplate(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* Read Sections for Offering */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readCourseSectionIdsForCourseOfferingRequest")
    public Source readCourseSectionIdsForCourseOffering(Source requestSource) {
		try {
			return courseManagementHandler.readCourseSectionIdsForCourseOffering(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* Section Association Snapshot */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readSectionAssociationsRequest")
    public Source readSectionAssociations(Source requestSource) {
		try {
			return courseManagementHandler.readSectionAssociations(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* SectionAssociations Save Point */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readSectionAssociationsFromSavePointRequest")
    public Source readSectionAssociationsFromSavePoint(Source requestSource) {
		try {
			return courseManagementHandler.readSectionAssociationsFromSavePoint(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* Read Section Assocation */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readSectionAssociationRequest")
    public Source readSectionAssociation(Source requestSource) {
		try {
			return courseManagementHandler.readSectionAssociation(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* Read SectionAssociations */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readSectionAssociationIdsFromSavePointRequest")
    public Source readSectionAssociationIdsFromSavePoint(Source requestSource) {
		try {
			return courseManagementHandler.readSectionAssociationIdsFromSavePoint(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* Remove CourseSection ID */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "removeCourseSectionIdRequest")
	public Source removeCourseSectionId(Source requestSource) {
		try {
			return courseManagementHandler.removeCourseSectionId(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

      /* Replace Course Offering      */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "replaceCourseOfferingRequest")
    public Source replaceCourseOffering(Source requestSource) {
		try {
			return courseManagementHandler.replaceCourseOffering(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

      /* replaceCourseSection      */
    @PayloadRoot(namespace = NAMESPACE_STRING, localPart = "replaceCourseSectionRequest")
    public Source replaceCourseSection(Source requestSource) {
		try {
			return courseManagementHandler.replaceCourseSection(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* Replace CourseTemplate */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "replaceCourseTemplateRequest")
    public Source replaceCourseTemplate(Source requestSource) {
		try {
			return courseManagementHandler.replaceCourseTemplate(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* Replace SectionAssociation */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "replaceSectionAssociationRequest")
    public Source replaceSectionAssociation(Source requestSource) {
		try {
			return courseManagementHandler.replaceSectionAssociation(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* Update Course Offering */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "updateCourseOfferingRequest")
    public Source updateCourseOffering(Source requestSource) {
		try {
			return courseManagementHandler.updateCourseOffering(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* updateCourseSectionStatus */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "updateCourseSectionStatusRequest")
    public Source updateCourseSectionStatus(Source requestSource) {
		try {
			return courseManagementHandler.updateCourseSectionStatus(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* updateCourseSection */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "updateCourseSectionRequest")
    public Source updateCourseSection(Source requestSource) {
		try {
			return courseManagementHandler.updateCourseSection(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* Update CourseTemplate */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "updateCourseTemplateRequest")
    public Source updateCourseTemplate(Source requestSource) {
		try {
			return courseManagementHandler.updateCourseTemplate(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* Update CourseOffering Status */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "updateCourseOfferingStatusRequest")
    public Source updateCourseOfferingStatus(Source requestSource) {
		try {
			return courseManagementHandler.updateCourseOfferingStatus(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

	/* Update SectionAssociation */
	@PayloadRoot(namespace = NAMESPACE_STRING, localPart = "updateSectionAssociationRequest")
    public Source updateSectionAssociation(Source requestSource) {
		try {
			return courseManagementHandler.updateSectionAssociation(requestSource);
		} catch (Exception ex) {
			throw new SoapFaultException(ex);
		}		
	}

}
