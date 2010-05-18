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
package net.unicon.sakora.api.ims;

import com.oracle.xmlns.enterprise.hcm.services.gms.AddGroupRelationshipRequest;
import com.oracle.xmlns.enterprise.hcm.services.gms.AddGroupRelationshipResponse;
import com.oracle.xmlns.enterprise.hcm.services.gms.CreateGroupRequest;
import com.oracle.xmlns.enterprise.hcm.services.gms.CreateGroupResponse;
import com.oracle.xmlns.enterprise.hcm.services.gms.DeleteGroupRequest;
import com.oracle.xmlns.enterprise.hcm.services.gms.DeleteGroupResponse;
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

public interface ImsGroupManagement
{
	// Group messages
	
	CreateGroupResponse createGroup( CreateGroupRequest request);
	
	UpdateGroupResponse updateGroup( UpdateGroupRequest request);
	
	DeleteGroupResponse deleteGroupe( DeleteGroupRequest request);
	
	ReplaceGroupResponse replaceGroup( ReplaceGroupRequest request);
	
	// Group Relationship messages
	
	AddGroupRelationshipResponse addGroupRelationShip( AddGroupRelationshipRequest request);
	
	RemoveGroupRelationshipResponse removeGroupRelationShip( RemoveGroupRelationshipRequest request);
	
	// read group batch information
	ReadGroupsResponse readGroups(ReadGroupsRequest request);
	
	ReadGroupsFromSavePointResponse readGroupsFromSavePoint(ReadGroupsFromSavePointRequest request);
	
}
