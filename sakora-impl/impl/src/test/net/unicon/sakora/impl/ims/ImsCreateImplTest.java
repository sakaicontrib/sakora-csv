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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.coursemanagement.api.CourseManagementAdministration;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.Meeting;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.event.api.UsageSessionService;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserEdit;

/**
 * Test that the handler will correctly parse the XML string and call the
 * course management and administration API's
 */
public class ImsCreateImplTest extends TestCase {
	public static final String DEPT_XML_STRING = "<group><sourcedId>FINANC_AUS</sourcedId><grouptype><scheme><language>en-US</language><text></text></scheme><typevalue><type><language>en-US</language><text>DEPARTMENT</text></type><level><language>en-US</language><text>2</text></level></typevalue></grouptype><org><orgname><language>en-US</language><text>Department of Finance</text></orgname><id><language>en-US</language><text>FINANC_AUS</text></id></org><description><shortDescription>Dept Fin</shortDescription><longDescription>Department of Finance</longDescription></description><datasource>CS</datasource><extension><extensionField><fieldName>Mode</fieldName><fieldType>String</fieldType><fieldValue></fieldValue></extensionField></extension></group>";
	public static final String TERM_XML_STRING = "<group><sourcedId>PSUNV-UGRD-0630</sourcedId><grouptype><scheme><language>en-US</language><text></text></scheme><typevalue><type><language>en-US</language><text>TERM</text></type><level><language>en-US</language><text>3</text></level></typevalue></grouptype><timeframe><begin><date>2009-08-30</date></begin><end><date>2009-12-12</date></end></timeframe><org><orgname><language>en-US</language><text>2009 Fall</text></orgname></org><description><shortDescription>2009 Fall</shortDescription><longDescription>2009 Fall</longDescription></description><datasource>CS</datasource><extension><extensionField><fieldName>Mode</fieldName><fieldType>String</fieldType><fieldValue></fieldValue></extensionField></extension></group>";
	public static final String TEMPLATE_XML_STRING = "<courseTemplate><sourcedId>001205</sourcedId><label><language>en-US</language><text>001205</text></label><title><language>en-US</language><text>History of World Art</text></title><catalogDescription><shortDescription></shortDescription><longDescription>History of World Art</longDescription><fullDescription><mediamode></mediamode><contentRefType></contentRefType><mimeType></mimeType><content></content></fullDescription></catalogDescription><courseNumber></courseNumber><status></status><defaultCredits></defaultCredits><parentTemplateId></parentTemplateId><org><orgName></orgName><orgUnit></orgUnit><type></type><id></id></org><listofTopics><topic></topic></listofTopics><listofPrerequisites><prerequisite></prerequisite></listofPrerequisites><dataSource>CS</dataSource><extension><extensionField><fieldName>Mode</fieldName><fieldType>String</fieldType><fieldValue>A</fieldValue></extensionField></extension></courseTemplate>";
	public static final String OFFERING_XML_STRING = "<courseOffering><sourcedId>001002</sourcedId><label><language>en-US</language><text>001002</text></label><title><language>en-US</language><text>Finite Mathematics</text></title><parentTemplateId></parentTemplateId><catalogDescription><shortDescription></shortDescription><longDescription>Finite Mathematics</longDescription></catalogDescription><status></status><defaultCredits></defaultCredits><academicSession><language>en-US</language><text>0592</text></academicSession><org><orgName></orgName><orgUnit></orgUnit><type></type><id></id></org><timeframe><begin><date>2007-09-06</date></begin><end><date>2007-11-14</date></end><restrict></restrict><adminPeriod></adminPeriod></timeframe><enrollControl><enrollAccept></enrollAccept><enrollAllowed></enrollAllowed></enrollControl><dataSource>CS</dataSource><extension><extensionField><fieldName>Mode</fieldName><fieldType>String</fieldType><fieldValue>A</fieldValue></extensionField></extension></courseOffering>";
	public static final String SECTION_XML_STRING = "<courseSection><sourcedId>007151-1-0590-OEE-2</SourcedId><courseSectionID>007151-1-0590-OEE-2</courseSectionID><Title>Human Paleontology</Title><ParentOfferingID/><CatalogDescription>Human Paleontology</CatalogDescription><Status/><DefaultCredits/><Category>LEC</Category><maxNumberofStudents/><sectionClass/><location/><meetOnlineWithUnit/><extension><Mode>A</Mode></extension><meeting><startTime></startTime><finishTime></finishTime><pattern></pattern><notes/><extension><Mode></Mode></extension></meeting></courseSection>";
	public static final String PERSON_XML_STRING = "<person><sourcedId>0057</sourcedId><formname><formattedName><language>en-US</language><text>John Quick Enroll 0595 Smith Quick Enroll 0595</text></formattedName></formname><name><partname><firstname>John Quick Enroll 0595</firstname><nickname></nickname><familyname>Smith Quick Enroll 0595</familyname><givenname>John Quick Enroll 0595</givenname><prefix></prefix><suffix></suffix><initials></initials><last>Smith Quick Enroll 0595</last><maternal></maternal><middle></middle><particle></particle><paternal></paternal><surname>Smith Quick Enroll 0595</surname></partname></name><address><addresspart><nonfieldedstreetaddress1></nonfieldedstreetaddress1><nonfieldedstreetaddress2></nonfieldedstreetaddress2><nonfieldedstreetaddress3></nonfieldedstreetaddress3><nonfieldedstreetaddress4></nonfieldedstreetaddress4><locality></locality><city></city><statepr></statepr><region></region><country></country><postcode></postcode></addresspart></address><contactinfo><telephone></telephone><email></email><webaddress></webaddress></contactinfo><demographics><gender>unknown</gender><placeofbirth><language>en-US</language><text></text></placeofbirth></demographics><birthday></birthday><dataSource>CS</dataSource><extension><extensionField><fieldName>Mode</fieldName><fieldType>String</fieldType><fieldValue>C</fieldValue></extensionField></extension></person>";
	private Mockery context;
	
