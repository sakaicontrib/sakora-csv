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

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;
import net.unicon.sakora.api.ims.ImsContentDelegator;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CanonicalCourse;
import org.sakaiproject.coursemanagement.api.CourseManagementAdministration;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.CourseSet;
import org.sakaiproject.coursemanagement.api.Meeting;
import org.sakaiproject.coursemanagement.api.Section;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.event.api.UsageSessionService;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserEdit;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Test that the handler will correctly parse the XML string and call the
 * course management and administration API's
 */
public class ImsContentDelegatorImplTest extends TestCase {
	public static final String DEPT_XML_STRING = "<group><sourcedId>GLAKE</sourcedId><grouptype><scheme><language>en-US</language><text></text></scheme><typevalue><type><language>en-US</language><text>INSTITUTE</text></type><level><language>en-US</language><text>1</text></level></typevalue></grouptype><description><shortDescription>Glake</shortDescription><longDescription>Great Lakes University</longDescription></description><datasource>CS</datasource><extension><extensionField><fieldName>Mode</fieldName><fieldType>String</fieldType><fieldValue></fieldValue></extensionField></extension></group>";
	public static final String TERM_XML_STRING = "<group><sourcedId>PSUNV-UGRD-0595</sourcedId><grouptype><scheme><language>en-US</language><text></text></scheme><typevalue><type><language>en-US</language><text>TERM</text></type><level><language>en-US</language><text>3</text></level></typevalue></grouptype><timeframe><begin><date>2008-01-03</date><date>2008-03-12</date></begin><end/></timeframe><org><orgname><language>en-US</language><text>2008 Winter Qtr</text></orgname></org><description><shortDescription>2008 WtQt</shortDescription><longDescription>2008 Winter Qtr</longDescription></description><datasource>CS</datasource><extension><extensionField><fieldName>Mode</fieldName><fieldType>String</fieldType><fieldValue></fieldValue></extensionField></extension></group>";
	public static final String TEMPLATE_XML_STRING = "<coursetemplate><sourcedid>001033</sourcedid><label>001033</label><title><text>Intro to Political Philosophy</text></title><catalogdescription><shortdescription/><longdescription>Intro to Political Philosophy</longdescription><fulldescription><mediamode/><contentreftype/><mimetype/><descriptiontext/></fulldescription></catalogdescription><coursenumber/><status/><defaultcredits/><org><orgname/><orgunit/><type/><id/></org><list_of_topics><topic/></list_of_topics><list_of_prerequisites><prerequisites/></list_of_prerequisites><listofequivtemplates/><extension><mode>A</mode></extension></coursetemplate>";
	public static final String OFFERING_XML_STRING = "<courseoffering><sourcedid>CS</sourcedid><label><text>001002</text></label><courseofferingnumber>1</courseofferingnumber><title><text>Finite Mathematics</text></title><parenttemplateId/><catalogdescription><shortdescription/><longdescription>Finite Mathematics</longdescription></catalogdescription><status/><defaultcredits><minimumunits>3</minimumunits><miximumunits>3</miximumunits></defaultcredits><academicsession><text>0592</text></academicsession><org><orgname/><orgunit/><type/><id/></org><timeframe><begin><date>2007-09-06</date></begin><end><date>2007-11-14</date></end><restrict/><adminperiod/></timeframe><enrollcontrol><enrollaccept/><enrollallowed/></enrollcontrol><extension><mode>A</mode></extension></courseoffering>";
	public static final String SECTION_XML_STRING = "<courseSection><sourcedId>001234-1-0590-1-1</sourcedId><title><language>en-US</language><text>Introduction to Anthropology</text></title><catalogDescription><shortDescription>Introduction to Anthropology</shortDescription></catalogDescription><category><language>en-US</language><text>LEC</text></category><sectionClass><day>MWF</day><startTime>10:00:00.000000</startTime><finishTime>10:50:00.000000</finishTime><location><language>en-US</language><text>PSCSHCDA</text></location><extension><extensionField><fieldName>Mode</fieldName><fieldType>String</fieldType><fieldValue>A</fieldValue></extensionField></extension></sectionClass><dataSource>CS</dataSource><extension><extensionField><fieldName>Mode</fieldName><fieldType>String</fieldType><fieldValue>A</fieldValue></extensionField></extension></courseSection>";
	public static final String PERSON_XML_STRING = "<person><sourcedId>0012</sourcedId><formname><formattedName><language>en-US</language><text>Syndi B. Forbes</text></formattedName></formname><name><partname><firstname>Syndi B.</firstname><nickname></nickname><familyname>Forbes</familyname><givenname>Syndi B.</givenname><prefix></prefix><suffix></suffix><initials></initials><last>Forbes</last><maternal></maternal><middle></middle><particle></particle><paternal></paternal><surname>Forbes</surname></partname></name><address><addresspart><nonfieldedstreetaddress1></nonfieldedstreetaddress1><nonfieldedstreetaddress2></nonfieldedstreetaddress2><nonfieldedstreetaddress3></nonfieldedstreetaddress3><nonfieldedstreetaddress4></nonfieldedstreetaddress4><locality></locality><city></city><statepr></statepr><region></region><country></country><postcode></postcode></addresspart></address><contactinfo><telephone></telephone><email></email><webaddress></webaddress></contactinfo><demographics><gender>unknown</gender><placeofbirth><language>en-US</language><text></text></placeofbirth></demographics><birthday>1980-06-15</birthday><dataSource>CS</dataSource><extension><extensionField><fieldName>Mode</fieldName><fieldType>String</fieldType><fieldValue>C</fieldValue></extensionField></extension></person>";
	private Mockery context;
	private ApplicationContext appContext;
	CourseManagementService cmService;
	CourseManagementAdministration cmAdmin;
	SessionManager sessionManager;
	UsageSessionService usageSessionService;
	AuthzGroupService authzGroupService;
	EventTrackingService eventTrackingService;	
	UserDirectoryService userDirService;
	
