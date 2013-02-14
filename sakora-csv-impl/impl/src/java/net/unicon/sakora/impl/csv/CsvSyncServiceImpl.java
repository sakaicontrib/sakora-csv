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

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import net.unicon.sakora.api.csv.CsvHandler;
import net.unicon.sakora.api.csv.CsvSyncContext;
import net.unicon.sakora.api.csv.CsvSyncService;
import net.unicon.sakora.api.csv.model.SakoraLog;
import net.unicon.sakora.impl.csv.dao.CsvSyncDao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.api.ServerConfigurationService;

/**
 * CSV based CourseManagement Sync service
 * 
 * @author Dan McCallum dmccallum@unicon.net
 * @author Aaron Zeckoski azeckoski@unicon.net
 * @author Joshua Ryan
 */
public class CsvSyncServiceImpl implements CsvSyncService {

	private static final Log log = LogFactory.getLog(CsvSyncServiceImpl.class);
	
	/**
	 * Sync context properties key. Points to a String representing the
	 * path at which new batches arrive.
	 */
	static final String BATCH_UPLOAD_DIR = CsvSyncService.SYNC_CONTEXT_PROPERTY_KEY_PREFIX + "batch-upload-dir";
	/**
	 * Sync context properties key. Points to a String representing the
	 * path to which the current batch has been moved prior to processing.
	 */
	static final String BATCH_PROCESSING_DIR = CsvSyncService.SYNC_CONTEXT_PROPERTY_KEY_PREFIX + "batch-processing-dir";
	/**
	 * Sync context properties key. Is the current batch in a processable state?
	 * Will be evaluated as a boolean.
	 */
	static final String IS_BATCH_OK= CsvSyncService.SYNC_CONTEXT_PROPERTY_KEY_PREFIX + "is-batch-ok";
	/**
	 * Sync context properties key. Is the currently processing action the last
	 * in the sequence defined by {@link #sync(CsvSyncContext)}? Will be
	 * evaluated as a boolean. Handle nulls as false.
	 */
	static final String IS_FINAL_ACTION = CsvSyncService.SYNC_CONTEXT_PROPERTY_KEY_PREFIX + "is-final-action";
	
	/**
	 * @deprecated please use {@link #BATCH_UPLOAD_DIR_SAKAI_PROPERTY} instead.
	 */
	public static final String BATCH_UPLOAD_DIR_LEGACY_SAKAI_PROPERTY = "net.unicon.sakora.csv.basePath";
	
	public static final String BATCH_UPLOAD_DIR_SAKAI_PROPERTY = "net.unicon.sakora.csv.batchUploadDir";
	
	public static final String DEFAULT_BATCH_UPLOAD_DIR_NAME = "sakora-csv";

	public static final String FINISHED_BATCH_PROCESSING_DIR_SUFFIX = "-finished";
	
	public static final String FAILED_BATCH_PROCESSING_DIR_SUFFIX = "-failed";
	
	public static final String BATCH_PROCESSING_DIR_BASE_NAME = "sakora-csv-batch";

	private CsvAcademicSessionHandler accademicSessionHandler;
	private CsvCanonicalCourseHandler canonicalCourseHandler;
	private CsvCourseOfferingHandler courseOfferingHandler;
	private CsvCourseSetHandler courseSetHandler;
	private CsvEnrollmentSetHandler enrollmentSetHandler;
	private CsvMembershipHandler courseMembershipHandler;
	private CsvMembershipHandler sectionMembershipHandler;
	private CsvPersonHandler personHandler;
	private CsvSectionHandler sectionHandler;
	private CsvSectionMeetingHandler sectionMeetingHandler;
	private ServerConfigurationService configurationService;
	private CsvSyncDao dbLog;
	private boolean cleanupData;
	private String batchUploadDir;
	private volatile boolean pleaseStop;

