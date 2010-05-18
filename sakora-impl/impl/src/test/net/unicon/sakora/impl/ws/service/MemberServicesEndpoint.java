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
package net.unicon.sakora.impl.ws.service;

import javax.xml.transform.Source;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

@Endpoint
public class MemberServicesEndpoint extends ResponseBuilderEndpoint {
	private static final String NAMESPACE_STRING = "http://xmlns.oracle.com/Enterprise/HCM/services/SAE_MEMBERSHIP_SERVICE";
//	private static final Namespace NAME_SPACE = Namespace.getNamespace(NAMESPACE_STRING);
	
	@PayloadRoot( namespace = NAMESPACE_STRING, localPart = "readMembershipsRequest")
	public Source readGroups(Source request) {
		return getResponseBuilder().buildSnapshotResponse("readMembershipsResponse", "READMEMBERSHIPS.xml");
	}

	@PayloadRoot( namespace = NAMESPACE_STRING, localPart = "readMembershipsFromSavePointRequest")
	public Source readGroupsFromSavePoint(Source request) {
		return getResponseBuilder().buildIncrementalResponse("readMembershipsFromSavePointResponse", "READMEMBERSHIPS.xml", "2008-01-01-1");
	}
}
