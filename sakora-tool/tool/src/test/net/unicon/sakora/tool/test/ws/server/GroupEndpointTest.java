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
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.oxm.support.MarshallingSource;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.oracle.xmlns.enterprise.hcm.services.cms.UpdateCourseSectionResponse;
import com.oracle.xmlns.enterprise.hcm.services.gms.AddGroupRelationshipRequest;
import com.oracle.xmlns.enterprise.hcm.services.gms.AddGroupRelationshipResponse;
import com.oracle.xmlns.enterprise.hcm.services.gms.ChangeGroupIdentifierRequest;
import com.oracle.xmlns.enterprise.hcm.services.gms.ChangeGroupIdentifierResponse;
import com.oracle.xmlns.enterprise.hcm.services.gms.CreateByProxyGroupRequest;
import com.oracle.xmlns.enterprise.hcm.services.gms.CreateByProxyGroupResponse;
import com.oracle.xmlns.enterprise.hcm.services.gms.CreateGroupRequest;
import com.oracle.xmlns.enterprise.hcm.services.gms.CreateGroupResponse;
import com.oracle.xmlns.enterprise.hcm.services.gms.DeleteGroupRequest;
import com.oracle.xmlns.enterprise.hcm.services.gms.DeleteGroupResponse;
import com.oracle.xmlns.enterprise.hcm.services.gms.DiscoverGroupIdsRequest;
import com.oracle.xmlns.enterprise.hcm.services.gms.ReadAllGroupIdsRequest;
import com.oracle.xmlns.enterprise.hcm.services.gms.ReadGroupIdsForPersonRequest;
import com.oracle.xmlns.enterprise.hcm.services.gms.ReadGroupIdsForPersonResponse;
import com.oracle.xmlns.enterprise.hcm.services.gms.ReadGroupIdsFromSavePointRequest;
import com.oracle.xmlns.enterprise.hcm.services.gms.ReadGroupIdsFromSavePointResponse;
import com.oracle.xmlns.enterprise.hcm.services.gms.ReadGroupRequest;
import com.oracle.xmlns.enterprise.hcm.services.gms.ReadGroupResponse;
import com.oracle.xmlns.enterprise.hcm.services.gms.ReadGroupsFromSavePointRequest;
import com.oracle.xmlns.enterprise.hcm.services.gms.ReadGroupsFromSavePointResponse;
import com.oracle.xmlns.enterprise.hcm.services.gms.ReadGroupsRequest;
import com.oracle.xmlns.enterprise.hcm.services.gms.ReadGroupsResponse;
import com.oracle.xmlns.enterprise.hcm.services.gms.RemoveGroupRelationshipRequest;
import com.oracle.xmlns.enterprise.hcm.services.gms.RemoveGroupRelationshipResponse;
import com.oracle.xmlns.enterprise.hcm.services.gms.ReplaceGroupRequest;
import com.oracle.xmlns.enterprise.hcm.services.gms.ReplaceGroupResponse;
import com.oracle.xmlns.enterprise.hcm.services.gms.UpdateGroupRequest;
import com.oracle.xmlns.enterprise.hcm.services.gms.UpdateGroupResponse;