	/**
	 * Algorithm for delegating to individual handlers.
	 * 
	 * @param handler
	 * @param action
	 * @param syncContext
	 */
	private void handleAction(CsvHandler handler, String action, CsvSyncContext syncContext) {
		if ( handler == null ) {
			log.warn("undefined handler called for " + action);
			return;
		}
		
		try {
			String isBatchOk = syncContext.getProperties().get(IS_BATCH_OK);
			if ( isBatchOk != null && !(Boolean.parseBoolean(isBatchOk)) ) {
				log.warn("Batch marked as being in error, skipping action [" + action + "]");
				return;
			}
			
			if ( pleaseStop ) {
				throw new IllegalStateException("CSV sync service received a stop request. Abandoning input read. This exception is thrown to ensure proper cleanup of overall batch state.");
			}
			
			// Batches need to be processed as a group, so move all currently
			// delivered files into a processing dir such that they are
			// less likely to be overwritten during processing by a subsequent
			// upload.
			if ( syncContext.getProperties().get(BATCH_PROCESSING_DIR) == null ) {
				try {
					moveBatch(syncContext);
				} catch ( IOException e ) {
					throw new IllegalStateException("Unable to move batch for processing", e);
				}
			}

			if (log.isDebugEnabled()) {
				log.debug("reading " + action);
			}
			handler.readInput(syncContext);

			if (log.isDebugEnabled()) {
				log.debug("processing " + action);
			}
			handler.process(syncContext);

			if (cleanupData) {
				if (log.isDebugEnabled()) {
					log.debug("cleaning up " + action);
				}
				handler.cleanUp(syncContext);
			}
		} catch ( Exception e ) {
			// Since batches have snapshot semantics and generally need
			// to be handled in their entirety, if handling fails
			// there's probably no sense in retrying that batch unless we
			// were to write logic to attempt to pick up where we left off.
			// It's much, much easier to just skip the rest of this batch
			// and upload a new one.
			syncContext.getProperties().put(IS_BATCH_OK, "false");
			String msg = "Failed to process batch at [" + 
				syncContext.getProperties().get(BATCH_PROCESSING_DIR) + 
				"] during action [" + action + "]. Skipping remainder of batch.";
			log.error(msg, e);
			dbLog.create(new SakoraLog(this.getClass().toString(), msg + "[" + e.getLocalizedMessage() + "]"));
		} finally {
			String isFinalAction = syncContext.getProperties().get(IS_FINAL_ACTION);
			if ( isFinalAction != null && Boolean.parseBoolean(isFinalAction) ) {
				markBatchFinished(syncContext);
			}
		}
	}
	
	public void sync(CsvSyncContext context) {
		if ( !(isBatchUploaded()) ) {
			String msg = "No batch found in upload dir [" + batchUploadDir + "]. Skipping all processing.";
			log.info(msg);
			dbLog.create(new SakoraLog(this.getClass().toString(), msg));
			return;
		}
		handleAction(accademicSessionHandler, "Sessions", context);
		handleAction(courseSetHandler, "Course Sets", context);
		handleAction(canonicalCourseHandler, "Canonical Courses", context);
		handleAction(courseOfferingHandler, "Course Offerings", context);
		handleAction(enrollmentSetHandler, "Enrollment Sets", context);
		handleAction(sectionHandler, "Sections", context);
		handleAction(sectionMeetingHandler, "Seciton Meetings", context);
		handleAction(personHandler, "Users", context);
		handleAction(courseMembershipHandler, "Course Membership", context);
		context.getProperties().put(IS_FINAL_ACTION, "true");
		handleAction(sectionMembershipHandler, "Section Membership", context);
	}
	
	private boolean isBatchUploaded() {
		File dir = new File(batchUploadDir);
		if ( !(dir.exists()) ) {
			if ( log.isDebugEnabled() ) {
				log.debug("Configured batch upload dir [" + batchUploadDir + 
						"] does not exist, so obviously no new batch has been uploaded.");
			}
			return false;
		}
		if ( !(dir.isDirectory()) ) {
			if ( log.isDebugEnabled() ) {
				log.debug("Configured batch upload dir [" + batchUploadDir + 
						"] is not a directory, so obviously no new batch has been uploaded.");
			}
			return false;
		}
		for ( File file : dir.listFiles() ) {
			if ( !(file.isDirectory()) ) {
				return true;
			}
		}
		if ( log.isDebugEnabled() ) {
			log.debug("Either no files in configured batch upload dir [" + batchUploadDir + 
					"] or all files therein are directories, so no new batch has been uploaded.");
		}
		return false;
	}