	@Override
	protected void setUp() throws Exception {
		context = new Mockery();
		appContext = new ClassPathXmlApplicationContext("components.xml");
		// prepare the mock object
		cmService = context.mock(CourseManagementService.class);
		cmAdmin = context.mock(CourseManagementAdministration.class);
		sessionManager = context.mock(SessionManager.class);
		usageSessionService = context.mock(UsageSessionService.class);
		authzGroupService = context.mock(AuthzGroupService.class);
		eventTrackingService = context.mock(EventTrackingService.class);	
		userDirService = context.mock(UserDirectoryService.class);	
	}
	
	public void testBeanDefinition() {
		assertNotNull("Application Context not loaded", appContext);
		ImsContentDelegator delegator = (ImsContentDelegator) appContext.getBean("net.unicon.sakora.api.ims.ImsContentDelegator");
		assertNotNull("Bean definition for net.unicon.sakora.api.ims.ImsContentDelegator not found",delegator);
	}
	
	public void testHandleInvalidElementTag() {
		ImsContentDelegator delegator = (ImsContentDelegator) appContext.getBean("net.unicon.sakora.api.ims.ImsContentDelegator");
		delegator.handleXmlContent("<enterprise></enterprise>");
		// how can I check that the was something written to to log?
	}
	
	public void testHandleCourseTemplates() {
		ImsContentDelegator delegator = (ImsContentDelegator) appContext.getBean("net.unicon.sakora.api.ims.ImsContentDelegator");
		ImsCreateCourseTemplate creator = (ImsCreateCourseTemplate)appContext.getBean("net.unicon.sakora.impl.ims.ImsCreateCourseTemplate");
		creator.setCmService(cmService);
		creator.setCmAdmin(cmAdmin);
		creator.setSessionManager(sessionManager);
		creator.setUsageSessionService(usageSessionService);
		creator.setAuthzGroupService(authzGroupService);
		creator.setEventTrackingService(eventTrackingService);
		creator.init();

		final Sequence maintest = context.sequence("maintest");
		final CanonicalCourse cc = context.mock(CanonicalCourse.class);
		context.checking(new Expectations() {{
			// log in to Sakai
			one(sessionManager).getCurrentSession(); inSequence(maintest);
			one(usageSessionService).startSession("admin", "127.0.0.1", "SakoraLoader");  inSequence(maintest);
			one(authzGroupService).refreshUser("admin"); inSequence(maintest);
			// process course set info
            one(cmService).isCanonicalCourseDefined("001033"); will(returnValue(true)); inSequence(maintest);
            one(cmService).getCanonicalCourse("001033"); will(returnValue(cc)); inSequence(maintest);
            one(cc).setTitle("Intro to Political Philosophy"); inSequence(maintest);
            one(cc).setDescription("Intro to Political Philosophy"); inSequence(maintest);
			one(cmAdmin).updateCanonicalCourse(cc); inSequence(maintest);
            // log out of Sakai
			one(sessionManager).getCurrentSession(); inSequence(maintest);
			ignoring(eventTrackingService);
        }});

		
		delegator.handleXmlContent(TEMPLATE_XML_STRING);
		context.assertIsSatisfied();
	}
	
