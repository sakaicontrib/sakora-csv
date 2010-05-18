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
package net.unicon.sakora.tool.test.ws.server;

import java.io.IOException;

import junit.framework.TestCase;

import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;
import org.springframework.context.ApplicationContext;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.oxm.support.MarshallingSource;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.oracle.xmlns.enterprise.hcm.services.cms.*;

public class CourseEndpointTest extends TestCase
{
	private ApplicationContext appContext;
	private WebServiceTemplate webServiceTemplate;

	private String endPointUrl;

	private Jaxb2Marshaller marshaller;
	
	protected Unmarshaller getUnmarshaller() {
		return (Unmarshaller) marshaller;
	}


	protected void setUp() throws Exception
	{
		super.setUp();
		webServiceTemplate = new WebServiceTemplate();
		endPointUrl = "http://localhost:8888/sakai-sakora-tool/services/";
		marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("com.oracle.xmlns.enterprise.hcm.services.cms");
		marshaller.afterPropertiesSet();
		
		webServiceTemplate.setDefaultUri(endPointUrl);
		webServiceTemplate.setMarshaller(marshaller);
		webServiceTemplate.setUnmarshaller(marshaller);
	}

	public void testAddCourseSectionId()
	{
		AddCourseSectionIdRequest request = new AddCourseSectionIdRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof AddCourseSectionIdResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testChangeCourseSectionIdentifier()
	{
		ChangeCourseSectionIdentifierRequest request = new ChangeCourseSectionIdentifierRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ChangeCourseSectionIdentifierResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testChangeCourseOfferingIdentifier()
	{
		ChangeCourseOfferingIdentifierRequest request = new ChangeCourseOfferingIdentifierRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ChangeCourseOfferingIdentifierResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testChangeCourseTemplateIdentifier()
	{
		ChangeCourseTemplateIdentifierRequest request = new ChangeCourseTemplateIdentifierRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ChangeCourseTemplateIdentifierResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testChangeSectionAssociationIdentifier()
	{
		ChangeSectionAssociationIdentifierRequest request = new ChangeSectionAssociationIdentifierRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ChangeSectionAssociationIdentifierResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testCreateByProxyCourseSection()
	{
		CreateByProxyCourseSectionRequest request = new CreateByProxyCourseSectionRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof CreateByProxyCourseSectionResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testCreateByProxyCourseOffering()
	{
		CreateByProxyCourseOfferingRequest request = new CreateByProxyCourseOfferingRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof CreateByProxyCourseOfferingResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testCreateByProxyCourseTemplate()
	{
		CreateByProxyCourseTemplateRequest request = new CreateByProxyCourseTemplateRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof CreateByProxyCourseTemplateResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testCreateByProxySectionAssociation()
	{
		CreateByProxySectionAssociationRequest request = new CreateByProxySectionAssociationRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof CreateByProxySectionAssociationResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testCreateCourseOffering()
	{
		CreateCourseOfferingRequest request = new CreateCourseOfferingRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof CreateCourseOfferingResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testCreateCourseSectionFromCourseSection()
	{
		CreateCourseSectionFromCourseSectionRequest request = new CreateCourseSectionFromCourseSectionRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof CreateCourseSectionFromCourseSectionResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testCreateCourseSection()
	{
		CreateCourseSectionRequest request = new CreateCourseSectionRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof CreateCourseSectionResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testCreateCourseTemplate()
	{
		CreateCourseTemplateRequest request = new CreateCourseTemplateRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof CreateCourseTemplateResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testCreateCourseOfferingFromCourseOffering()
	{
		CreateCourseOfferingFromCourseOfferingRequest request = new CreateCourseOfferingFromCourseOfferingRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof CreateCourseOfferingFromCourseOfferingResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testCreateSectionAssociation()
	{
		CreateSectionAssociationRequest request = new CreateSectionAssociationRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof CreateSectionAssociationResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testDeleteCourseOffering()
	{
		DeleteCourseOfferingRequest request = new DeleteCourseOfferingRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof DeleteCourseOfferingResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testDeleteCourseSection()
	{
		DeleteCourseSectionRequest request = new DeleteCourseSectionRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof DeleteCourseSectionResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testDeleteCourseTemplate()
	{
		DeleteCourseTemplateRequest request = new DeleteCourseTemplateRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof DeleteCourseTemplateResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testDeleteSectionAssociation()
	{
		DeleteSectionAssociationRequest request = new DeleteSectionAssociationRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof DeleteSectionAssociationResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void _testDiscoverCourseOfferingIds()
	{
		DiscoverCourseOfferingIdsRequest request = new DiscoverCourseOfferingIdsRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			System.out.println("testDiscoverCourseOfferingIds 1");
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			System.out.println("testDiscoverCourseOfferingIds 2");
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof DiscoverCourseOfferingIdsResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void _testDiscoverCourseSectionIds()
	{
		System.out.println("testDiscoverCourseSectionIds");
		DiscoverCourseSectionIdsRequest request = new DiscoverCourseSectionIdsRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof DiscoverCourseSectionIdsResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void _testDiscoverCourseTemplateIds()
	{
		DiscoverCourseTemplateIdsRequest request = new DiscoverCourseTemplateIdsRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof DiscoverCourseTemplateIdsResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void _testDiscoverSectionAssociationIds()
	{
		DiscoverSectionAssociationIdsRequest request = new DiscoverSectionAssociationIdsRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof DiscoverSectionAssociationIdsResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void _testReadAllActiveCourseOfferingIdsForAcademicSession()
	{
		ReadAllActiveCourseOfferingIdsForAcademicSessionRequest request = new ReadAllActiveCourseOfferingIdsForAcademicSessionRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ReadAllActiveCourseOfferingIdsForAcademicSessionResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void _testReadAllCourseOfferingIds()
	{
		ReadAllCourseOfferingIdsRequest request = new ReadAllCourseOfferingIdsRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ReadAllCourseOfferingIdsResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void _testReadAllCourseSectionIds()
	{
		ReadAllCourseSectionIdsRequest request = new ReadAllCourseSectionIdsRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ReadAllCourseSectionIdsResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void _testReadAllCourseTemplateIds()
	{
		ReadAllCourseTemplateIdsRequest request = new ReadAllCourseTemplateIdsRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ReadAllCourseTemplateIdsResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testReadAllSectionAssociationIds()
	{
		ReadAllSectionAssociationIdsRequest request = new ReadAllSectionAssociationIdsRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ReadAllSectionAssociationIdsResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testReadCourseOfferingIdsFromSavePoint()
	{
		ReadCourseOfferingIdsFromSavePointRequest request = new ReadCourseOfferingIdsFromSavePointRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ReadCourseOfferingIdsFromSavePointResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testReadCourseOfferings()
	{
		ReadCourseOfferingsRequest request = new ReadCourseOfferingsRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ReadCourseOfferingsResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testReadCourseOfferingsFromSavePoint()
	{
		ReadCourseOfferingsFromSavePointRequest request = new ReadCourseOfferingsFromSavePointRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ReadCourseOfferingsFromSavePointResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testReadCourseOffering()
	{
		ReadCourseOfferingRequest request = new ReadCourseOfferingRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ReadCourseOfferingResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testReadCourseSections()
	{
		ReadCourseSectionsRequest request = new ReadCourseSectionsRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ReadCourseSectionsResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testReadCourseSectionsFromSavePoint()
	{
		ReadCourseSectionsFromSavePointRequest request = new ReadCourseSectionsFromSavePointRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ReadCourseSectionsFromSavePointResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testReadCourseSection()
	{
		ReadCourseSectionRequest request = new ReadCourseSectionRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ReadCourseSectionResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testReadCourseTemplateIdsFromSavePoint()
	{
		ReadCourseTemplateIdsFromSavePointRequest request = new ReadCourseTemplateIdsFromSavePointRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ReadCourseTemplateIdsFromSavePointResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testReadCourseTemplates()
	{
		ReadCourseTemplatesRequest request = new ReadCourseTemplatesRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ReadCourseTemplatesResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testReadCourseTemplatesFromSavePoint()
	{
		ReadCourseTemplatesFromSavePointRequest request = new ReadCourseTemplatesFromSavePointRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ReadCourseTemplatesFromSavePointResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testReadCourseTemplate()
	{
		ReadCourseTemplateRequest request = new ReadCourseTemplateRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ReadCourseTemplateResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testReadCourseOfferingIdsForCourseTemplate()
	{
		ReadCourseOfferingIdsForCourseTemplateRequest request = new ReadCourseOfferingIdsForCourseTemplateRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ReadCourseOfferingIdsForCourseTemplateResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testReadCourseSectionIdsForCourseOffering()
	{
		ReadCourseSectionIdsForCourseOfferingRequest request = new ReadCourseSectionIdsForCourseOfferingRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ReadCourseSectionIdsForCourseOfferingResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testReadSectionAssociations()
	{
		ReadSectionAssociationsRequest request = new ReadSectionAssociationsRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ReadSectionAssociationsResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testReadSectionAssociationsFromSavePoint()
	{
		ReadSectionAssociationsFromSavePointRequest request = new ReadSectionAssociationsFromSavePointRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ReadSectionAssociationsFromSavePointResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testReadSectionAssociation()
	{
		ReadSectionAssociationRequest request = new ReadSectionAssociationRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ReadSectionAssociationResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testReadSectionAssociationIdsFromSavePoint()
	{
		ReadSectionAssociationIdsFromSavePointRequest request = new ReadSectionAssociationIdsFromSavePointRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ReadSectionAssociationIdsFromSavePointResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testRemoveCourseSectionId()
	{
		RemoveCourseSectionIdRequest request = new RemoveCourseSectionIdRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof RemoveCourseSectionIdResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testReplaceCourseOffering()
	{
		ReplaceCourseOfferingRequest request = new ReplaceCourseOfferingRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ReplaceCourseOfferingResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testReplaceCourseSection()
	{
		ReplaceCourseSectionRequest request = new ReplaceCourseSectionRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ReplaceCourseSectionResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testReplaceCourseTemplate()
	{
		ReplaceCourseTemplateRequest request = new ReplaceCourseTemplateRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ReplaceCourseTemplateResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testReplaceSectionAssociation()
	{
		ReplaceSectionAssociationRequest request = new ReplaceSectionAssociationRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ReplaceSectionAssociationResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testUpdateCourseOffering()
	{
		UpdateCourseOfferingRequest request = new UpdateCourseOfferingRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof UpdateCourseOfferingResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testUpdateCourseSectionStatus()
	{
		UpdateCourseSectionStatusRequest request = new UpdateCourseSectionStatusRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof UpdateCourseSectionStatusResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testUpdateCourseSection()
	{
		UpdateCourseSectionRequest request = new UpdateCourseSectionRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof UpdateCourseSectionResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testUpdateCourseTemplate()
	{
		UpdateCourseTemplateRequest request = new UpdateCourseTemplateRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof UpdateCourseTemplateResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testUpdateCourseOfferingStatus()
	{
		UpdateCourseOfferingStatusRequest request = new UpdateCourseOfferingStatusRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof UpdateCourseOfferingStatusResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

	public void testUpdateSectionAssociation()
	{
		UpdateSectionAssociationRequest request = new UpdateSectionAssociationRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(
					new MarshallingSource(webServiceTemplate.getMarshaller(), request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = webServiceTemplate.getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof UpdateSectionAssociationResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) 	{
			fail(ioe.getMessage());
		} 
	}

}
