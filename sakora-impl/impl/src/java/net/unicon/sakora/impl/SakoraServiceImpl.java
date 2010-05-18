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
package net.unicon.sakora.impl;

import net.unicon.sakora.api.SakoraService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This is a placeholder class to verify module layout.
 * 
 * @author dmccallum@unicon.net
 */
public class SakoraServiceImpl implements SakoraService {

	private static Log log = LogFactory.getLog(SakoraServiceImpl.class);
	private boolean isRunning;
		
	public void start() {
		log.info("Starting");
		isRunning = true;
	}

	public void stop() {
		log.info("Stopping");
		isRunning = false;
	}
	
	public boolean isRunning() {
		return isRunning;
	}
	
}
