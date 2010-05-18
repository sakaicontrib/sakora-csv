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
package net.unicon.sakora.impl.ims.extern;

import net.unicon.sakora.api.ims.extern.ImsExternalHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.util.StringUtil;

/**
 * @author bsawert
 *
 */
public class ImsExternalHandlerImpl implements ImsExternalHandler {
	private static final Log log = LogFactory.getLog(ImsExternalHandlerImpl.class);

	public void init() {
		log.debug("Initializing " + getClass().getName());
	}
	
	/* (non-Javadoc)
	 * @see net.unicon.sakora.api.ims.extern.ImsExternalHandler#getPersonEidFromImsId(java.lang.String)
	 */
	public String getPersonEidFromImsId(String imsId) {
		// echo back the lowercase version of the original id
		return StringUtil.trimToNullLower(imsId);
	}

	/* (non-Javadoc)
	 * @see net.unicon.sakora.api.ims.extern.ImsExternalHandler#externalDeletePerson(java.lang.String)
	 */
	public void externalDeletePerson(String eid) {
		// do nothing
		return;
	}

	/* (non-Javadoc)
	 * @see net.unicon.sakora.api.ims.extern.ImsExternalHandler#mustDeleteLocalPerson(java.lang.String)
	 */
	public boolean mustDeleteLocalPerson(String eid) {
		// request local delete
		return true;
	}

	/* (non-Javadoc)
	 * @see net.unicon.sakora.api.ims.extern.ImsExternalHandler#makeMemberInactiveToDelete(java.lang.String)
	 */
	public boolean makeMemberInactiveToDelete(String eid) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}
