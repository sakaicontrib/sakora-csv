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
package net.unicon.sakora.api.ws;

/**
 * Job context for Oracle SIS web service calls
 * 
 * @author bsawert
 *
 */

public class ImsSyncContextImpl implements ImsSyncContext {
	String contextName;
	String lastToken;
	String newToken;
	FeedType feedType;
	boolean isFinished;
	
	
	public String getContextName() {
		return contextName;
	}

	public void setContextName(String contextName) {
		this.contextName = contextName;
	}

	public String getLastToken() {
		return lastToken;
	}
	
	public void setLastToken(String lastToken) {
		this.lastToken = lastToken;
	}
	
	public String getNewToken() {
		return newToken;
	}
	
	public void setNewToken(String newToken) {
		this.newToken = newToken;
	}
	
	public FeedType getFeedType() {
		return feedType;
	}

	public void setFeedType(FeedType feedType) {
		this.feedType = feedType;
	}

	public boolean isFinished() {
		return isFinished;
	}
	
	public void setFinished(boolean isFinished) {
		this.isFinished = isFinished;
	}
	
}
