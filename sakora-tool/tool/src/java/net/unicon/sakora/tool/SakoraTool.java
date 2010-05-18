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
package net.unicon.sakora.tool;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.unicon.sakora.api.SakoraService;

import org.sakaiproject.component.cover.ComponentManager;

/**
 * Just a placeholder class to verify module layout
 * 
 * @author dmccallum
 *
 */
public class SakoraTool extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest rqst, HttpServletResponse rsp) 
	throws IOException {
		SakoraService sakoraService = 
			(SakoraService)ComponentManager.get("net.unicon.sakora.api.SakoraService");
		PrintWriter out = rsp.getWriter();
		out.println("net.unicon.sakora.tool.SakoraTool, reporting for duty...");
		out.println();
		out.println("SakoraService component is running [" + sakoraService.isRunning() + "]");
		if ( !(sakoraService.isRunning()) ) {
			out.println("Starting SakoraService component...");
			sakoraService.start();
			out.println("SakoraService component is running [" + sakoraService.isRunning() + "]");
		}
		out.println("Stopping SakoraService component...");
		sakoraService.stop();
		out.println("SakoraService component is running [" + sakoraService.isRunning() + "]");
		out.flush();
		
	}
	
}
