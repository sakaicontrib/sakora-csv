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
package net.unicon.sakora.api.csv.model;

import java.util.Date;

public class Membership {

	private long id;
    private String userEid;
    private String containerEid;
    private String role;
	private String mode; //section or course
    private Date inputTime;
    
    public Membership() {}

	public Membership(String userEid, String containerEid, String role, String mode, Date inputTime) {
		this.userEid = userEid;
		this.containerEid = containerEid;
		this.role = role;
		this.inputTime = inputTime;
		this.mode = mode;
	}

	public String getUserEid() {
		return userEid;
	}

	public void setUserEid(String userEid) {
		this.userEid = userEid;
	}

	public String getContainerEid() {
		return containerEid;
	}

	public void setSectionEid(String containerEid) {
		this.containerEid = containerEid;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Date getInputTime() {
		return inputTime;
	}

	public void setInputTime(Date inputTime) {
		this.inputTime = inputTime;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public void setContainerEid(String containerEid) {
		this.containerEid = containerEid;
	}
}