	/**
	 * Move files in one directory into another. Both dirs must
	 * already exist. Bails with an IOException on the first
	 * failed file move. Performs no cleanup as the result of
	 * such failure. Skips directories.
	 * 
	 * @param from
	 * @param to
	 * @throws IOException
	 */
	private void moveFiles(String from, String to) throws IOException {
		File fromDir = new File(from);
		if ( !(fromDir.exists()) ) {
			throw new IllegalArgumentException("Source directory does not exist [" + from + "]");
		}
		if ( !(fromDir.isDirectory()) ) {
			throw new IllegalArgumentException("Source directory is not a directory [" + fromDir + "]");
		}
		File toDir = new File(to);
		if ( !(toDir.exists()) ) {
			throw new IllegalArgumentException("Target directory does not exist [" + toDir + "]");
		}
		if ( !(toDir.isDirectory()) ) {
			throw new IllegalArgumentException("Target directory is not a directory [" + toDir + "]");
		}
		for ( File file : fromDir.listFiles() ) {
			if ( file.isDirectory() ) {
				continue;
			}
			File newFile = new File(toDir, file.getName()); 
			if ( !(file.renameTo(newFile)) ) {
				throw new IOException("Unable to move [" + file + "] to [" + newFile + "]");
			}
		}
	}
	
	/**
	 * Non-recursive directory content deletion. Makes
	 * an attempt to delete everything except directories, 
	 * returning false if any such attempt fails. This behavior
	 * allows batch processing dirs to be nested inside the
	 * batch upload dir. 
	 * 
	 * <p>If the given path doesn't exist just returns false. If the 
	 * given path exists but isn't a directory throws a 
	 * runtime exception.</p>
	 * 
	 * @param path
	 */
	private boolean deleteFilesInDir(String path) {
		File dir = new File(path);
		if ( !(dir.exists()) ) {
			return false;
		}
		if ( !(dir.isDirectory()) ) {
			throw new IllegalArgumentException("Cannot empty directory [" + path + 
					"] because it is not a directory");
		}
		boolean deletedAll = true;
		for ( File file : dir.listFiles() ) {
			if ( file.isDirectory() ) {
				continue;
			}
			if ( !(file.delete()) ) {
				deletedAll = false;
			}
		}
		return deletedAll;
	}
	

	/**
	 * Post-DI initialization
	 */
	public void init() {
		initBatchUploadDir();
	}
	
	/**
	 * DI destruction
	 */
	public void destroy() {
		pleaseStop = true;
	}
	
	/**
	 * Initializes the batch upload directory.
	 * 
	 * <p>If <code>batchUploadDir</code> is null or the empty string, will
	 * default to a Sakai property named 
	 * {@link #BATCH_UPLOAD_DIR_LEGACY_SAKAI_PROPERTY} or 
	 * a Sakai property named {@link #BATCH_UPLOAD_DIR_SAKAI_PROPERTY} or
	 * ${sakai.home}/{@link #DEFAULT_BATCH_UPLOAD_DIR_NAME} if that 
	 * property is not set.</p>
	 * 
	 * <p>Makes an attempt to ensure <code>batchUploadDir</code> exists,
	 * is a directory, and is readable and writable. Will not throw
	 * an exception if these conditions do not hold, but batch processing
	 * is highly likely to succeed if this is the case.</p>
	 * 
	 */
	private void initBatchUploadDir() {
		if (batchUploadDir == null || "".equals(batchUploadDir)) {
			batchUploadDir = configurationService.getString(BATCH_UPLOAD_DIR_LEGACY_SAKAI_PROPERTY);
			if (batchUploadDir == null || "".equals(batchUploadDir)) {
				batchUploadDir = configurationService.getString(BATCH_UPLOAD_DIR_SAKAI_PROPERTY);
			}
			if (batchUploadDir == null || "".equals(batchUploadDir)) {
				batchUploadDir = configurationService.getSakaiHomePath() + File.separator
						+ DEFAULT_BATCH_UPLOAD_DIR_NAME;
			}
		}
		if ( log.isDebugEnabled() ) {
			log.debug("Initializing batch upload dir at [" + batchUploadDir + "]");
		}
		File dir = new File(batchUploadDir);
		if ( !(dir.exists()) ) {
			if ( !(dir.mkdir()) ) {
				log.warn("Unable to create batch upload dir at [" + batchUploadDir + 
						"]. CSV batch processing is highly unlikely to be successful.");
			}
		} else if ( !(dir.isDirectory()) ) {
			log.warn("The configured batch upload directory is not a directory [" + batchUploadDir + 
					"]. CSV batch processing is highly unlikely to be successful.");
		} else if ( !(dir.canRead()) ) {
			log.warn("The configured batch upload directory is not readable [" + batchUploadDir + 
					"]. CSV batch processing is highly unlikely to be successful.");
		} else if ( !(dir.canWrite()) ) {
			log.warn("The configured batch upload directory is not a writable [" + batchUploadDir + 
					"]. CSV batch processing is highly unlikely to be successful.");
		} else {
		    log.info("SakoraCSV batch upload directory initialized: "+batchUploadDir);
		}
	}
	
