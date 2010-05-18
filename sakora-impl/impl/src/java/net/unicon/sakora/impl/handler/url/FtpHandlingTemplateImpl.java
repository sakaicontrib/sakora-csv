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
package net.unicon.sakora.impl.handler.url;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;

import net.unicon.sakora.api.handler.xml.XMLStreamHandlingCallback;
import net.unicon.sakora.impl.exception.SakoraRuntimeException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;

public class FtpHandlingTemplateImpl extends URLHandlingTemplateImpl
{
	private static final Log LOG = LogFactory.getLog(FtpHandlingTemplateImpl.class);

	private boolean enterPassiveMode;
	
	public void setEnterPassiveMode(boolean passiveMode) {
		enterPassiveMode = passiveMode;
	}
	
	public void handleURL(String urlString, XMLStreamHandlingCallback xshc)
	{
		URL url = null;
		FTPClient client = null;
		InputStream inputStream = null;

		try
		{
			url = new URL(urlString);
			FTPClientConfig config = new FTPClientConfig(FTPClientConfig.SYST_UNIX);
			client = new FTPClient();
			client.configure(config);
			if (url.getPort() == -1) {
				client.connect(url.getHost());
			} else {
				client.connect(url.getHost(), url.getPort());
			}
			
			if (getUserId() != null) {
				// if we have a user id try to login
				if (!client.login(getUserId(),getPassword())) {  
					throw new SakoraRuntimeException("Login for " + urlString + " failed.");
				}
			}
			
			// if the ftp server is configured to run in passive mode we will switch to it here.
			if (enterPassiveMode) {
				client.enterLocalPassiveMode();
			}
			
			String file = url.getFile();
			// ftp does not like leading / so we remove it
			if (file.charAt(0) == '/' ) {
				file = file.substring(1);
			}
			
			
			inputStream = client.retrieveFileStream(file);
			if (inputStream == null) {
				throw new SakoraRuntimeException("Retrieving of file " + file + " failed.");
			}
			xshc.handleStream(inputStream);
		}
		catch (MalformedURLException mfe)
		{
			LOG.error("Invalid URL: " + url);
			throw new SakoraRuntimeException(mfe);
		}
		catch (SocketException se)
		{
			LOG.error("Error connecting to URL " + url, se);
			throw new SakoraRuntimeException(se);
		}
		catch (IOException ioe)
		{
			LOG.error("Error reading from " + url, ioe);
			throw new SakoraRuntimeException(ioe);
		}
		finally
		{
			if (inputStream != null)
			{
				try
				{
					inputStream.close();
				}
				catch (IOException ignore)
				{
				}
			}
			if (client != null && client.isConnected())
			{
				try
				{
					client.disconnect();
				}
				catch (IOException ioe)
				{
				}
			}
		}
	}
}
