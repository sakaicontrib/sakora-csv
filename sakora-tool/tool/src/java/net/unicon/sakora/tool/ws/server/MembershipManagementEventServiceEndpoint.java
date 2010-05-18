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
package net.unicon.sakora.tool.ws.server;

import javax.xml.transform.Source;

import net.unicon.sakora.tool.ws.handler.MembershipManagementHandler;
import net.unicon.sakora.tool.ws.handler.PersonManagementHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.soap.SoapFaultException;

@Endpoint
public class MembershipManagementEventServiceEndpoint extends ImsEventServiceEndpoint
{
	private static final Log log = LogFactory
			.getLog(MembershipManagementEventServiceEndpoint.class);

	private static final String NAMESPACE_STRING = "http://www.imsglobal.org/services/es/mms2p0/xsd/imsmms_v2p0";

	private MembershipManagementHandler membershipManagementHandler;

	public void setMembershipManagementHandler(MembershipManagementHandler membershipManagementHandler)
	{
		this.membershipManagementHandler = membershipManagementHandler;
	}

  /*  Change Membership Identifier  */
  @PayloadRoot(namespace=NAMESPACE_STRING, localPart="changeMembershipIdentifierRequest")
  public Source changeMembershipIdentifier(Source requestSource) {		
    	try {
    		return membershipManagementHandler.changeMembershipIdentifier(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException(ex);
    	}		
    }

  /*  Create By Proxy Membership  */
  @PayloadRoot(namespace=NAMESPACE_STRING, localPart="createByProxyMembershipRequest")
  public Source createByProxyMembership(Source requestSource) {		
    	try {
    		return membershipManagementHandler.createByProxyMembership(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException(ex);
    	}		
    }

  /*  Create Membership  */
  @PayloadRoot(namespace=NAMESPACE_STRING, localPart="createMembershipRequest")
  public Source createMembership(Source requestSource) {		
    	try {
    		return membershipManagementHandler.createMembership(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException(ex);
    	}		
    }

  /*  Delete Membership  */
  @PayloadRoot(namespace=NAMESPACE_STRING, localPart="deleteMembershipRequest")
  public Source deleteMembership(Source requestSource) {		
    	try {
    		return membershipManagementHandler.deleteMembership(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException(ex);
    	}		
    }

  /*  Discover Membership Ids  */
  @PayloadRoot(namespace=NAMESPACE_STRING, localPart="discoverMembershipIdsRequest")
  public Source discoverMembershipIds(Source requestSource) {		
    	try {
    		return membershipManagementHandler.discoverMembershipIds(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException(ex);
    	}		
    }

  /*  Read All Membership Ids  */
  @PayloadRoot(namespace=NAMESPACE_STRING, localPart="readAllMembershipIdsRequest")
  public Source readAllMembershipIds(Source requestSource) {		
    	try {
    		return membershipManagementHandler.readAllMembershipIds(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException(ex);
    	}		
    }

  /*  Read Membership Ids Collection  */
  @PayloadRoot(namespace=NAMESPACE_STRING, localPart="readMembershipIdsForCollectionRequest")
  public Source readMembershipIdsForCollection(Source requestSource) {		
    	try {
    		return membershipManagementHandler.readMembershipIdsForCollection(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException(ex);
    	}		
    }

  /*  Read Membership Ids For roles  */
  @PayloadRoot(namespace=NAMESPACE_STRING, localPart="readMembershipIdsForPersonWithRoleRequest")
  public Source readMembershipIdsForPersonWithRole(Source requestSource) {		
    	try {
    		return membershipManagementHandler.membershipManagementHandler(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException(ex);
    	}		
    }

  /*  Read Membership Ids from save  */
  @PayloadRoot(namespace=NAMESPACE_STRING, localPart="readMembershipIdsFromSavePointRequest")
  public Source readMembershipIdsFromSavePoint(Source requestSource) {		
    	try {
    		return membershipManagementHandler.readMembershipIdsFromSavePoint(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException(ex);
    	}		
    }

    /*  Read Membership Ids For Person  */
  @PayloadRoot(namespace=NAMESPACE_STRING, localPart="readMembershipIdsForPersonRequest")
  public Source readMembershipIdsForPerson(Source requestSource) {		
    	try {
    		return membershipManagementHandler.readMembershipIdsForPerson(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException(ex);
    	}		
    }

    /*  Membership Snapshot  */
  @PayloadRoot(namespace=NAMESPACE_STRING, localPart="readMembershipsRequest")
  public Source readMemberships(Source requestSource) {		
    	try {
    		return membershipManagementHandler.readMemberships(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException(ex);
    	}		
    }

    /*  Memberships from save point  */
  @PayloadRoot(namespace=NAMESPACE_STRING, localPart="readMembershipsFromSavePointRequest")
  public Source readMembershipsFromSavePoint(Source requestSource) {		
    	try {
    		return membershipManagementHandler.readMembershipsFromSavePoint(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException(ex);
    	}		
    }

    /*  Read Membership  */
  @PayloadRoot(namespace=NAMESPACE_STRING, localPart="readMembershipRequest")
  public Source readMembership(Source requestSource) {		
    	try {
    		return membershipManagementHandler.readMembership(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException(ex);
    	}		
    }

    /*  Replace Membership  */
  @PayloadRoot(namespace=NAMESPACE_STRING, localPart="replaceMembershipRequest")
  public Source replaceMembership(Source requestSource) {		
    	try {
    		return membershipManagementHandler.replaceMembership(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException(ex);
    	}		
    }

    /*  Update Membership  */
  @PayloadRoot(namespace=NAMESPACE_STRING, localPart="updateMembershipRequest")
  public Source updateMembership(Source requestSource) {		
    	try {
    		return membershipManagementHandler.updateMembership(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException(ex);
    	}		
    }

}
