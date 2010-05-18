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

import junit.framework.TestCase;

import org.jdom.transform.JDOMResult;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.oxm.support.MarshallingSource;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.oracle.xmlns.enterprise.hcm.services.pms.CreatePersonRequest;
import com.oracle.xmlns.enterprise.hcm.services.pms.DeletePersonRequest;
import com.oracle.xmlns.enterprise.hcm.services.pms.ReadPersonsFromSavePointRequest;
import com.oracle.xmlns.enterprise.hcm.services.pms.ReadPersonsRequest;
import com.oracle.xmlns.enterprise.hcm.services.pms.ReplacePersonRequest;

public class PersonEndpointTest extends TestCase
{
	private WebServiceTemplate webServiceTemplate;

	private String endPointUrl;

	private Jaxb2Marshaller marshaller;

	protected void setUp() throws Exception
	{
		super.setUp();
		webServiceTemplate = new WebServiceTemplate();
		endPointUrl = "http://localhost:8888/sakai-sakora-tool/services/";
		marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("com.oracle.xmlns.enterprise.hcm.services.pms");
		marshaller.afterPropertiesSet();
	}

//	public void testChangePersonIdentifier()
//	{
//		ChangePersonIdentifierRequest request = new ChangePersonIdentifierRequest();
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

//	public void testCreateByProxyPerson()
//	{
//		CreateByProxyPersonRequest request = new CreateByProxyPersonRequest();
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
//
//	}

	public void testCreatePerson()
	{
		CreatePersonRequest request = new CreatePersonRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(endPointUrl,
					new MarshallingSource(marshaller, request), responseResult);
			assertTrue(responseResult.getDocument() != null);
		} catch (SoapFaultClientException se) {
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		}

	}

	public void testDeletePerson()
	{
		DeletePersonRequest request = new DeletePersonRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(endPointUrl,
					new MarshallingSource(marshaller, request), responseResult);
			assertTrue(responseResult.getDocument() != null);
		}
		catch (SoapFaultClientException se)
		{
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		}

	}

//	public void testDiscoverPersonIds()
//	{
//		DiscoverPersonIdsRequest request = new DiscoverPersonIdsRequest();
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
//
//	}

//	public void testReadAllPersonIds()
//	{
//		ReadAllPersonIdsRequest request = new ReadAllPersonIdsRequest();
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
//
//	}

//	public void testReadPersonCore()
//	{
//		ReadPersonCoreRequest request = new ReadPersonCoreRequest();
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
//
//	}

//	public void testReadPersonIdsFromSavePoint()
//	{
//		ReadPersonIdsFromSavePointRequest request = new ReadPersonIdsFromSavePointRequest();
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
//
//	}

	public void testReadPersons()
	{
		ReadPersonsRequest request = new ReadPersonsRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(endPointUrl,
					new MarshallingSource(marshaller, request), responseResult);
			assertTrue(responseResult.getDocument() != null);
		}
		catch (SoapFaultClientException se)
		{
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		}

	}

	public void testReadPersonsFromSavePoint()
	{
		ReadPersonsFromSavePointRequest request = new ReadPersonsFromSavePointRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(endPointUrl,
					new MarshallingSource(marshaller, request), responseResult);
			assertTrue(responseResult.getDocument() != null);
		}
		catch (SoapFaultClientException se)
		{
			if (se.getMessage().indexOf("Not yet implemented") == -1)
			fail(se.getFaultStringOrReason());
		}

	}

//	public void testReadPerson()
//	{
//		ReadPersonRequest request = new ReadPersonRequest();
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
//
//	}

	public void testReplacePerson()
	{
		ReplacePersonRequest request = new ReplacePersonRequest();

		JDOMResult responseResult = new JDOMResult();
		try
		{
			webServiceTemplate.sendSourceAndReceiveToResult(endPointUrl,
					new MarshallingSource(marshaller, request), responseResult);
			assertTrue(responseResult.getDocument() != null);
		}
		catch (SoapFaultClientException se)
		{
			if (se.getMessage().indexOf("Not yet implemented") == -1)
				fail(se.getFaultStringOrReason());
		}

	}

//	public void testUpdatePerson()
//	{
//		UpdatePersonRequest request = new UpdatePersonRequest();
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
//
//	}

}