	public void testHandleCourseOffering() {
		ImsContentDelegator delegator = (ImsContentDelegator) appContext.getBean("net.unicon.sakora.api.ims.ImsContentDelegator");
		ImsCreateCourseOffering creator = (ImsCreateCourseOffering)appContext.getBean("net.unicon.sakora.impl.ims.ImsCreateCourseOffering");
		creator.setCmService(cmService);
		creator.setCmAdmin(cmAdmin);
		creator.setSessionManager(sessionManager);
		creator.setUsageSessionService(usageSessionService);
		creator.setAuthzGroupService(authzGroupService);
		creator.setEventTrackingService(eventTrackingService);
		creator.init();


		final Sequence maintest = context.sequence("maintest");
		final CourseOffering co = context.mock(CourseOffering.class);
		final AcademicSession as = context.mock(AcademicSession.class);
		try {
			context.checking(new Expectations() {{
				// log in to Sakai
				one(sessionManager).getCurrentSession(); inSequence(maintest);
				one(usageSessionService).startSession("admin", "127.0.0.1", "SakoraLoader");  inSequence(maintest);
				one(authzGroupService).refreshUser("admin"); inSequence(maintest);
				// process course set info
	            one(cmService).isCourseOfferingDefined("CS"); will(returnValue(true)); inSequence(maintest);
	            one(cmService).getCourseOffering("CS"); will(returnValue(co)); inSequence(maintest);
	            one(co).setTitle("Finite Mathematics"); inSequence(maintest);
	            one(co).setDescription("Finite Mathematics"); inSequence(maintest);
	            one(co).setStatus(""); inSequence(maintest);
	            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	            one(co).setStartDate(df.parse("2007-09-06")); inSequence(maintest);
	            one(co).setEndDate(df.parse("2007-11-14")); inSequence(maintest);
	            exactly(2).of(co).getAcademicSession(); will(returnValue(as)); inSequence(maintest);
	            one(as).getEid(); will(returnValue("0592"));inSequence(maintest);
	            one(cmAdmin).updateCourseOffering(co); inSequence(maintest);
	            // log out of Sakai
				one(sessionManager).getCurrentSession(); inSequence(maintest);
				ignoring(eventTrackingService);
	        }});
	
		} catch (ParseException pe) {
			fail("Invalid date value" + pe.getMessage());
		}
		delegator.handleXmlContent(OFFERING_XML_STRING);
		context.assertIsSatisfied();
	}
	
//	public void testHandleSimple() {
//		final ImsContentDelegator delegator = (ImsContentDelegator) appContext.getBean("net.unicon.sakora.api.ims.ImsContentDelegator");
//		final ImsEntityCreator creator = context.mock(ImsEntityCreator.class);
//		context.checking(new Expectations() {{
//			one(creator).canHandle(SECTION_XML_STRING);
//			one(creator).handleXml(SECTION_XML_STRING);
//        }});
//		delegator.handleXmlContent(SECTION_XML_STRING);
//		context.assertIsSatisfied();
//	}
//	

