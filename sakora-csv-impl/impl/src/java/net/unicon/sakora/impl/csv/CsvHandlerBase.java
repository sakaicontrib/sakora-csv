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
package net.unicon.sakora.impl.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.unicon.sakora.api.csv.CsvHandler;
import net.unicon.sakora.api.csv.CsvSyncContext;
import net.unicon.sakora.api.csv.CsvSyncService;
import net.unicon.sakora.api.csv.model.SakoraLog;
import net.unicon.sakora.impl.csv.dao.CsvSyncDao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.coursemanagement.api.CourseManagementAdministration;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.event.api.UsageSessionService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.UserDirectoryService;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

/**
 * Base class which is extended by all CSV processors
 * 
 * @author Dan McCallum dmccallum@unicon.net
 * @author Aaron Zeckoski azeckoski@unicon.net
 */
public abstract class CsvHandlerBase implements CsvHandler {
	
	/**
	 * Sync context property key. Path to batch file currently
	 * being handled by this handler.
	 */
	protected static final String BATCH_FILE_PATH = CsvSyncService.SYNC_CONTEXT_PROPERTY_KEY_PREFIX + "batch-file-path";
	/**
	 * Sync context property key. Indicates that the current
	 * batch file associated with this handler has been completely
	 * consumed by {@link #readInput(CsvSyncContext)} without
	 * fatal error. Evaluated as a boolean.
	 */
	protected static final String READ_ALL_LINES = CsvSyncService.SYNC_CONTEXT_PROPERTY_KEY_PREFIX + "read-all-lines";
	private final Log log = LogFactory.getLog(getClass());
	protected volatile boolean pleaseStop;
    protected String csvFileName;
	protected int searchPageSize = 1000;
	protected int time = 0;
	protected CsvSyncDao dao;
	protected boolean hasHeader = false;
	protected File inputFile = null;
	protected BufferedReader br = null;
	protected CSVReader csvr = null;
	protected int lines = 0;
	protected int errors = 0;
	protected int adds = 0;
	protected int updates = 0;
	protected int deletes = 0;
	protected int seconds = 0;
	protected int start = 0;
	protected int end = 0;
	protected ContentHostingService contentHostingService = null;
	protected ServerConfigurationService configurationService = null;
	protected CsvCommonHandlerService commonHandlerService = null;

	// By default date strings look like "2007-09-06", "2007-11-14"
	protected String dateFormat = "yyyy-MM-dd";

	public void init() {}
	
	public void destroy() {
	    // shared destroy
		pleaseStop = true;
	}

	/* (non-Javadoc)
	 * @see net.unicon.sakora.api.csv.CsvHandler#before(net.unicon.sakora.api.csv.CsvSyncContext)
	 */
	public void before(CsvSyncContext context) {
	    // reset the stats counters
	    lines = 0;
	    errors = 0;
	    adds = 0;
	    updates = 0;
	    deletes = 0;

	    time = start = (int) (new Date().getTime() / 1000);
	}

	/* (non-Javadoc)
	 * @see net.unicon.sakora.api.csv.CsvHandler#after(net.unicon.sakora.api.csv.CsvSyncContext)
	 */
	public void after(CsvSyncContext context) {
	    // update stats
	    end = (int) (new Date().getTime() / 1000);
	    seconds = end - start;
	    // helpful logging
	    String handlerName = this.getClass().getSimpleName().replace("Handler", "").replace("Csv", "");
	    if ("fail".equals(commonHandlerService.getCurrentSyncState())) {
	        // error
	        handlerName += " [FAILED]";
	    } else {
	        // success
	        handlerName += " completed";
	    }
	    if (errors > 0) {
	        log.warn("SakoraCSV handler "+handlerName+" encountered "+errors+" errors while processing "+lines+" lines, please check and correct the feed");
	    }
	    log.info("SakoraCSV handler "+handlerName+" processing "+lines+" lines with "+errors+" errors: adds="+adds+", updates="+updates+", deletes="+deletes);
	}

