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

import com.oracle.xmlns.enterprise.hcm.services.mms.CreateMembershipRequest;
import com.oracle.xmlns.enterprise.hcm.services.mms.CreateMembershipResponse;
import com.oracle.xmlns.enterprise.hcm.services.mms.DeleteMembershipRequest;
import com.oracle.xmlns.enterprise.hcm.services.mms.DeleteMembershipResponse;
import com.oracle.xmlns.enterprise.hcm.services.mms.ReadMembershipRequest;
import com.oracle.xmlns.enterprise.hcm.services.mms.ReadMembershipResponse;
import com.oracle.xmlns.enterprise.hcm.services.mms.ReadMembershipsFromSavePointRequest;
import com.oracle.xmlns.enterprise.hcm.services.mms.ReadMembershipsFromSavePointResponse;
import com.oracle.xmlns.enterprise.hcm.services.mms.ReadMembershipsRequest;
import com.oracle.xmlns.enterprise.hcm.services.mms.ReadMembershipsResponse;
import com.oracle.xmlns.enterprise.hcm.services.mms.UpdateMembershipRequest;
import com.oracle.xmlns.enterprise.hcm.services.mms.UpdateMembershipResponse;

public class MembershipEndpointTest extends TestCase
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
		marshaller.setContextPath("com.oracle.xmlns.enterprise.hcm.services.mms");
		marshaller.afterPropertiesSet();
	}


//	public void testChangeMembershipIdentifier()
//	{
//		ChangeMembershipIdentifierRequest request = new ChangeMembershipIdentifierRequest();
//
//		JDOMResult responseResult = new JDOMResult();
//		try
//		{
//			webServiceTemplate.sendSourceAndReceiveToResult(endPointUrl,
//					new MarshallingSource(marshaller, request), responseResult);
//			assertTrue(responseResult.getDocument() != null);
//		}
//		catch (SoapFaultClientException se)
//		{
//			fail(se.getFaultStringOrReason());
//		}
//	}