	/**
	 * Moves a just-arrived batch into a unique directory.
	 * 
	 * @param syncContext
	 * @throws IOException 
	 */
	private void moveBatch(CsvSyncContext syncContext) throws IOException {
		if ( !(syncContext.getProperties().containsKey(BATCH_UPLOAD_DIR)) ) {
			syncContext.getProperties().put(BATCH_UPLOAD_DIR, getBatchUploadDir());
		}
		if ( !(syncContext.getProperties().containsKey(BATCH_PROCESSING_DIR)) ) {
			syncContext.getProperties().put(BATCH_PROCESSING_DIR, nextBatchProcessingDir());
		}
		try {
			if ( log.isInfoEnabled() ) {
				log.info("Moving batch from [" + syncContext.getProperties().get(BATCH_UPLOAD_DIR) + 
						"] to [" + syncContext.getProperties().get(BATCH_PROCESSING_DIR) + "]");
			}
			moveFiles(syncContext.getProperties().get(BATCH_UPLOAD_DIR),
				syncContext.getProperties().get(BATCH_PROCESSING_DIR));
		} catch ( IOException e ) {
			if ( !(deleteFilesInDir((String)syncContext.getProperties().get(BATCH_UPLOAD_DIR))) ) {
				log.error("Failed to empty batch upload directory [" + syncContext.getProperties().get(BATCH_UPLOAD_DIR) + 
						"]. Probably not a serious problem in and of itself, but does probably indicate some sort of fundamentally misconfigured file system or JVM process");
			}
			if ( !(deleteFilesInDir((String)syncContext.getProperties().get(BATCH_PROCESSING_DIR))) ) {
				log.error("Failed to empty batch processing directory [" + syncContext.getProperties().get(BATCH_PROCESSING_DIR) +
						"]. Probably not a serious problem in and of itself, but does probably indicate some sort of fundamentally misconfigured file system or JVM process");
			}
			throw e;
		}
	}
	
	private String nextBatchProcessingDir() throws IOException {
		int cnt = 0;
		while ( cnt++ < 5 ) {
			long now = System.currentTimeMillis();
			File processingDir = new File(new File(batchUploadDir), BATCH_PROCESSING_DIR_BASE_NAME + "-" + now);
			if ( processingDir.exists() ) {
				try {
					Thread.sleep(5L);
				} catch ( InterruptedException e) {}
				continue;
			}
			if ( !(processingDir.mkdir()) ) {
				throw new IOException("Unable to create directory at [" + processingDir + "]");
			}
			return processingDir.getPath();
		}
		throw new IOException("Unable to create a batch processing directory after several tries.");
	}

	private void markBatchFinished(CsvSyncContext syncContext) {
		try {
			closeBatchProcessingPath(syncContext);
		} catch ( Exception e ) {
			// trap here his b/c this _is_ the cleanup mechanism
			log.error("Failed to close batch processing path", e);
		}
		try {
			cleanupSyncServiceState(syncContext);
		} catch ( Exception e ) {
			// trap here his b/c this _is_ the cleanup mechanism
			log.error("Failed to cleanup CSV handling context", e);
		}
	}
	
	private void cleanupSyncServiceState(CsvSyncContext syncContext) {
		Iterator<Entry<String,String>> entries = syncContext.getProperties().entrySet().iterator();
		while ( entries.hasNext() ) {
			Entry<String,String>entry = entries.next();
			if ( entry.getKey().startsWith(CsvSyncService.SYNC_CONTEXT_PROPERTY_KEY_PREFIX) ) {
				entries.remove();
			}
		}
	}

