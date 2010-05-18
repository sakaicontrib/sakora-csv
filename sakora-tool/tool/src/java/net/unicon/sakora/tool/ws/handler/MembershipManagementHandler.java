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

public interface MembershipManagementHandler
{

	Source changeMembershipIdentifier(Source requestSource);

	Source createByProxyMembership(Source requestSource);

	Source createMembership(Source requestSource);

	Source deleteMembership(Source requestSource);

	Source discoverMembershipIds(Source requestSource);

	Source readAllMembershipIds(Source requestSource);

	Source readMembershipIdsForCollection(Source requestSource);

	Source membershipManagementHandler(Source requestSource);

	Source readMembershipIdsFromSavePoint(Source requestSource);

	Source readMembershipIdsForPerson(Source requestSource);

	Source readMemberships(Source requestSource);

	Source readMembershipsFromSavePoint(Source requestSource);

	Source readMembership(Source requestSource);

	Source replaceMembership(Source requestSource);

	Source updateMembership(Source requestSource);

}