	/* (non-Javadoc)
	 * @see net.unicon.sakora.api.csv.CsvHandler#getName()
	 */
	abstract public String getName(); //return this.getClass().getSimpleName().replace("Handler", "").replace("Csv", "");

	/* (non-Javadoc)
	 * @see net.unicon.sakora.api.csv.CsvHandler#getStats()
	 */
	public Map<String, Integer> getStats() {
	    HashMap<String, Integer> stats = new HashMap<String, Integer>();
	    stats.put("lines", lines);
	    stats.put("errors", errors);
	    stats.put("adds", adds);
	    stats.put("updates", updates);
	    stats.put("deletes", deletes);
	    stats.put("start", start);
	    stats.put("seconds", seconds);
	    stats.put("end", end);
	    return stats;
	}

	/**
	 * Handled initial setup of the sync context,
	 * called from {@link #readInput(CsvSyncContext)}
	 * 
	 * @param context the sync context
	 * @return true if the file was read successfully, false otherwise
	 */
	protected boolean setup(CsvSyncContext context) {
		/* intentionally avoid storing this as an instance member to
		 * try to get away from storing processing state in singleton beans
		 */
	    boolean fileWasRead = false;
		String csvPath = context.getProperties().get(CsvSyncServiceImpl.BATCH_PROCESSING_DIR) + File.separator + csvFileName;
		context.getProperties().put(BATCH_FILE_PATH, csvPath);
		context.getProperties().put(READ_ALL_LINES, "false");
		csvr = null;
		try {
			// TODO: support for reading input from CHS started but not yet finished
			if (false /* is a valid content entity reference */) {
				try {
					br = new BufferedReader(new InputStreamReader(contentHostingService.getResource(csvPath).streamContent()));
				} catch (ServerOverloadException e) {
					e.printStackTrace();
				} catch (PermissionException e) {
					e.printStackTrace();
				} catch (IdUnusedException e) {
					e.printStackTrace();
				} catch (TypeException e) {
					e.printStackTrace();
				}
			} else {
				inputFile = new File(csvPath);
				br = new BufferedReader(new FileReader(inputFile));
			}

			csvr = new CSVReader(br);

			// if the csv files have headers, skip them
			if (hasHeader) {
				csvr.readNext();
			}
			fileWasRead = true;
		} catch (FileNotFoundException ffe) {
		    dao.create(new SakoraLog(this.getClass().toString(), ffe.getLocalizedMessage()));
		    log.info("SakoraCSV reader failed to locate file [" + csvPath + "] (this is OK if you did not upload this file as part of the feed): "+ffe.getLocalizedMessage());
		} catch (IOException ioe) {
			dao.create(new SakoraLog(this.getClass().toString(), ioe.getLocalizedMessage()));
			log.warn("SakoraCSV reader failed to read from file [" + csvPath + "]: "+ioe, ioe);
		} catch (CsvValidationException cve) {
			dao.create(new SakoraLog(this.getClass().toString(), cve.getLocalizedMessage()));
			log.warn("SakoraCSV reader failed validation of file [" + csvPath + "]: "+cve, cve);
		}
		return fileWasRead;
	}