	private void closeBatchProcessingPath(CsvSyncContext syncContext) 
	throws IOException {
		String batchProcessingPath = (String)syncContext.getProperties().get(BATCH_PROCESSING_DIR);
		if ( batchProcessingPath == null ) {
			throw new IllegalArgumentException("No batch processing path to mark as finished");
		}
		File batchProcessingDir = new File(batchProcessingPath);
		if ( !(batchProcessingDir.exists()) ) {
			throw new IllegalArgumentException("No batch processing dir at [" + batchProcessingPath + "]");
		}
		if ( !(batchProcessingDir.isDirectory()) ) {
			throw new IllegalArgumentException("No batch processing dir at [" + batchProcessingPath + "] is not a dir");
		}
		String isBatchOkStr = syncContext.getProperties().get(IS_BATCH_OK);
		boolean isBatchOk = isBatchOkStr == null || Boolean.parseBoolean(isBatchOkStr);
		String newName = batchProcessingDir.getName() + 
			(isBatchOk ? FINISHED_BATCH_PROCESSING_DIR_SUFFIX : FAILED_BATCH_PROCESSING_DIR_SUFFIX);
		File newPath = new File(batchProcessingDir.getParentFile(), newName);
		if ( !(batchProcessingDir.renameTo(newPath)) ) {
			throw new IOException("Unable to rename batch processing dir at [" + batchProcessingPath + "] to [" + newPath + "]");
		}
	}

	public void setCleanupData(String cleanupDataString) {
		this.cleanupData = Boolean.getBoolean(cleanupDataString);
	}
	
	public void setCleanupData(boolean cleanupData) {
		this.cleanupData = cleanupData;
	}

	public CsvMembershipHandler getCourseMembershipHandler() {
		return courseMembershipHandler;
	}

	public void setCourseMembershipHandler(
			CsvMembershipHandler courseMembershipHandler) {
		this.courseMembershipHandler = courseMembershipHandler;
	}

	public CsvMembershipHandler getSectionMembershipHandler() {
		return sectionMembershipHandler;
	}

	public void setSectionMembershipHandler(
			CsvMembershipHandler sectionMembershipHandler) {
		this.sectionMembershipHandler = sectionMembershipHandler;
	}

	public CsvPersonHandler getPersonHandler() {
		return personHandler;
	}

	public void setPersonHandler(CsvPersonHandler personHandler) {
		this.personHandler = personHandler;
	}

	public CsvSectionHandler getSectionHandler() {
		return sectionHandler;
	}

	public void setSectionHandler(CsvSectionHandler sectionHandler) {
		this.sectionHandler = sectionHandler;
	}

	public CsvSectionMeetingHandler getSectionMeetingHandler() {
		return sectionMeetingHandler;
	}

	public void setSectionMeetingHandler(
			CsvSectionMeetingHandler sectionMeetingHandler) {
		this.sectionMeetingHandler = sectionMeetingHandler;
	}

	public CsvCanonicalCourseHandler getCanonicalCourseHandler() {
		return canonicalCourseHandler;
	}

	public void setCanonicalCourseHandler(
			CsvCanonicalCourseHandler canonicalCourseHandler) {
		this.canonicalCourseHandler = canonicalCourseHandler;
	}

	public CsvCourseOfferingHandler getCourseOfferingHandler() {
		return courseOfferingHandler;
	}

	public void setCourseOfferingHandler(
			CsvCourseOfferingHandler courseOfferingHandler) {
		this.courseOfferingHandler = courseOfferingHandler;
	}

	public CsvCourseSetHandler getCourseSetHandler() {
		return courseSetHandler;
	}

	public void setCourseSetHandler(CsvCourseSetHandler courseSetHandler) {
		this.courseSetHandler = courseSetHandler;
	}

	public CsvAcademicSessionHandler getAccademicSessionHandler() {
		return accademicSessionHandler;
	}

	public void setAccademicSessionHandler(
			CsvAcademicSessionHandler accademicSessionHandler) {
		this.accademicSessionHandler = accademicSessionHandler;
	}

	public boolean isCleanupData() {
		return cleanupData;
	}

	public CsvEnrollmentSetHandler getEnrollmentSetHandler() {
		return enrollmentSetHandler;
	}

	public void setEnrollmentSetHandler(CsvEnrollmentSetHandler enrollmentSetHandler) {
		this.enrollmentSetHandler = enrollmentSetHandler;
	}
	
	public ServerConfigurationService getConfigurationService() {
		return configurationService;
	}

	public void setConfigurationService(
			ServerConfigurationService configurationService) {
		this.configurationService = configurationService;
	}
	
	public String getBatchUploadDir() {
		return this.batchUploadDir;
	}
	
	public void setBatchUploadDir(String batchUploadDir) {
		this.batchUploadDir = batchUploadDir;
	}
	
	public CsvSyncDao getDbLog() {
		return dbLog;
	}

	public void setDbLog(CsvSyncDao dbLog) {
		this.dbLog = dbLog;
	}
}