//	public void testCreateByProxyMembership()
//	{
//		CreateByProxyMembershipRequest request = new CreateByProxyMembershipRequest();
//
//		JDOMResult responseResult = new JDOMResult();
//		try
//		{
//			webServiceTemplate.sendSourceAndReceiveToResult(endPointUrl,
//					new MarshallingSource(marshaller, request), responseResult);
//			assertTrue(responseResult.getDocument() != null);
//		}
//		catch (SoapFaultClientException se)
//		{
//			fail(se.getFaultStringOrReason());
//		}
//	}

	public void testCreateMembership()
	{
		CreateMembershipRequest request = new CreateMembershipRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(endPointUrl,
					new MarshallingSource(marshaller, request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof CreateMembershipResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) {
			fail(ioe.getMessage());
		}
	}

	public void testDeleteMembership()
	{
		DeleteMembershipRequest request = new DeleteMembershipRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(endPointUrl,
					new MarshallingSource(marshaller, request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof DeleteMembershipResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) {
			fail(ioe.getMessage());
		}
	}

//	public void testDiscoverMembershipIds()
//	{
//		DiscoverMembershipIdsRequest request = new DiscoverMembershipIdsRequest();
//
//		JDOMResult responseResult = new JDOMResult();
//		try
//		{
//			webServiceTemplate.sendSourceAndReceiveToResult(endPointUrl,
//					new MarshallingSource(marshaller, request), responseResult);
//			assertTrue(responseResult.getDocument() != null);
//		}
//		catch (SoapFaultClientException se)
//		{
//			fail(se.getFaultStringOrReason());
//		}
//	}

//	public void testReadAllMembershipIds()
//	{
//		ReadAllMembershipIdsRequest request = new ReadAllMembershipIdsRequest();
//
//		JDOMResult responseResult = new JDOMResult();
//		try
//		{
//			webServiceTemplate.sendSourceAndReceiveToResult(endPointUrl,
//					new MarshallingSource(marshaller, request), responseResult);
//			assertTrue(responseResult.getDocument() != null);
//		}
//		catch (SoapFaultClientException se)
//		{
//			fail(se.getFaultStringOrReason());
//		}
//	}

//	public void testReadMembershipIdsForCollection()
//	{
//		ReadMembershipIdsForCollectionRequest request = new ReadMembershipIdsForCollectionRequest();
//
//		JDOMResult responseResult = new JDOMResult();
//		try
//		{
//			webServiceTemplate.sendSourceAndReceiveToResult(endPointUrl,
//					new MarshallingSource(marshaller, request), responseResult);
//			assertTrue(responseResult.getDocument() != null);
//		}
//		catch (SoapFaultClientException se)
//		{
//			fail(se.getFaultStringOrReason());
//		}
//	}

//	public void testReadMembershipIdsForPersonWithRole()
//	{
//		ReadMembershipIdsForPersonWithRoleRequest request = new ReadMembershipIdsForPersonWithRoleRequest();
//
//		JDOMResult responseResult = new JDOMResult();
//		try
//		{
//			webServiceTemplate.sendSourceAndReceiveToResult(endPointUrl,
//					new MarshallingSource(marshaller, request), responseResult);
//			assertTrue(responseResult.getDocument() != null);
//		}
//		catch (SoapFaultClientException se)
//		{
//			fail(se.getFaultStringOrReason());
//		}
//	}

//	public void testReadMembershipIdsFromSavePoint()
//	{
//		ReadMembershipIdsFromSavePointRequest request = new ReadMembershipIdsFromSavePointRequest();
//
//		JDOMResult responseResult = new JDOMResult();
//		try
//		{
//			webServiceTemplate.sendSourceAndReceiveToResult(endPointUrl,
//					new MarshallingSource(marshaller, request), responseResult);
//			assertTrue(responseResult.getDocument() != null);
//		}
//		catch (SoapFaultClientException se)
//		{
//			fail(se.getFaultStringOrReason());
//		}
//	}

//	public void testReadMembershipIdsForPerson()
//	{
//		ReadMembershipIdsForPersonRequest request = new ReadMembershipIdsForPersonRequest();
//
//		JDOMResult responseResult = new JDOMResult();
//		try
//		{
//			webServiceTemplate.sendSourceAndReceiveToResult(endPointUrl,
//					new MarshallingSource(marshaller, request), responseResult);
//			assertTrue(responseResult.getDocument() != null);
//		}
//		catch (SoapFaultClientException se)
//		{
//			fail(se.getFaultStringOrReason());
//		}
//	}

	public void testReadMemberships()
	{
		ReadMembershipsRequest request = new ReadMembershipsRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(endPointUrl,
					new MarshallingSource(marshaller, request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ReadMembershipsResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) {
			fail(ioe.getMessage());
		}
	}

	public void testReadMembershipsFromSavePoint()
	{
		ReadMembershipsFromSavePointRequest request = new ReadMembershipsFromSavePointRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(endPointUrl,
					new MarshallingSource(marshaller, request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ReadMembershipsFromSavePointResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) {
			fail(ioe.getMessage());
		}
	}

	public void testReadMembership()
	{
		ReadMembershipRequest request = new ReadMembershipRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(endPointUrl,
					new MarshallingSource(marshaller, request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof ReadMembershipResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) {
			fail(ioe.getMessage());
		}
	}

//	public void testReplaceMembership()
//	{
//		ReplaceMembershipRequest request = new ReplaceMembershipRequest();
//
//		JDOMResult responseResult = new JDOMResult();
//		try
//		{
//			webServiceTemplate.sendSourceAndReceiveToResult(endPointUrl,
//					new MarshallingSource(marshaller, request), responseResult);
//			assertTrue(responseResult.getDocument() != null);
//		}
//		catch (SoapFaultClientException se)
//		{
//			fail(se.getFaultStringOrReason());
//		}
//	}

	public void testUpdateMembership()
	{
		UpdateMembershipRequest request = new UpdateMembershipRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(endPointUrl,
					new MarshallingSource(marshaller, request), responseResult);
			assertTrue(responseResult.getDocument() != null);
			Object response = getUnmarshaller().unmarshal(new JDOMSource(responseResult.getDocument()));
			assertTrue("Wrong response returned", response instanceof UpdateMembershipResponse);
			
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		} catch (IOException ioe) {
			fail(ioe.getMessage());
		}
	}

}