public class GroupEndpointTest extends TestCase
{
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
		marshaller.setContextPath("com.oracle.xmlns.enterprise.hcm.services.gms");
		marshaller.afterPropertiesSet();
	}

	public void testAddGroupRelationship()
	{
		AddGroupRelationshipRequest request = new AddGroupRelationshipRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(endPointUrl,
					new MarshallingSource(marshaller, request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof AddGroupRelationshipResponse);

		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) {
			fail(ioe.getMessage());
		}
	}

	public void testChangeGroupIdentifier()
	{
		ChangeGroupIdentifierRequest request = new ChangeGroupIdentifierRequest();
		try
		{

			JDOMResult responseResult = new JDOMResult();
			webServiceTemplate.sendSourceAndReceiveToResult(endPointUrl,
					new MarshallingSource(marshaller, request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ChangeGroupIdentifierResponse);

		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) {
			fail(ioe.getMessage());
		}
	}

	public void testCreateByProxyGroup()
	{
		CreateByProxyGroupRequest request = new CreateByProxyGroupRequest();

		try
		{
			JDOMResult responseResult = new JDOMResult();
			webServiceTemplate.sendSourceAndReceiveToResult(endPointUrl,
					new MarshallingSource(marshaller, request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof CreateByProxyGroupResponse);

		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) {
			fail(ioe.getMessage());
		}
	}

	public void testCreateGroup()
	{
		CreateGroupRequest request = new CreateGroupRequest();

		try
		{
			JDOMResult responseResult = new JDOMResult();
			webServiceTemplate.sendSourceAndReceiveToResult(endPointUrl,
					new MarshallingSource(marshaller, request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof CreateGroupResponse);

		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) {
			fail(ioe.getMessage());
		}
	}

	public void testDeleteGroup()
	{
		DeleteGroupRequest request = new DeleteGroupRequest();

		try
		{
			JDOMResult responseResult = new JDOMResult();
			webServiceTemplate.sendSourceAndReceiveToResult(endPointUrl,
					new MarshallingSource(marshaller, request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof DeleteGroupResponse);

		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) {
			fail(ioe.getMessage());
		}
	}

	public void testDiscoverGroupIds()
	{
		DiscoverGroupIdsRequest request = new DiscoverGroupIdsRequest();

		try
		{
			JDOMResult responseResult = new JDOMResult();
			webServiceTemplate.sendSourceAndReceiveToResult(endPointUrl,
					new MarshallingSource(marshaller, request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof UpdateCourseSectionResponse);

		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) {
			fail(ioe.getMessage());
		}
	}

	public void testReadAllGroupIds()
	{
		ReadAllGroupIdsRequest request = new ReadAllGroupIdsRequest();

		try
		{
			JDOMResult responseResult = new JDOMResult();
			webServiceTemplate.sendSourceAndReceiveToResult(endPointUrl,
					new MarshallingSource(marshaller, request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof UpdateCourseSectionResponse);

		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) {
			fail(ioe.getMessage());
		}
	}

	public void testReadGroupIdsForPerson()
	{
		ReadGroupIdsForPersonRequest request = new ReadGroupIdsForPersonRequest();

		try
		{
			JDOMResult responseResult = new JDOMResult();
			webServiceTemplate.sendSourceAndReceiveToResult(endPointUrl,
					new MarshallingSource(marshaller, request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ReadGroupIdsForPersonResponse);

		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) {
			fail(ioe.getMessage());
		}
	}

	public void testReadGroupIdsFromSavePoint()
	{
		ReadGroupIdsFromSavePointRequest request = new ReadGroupIdsFromSavePointRequest();

		try
		{
			JDOMResult responseResult = new JDOMResult();
			webServiceTemplate.sendSourceAndReceiveToResult(endPointUrl,
					new MarshallingSource(marshaller, request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ReadGroupIdsFromSavePointResponse);

		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) {
			fail(ioe.getMessage());
		}
	}

	public void ReadGroupsFromSavePoint()
	{
		ReadGroupsFromSavePointRequest request = new ReadGroupsFromSavePointRequest();

		try
		{
			JDOMResult responseResult = new JDOMResult();
			webServiceTemplate.sendSourceAndReceiveToResult(endPointUrl,
					new MarshallingSource(marshaller, request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ReadGroupsFromSavePointResponse);

		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) {
			fail(ioe.getMessage());
		}
	}

	public void testReadGroups()
	{
		ReadGroupsRequest request = new ReadGroupsRequest();

		try
		{
			JDOMResult responseResult = new JDOMResult();
			webServiceTemplate.sendSourceAndReceiveToResult(endPointUrl,
					new MarshallingSource(marshaller, request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ReadGroupsResponse);

		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) {
			fail(ioe.getMessage());
		}
	}

	public void testReadGroup()
	{
		ReadGroupRequest request = new ReadGroupRequest();

		try
		{
			JDOMResult responseResult = new JDOMResult();
			webServiceTemplate.sendSourceAndReceiveToResult(endPointUrl,
					new MarshallingSource(marshaller, request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ReadGroupResponse);

		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) {
			fail(ioe.getMessage());
		}
	}

	public void testRemoveGroupRelationship()
	{
		RemoveGroupRelationshipRequest request = new RemoveGroupRelationshipRequest();

		try
		{
			JDOMResult responseResult = new JDOMResult();
			webServiceTemplate.sendSourceAndReceiveToResult(endPointUrl,
					new MarshallingSource(marshaller, request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof RemoveGroupRelationshipResponse);

		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) {
			fail(ioe.getMessage());
		}
	}

	public void testReplaceGroup()
	{
		ReplaceGroupRequest request = new ReplaceGroupRequest();

		try
		{
			JDOMResult responseResult = new JDOMResult();
			webServiceTemplate.sendSourceAndReceiveToResult(endPointUrl,
					new MarshallingSource(marshaller, request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ReplaceGroupResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) {
			fail(ioe.getMessage());
		}
	}

	public void testUpdateGroup()
	{
		UpdateGroupRequest request = new UpdateGroupRequest();

		try
		{
			JDOMResult responseResult = new JDOMResult();
			webServiceTemplate.sendSourceAndReceiveToResult(endPointUrl,
					new MarshallingSource(marshaller, request), responseResult);

			assertTrue(responseResult.getDocument() != null);
			Object response = getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof UpdateGroupResponse);
		}
		catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) {
			fail(ioe.getMessage());
		}
	}

}
