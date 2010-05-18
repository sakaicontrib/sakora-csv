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

import com.oracle.xmlns.enterprise.hcm.services.mms.CreateMembershipResponse;
import com.oracle.xmlns.enterprise.hcm.services.mms.DeleteMembershipResponse;

public class JMSMembershipManagementHandler extends JMSManagementHandler implements
		MembershipManagementHandler
{
	private static final String MEMBERSHIP_ELEMENT = "membership";
	
	public Source changeMembershipIdentifier(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source createByProxyMembership(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source createMembership(Source requestSource)
	{
		parseSourceAndProcess(requestSource, MEMBERSHIP_ELEMENT);

		CreateMembershipResponse response = new CreateMembershipResponse();
		return new MarshallingSource(getMarshaller(), response);
	}

	public Source deleteMembership(Source requestSource)
	{
		parseSourceAndProcess(requestSource, MEMBERSHIP_ELEMENT);

		DeleteMembershipResponse response = new DeleteMembershipResponse();
		return new MarshallingSource(getMarshaller(), response);
	}

	public Source discoverMembershipIds(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source membershipManagementHandler(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readAllMembershipIds(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readMembership(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readMembershipIdsForCollection(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readMembershipIdsForPerson(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readMembershipIdsFromSavePoint(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readMemberships(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readMembershipsFromSavePoint(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source replaceMembership(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source updateMembership(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

}
