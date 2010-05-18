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
package net.unicon.sakora.impl.ims;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import junit.framework.TestCase;
import net.unicon.sakora.api.csv.model.Membership;
import net.unicon.sakora.api.csv.model.Session;
import net.unicon.sakora.api.csv.model.SakoraLog;
import net.unicon.sakora.api.ws.ImsSyncContext;
import net.unicon.sakora.impl.csv.CsvAcademicSessionHandler;
import net.unicon.sakora.impl.csv.CsvCanonicalCourseHandler;
import net.unicon.sakora.impl.csv.CsvCourseOfferingHandler;
import net.unicon.sakora.impl.csv.CsvCourseSetHandler;
import net.unicon.sakora.impl.csv.CsvEnrollmentSetHandler;
import net.unicon.sakora.impl.csv.CsvHandlerBase;
import net.unicon.sakora.impl.csv.CsvMembershipHandler;
import net.unicon.sakora.impl.csv.CsvPersonHandler;
import net.unicon.sakora.impl.csv.CsvSectionHandler;
import net.unicon.sakora.impl.csv.CsvSectionMeetingHandler;
import net.unicon.sakora.impl.csv.CsvSyncServiceImpl;
import net.unicon.sakora.impl.csv.dao.CsvSyncDao;
import net.unicon.sakora.api.ws.ImsSyncContextImpl;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.coursemanagement.api.CourseManagementAdministration;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.event.api.UsageSessionService;
import org.sakaiproject.genericdao.api.search.Search;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.UserAlreadyDefinedException;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserEdit;
import org.sakaiproject.user.api.UserIdInvalidException;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.api.UserPermissionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CsvSyncServiceTest extends TestCase {
	// TODO all this commented out b/c of breaking changes in the
	// relationship between the sync service and individual handlers.
	// Bad, I know.
	/*
	Mockery context;
	ApplicationContext appContext;
	CourseManagementService cmService;
	CourseManagementAdministration cmAdmin;
	SessionManager sessionManager;
	UsageSessionService usageSessionService;
	AuthzGroupService authzGroupService;
	EventTrackingService eventTrackingService;	
	UserDirectoryService userDirService;
	ContentHostingService contentHostingService;
	CsvAcademicSessionHandler sessionHandler;
	CsvCanonicalCourseHandler courseHandler;
	CsvCourseOfferingHandler offeringHandler;
	CsvCourseSetHandler courseSetHandler;
	CsvEnrollmentSetHandler enrollmentSetHandler;
	CsvPersonHandler personHandler;
	CsvSectionHandler sectionHandler;
	CsvSectionMeetingHandler meetingHandler;
	CsvMembershipHandler sectionMembershipHandler;
	CsvMembershipHandler courseMembershipHandler;
	CsvSyncServiceImpl syncService;
	ImsSyncContext syncContext;
	CsvSyncDao dao;
	SimpleDateFormat df;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		context = new Mockery();
		appContext = new ClassPathXmlApplicationContext("components.xml");
		cmService = context.mock(CourseManagementService.class);
		cmAdmin = context.mock(CourseManagementAdministration.class);
		sessionManager = context.mock(SessionManager.class);
		usageSessionService = context.mock(UsageSessionService.class);
		
		authzGroupService = context.mock(AuthzGroupService.class);
		eventTrackingService = context.mock(EventTrackingService.class);	
		userDirService = context.mock(UserDirectoryService.class);
		contentHostingService = context.mock(ContentHostingService.class);
		sessionHandler = new CsvAcademicSessionHandler();
		dao = context.mock(CsvSyncDao.class);
		setDeps(sessionHandler, "./resources/test/webapp/data/sessions.csv");
		courseHandler = new CsvCanonicalCourseHandler();
		setDeps(courseHandler, "./resources/test/webapp/data/courses.csv");
		offeringHandler = new CsvCourseOfferingHandler();
		setDeps(offeringHandler, "./resources/test/webapp/data/courseOfferings.csv");
		courseSetHandler = new CsvCourseSetHandler();
		setDeps(courseSetHandler, "./resources/test/webapp/data/courseSets.csv");
		sectionHandler = new CsvSectionHandler();
		setDeps(sectionHandler, "./resources/test/webapp/data/courseSections.csv");
		meetingHandler = new CsvSectionMeetingHandler();
		setDeps(meetingHandler, "./resources/test/webapp/data/meetings.csv");
		enrollmentSetHandler = new CsvEnrollmentSetHandler();
		setDeps(enrollmentSetHandler, "./resources/test/webapp/data/enrollmentSets.csv");
		syncContext = new ImsSyncContextImpl();
		personHandler = new CsvPersonHandler();
		setDeps(personHandler, "./resources/test/webapp/data/people.csv");
		sectionMembershipHandler = new CsvMembershipHandler();
		setDeps(sectionMembershipHandler, "./resources/test/webapp/data/sectionMembership.csv");
		sectionMembershipHandler.setMode("section");
		courseMembershipHandler = new CsvMembershipHandler();
		setDeps(courseMembershipHandler, "./resources/test/webapp/data/courseMembership.csv");
		courseMembershipHandler.setMode("course");
		
		//dao = new CsvSyncDaoMock();

		syncService = new CsvSyncServiceImpl();
		syncService.setAccademicSessionHandler(sessionHandler);
		syncService.setCanonicalCourseHandler(courseHandler);
		syncService.setCourseMembershipHandler(courseMembershipHandler);
		syncService.setCourseOfferingHandler(offeringHandler);
		syncService.setEnrollmentSetHandler(enrollmentSetHandler);
		syncService.setCourseSetHandler(courseSetHandler);
		syncService.setPersonHandler(personHandler);
		syncService.setSectionHandler(sectionHandler);
		syncService.setSectionMeetingHandler(meetingHandler);
		syncService.setSectionMembershipHandler(sectionMembershipHandler);
		syncService.setCleanupData(false);
		
		df = new SimpleDateFormat("yyyy-MM-dd");
	}

	public void testGroupReads() throws ParseException {
		try {
			setUp();
		} catch (Exception e) {
			e.printStackTrace();
		}
		final Sequence grouptest = context.sequence("grouptest");
		context.checking(new Expectations() {{
			ignoring(eventTrackingService);
			
			// handle accademic sessions
			one(dao).create(with(any(SakoraLog.class)));
			// log in to Sakai
			one(sessionManager).getCurrentSession(); inSequence(grouptest);
			one(usageSessionService).startSession("admin", "127.0.0.1", "SakoraLoader"); inSequence(grouptest);
			one(authzGroupService).refreshUser("admin"); inSequence(grouptest);

			// TODO: write in expected flow from test data
			one(cmService).isAcademicSessionDefined("Summer_1_2009");
			inSequence(grouptest);
			one(cmAdmin).createAcademicSession("Summer_1_2009", "Summer 1 2009", "First summer session 2009", df.parse("2009-6-5"), df.parse("2009-7-21"));
			inSequence(grouptest);

			one (dao).findOneBySearch(with(any(Class.class)), with(any(Search.class))); will(returnValue(null)); inSequence(grouptest);
			one (dao).save(with(any(Session.class))); inSequence(grouptest);

			one(cmService).isAcademicSessionDefined("Summer_2_2009");
			inSequence(grouptest);
			one(cmAdmin).createAcademicSession("Summer_2_2009", "Summer 2 2009", "Second summer session 2009", df.parse("2009-7-25"), df.parse("2009-8-15"));
			inSequence(grouptest);

			one (dao).findOneBySearch(with(any(Class.class)), with(any(Search.class))); will(returnValue(null)); inSequence(grouptest);
			one (dao).save(with(any(Session.class))); inSequence(grouptest);

			// log out of Sakai
			one(sessionManager).getCurrentSession(); inSequence(grouptest);
			one(dao).create(with(any(SakoraLog.class)));

			// log in to Sakai
			one(sessionManager).getCurrentSession(); inSequence(grouptest);
			one(usageSessionService).startSession("admin", "127.0.0.1", "SakoraLoader"); inSequence(grouptest);
			one(authzGroupService).refreshUser("admin"); inSequence(grouptest);

			one(dao).findBySearch(with(any(Class.class)), with(any(Search.class)));
			one(cmAdmin).setCurrentAcademicSessions(with(any(List.class))); inSequence(grouptest);

			// log out of Sakai
			one(sessionManager).getCurrentSession(); inSequence(grouptest);
			one(dao).create(with(any(SakoraLog.class)));

			// handle course sets
			// log back into Sakai
			one(sessionManager).getCurrentSession(); inSequence(grouptest);
			one(usageSessionService).startSession("admin", "127.0.0.1", "SakoraLoader"); inSequence(grouptest);
			one(authzGroupService).refreshUser("admin"); inSequence(grouptest);
			
			one(cmService).isCourseSetDefined("Math"); inSequence(grouptest);
			one(cmAdmin).createCourseSet("Math", "Math", "Mathematics", "CM", null); inSequence(grouptest);
			one(cmService).isCourseSetDefined("Reading"); inSequence(grouptest);
			one(cmAdmin).createCourseSet("Reading", "Reading", "Reading", "CM", null); inSequence(grouptest);
			one(cmService).isCourseSetDefined("CS"); inSequence(grouptest);
			one(cmAdmin).createCourseSet("CS", "Computer Science", "Computer Science", "CM", null); inSequence(grouptest);
			
			// log out of Sakai
			one(sessionManager).getCurrentSession(); inSequence(grouptest);
			one(dao).create(with(any(SakoraLog.class)));
        }});
		syncService.readGroups(syncContext);
	}

	public void testCourseTemplateReads() {
		try {
			setUp();
		} catch (Exception e) {
			e.printStackTrace();
		}
		final Sequence grouptest = context.sequence("grouptest");
		context.checking(new Expectations() {{
			ignoring(eventTrackingService);
			
			// handle course templates
			one(dao).create(with(any(SakoraLog.class)));
			// log in to Sakai
			one(sessionManager).getCurrentSession(); inSequence(grouptest);
			one(usageSessionService).startSession("admin", "127.0.0.1", "SakoraLoader"); inSequence(grouptest);
			one(authzGroupService).refreshUser("admin"); inSequence(grouptest);

			one(cmService).isCanonicalCourseDefined("TEST101");
			inSequence(grouptest);
			one(cmAdmin).createCanonicalCourse("TEST101", "Hello World", "Basic test course");
			inSequence(grouptest);
			one(cmService).isCanonicalCourseDefined("TEST102");inSequence(grouptest);
			one(cmAdmin).createCanonicalCourse("TEST102", "Hello Universe", "More advanced test course");
			inSequence(grouptest);
			one(cmService).isCanonicalCourseDefined("Math_101");
			inSequence(grouptest);
			one(cmAdmin).createCanonicalCourse("Math_101", "Addition", "Basic Addition");
			inSequence(grouptest);
			one(cmService).isCourseSetDefined("Math"); will(returnValue(true));
			inSequence(grouptest);
			
			one(cmAdmin).addCanonicalCourseToCourseSet("Math", "Math_101"); 
			inSequence(grouptest);

			// log out of Sakai
			one(sessionManager).getCurrentSession();
			inSequence(grouptest);
			one(dao).create(with(any(SakoraLog.class)));

        }});
		syncService.readCourseTemplates(syncContext);
	}
	
	public void testCourseSectionReads() {
		try {
			setUp();
		} catch (Exception e) {
			e.printStackTrace();
		}
		final Sequence grouptest = context.sequence("grouptest");
		context.checking(new Expectations() {{
			ignoring(eventTrackingService);
			
			// handle sections
			one(dao).create(with(any(SakoraLog.class)));
			// log in to Sakai
			one(sessionManager).getCurrentSession(); inSequence(grouptest);
			one(usageSessionService).startSession("admin", "127.0.0.1", "SakoraLoader"); inSequence(grouptest);
			one(authzGroupService).refreshUser("admin"); inSequence(grouptest);

			one(cmService).getSectionCategoryDescription(with(any(String.class))); inSequence(grouptest);
			one(cmService).isSectionDefined("TEST101_Ryan_A");
			inSequence(grouptest);
			one(cmAdmin).createSection("TEST101_Ryan_A", "T/TH Section", "Tuesday Thursday section of TEST101", "CM", null, "TEST101_Summer_2_2009_Ryan", null);
			inSequence(grouptest);
			one(cmService).getSectionCategoryDescription(with(any(String.class))); inSequence(grouptest);
			one(cmService).isSectionDefined("TEST101_Ryan_B");
			inSequence(grouptest);
			one(cmAdmin).createSection("TEST101_Ryan_B", "T/TH Section (Honors)", "Tuesday Thursday section of TEST101 for honor students", "CM", null,"TEST101_Summer_2_2009_Ryan", null);
			inSequence(grouptest);
			one(cmService).getSectionCategoryDescription(with(any(String.class))); inSequence(grouptest);
			one(cmService).isSectionDefined("Math_101_Summer_2_2009_A");
			inSequence(grouptest);
			one(cmAdmin).createSection("Math_101_Summer_2_2009_A", "Addition 2009", "Basic addition", "CM", null, "Math_101_Summer_2_2009", null);
			inSequence(grouptest);
			
			// log out of Sakai
			one(sessionManager).getCurrentSession(); inSequence(grouptest);
			one(dao).create(with(any(SakoraLog.class)));
			
			// handle section meetings
			// log back into Sakai
			one(sessionManager).getCurrentSession(); inSequence(grouptest);
			one(usageSessionService).startSession("admin", "127.0.0.1", "SakoraLoader"); inSequence(grouptest);
			one(authzGroupService).refreshUser("admin"); inSequence(grouptest);
			
			one(cmService).isSectionDefined("TEST101_Ryan_A");
			inSequence(grouptest);
			
			// log out of Sakai
			one(sessionManager).getCurrentSession(); inSequence(grouptest);
			one(dao).create(with(any(SakoraLog.class)));
        }});
		syncService.readCourseSections(syncContext);
	}

	public void testCourseOfferingsReads() throws ParseException {
		try {
			setUp();
		} catch (Exception e) {
			e.printStackTrace();
		}
		final Sequence grouptest = context.sequence("grouptest");
		context.checking(new Expectations() {{
			ignoring(eventTrackingService);
			
			// handle course offerings
			one(dao).create(with(any(SakoraLog.class)));
			// log in to Sakai
			one(sessionManager).getCurrentSession(); inSequence(grouptest);
			one(usageSessionService).startSession("admin", "127.0.0.1", "SakoraLoader"); inSequence(grouptest);
			one(authzGroupService).refreshUser("admin"); inSequence(grouptest);

			one(cmService).isCourseOfferingDefined("TEST101_Summer_1_2009_Ryan");
			inSequence(grouptest);
			one(cmAdmin).createCourseOffering("TEST101_Summer_1_2009_Ryan", "Hello World", "Basic test course", "open", "Summer_1_2009", "TEST101", df.parse("2009-6-5"), df.parse("2009-7-21"));
			inSequence(grouptest);
			one(cmService).isCourseOfferingDefined("TEST101_Summer_2_2009_Ryan");
			inSequence(grouptest);
			one(cmAdmin).createCourseOffering("TEST101_Summer_2_2009_Ryan", "Hello World", "Basic test course", "open", "Summer_2_2009", "TEST101", df.parse("2009-7-25"), df.parse("2009-8-15"));
			inSequence(grouptest);
			one(cmService).isCourseSetDefined("CS");
			inSequence(grouptest);
			one(cmService).isCourseOfferingDefined("Math_101_Summer_2_2009");
			inSequence(grouptest);
			one(cmAdmin).createCourseOffering("Math_101_Summer_2_2009", "Addition", "Basic Addition", "open", "Summer_2_2009", "Math_101", df.parse("2009-7-25"), df.parse("2009-8-15"));
			inSequence(grouptest);
			one(cmService).isCourseSetDefined("Math");
			inSequence(grouptest);

			
			// log out of Sakai
			one(sessionManager).getCurrentSession(); inSequence(grouptest);
			one(dao).create(with(any(SakoraLog.class)));
			
			// handle enrollment sets
			// log back into Sakai
			one(sessionManager).getCurrentSession(); inSequence(grouptest);
			one(usageSessionService).startSession("admin", "127.0.0.1", "SakoraLoader"); inSequence(grouptest);
			one(authzGroupService).refreshUser("admin"); inSequence(grouptest);
			
			one(cmService).isEnrollmentSetDefined("Math_101_Summer_2_2009");
			inSequence(grouptest);
			one(cmAdmin).createEnrollmentSet("Math_101_Summer_2_2009", "Addition", "Addition", "Math", "3", "Math_101_Summer_2_2009", null);
			inSequence(grouptest);
			one(cmService).isEnrollmentSetDefined("honors_cs_09");
			inSequence(grouptest);
			one(cmAdmin).createEnrollmentSet("honors_cs_09", "Honors Computer Science", "Honors Computer Science includes extra out of class work and research", "CS", "4", "TEST101_Summer_1_2009_Ryan", null);
			inSequence(grouptest);
			
			
			// log out of Sakai
			one(sessionManager).getCurrentSession(); inSequence(grouptest);
			one(dao).create(with(any(SakoraLog.class)));
        }});
		syncService.readCourseOfferings(syncContext);
	}

	public void testMembershipReads() {
		try {
			setUp();
		} catch (Exception e) {
			e.printStackTrace();
		}
		final Sequence grouptest = context.sequence("grouptest");
		context.checking(new Expectations() {{
			ignoring(eventTrackingService);
			
			// handle course membership
			one(dao).create(with(any(SakoraLog.class)));
			// log in to Sakai
			one(sessionManager).getCurrentSession(); inSequence(grouptest);
			one(usageSessionService).startSession("admin", "127.0.0.1", "SakoraLoader"); inSequence(grouptest);
			one(authzGroupService).refreshUser("admin"); inSequence(grouptest);

			// TODO: write in expected flow from test data
			ignoring(cmService);
			ignoring(cmAdmin);
			one(dao).findOneBySearch(with(any(Class.class)), with(any(Search.class))); will(returnValue(null));

			// log out of Sakai
			one(sessionManager).getCurrentSession(); inSequence(grouptest);
			one(dao).create(with(any(SakoraLog.class)));
			
			// log back into Sakai
			one(sessionManager).getCurrentSession(); inSequence(grouptest);
			one(usageSessionService).startSession("admin", "127.0.0.1", "SakoraLoader"); inSequence(grouptest);
			one(authzGroupService).refreshUser("admin"); inSequence(grouptest);
			
			// TODO: write in expected flow from test data
			one (dao).findBySearch(with(any(Class.class)), with(any(Search.class)));
			one (dao).save(with(any(Membership.class)));

			// log out of Sakai
			one(sessionManager).getCurrentSession(); inSequence(grouptest);
			
			// handle section membership
			// log back into Sakai
			one(sessionManager).getCurrentSession(); inSequence(grouptest);
			one(usageSessionService).startSession("admin", "127.0.0.1", "SakoraLoader"); inSequence(grouptest);
			one(authzGroupService).refreshUser("admin"); inSequence(grouptest);
			
			// TODO: write in expected flow from test data
			exactly(11).of (dao).findOneBySearch(with(any(Class.class)), with(any(Search.class))); will(returnValue(null));
			exactly(11).of (dao).save(with(any(Membership.class)));
			exactly(1).of (cmAdmin).addOrUpdateCourseOfferingMembership("TEST101_Summer_2_2009_Ryan", "joshryan", "I", "enrolled");
			exactly(1).of (cmAdmin).addOrUpdateCourseOfferingMembership("Math_101_Summer_2_2009", "joshryan", "I", "enrolled");
			exactly(7).of(cmAdmin).addOrUpdateSectionMembership(with(any(String.class)), with(any(String.class)), with(any(String.class)), with(any(String.class)));
			exactly(7).of(cmAdmin).addOrUpdateEnrollment(with(any(String.class)), with(any(String.class)), with(any(String.class)), with(any(String.class)), with(any(String.class)));
			
			// log out of Sakai
			one(sessionManager).getCurrentSession(); inSequence(grouptest);
			one(dao).create(with(any(SakoraLog.class)));
			
			// log back into Sakai
			one(sessionManager).getCurrentSession(); inSequence(grouptest);
			one(usageSessionService).startSession("admin", "127.0.0.1", "SakoraLoader"); inSequence(grouptest);
			one(authzGroupService).refreshUser("admin"); inSequence(grouptest);
			
			// TODO: write in expected flow from test data
			one(dao).findBySearch(with(any(Class.class)), with(any(Search.class)));
			
			// log out of Sakai
			one(sessionManager).getCurrentSession(); inSequence(grouptest);
        }});
		syncService.readMemberships(syncContext);
	}
	
	public void testPersonReads() throws UserNotDefinedException, UserIdInvalidException, UserAlreadyDefinedException, UserPermissionException {
		try {
			setUp();
		} catch (Exception e) {
			e.printStackTrace();
		}
		final Sequence grouptest = context.sequence("grouptest");
		context.checking(new Expectations() {{
			ignoring(eventTrackingService);
			ignoring(cmService);
			ignoring(cmAdmin);

			// log in to Sakai
			one(sessionManager).getCurrentSession(); inSequence(grouptest);
			one(usageSessionService).startSession("admin", "127.0.0.1", "SakoraLoader"); inSequence(grouptest);
			one(authzGroupService).refreshUser("admin"); inSequence(grouptest);

			// TODO: write in expected flow from test data

			one(userDirService).getUserId("joshryan");
			inSequence(grouptest);
			one(userDirService).addUser("joshryan", "joshryan");
			inSequence(grouptest);
			one(userDirService).commitEdit(with(any(UserEdit.class)));
			inSequence(grouptest);
			one(dao).save(with(any(SakoraLog.class)));
			inSequence(grouptest);
			one(userDirService).getUserId("barney");
			inSequence(grouptest);
			one(userDirService).addUser("barney", "barney");
			inSequence(grouptest);
			one(userDirService).commitEdit(with(any(UserEdit.class)));
			inSequence(grouptest);
			one(dao).save(with(any(SakoraLog.class)));
			inSequence(grouptest);
			one(userDirService).getUserId("betty");
			inSequence(grouptest);
			one(userDirService).addUser("betty", "betty");
			inSequence(grouptest);
			one(userDirService).commitEdit(with(any(UserEdit.class)));
			inSequence(grouptest);
			one(dao).save(with(any(SakoraLog.class)));
			inSequence(grouptest);
			one(userDirService).getUserId("kazoo");
			inSequence(grouptest);
			one(userDirService).addUser("kazoo", "kazoo");
			inSequence(grouptest);
			one(userDirService).commitEdit(with(any(UserEdit.class)));
			inSequence(grouptest);
			one(dao).save(with(any(SakoraLog.class)));
			inSequence(grouptest);
			one(userDirService).getUserId("fred");
			inSequence(grouptest);
			one(userDirService).addUser("fred", "fred");
			inSequence(grouptest);
			one(userDirService).commitEdit(with(any(UserEdit.class)));
			inSequence(grouptest);
			one(dao).save(with(any(SakoraLog.class)));
			inSequence(grouptest);
			one(userDirService).getUserId("wilma");
			inSequence(grouptest);
			one(userDirService).addUser("wilma", "wilma");
			inSequence(grouptest);
			one(userDirService).commitEdit(with(any(UserEdit.class)));
			inSequence(grouptest);
			one(dao).save(with(any(SakoraLog.class)));
			inSequence(grouptest);
			one(userDirService).getUserId("bambam");
			inSequence(grouptest);
			one(userDirService).addUser("bambam", "bambam");
			inSequence(grouptest);
			one(userDirService).commitEdit(with(any(UserEdit.class)));
			inSequence(grouptest);
			one(dao).save(with(any(SakoraLog.class)));
			inSequence(grouptest);
			
			
			// log out of Sakai
			one(sessionManager).getCurrentSession(); inSequence(grouptest);
			one(dao).create(with(any(SakoraLog.class)));
			
			// log back into Sakai
			one(sessionManager).getCurrentSession(); inSequence(grouptest);
			one(usageSessionService).startSession("admin", "127.0.0.1", "SakoraLoader"); inSequence(grouptest);
			one(authzGroupService).refreshUser("admin"); inSequence(grouptest);
			
			// TODO: write in expected flow from test data
			one(dao).findBySearch(with(any(Class.class)), with(any(Search.class)));
			
			// log out of Sakai
			one(sessionManager).getCurrentSession(); inSequence(grouptest);
        }});
		syncService.readPersons(syncContext);
	}

	private void setDeps(CsvHandlerBase handler, String path) {
		handler.setDao(dao);
		handler.setCmAdmin(cmAdmin);
		handler.setCmService(cmService);
		handler.setContentHostingService(contentHostingService);
		handler.setEventTrackingService(eventTrackingService);
		handler.setAuthzGroupService(authzGroupService);
		handler.setHasHeader(true);
		handler.setSessionManager(sessionManager);
		handler.setUsageSessionService(usageSessionService);
		handler.setUserDirService(userDirService);
		handler.setSearchPageSize(1000);
		handler.setCsvPath(path);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	*/

}
