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

import net.unicon.sakora.tool.ws.handler.GroupManagementHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.soap.SoapFaultException;

@Endpoint
public class GroupManagementEventServiceEndpoint extends ImsEventServiceEndpoint
{
	private static final Log log = LogFactory.getLog(GroupManagementEventServiceEndpoint.class);

	private static final String NAMESPACE_STRING = "http://www.imsglobal.org/services/es/gms2p0/xsd/imsgms_v2p0";

	private GroupManagementHandler groupManagementHandler;
	public void setGroupManagementHandler(GroupManagementHandler groupManagementHandler) {
		this.groupManagementHandler = groupManagementHandler;
	}
		
    /*  addGroupRelationship  */
    @PayloadRoot(namespace = NAMESPACE_STRING, localPart = "addGroupRelationshipRequest")
    public Source addGroupRelationship(Source requestSource) {		
    	try {
    		if (log.isDebugEnabled()) {
    			logRequest(requestSource);
    		}
    		return groupManagementHandler.addGroupRelationship(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException("Exception in addGroupRelationshipRequest", ex);
    	}		
    }

    /*  changeGroupIdentifier  */
    @PayloadRoot(namespace = NAMESPACE_STRING, localPart = "changeGroupIdentifierRequest")
    public Source changeGroupIdentifier(Source requestSource) {		
    	try {
    		if (log.isDebugEnabled()) {
    			logRequest(requestSource);
    		}
    		return groupManagementHandler.changeGroupIdentifier(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException("Exception in changeGroupIdentifierRequest", ex);
    	}		
    }

    /*  createByProxyGroup  */
    @PayloadRoot(namespace = NAMESPACE_STRING, localPart = "createByProxyGroupRequest")
    public Source createByProxyGroup(Source requestSource) {		
    	try {
    		if (log.isDebugEnabled()) {
    			logRequest(requestSource);
    		}
    		return groupManagementHandler.createByProxyGroup(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException("Exception in createByProxyGroupRequest", ex);
    	}		
    }

    /*  createGroup  */
    @PayloadRoot(namespace = NAMESPACE_STRING, localPart = "createGroupRequest")
    public Source createGroup(Source requestSource) {		
    	try {
    		if (log.isDebugEnabled()) {
    			logRequest(requestSource);
    		}
    		return groupManagementHandler.createGroup(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException("Exception in createGroupRequest", ex);
    	}		
    }

    /*  deleteGroup  */
    @PayloadRoot(namespace = NAMESPACE_STRING, localPart = "deleteGroupRequest")
    public Source deleteGroup(Source requestSource) {		
    	try {
    		if (log.isDebugEnabled()) {
    			logRequest(requestSource);
    		}
   		return groupManagementHandler.deleteGroup(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException("Exception in deleteGroupRequest", ex);
    	}		
    }

    /*  discoverGroupIds  */
    @PayloadRoot(namespace = NAMESPACE_STRING, localPart = "discoverGroupIdsRequest")
    public Source discoverGroupIds(Source requestSource) {		
    	try {
    		if (log.isDebugEnabled()) {
    			logRequest(requestSource);
    		}
   		return groupManagementHandler.discoverGroupIds(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException("Exception in discoverGroupIdsRequest", ex);
    	}		
    }

    /*  readAllGroupIds  */
    @PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readAllGroupIdsRequest")
    public Source readAllGroupIds(Source requestSource) {		
    	try {
    		if (log.isDebugEnabled()) {
    			logRequest(requestSource);
    		}
   		return groupManagementHandler.readAllGroupIds(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException("Exception in readAllGroupIdsRequest", ex);
    	}		
    }

    /*  readGroupIdsForPerson  */
    @PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readGroupIdsForPersonRequest")
    public Source readGroupIdsForPerson(Source requestSource) {		
    	try {
    		if (log.isDebugEnabled()) {
    			logRequest(requestSource);
    		}
    		return groupManagementHandler.readGroupIdsForPerson(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException("Exception in readGroupIdsForPersonRequest", ex);
    	}		
    }

    /*  readGroupIdsFromSavePoint  */
    @PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readGroupIdsFromSavePointRequest")
    public Source readGroupIdsFromSavePoint(Source requestSource) {		
    	try {
    		if (log.isDebugEnabled()) {
    			logRequest(requestSource);
    		}
    		return groupManagementHandler.readGroupIdsFromSavePoint(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException("Exception in readGroupIdsFromSavePointRequest", ex);
    	}		
    }

    /*  READGROUPSFROMSAVEPOINT  */
    @PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readGroupsFromSavePointRequest")
    public Source readGroupsFromSavePoint(Source requestSource) {		
    	try {
    		if (log.isDebugEnabled()) {
    			logRequest(requestSource);
    		}
   		return groupManagementHandler.readGroupsFromSavePoint(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException("Exception in readGroupsFromSavePointRequest", ex);
    	}		
    }

    /*  Group Snapshot SrvOperation  */
    @PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readGroupsRequest")
    public Source readGroups(Source requestSource) {		
    	try {
    		return groupManagementHandler.readGroups(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException("Exception in readGroupsRequest", ex);
    	}		
    }

    /*  readGroup  */
    @PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readGroupRequest")
    public Source readGroup(Source requestSource) {		
    	try {
    		if (log.isDebugEnabled()) {
    			logRequest(requestSource);
    		}
    		return groupManagementHandler.readGroup(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException("Exception in readGroupRequest", ex);
    	}		
    }

    /*  removeGroupRelationship  */
    @PayloadRoot(namespace = NAMESPACE_STRING, localPart = "removeGroupRelationshipRequest")
    public Source removeGroupRelationship(Source requestSource) {		
    	try {
    		if (log.isDebugEnabled()) {
    			logRequest(requestSource);
    		}
    		return groupManagementHandler.removeGroupRelationship(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException("Exception in removeGroupRelationshipRequest", ex);
    	}		
    }

    /*  replaceGroup  */
    @PayloadRoot(namespace = NAMESPACE_STRING, localPart = "replaceGroupRequest")
    public Source replaceGroup(Source requestSource) {		
    	try {
    		if (log.isDebugEnabled()) {
    			logRequest(requestSource);
    		}
    		return groupManagementHandler.replaceGroup(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException("Exception in replaceGroupRequest", ex);
    	}		
    }

    /*  updateGroup  */
    @PayloadRoot(namespace = NAMESPACE_STRING, localPart = "updateGroupRequest")
    public Source updateGroup(Source requestSource) {		
    	try {
    		if (log.isDebugEnabled()) {
    			logRequest(requestSource);
    		}
    		return groupManagementHandler.updateGroup(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException("Exception in updateGroupRequest", ex);
    	}		
    }

}