	public void testHandleCourseSection() {
		ImsContentDelegator delegator = (ImsContentDelegator) appContext.getBean("net.unicon.sakora.api.ims.ImsContentDelegator");
		ImsCreateCourseSection creator = (ImsCreateCourseSection)appContext.getBean("net.unicon.sakora.impl.ims.ImsCreateCourseSection");
		creator.setCmService(cmService);
		creator.setCmAdmin(cmAdmin);
		creator.setSessionManager(sessionManager);
		creator.setUsageSessionService(usageSessionService);
		creator.setAuthzGroupService(authzGroupService);
		creator.setEventTrackingService(eventTrackingService);
		creator.init();
		

		final Sequence maintest = context.sequence("maintest");
		final Section sec = context.mock(Section.class);
		final Meeting mockMeeting = context.mock(Meeting.class);
		context.checking(new Expectations() {{
			// log in to Sakai
			one(sessionManager).getCurrentSession(); inSequence(maintest);
			one(usageSessionService).startSession("admin", "127.0.0.1", "SakoraLoader");  inSequence(maintest);
			one(authzGroupService).refreshUser("admin"); inSequence(maintest);
			// process course set info
			one(cmService).getSectionCategoryDescription("LEC");will(returnValue(null));inSequence(maintest);
			one(cmAdmin).addSectionCategory("LEC", "LEC"); inSequence(maintest);		
			one(cmService).isSectionDefined("001234-1-0590-1-1"); will(returnValue(true)); inSequence(maintest);
            one(cmService).getSection("001234-1-0590-1-1"); will(returnValue(sec)); inSequence(maintest);
            one(sec).setTitle("Introduction to Anthropology (LEC)"); inSequence(maintest);
            one(sec).setDescription("001234-1-0590-1-1"); inSequence(maintest);
            one(sec).setCategory("LEC"); inSequence(maintest);
            one(sec).getMeetings(); will(returnValue(null)); inSequence(maintest);
            one(cmAdmin).newSectionMeeting("001234-1-0590-1-1", "PSCSHCDA", Time.valueOf("10:00:00"),Time.valueOf("10:50:00"), null);
            	will(returnValue(mockMeeting));
            	inSequence(maintest);
            
            one(mockMeeting).setMonday(true);
            one(mockMeeting).setWednesday(true);
            one(mockMeeting).setFriday(true);
            Set<Meeting> mtgSet = new HashSet<Meeting>();
            mtgSet.add(mockMeeting);
            one(sec).setMeetings(mtgSet);
            one(cmAdmin).updateSection(sec); inSequence(maintest);
            // log out of Sakai
			one(sessionManager).getCurrentSession(); inSequence(maintest);
			ignoring(eventTrackingService);
        }});
	
		delegator.handleXmlContent(SECTION_XML_STRING);
		context.assertIsSatisfied();
	}
	
	public void testHandlePerson() {
		ImsContentDelegator delegator = (ImsContentDelegator) appContext.getBean("net.unicon.sakora.api.ims.ImsContentDelegator");
		ImsCreatePerson creator = (ImsCreatePerson)appContext.getBean("net.unicon.sakora.impl.ims.ImsCreatePerson");
		creator.setCmService(cmService);
		creator.setCmAdmin(cmAdmin);
		creator.setSessionManager(sessionManager);
		creator.setUsageSessionService(usageSessionService);
		creator.setAuthzGroupService(authzGroupService);
		creator.setUserDirService(userDirService);
		creator.setEventTrackingService(eventTrackingService);
		creator.init();

		final Sequence maintest = context.sequence("maintest");
		final UserEdit ue = context.mock(UserEdit.class);
		try {
			context.checking(new Expectations() {{
				// log in to Sakai
				one(sessionManager).getCurrentSession(); inSequence(maintest);
				one(usageSessionService).startSession("admin", "127.0.0.1", "SakoraLoader");  inSequence(maintest);
				one(authzGroupService).refreshUser("admin"); inSequence(maintest);
				// process course set info
				one(userDirService).getUserId("0012"); will(returnValue("0012")); inSequence(maintest);
				one(userDirService).editUser("0012"); will(returnValue(ue)); inSequence(maintest);
				one(ue).setFirstName("Syndi B.");inSequence(maintest);
				one(ue).setLastName("Forbes");inSequence(maintest);
				one(ue).setEmail("");inSequence(maintest);
				one(userDirService).commitEdit(ue);
	            // log out of Sakai
				one(sessionManager).getCurrentSession(); inSequence(maintest);
				ignoring(eventTrackingService);
	        }});
		
			delegator.handleXmlContent(PERSON_XML_STRING);
			context.assertIsSatisfied();
		} catch (Exception ex) {
			fail("Test throw exception " + ex.getMessage());
		}
	}
	
