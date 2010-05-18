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
package net.unicon.sakora.tool.producers;

import java.io.File;
import java.lang.reflect.Method;

import net.unicon.sakora.api.ws.ImsEsSyncService;
import net.unicon.sakora.api.ws.ImsSyncContext;
import net.unicon.sakora.api.ws.ImsSyncContextImpl;
import uk.org.ponder.rsf.components.UIBranchContainer;
import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIInternalLink;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.components.UIVerbatim;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.DefaultView;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.SimpleViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParamsReporter;

public class MainProducer implements ViewComponentProducer, DefaultView, ViewParamsReporter {

		// The VIEW_ID must match the html template (without the .html)
		public static final String VIEW_ID = "SakoraMain";
		public String getViewID() {
			return VIEW_ID;
		}

		private ImsEsSyncService wsSyncService;
		public void setWsSyncService(ImsEsSyncService wsSyncService) {
			this.wsSyncService = wsSyncService;
		}
		
		public ImsEsSyncService setWsSyncService() {
			return wsSyncService;
		}
		
		public void fillComponents(UIContainer tofill, ViewParameters viewparams,
				ComponentChecker checker) {
			
			UIOutput.make(tofill, "wsSyncClass", wsSyncService.getClass().getName());
			try {
				if (wsSyncService.getClass().getName().equals("net.unicon.sakora.impl.ws.client.ImsEsSyncServiceWsClientImpl")) {
					Method method = wsSyncService.getClass().getMethod("getSaipEndpoint", new Class[0]);
					UIOutput.make(tofill, "saipEndpoint", method.invoke(wsSyncService, null).toString());				
					UIInternalLink.make(tofill, "testWebService", new TestViewParams(getViewID(), "testWS"));
//					command.
				}
				
			} catch (Exception ex ) {
				UIVerbatim.make(tofill, "resultMessage", "<b>Exception during execution</b>");				
				UIOutput.make(tofill, "exceptionMessage", ex.getMessage());
				return;
			}
			
			String logsFolder= System.getProperty("catalina.home") + "/logs";
			File logFile = new File(logsFolder,"catalina.out");
			Long fileLength = logFile.length();
			UIOutput.make(tofill, "fileName", logFile.getAbsolutePath());				
			UIOutput.make(tofill, "fileSize", ""+fileLength);				
			UIInternalLink.make(tofill, "viewLogFile", new SimpleViewParameters(ViewLogFileProducer.VIEW_ID));
			
			TestViewParams myParams = (TestViewParams) viewparams;
			if ("testWS".equals(myParams.test)) {
				try {
					ImsSyncContextImpl syncContext = new ImsSyncContextImpl();
					syncContext.setFeedType(ImsSyncContext.FeedType.SNAPSHOT);
//					Method method = wsSyncService.getClass().getMethod("setXmlStreamHandlingCallback", 
//							new Class[] {XMLStreamHandlingCallback.class});
//					method.invoke(wsSyncService, new Object[] {new XMLStreamValidatorCallbackImpl()});
//
					wsSyncService.readCourseOfferings(syncContext);		
					UIVerbatim.make(tofill, "resultMessage", "<b>Execution was sucessfull</b>");
				} catch (Exception ex) {
					UIVerbatim.make(tofill, "resultMessage", "<b>Exception during execution</b>");
					UIOutput.make(tofill, "exceptionMessage", ex.getMessage());
					int i = 0;
					for (StackTraceElement stElement : ex.getStackTrace())
					{
						UIBranchContainer row = UIBranchContainer.make(tofill, "exceptionRow:");
						UIOutput.make(row, "stackElement", stElement.toString());
						if (i++ > 20) break;
					}
				}
			}
			
		}

		public ViewParameters getViewParameters()
		{
			return new TestViewParams();
		}
}
