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

import com.oracle.xmlns.enterprise.hcm.services.gms.CreateGroupResponse;
import com.oracle.xmlns.enterprise.hcm.services.gms.ReplaceGroupResponse;
import com.oracle.xmlns.enterprise.hcm.services.gms.UpdateGroupResponse;

public class JMSGroupManagementHandler extends JMSManagementHandler implements
		GroupManagementHandler
{
	private static final String GROUP_ELEMENT = "group";

	public Source addGroupRelationship(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source changeGroupIdentifier(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source createByProxyGroup(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source createGroup(Source requestSource)
	{
		parseSourceAndProcess(requestSource, GROUP_ELEMENT);

		CreateGroupResponse response = new CreateGroupResponse();
		return new MarshallingSource(getMarshaller(), response);
	}

	public Source deleteGroup(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source discoverGroupIds(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readAllGroupIds(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readGroup(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readGroupIdsForPerson(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readGroupIdsFromSavePoint(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readGroups(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source readGroupsFromSavePoint(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source removeGroupRelationship(Source requestSource)
	{
		// TODO Auto-generated method stub
		throw new IllegalStateException("Not yet implemented");
	}

	public Source replaceGroup(Source requestSource)
	{
		parseSourceAndProcess(requestSource, GROUP_ELEMENT);

		ReplaceGroupResponse response = new ReplaceGroupResponse();
		return new MarshallingSource(getMarshaller(), response);
	}

	public Source updateGroup(Source requestSource)
	{
		parseSourceAndProcess(requestSource, GROUP_ELEMENT);

		UpdateGroupResponse response = new UpdateGroupResponse();
		return new MarshallingSource(getMarshaller(), response);
	}

}