	public void cleanUp(CsvSyncContext context) {
		if (csvr != null) {
			try {
				csvr.close();
			} catch (IOException e) {
				if ( log.isDebugEnabled() ) {
					log.debug("SakoraCSV Failed to cleanly close CSV reader [file name: " + csvFileName + "]", e);
				}
			}
		}
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				if ( log.isDebugEnabled() ) {
					log.debug("SakoraCSV Failed to cleanly close underlying reader for CSV file [file name: " + csvFileName + "]", e);
				}
			}
		}
	}

	public void readInput(CsvSyncContext context) {
		if ( pleaseStop ) {
			throw new IllegalStateException("Handler received a stop request. Abandoning input read of [" + context.getProperties().get(BATCH_FILE_PATH) + "]. This exception is thrown to ensure proper cleanup of overall batch state.");
		}
		boolean success = setup(context);
		if ( pleaseStop ) {
			throw new IllegalStateException("Handler received a stop request. Abandoning input read of [" + context.getProperties().get(BATCH_FILE_PATH) + "]. This exception is thrown to ensure proper cleanup of overall batch state.");
		}
		if (success) {
		    loginToSakai();
		    String[] line = null;
		    try {
		        int linesReadCnt = 0;
		        while (csvr != null && (line = csvr.readNext()) != null) {
		            if ( pleaseStop ) {
		                throw new IllegalStateException("Handler received a stop request. Abandoning input read of [" + context.getProperties().get(BATCH_FILE_PATH) + "]. This exception is thrown to ensure proper cleanup of overall batch state.");
		            }
		            if ( log.isDebugEnabled() ) {
		                log.debug("Handling line: " + Arrays.toString(line));
		            }
		            readInputLine(context, line);
		            lines++;
		            linesReadCnt++;
		        }
		        context.getProperties().put(READ_ALL_LINES, (linesReadCnt > 0 ? "true" : "false"));
		    } catch ( IOException e ) {
		        // this is very unlikely since the file read attempt was already made previously and would have died before getting here
		        dao.create(new SakoraLog(this.getClass().toString(), getClass().getSimpleName() + ":: IO error reading " + context.getProperties().get(BATCH_FILE_PATH)));
		        log.error(getClass().getSimpleName() + ":: IO error reading " + context.getProperties().get(BATCH_FILE_PATH) + ": "+e, e);
		    } catch ( IdNotFoundException ine ) {
		        // can be thrown by methods in readInputLine but generally this is unlikely to happen because most (or all) handlers catch this exception themselves
		        dao.create(new SakoraLog(this.getClass().toString(), getClass().getSimpleName() + ":: " + ine.getLocalizedMessage()));
		        log.error(getClass().getSimpleName() + ":: " + ine.getLocalizedMessage(), ine);
		    } catch (CsvValidationException cve) {
		        dao.create(new SakoraLog(this.getClass().toString(), cve.getLocalizedMessage()));
		        log.warn("SakoraCSV reader failed validation of file [" + context.getProperties().get(BATCH_FILE_PATH) + "]: "+cve, cve);
		    }
		    finally {
		        logoutFromSakai();
		        dao.create(new SakoraLog(this.getClass().toString(),
		                (pleaseStop ? "Aborted" : "Finished") + 
		                " reading input from [" + context.getProperties().get(BATCH_FILE_PATH) + "], added " + adds + " and updated " + updates));
		    }
		} else {
		    dao.create(new SakoraLog(this.getClass().toString(), "Unable to read input from [" + context.getProperties().get(BATCH_FILE_PATH) + "]"));
		}
	}
	
	protected abstract void readInputLine(CsvSyncContext context, String[] line);
	
	public void process(CsvSyncContext context) {
		if ( pleaseStop ) {
			throw new IllegalStateException("Handler received a stop request. Abandoning snapshot processing of [" + context.getProperties().get(BATCH_FILE_PATH) + "]. This exception is thrown to ensure proper cleanup of overall batch state.");
		}
		String readAllLines = context.getProperties().get(READ_ALL_LINES);
		if ( readAllLines != null && Boolean.parseBoolean(readAllLines) ) {
			processInternal(context);
		} else {
		    dao.create(new SakoraLog(this.getClass().toString(), "Skipped post processing for "+getName()+" because we only partially processed the lines in CSV file [" + context.getProperties().get(BATCH_FILE_PATH) + "]"));
		    if ( log.isDebugEnabled() ) {
		        log.debug("SakoraCSV Did not process all lines of current file [" + context.getProperties().get(BATCH_FILE_PATH) + 
		                "] so skipping post processing. This ensures we do not mistakenly interpret a partially-read snapshot file as specifying a too-great number of deletes.");
		    }
		}
	}
	
	protected abstract void processInternal(CsvSyncContext context);

	/**
	 * Checks for valid value of input value, logs errors to db.
	 * 
	 * @param item
	 * @param label
	 * @param eid
	 * @return if item is valid
	 */
	protected boolean isValid(Object item, String label, String eid) {
		if (eid == null) {
			dao.create(new SakoraLog(this.getClass().toString(), "A valid Eid value is required"));
			return false;
		}
		else if (item == null) {
			dao.create(new SakoraLog(this.getClass().toString(), "A valid " + label + " value is required for " + eid));
			return false;
		}
		return true;
	}
	
	/**
	 * Trims leading and trailing white space from all elements in a String array
	 * 
	 * @param line
	 * @return line with every element trim()'d
	 */
	protected String[] trimAll (String[] line) {
		if (line != null) {
			for (int i = 0; i < line.length; i++) {
				if (line[i] != null)
					line[i] = line[i].trim();
				if ("".equals(line[i]))
					line[i] = null;
			}
		}
		return line;
	}
	
	protected UserDirectoryService userDirService;
	public void setUserDirService(UserDirectoryService userDirService) {
		this.userDirService = userDirService;
	}

	protected UsageSessionService usageSessionService;
	public void setUsageSessionService(UsageSessionService usageSessionService) {
		this.usageSessionService = usageSessionService;
	}

	protected AuthzGroupService authzGroupService;
	public void setAuthzGroupService(AuthzGroupService authzGroupService) {
		this.authzGroupService = authzGroupService;
	}

	protected EventTrackingService eventTrackingService;
	public void setEventTrackingService(EventTrackingService eventTrackingService) {
		this.eventTrackingService = eventTrackingService;
	}

	protected SessionManager sessionManager;
	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	protected CourseManagementAdministration cmAdmin;
	public void setCmAdmin(CourseManagementAdministration cmAdmin) {
		this.cmAdmin = cmAdmin;
	}

	protected CourseManagementService cmService;
	public void setCmService(CourseManagementService cmService) {
		this.cmService = cmService;
	}

	protected void loginToSakai() {
	    Session sakaiSession = sessionManager.getCurrentSession();
		sakaiSession.setUserId("admin");
		sakaiSession.setUserEid("admin");

		// establish the user's session
		usageSessionService.startSession("admin", "127.0.0.1", "SakoraLoader");

		// update the user's externally provided realm definitions
		authzGroupService.refreshUser("admin");

		// post the login event
		eventTrackingService.post(eventTrackingService.newEvent(UsageSessionService.EVENT_LOGIN, null, true));
	}

	protected void logoutFromSakai() {
	    Session sakaiSession = sessionManager.getCurrentSession();
		sakaiSession.invalidate();

		// post the logout event
		eventTrackingService.post(eventTrackingService.newEvent(UsageSessionService.EVENT_LOGOUT, null, true));
	}

	public int getSearchPageSize() {
		return searchPageSize;
	}

	public void setSearchPageSize(int searchPageSize) {
		this.searchPageSize = searchPageSize;
	}

	public CsvSyncDao getDao() {
		return dao;
	}

	public void setDao(CsvSyncDao dao) {
		this.dao = dao;
	}

	public boolean isHasHeader() {
		return hasHeader;
	}

	public void setHasHeader(boolean hasHeader) {
		this.hasHeader = hasHeader;
	}

	protected Date parseDate(String input) {
		Date output = null;

        DateFormat df = new SimpleDateFormat(dateFormat);
        try {
            if (input != null) {
                    output = df.parse(input);
            }
        }
        catch(ParseException pe) {pe.printStackTrace();}
        
        return output;
	}

	public ContentHostingService getContentHostingService() {
		return contentHostingService;
	}

	public void setContentHostingService(ContentHostingService contentHostingService) {
		this.contentHostingService = contentHostingService;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getCsvFileName() {
		return csvFileName;
	}

	public void setCsvFileName(String csvFileName) {
		this.csvFileName = csvFileName;
	}

	public ServerConfigurationService getConfigurationService() {
		return configurationService;
	}

	public void setConfigurationService(
			ServerConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

    public void setCommonHandlerService(CsvCommonHandlerService commonHandlerService) {
        this.commonHandlerService = commonHandlerService;
    }

}
