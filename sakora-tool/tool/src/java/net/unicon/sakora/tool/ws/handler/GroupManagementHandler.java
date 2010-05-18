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

public interface GroupManagementHandler
{

	Source addGroupRelationship(Source requestSource);

	Source changeGroupIdentifier(Source requestSource);

	Source createByProxyGroup(Source requestSource);

	Source createGroup(Source requestSource);

	Source deleteGroup(Source requestSource);

	Source discoverGroupIds(Source requestSource);

	Source readAllGroupIds(Source requestSource);

	Source readGroupIdsForPerson(Source requestSource);

	Source readGroupIdsFromSavePoint(Source requestSource);

	Source readGroupsFromSavePoint(Source requestSource);

	Source readGroups(Source requestSource);

	Source readGroup(Source requestSource);

	Source removeGroupRelationship(Source requestSource);

	Source replaceGroup(Source requestSource);

	Source updateGroup(Source requestSource);

}
