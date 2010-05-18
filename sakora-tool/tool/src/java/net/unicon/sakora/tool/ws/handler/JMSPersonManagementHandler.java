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

import org.springframework.oxm.support.MarshallingSource;

import com.oracle.xmlns.enterprise.hcm.services.pms.CreatePersonResponse;
import com.oracle.xmlns.enterprise.hcm.services.pms.ReplacePersonResponse;

public class JMSPersonManagementHandler extends JMSManagementHandler 
	implements PersonManagementHandler
{
	private static final String PERSON_ELEMENT = "person";

	public Source changePersonIdentifier(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source createByProxyPerson(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source createPerson(Source requestSource)
	{
		parseSourceAndProcess(requestSource, PERSON_ELEMENT);

		CreatePersonResponse response = new CreatePersonResponse();
		return new MarshallingSource(getMarshaller(), response);
	}

	public Source deletePerson(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source discoverPersonIds(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readAllPersonIds(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readPerson(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readPersonCore(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readPersonIdsFromSavePoint(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readPersons(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readPersonsFromSavePoint(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source replacePerson(Source requestSource)
	{
		parseSourceAndProcess(requestSource, PERSON_ELEMENT);

		ReplacePersonResponse response = new ReplacePersonResponse();
		return new MarshallingSource(getMarshaller(), response);
	}

	public Source updatePerson(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

}
