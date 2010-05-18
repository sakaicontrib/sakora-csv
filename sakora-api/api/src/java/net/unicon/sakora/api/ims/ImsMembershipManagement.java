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

import com.oracle.xmlns.enterprise.hcm.services.mms.CreateMembershipRequest;
import com.oracle.xmlns.enterprise.hcm.services.mms.CreateMembershipResponse;
import com.oracle.xmlns.enterprise.hcm.services.mms.DeleteMembershipRequest;
import com.oracle.xmlns.enterprise.hcm.services.mms.DeleteMembershipResponse;
import com.oracle.xmlns.enterprise.hcm.services.mms.ReadMembershipsFromSavePointRequest;
import com.oracle.xmlns.enterprise.hcm.services.mms.ReadMembershipsFromSavePointResponse;
import com.oracle.xmlns.enterprise.hcm.services.mms.ReadMembershipsRequest;
import com.oracle.xmlns.enterprise.hcm.services.mms.ReadMembershipsResponse;
import com.oracle.xmlns.enterprise.hcm.services.mms.UpdateMembershipRequest;
import com.oracle.xmlns.enterprise.hcm.services.mms.UpdateMembershipResponse;

public interface ImsMembershipManagement
{
	// Membership event messages
	
	CreateMembershipResponse createMembership(CreateMembershipRequest request);
	
	DeleteMembershipResponse deleteMembership(DeleteMembershipRequest request);
	
//	ReplaceM replaceMembership(ReplaceM request);
	
	UpdateMembershipResponse updateMembership(UpdateMembershipRequest request);
	
	// Memebership batch messages
	
	ReadMembershipsResponse readMemberships(ReadMembershipsRequest request);
	
	ReadMembershipsFromSavePointResponse readMembershipsFromSavePoint(ReadMembershipsFromSavePointRequest request);
}
