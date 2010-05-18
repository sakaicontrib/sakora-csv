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

import com.oracle.xmlns.enterprise.hcm.services.pms.CreatePersonRequest;
import com.oracle.xmlns.enterprise.hcm.services.pms.CreatePersonResponse;
import com.oracle.xmlns.enterprise.hcm.services.pms.DeletePersonRequest;
import com.oracle.xmlns.enterprise.hcm.services.pms.DeletePersonResponse;
import com.oracle.xmlns.enterprise.hcm.services.pms.ReadPersonsFromSavePointRequest;
import com.oracle.xmlns.enterprise.hcm.services.pms.ReadPersonsFromSavePointResponse;
import com.oracle.xmlns.enterprise.hcm.services.pms.ReadPersonsRequest;
import com.oracle.xmlns.enterprise.hcm.services.pms.ReadPersonsResponse;
import com.oracle.xmlns.enterprise.hcm.services.pms.ReplacePersonRequest;
import com.oracle.xmlns.enterprise.hcm.services.pms.ReplacePersonResponse;

public interface ImsPersonManagement
{	
	// Person event messages
	
	CreatePersonResponse createPerson(CreatePersonRequest request);
	
//	UpdatePersonResponse updatePerson(UpdatePersonRequest request);
	
	ReplacePersonResponse replacePerson(ReplacePersonRequest request);
	
	DeletePersonResponse deletePerson(DeletePersonRequest request);

	// read person batch information
	
	ReadPersonsResponse readPersons(ReadPersonsRequest request);
	
	ReadPersonsFromSavePointResponse readPersonsFromSavePoint(ReadPersonsFromSavePointRequest request);
	
}
