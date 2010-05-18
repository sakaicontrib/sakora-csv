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

import net.unicon.sakora.tool.ws.handler.PersonManagementHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.soap.SoapFaultException;

@Endpoint
public class PersonManagementEventServiceEndpoint extends ImsEventServiceEndpoint
{
	private static final Log log = LogFactory
			.getLog(PersonManagementEventServiceEndpoint.class);

	private static final String NAMESPACE_STRING = "http://www.imsglobal.org/services/es/pms2p0/xsd/imspms_v2p0";

	private PersonManagementHandler personManagementHandler;

	public void setPersonManagementHandler(PersonManagementHandler personManagementHandler)
	{
		this.personManagementHandler = personManagementHandler;
	}

    /* Change Person Identifier */
    @PayloadRoot(namespace = NAMESPACE_STRING, localPart = "changePersonIdentifierRequest")
    public Source changePersonIdentifier(Source requestSource) {		
    	try {
    		return personManagementHandler.changePersonIdentifier(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException(ex);
    	}		
    }

    /* Create by proxy person */
    @PayloadRoot(namespace = NAMESPACE_STRING, localPart = "createByProxyPersonRequest")
    public Source createByProxyPerson(Source requestSource) {		
    	try {
    		return personManagementHandler.createByProxyPerson(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException(ex);
    	}		
    }

    /* Create Person */
    @PayloadRoot(namespace = NAMESPACE_STRING, localPart = "createPersonRequest")
    public Source createPerson(Source requestSource) {		
    	try {
    		return personManagementHandler.createPerson(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException(ex);
    	}		
    }

    /* Delete person */
    @PayloadRoot(namespace = NAMESPACE_STRING, localPart = "deletePersonRequest")
    public Source deletePerson(Source requestSource) {		
    	try {
    		return personManagementHandler.deletePerson(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException(ex);
    	}		
    }

    /* Discover Person Ids */
    @PayloadRoot(namespace = NAMESPACE_STRING, localPart = "discoverPersonIdsRequest")
    public Source discoverPersonIds(Source requestSource) {		
    	try {
    		return personManagementHandler.discoverPersonIds(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException(ex);
    	}		
    }

    /* Read All Person Ids */
    @PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readAllPersonIdsRequest")
    public Source readAllPersonIds(Source requestSource) {		
    	try {
    		return personManagementHandler.readAllPersonIds(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException(ex);
    	}		
    }

    /* Read Person Core */
    @PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readPersonCoreRequest")
    public Source readPersonCore(Source requestSource) {		
    	try {
    		return personManagementHandler.readPersonCore(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException(ex);
    	}		
    }

    /* Read Person Ids From SavePoint */
    @PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readPersonIdsFromSavePointRequest")
    public Source readPersonIdsFromSavePoint(Source requestSource) {		
    	try {
    		return personManagementHandler.readPersonIdsFromSavePoint(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException(ex);
    	}		
    }

    /* Read all Persons */
    @PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readPersonsRequest")
    public Source readPersons(Source requestSource) {		
    	try {
    		return personManagementHandler.readPersons(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException(ex);
    	}		
    }

    /* Read persons from save point */
    @PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readPersonsFromSavePointRequest")
    public Source readPersonsFromSavePoint(Source requestSource) {		
    	try {
    		return personManagementHandler.readPersonsFromSavePoint(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException(ex);
    	}		
    }

    /* Read Person */
    @PayloadRoot(namespace = NAMESPACE_STRING, localPart = "readPersonRequest")
    public Source readPerson(Source requestSource) {		
    	try {
    		return personManagementHandler.readPerson(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException(ex);
    	}		
    }

    /* Replace Person */
    @PayloadRoot(namespace = NAMESPACE_STRING, localPart = "replacePersonRequest")
    public Source replacePerson(Source requestSource) {		
    	try {
    		return personManagementHandler.replacePerson(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException(ex);
    	}		
    }

    /* Update Person */
    @PayloadRoot(namespace = NAMESPACE_STRING, localPart = "updatePersonRequest")
    public Source updatePerson(Source requestSource) {		
    	try {
    		return personManagementHandler.updatePerson(requestSource);
    	} catch (Exception ex) {
    		throw new SoapFaultException(ex);
    	}		
    }

}