	@Override
	protected void setUp() throws Exception {
		context = new Mockery();
	}
	
	
	public void testXmlCourseSetParse() {
		// prepare the mock object
		final CourseManagementService cmService = context.mock(CourseManagementService.class);
		final CourseManagementAdministration cmAdmin = context.mock(CourseManagementAdministration.class);
		final SessionManager sessionManager = context.mock(SessionManager.class);
		final UsageSessionService usageSessionService = context.mock(UsageSessionService.class);
		final AuthzGroupService authzGroupService = context.mock(AuthzGroupService.class);
		final EventTrackingService eventTrackingService = context.mock(EventTrackingService.class);	

		final Sequence maintest = context.sequence("maintest");
		context.checking(new Expectations() {{
			// log in to Sakai
			one(sessionManager).getCurrentSession(); inSequence(maintest);
			one(usageSessionService).startSession("admin", "127.0.0.1", "SakoraLoader");  inSequence(maintest);
			one(authzGroupService).refreshUser("admin");  inSequence(maintest);
			// process course set info
            one(cmService).isCourseSetDefined("FINANC_AUS"); inSequence(maintest);
            one(cmAdmin).createCourseSet("FINANC_AUS", "Dept Fin", "Department of Finance", "DEPARTMENT", null); inSequence(maintest);
            // log out of Sakai
			one(sessionManager).getCurrentSession(); inSequence(maintest);
			ignoring(eventTrackingService);
        }});

		ImsCreateGroup creator = new ImsCreateGroup();
		// do what spring would do
		creator.setCmService(cmService);
		creator.setCmAdmin(cmAdmin);
		creator.setSessionManager(sessionManager);
		creator.setUsageSessionService(usageSessionService);
		creator.setAuthzGroupService(authzGroupService);
		creator.setEventTrackingService(eventTrackingService);
		creator.init();

		creator.handleXml(DEPT_XML_STRING);
		
		context.assertIsSatisfied();
	}

	
	public void testXmlTermParse() {
		// prepare the mock object
		final CourseManagementService cmService = context.mock(CourseManagementService.class);
		final CourseManagementAdministration cmAdmin = context.mock(CourseManagementAdministration.class);
		final SessionManager sessionManager = context.mock(SessionManager.class);
		final UsageSessionService usageSessionService = context.mock(UsageSessionService.class);
		final AuthzGroupService authzGroupService = context.mock(AuthzGroupService.class);
		final EventTrackingService eventTrackingService = context.mock(EventTrackingService.class);	
		
		final Date startDate;
		final Date endDate;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date tempStartDate = null;
		Date tempEndDate = null;
		
		try {
			tempStartDate = df.parse("2009-08-30");
			tempEndDate = df.parse("2009-12-12");
		}
		catch(ParseException pe) {
			System.out.print(pe.getMessage());
		}

		// initialize final variables
		startDate = tempStartDate;
		endDate = tempEndDate;
		
		final Sequence maintest = context.sequence("maintest");
		context.checking(new Expectations() {{
			// log in to Sakai
			one(sessionManager).getCurrentSession(); inSequence(maintest);
			one(usageSessionService).startSession("admin", "127.0.0.1", "SakoraLoader");  inSequence(maintest);
			one(authzGroupService).refreshUser("admin");  inSequence(maintest);
			// process academic session info
            one(cmService).isAcademicSessionDefined("PSUNV-UGRD-0630"); inSequence(maintest);
            one(cmAdmin).createAcademicSession("PSUNV-UGRD-0630", "2009 Fall", "2009 Fall", startDate, endDate); inSequence(maintest);
            // log out of Sakai
			one(sessionManager).getCurrentSession(); inSequence(maintest);
			ignoring(eventTrackingService);
        }});

		ImsCreateGroup creator = new ImsCreateGroup();
		// do what spring would do
		creator.setCmService(cmService);
		creator.setCmAdmin(cmAdmin);
		creator.setSessionManager(sessionManager);
		creator.setUsageSessionService(usageSessionService);
		creator.setAuthzGroupService(authzGroupService);
		creator.setEventTrackingService(eventTrackingService);
		creator.init();

		creator.handleXml(TERM_XML_STRING);
		
		context.assertIsSatisfied();
	}

	
	public void testXmlTemplateParse() {
		// prepare the mock object
		final CourseManagementService cmService = context.mock(CourseManagementService.class);
		final CourseManagementAdministration cmAdmin = context.mock(CourseManagementAdministration.class);
		final SessionManager sessionManager = context.mock(SessionManager.class);
		final UsageSessionService usageSessionService = context.mock(UsageSessionService.class);
		final AuthzGroupService authzGroupService = context.mock(AuthzGroupService.class);
		final EventTrackingService eventTrackingService = context.mock(EventTrackingService.class);	

		final Sequence maintest = context.sequence("maintest");
		context.checking(new Expectations() {{
			// log in to Sakai
			one(sessionManager).getCurrentSession(); inSequence(maintest);
			one(usageSessionService).startSession("admin", "127.0.0.1", "SakoraLoader");  inSequence(maintest);
			one(authzGroupService).refreshUser("admin");  inSequence(maintest);
			// process course template info
            one(cmService).isCanonicalCourseDefined("001205"); inSequence(maintest);
            one(cmAdmin).createCanonicalCourse("001205", "History of World Art", "History of World Art"); inSequence(maintest);
            // log out of Sakai
			one(sessionManager).getCurrentSession(); inSequence(maintest);
			ignoring(eventTrackingService);
        }});

		ImsCreateCourseTemplate creator = new ImsCreateCourseTemplate();
		// do what spring would do
		creator.setCmService(cmService);
		creator.setCmAdmin(cmAdmin);
		creator.setSessionManager(sessionManager);
		creator.setUsageSessionService(usageSessionService);
		creator.setAuthzGroupService(authzGroupService);
		creator.setEventTrackingService(eventTrackingService);
		creator.init();

		creator.handleXml(TEMPLATE_XML_STRING);
		
		context.assertIsSatisfied();
	}

