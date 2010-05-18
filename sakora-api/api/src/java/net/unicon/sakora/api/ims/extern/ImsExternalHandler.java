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
package net.unicon.sakora.api.ims.extern;

/**
 * @author bsawert
 *
 */
public interface ImsExternalHandler {
	
	/**
	 * Retrieve a Person eid from an external system, using the IMS id.
	 * 
	 * @param imsId		IMS id from Person XML
	 * 
	 * @return			Person enterprise id
	 */
	public String getPersonEidFromImsId(String imsId);
	
	/**
	 * Call external system to delete Person
	 * 
	 * @param eid		Person enterprise id
	 */
	public void externalDeletePerson(String eid);
	
	/**
	 * Check if local delete Person is required
	 * 
	 * @param eid		Person enterprise id
	 */
	public boolean mustDeleteLocalPerson(String eid);

	/**
	 * Check membership is marked inactive or truly deleted.
	 * 
	 * @param eid		Person enterprise id
	 */
	public boolean makeMemberInactiveToDelete(String eid);

}