	public void testHandleGroup() {
		ImsContentDelegator delegator = (ImsContentDelegator) appContext.getBean("net.unicon.sakora.api.ims.ImsContentDelegator");
		ImsCreateGroup creator = (ImsCreateGroup)appContext.getBean("net.unicon.sakora.impl.ims.ImsCreateGroup");
		creator.setCmService(cmService);
		creator.setCmAdmin(cmAdmin);
		creator.setSessionManager(sessionManager);
		creator.setUsageSessionService(usageSessionService);
		creator.setAuthzGroupService(authzGroupService);
		creator.setUserDirService(userDirService);
		creator.setEventTrackingService(eventTrackingService);
		creator.init();

		final Sequence maintest = context.sequence("maintest");
		final CourseSet cs = context.mock(CourseSet.class);
		context.checking(new Expectations() {{
			// log in to Sakai
			one(sessionManager).getCurrentSession(); inSequence(maintest);
			one(usageSessionService).startSession("admin", "127.0.0.1", "SakoraLoader");  inSequence(maintest);
			one(authzGroupService).refreshUser("admin"); inSequence(maintest);
			// process course set info
			one(cmService).isCourseSetDefined("GLAKE"); will(returnValue(true)); inSequence(maintest);
			one(cmService).getCourseSet("GLAKE"); will(returnValue(cs)); inSequence(maintest);
			one(cs).setTitle("Glake");  inSequence(maintest);
			one(cs).setDescription("Great Lakes University");  inSequence(maintest);
			one(cs).setCategory("INSTITUTE");  inSequence(maintest);
			one(cmAdmin).updateCourseSet(cs); inSequence(maintest);
            // log out of Sakai
			one(sessionManager).getCurrentSession(); inSequence(maintest);
			ignoring(eventTrackingService);
        }});
	
		delegator.handleXmlContent(DEPT_XML_STRING);
		context.assertIsSatisfied();
	}
	
	public void testHandleTerm() {
		ImsContentDelegator delegator = (ImsContentDelegator) appContext.getBean("net.unicon.sakora.api.ims.ImsContentDelegator");
		ImsCreateGroup creator = (ImsCreateGroup)appContext.getBean("net.unicon.sakora.impl.ims.ImsCreateGroup");
		creator.setCmService(cmService);
		creator.setCmAdmin(cmAdmin);
		creator.setSessionManager(sessionManager);
		creator.setUsageSessionService(usageSessionService);
		creator.setAuthzGroupService(authzGroupService);
		creator.setUserDirService(userDirService);
		creator.setEventTrackingService(eventTrackingService);
		creator.init();

		final Sequence maintest = context.sequence("maintest");
		final AcademicSession sess = context.mock(AcademicSession.class);
		try {
			context.checking(new Expectations() {{
				// log in to Sakai
				one(sessionManager).getCurrentSession(); inSequence(maintest);
				one(usageSessionService).startSession("admin", "127.0.0.1", "SakoraLoader");  inSequence(maintest);
				one(authzGroupService).refreshUser("admin"); inSequence(maintest);
				// process course set info
				one(cmService).isAcademicSessionDefined("PSUNV-UGRD-0595"); will(returnValue(true)); inSequence(maintest);
				one(cmService).getAcademicSession("PSUNV-UGRD-0595"); will(returnValue(sess)); inSequence(maintest);
				one(sess).setTitle("2008 WtQt"); inSequence(maintest);
				one(sess).setDescription(with(any(String.class))); inSequence(maintest);
	            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				one(sess).setStartDate(df.parse("2008-01-03")); inSequence(maintest);
				one(sess).setEndDate(null); inSequence(maintest);
				one(cmAdmin).updateAcademicSession(sess);  inSequence(maintest);
	            // log out of Sakai
				one(sessionManager).getCurrentSession();  inSequence(maintest);
				ignoring(eventTrackingService);
	        }});
		
			delegator.handleXmlContent(TERM_XML_STRING);
			context.assertIsSatisfied();
		} catch (ParseException pe) {
			fail("Invalid date value" + pe.getMessage());
		}
	}
	
	@Override
	protected void tearDown() throws Exception {
		// nothing to do yet
	}
}