	public void testXmlCourseOfferingParse() {
		// prepare the mock object
		final CourseManagementService cmService = context.mock(CourseManagementService.class);
		final CourseManagementAdministration cmAdmin = context.mock(CourseManagementAdministration.class);
		final SessionManager sessionManager = context.mock(SessionManager.class);
		final UsageSessionService usageSessionService = context.mock(UsageSessionService.class);
		final AuthzGroupService authzGroupService = context.mock(AuthzGroupService.class);
		final EventTrackingService eventTrackingService = context.mock(EventTrackingService.class);	

		final Date startDate;
		final Date endDate;
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date tempStartDate = null;
		Date tempEndDate = null;
		
		try {
			tempStartDate = df.parse("2007-09-06");
			tempEndDate = df.parse("2007-11-14");
		}
		catch(ParseException pe) {
			System.out.print(pe.getMessage());
		}

		// initialize final variables
		startDate = tempStartDate;
		endDate = tempEndDate;
		
		final Sequence maintest = context.sequence("maintest");
		context.checking(new Expectations() {{
			// log in to Sakai
			one(sessionManager).getCurrentSession(); inSequence(maintest);
			one(usageSessionService).startSession("admin", "127.0.0.1", "SakoraLoader");  inSequence(maintest);
			one(authzGroupService).refreshUser("admin");  inSequence(maintest);
			// process course offering info
            one(cmService).isCourseOfferingDefined("001002"); inSequence(maintest);
            one(cmAdmin).createCourseOffering("001002",
            	"Finite Mathematics", "Finite Mathematics", "", "0592", null, startDate, endDate); inSequence(maintest);
            // log out of Sakai
			one(sessionManager).getCurrentSession(); inSequence(maintest);
			ignoring(eventTrackingService);
        }});

		ImsCreateCourseOffering creator = new ImsCreateCourseOffering();
		// do what spring would do
		creator.setCmService(cmService);
		creator.setCmAdmin(cmAdmin);
		creator.setSessionManager(sessionManager);
		creator.setUsageSessionService(usageSessionService);
		creator.setAuthzGroupService(authzGroupService);
		creator.setEventTrackingService(eventTrackingService);
		creator.init();

		creator.handleXml(OFFERING_XML_STRING);
		
		context.assertIsSatisfied();
	}

		
	public void testXmlSectionParse() {
		// prepare the mock object
		final CourseManagementService cmService = context.mock(CourseManagementService.class);
		final CourseManagementAdministration cmAdmin = context.mock(CourseManagementAdministration.class);
		final SessionManager sessionManager = context.mock(SessionManager.class);
		final UsageSessionService usageSessionService = context.mock(UsageSessionService.class);
		final AuthzGroupService authzGroupService = context.mock(AuthzGroupService.class);
		final EventTrackingService eventTrackingService = context.mock(EventTrackingService.class);	

		final Section sec = context.mock(Section.class);
		final Set meetings = new HashSet<Meeting>();
		final Meeting meeting = context.mock(Meeting.class);
		meetings.add(meeting);
		
		final Sequence maintest = context.sequence("maintest");
		context.checking(new Expectations() {{
			// log in to Sakai
			one(sessionManager).getCurrentSession(); inSequence(maintest);
			one(usageSessionService).startSession("admin", "127.0.0.1", "SakoraLoader");  inSequence(maintest);
			one(authzGroupService).refreshUser("admin");  inSequence(maintest);
			// process course section info
            one(cmService).isSectionDefined("007151-1-0590-OEE-2"); inSequence(maintest);
            one(cmAdmin).createSection("007151-1-0590-OEE-2", "007151-1-0590-OEE-2", "007151-1-0590-OEE-2",
            	null, null, null, null); inSequence(maintest); will(returnValue(sec));
            one(sec).getMeetings(); inSequence(maintest); will(returnValue(null));
            one(cmAdmin).newSectionMeeting("007151-1-0590-OEE-2", null, null, null, null); inSequence(maintest); will(returnValue(meeting));
            one(sec).setMeetings(meetings); inSequence(maintest);
            one(cmAdmin).updateSection(sec);
            // log out of Sakai
			one(sessionManager).getCurrentSession(); inSequence(maintest);
			ignoring(eventTrackingService);
        }});

		ImsCreateCourseSection creator = new ImsCreateCourseSection();
		// do what spring would do
		creator.setCmService(cmService);
		creator.setCmAdmin(cmAdmin);
		creator.setSessionManager(sessionManager);
		creator.setUsageSessionService(usageSessionService);
		creator.setAuthzGroupService(authzGroupService);
		creator.setEventTrackingService(eventTrackingService);
		creator.init();

		creator.handleXml(SECTION_XML_STRING);
		
		context.assertIsSatisfied();
	}

	
	public void testXmlPersonParse() throws Exception {
		// prepare the mock object
		final UserDirectoryService userDirService = context.mock(UserDirectoryService.class);
		final SessionManager sessionManager = context.mock(SessionManager.class);
		final UsageSessionService usageSessionService = context.mock(UsageSessionService.class);
		final AuthzGroupService authzGroupService = context.mock(AuthzGroupService.class);
		final EventTrackingService eventTrackingService = context.mock(EventTrackingService.class);
		
		final UserEdit ue = context.mock(UserEdit.class);

		final Sequence maintest = context.sequence("maintest");
		context.checking(new Expectations() {{
			// log in to Sakai
			one(sessionManager).getCurrentSession(); inSequence(maintest);
			one(usageSessionService).startSession("admin", "127.0.0.1", "SakoraLoader");  inSequence(maintest);
			one(authzGroupService).refreshUser("admin");  inSequence(maintest);
			// process person info
            one(userDirService).getUserId("0057"); inSequence(maintest); will(returnValue(null));
            one(userDirService).addUser(null, "0057"); inSequence(maintest); will(returnValue(ue));
    		one(ue).setPassword("0057"); inSequence(maintest);
    		one(ue).setFirstName("John Quick Enroll 0595"); inSequence(maintest);
    		one(ue).setLastName("Smith Quick Enroll 0595"); inSequence(maintest);
    		one(ue).setEmail(""); inSequence(maintest);
            one(userDirService).commitEdit(ue); inSequence(maintest);
            // log out of Sakai
			one(sessionManager).getCurrentSession(); inSequence(maintest);
			ignoring(eventTrackingService);
        }});
		ImsCreatePerson creator = new ImsCreatePerson();
		// do what spring would do
		creator.setUserDirService(userDirService);
		creator.setSessionManager(sessionManager);
		creator.setUsageSessionService(usageSessionService);
		creator.setAuthzGroupService(authzGroupService);
		creator.setEventTrackingService(eventTrackingService);
		creator.init();

		creator.handleXml(PERSON_XML_STRING);
		
		context.assertIsSatisfied();
	}
	
	
	public void testXmlPersonParseNull() throws Exception {
		ImsCreatePerson creator = new ImsCreatePerson();
		creator.init();
		assertFalse(creator.canHandle((String)null));
		creator.handleXml((String)null);
	}

	@Override
	protected void tearDown() throws Exception {
		// nothing to do yet
	}
}
