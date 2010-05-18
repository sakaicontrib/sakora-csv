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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import uk.org.ponder.rsf.components.UIContainer;
import uk.org.ponder.rsf.components.UIOutput;
import uk.org.ponder.rsf.view.ComponentChecker;
import uk.org.ponder.rsf.view.DefaultView;
import uk.org.ponder.rsf.view.ViewComponentProducer;
import uk.org.ponder.rsf.viewstate.ViewParameters;
import uk.org.ponder.rsf.viewstate.ViewParamsReporter;

public class ViewLogFileProducer implements ViewComponentProducer, ViewParamsReporter
{

	// The VIEW_ID must match the html template (without the .html)
	public static final String VIEW_ID = "ViewLogFile";

	public String getViewID()
	{
		return VIEW_ID;
	}

	public void fillComponents(UIContainer tofill, ViewParameters viewparams,
			ComponentChecker checker)
	{

		String logsFolder = System.getProperty("catalina.home") + "/logs";
		File logFile = new File(logsFolder, "catalina.out");
		Long fileLength = logFile.length();
		UIOutput.make(tofill, "fileName", logFile.getAbsolutePath());
		UIOutput.make(tofill, "fileSize", "" + fileLength);
		UIOutput.make(tofill, "content", readFileContent(logFile));
	}

	
	public String readFileContent(File file) {
		FileReader reader = null;
		StringBuffer buffer = new StringBuffer();
		try {
			reader = new FileReader(file);
			char [] cbuf = new char[1024];
			int readBytes = 0;
			while (readBytes >= 0) {
				readBytes = reader.read(cbuf);
				if (readBytes > 0) {
					buffer.append(cbuf, 0, readBytes);
				}
			}
		} catch (FileNotFoundException fne) {
			return "File not found.";
		} catch (IOException ex) {
			return "Problem reading file. " + ex.getMessage();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ex) {
					// ignore the exception
				}
			}
		}
		return buffer.toString();
	}
	
	public ViewParameters getViewParameters()
	{
		return new TestViewParams();
	}
}
