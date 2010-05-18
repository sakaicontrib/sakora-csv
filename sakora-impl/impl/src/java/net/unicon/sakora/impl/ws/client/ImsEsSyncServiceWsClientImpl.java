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
package net.unicon.sakora.impl.ws.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.xml.transform.Source;

import net.unicon.sakora.api.handler.url.URLHandlingTemplate;
import net.unicon.sakora.api.handler.xml.XMLStreamHandlingCallback;
import net.unicon.sakora.api.ws.ImsEsSyncService;
import net.unicon.sakora.api.ws.ImsSyncContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;
import org.jdom.xpath.XPath;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;

public class ImsEsSyncServiceWsClientImpl implements ImsEsSyncService {
	private static final Log log = LogFactory.getLog(ImsEsSyncServiceWsClientImpl.class);
	private static final String DEFAILT_SAIP_ENDPOINT = "http://localhost:8888/services/";
	private static final String DEFAULT_SAVE_POINT = "2001-01-01";
	/** 
	 * Keys to the requestDocumentMap.
	 */		
	private final static String READ_COURSE_TEMPLATES_KEY = "readCourseTemplates";
	private final static String READ_COURSE_TEMPLATES_FROM_SAVE_POINT_KEY = "readCourseTemplatesFromSavePoint";
	private final static String READ_COURSE_OFFERINGS_KEY = "readCourseOfferings";
	private final static String READ_COURSE_OFFERINGS_FROM_SAVE_POINT_KEY = "readCourseOfferingsFromSavePoint";
	private final static String READ_COURSE_SECTIONS_KEY = "readCourseSections";
	private final static String READ_COURSE_SECTIONS_FROM_SAVE_POINT_KEY = "readCourseSectionsFromSavePoint";
	private final static String READ_GROUPS_KEY = "readGroups";
	private final static String READ_GROUPS_FROM_SAVE_POINT_KEY = "readGroupsFromSavePoint";
	private final static String READ_MEMBERSHIPS_KEY = "readMemberships";
	private final static String READ_MEMBERSHIPS_FROM_SAVE_POINT_KEY = "readMembershipsFromSavePoint";
	private final static String READ_PERSONS_KEY = "readPersons";
	private final static String READ_PERSONS_FROM_SAVE_POINT_KEY = "readPersonsFromSavePoint";
	private final static String READ_SECTION_ASSOCIATIONS_KEY = "readSectionAssociations";
	private final static String READ_SECTION_ASSOCIATIONS_FROM_SAVE_POINT_KEY = "readSectionAssociationsFromSavePoint";
	
	/** 
	 * Keys to the document XPATH and namespace values
	 */		
	private final static String DATAFILE_XPATH_KEY = "dataFile_xpath";
	private final static String SAVEPOINT_XPATH_KEY = "savepoint_xpath";
	private final static String FROM_SAVEPOINT_XPATH_KEY= "from_savepoint";
//	private final static String NAMESPACE_KEY = "namespace";
//	private final static String PREFIX_KEY = "prefix";

	/** 
	 * The spring web service template that is used to make web service calls.
	 */		
	private WebServiceTemplate webServiceTemplate;

	/** 
	 * This is a spring injected map of the class path locations to the initial XML request documents.
	 */		
	private Map<String,String> requestDocumentMap;

	/** 
	 * This is a spring injected map of the class path locations to the initial XML request documents.
	 */		
	private Map<String,String> documentInfoMap;
	
	/** 
	 * This is a spring injected map of request keys to SOAPAction header values.
	 */
	private Map<String, String> soapActionMap;

	/** 
	 * The end point location for the web service. Value set by spring.
	 */		
	private String saipEndpoint;

	/** 
	 * When a response is received from the web service call it contains a URL to an XML doucment with the information we seek.
	 * This class will handle the URL and retrieve the documment and send it onto the XMLStreamHandlingCallback for processing.
	 */		
	private URLHandlingTemplate urlHandlingTemplate;	

	/** 
	 * Processes the XML stream retrieved by URLHandlingTemplate.
	 */		
	private XMLStreamHandlingCallback xmlStreamHandlingCallback;
	

	public void init() {
		if (saipEndpoint == null) {
			setSaipEndpoint(DEFAILT_SAIP_ENDPOINT);
		}
	}
	/** 
	 * Invoke the web service call based on the request document and return the response document.
	 * 
	 * @param requestDocument - request document to be sent
	 * @param requestKey - identifies which request is called.
	 * @return received document as {@link org.jdom.Document}
	 */		
	private Document invokeWebServiceTemplate(Document requestDocument, String requestKey) {
		Source source = new JDOMSource(requestDocument);
		JDOMResult result = new JDOMResult();
		webServiceTemplate.sendSourceAndReceiveToResult(saipEndpoint, source, 
				newWebServiceMessageCallback(requestKey), result);
		return result.getDocument();
	}
	
	/**
	 * Define a new instance of the callback passed to 
	 * {@link WebServiceTemplate#sendSourceAndReceiveToResult(String, Source, WebServiceMessageCallback, javax.xml.transform.Result)}
	 * by a private dispatch mechanism invoked by <code>read*</code> methods. The API
	 * assumes a new callback will be required on each invocation because its behavior
	 * is typically affected by the details of the current request. This particular
	 * implementation returns a {@link SoapActionCallback} configured with a value
	 * derived from the given <code>requestKey</code>
	 * 
	 * @param requestKey identifies which request is called. In this implementation, is used
	 *   to calculate the proper SOAPAction
	 * @return
	 */
	protected WebServiceMessageCallback newWebServiceMessageCallback(String requestKey) {
		return new SoapActionCallback(getSoapActionForRequestKey(requestKey));
	}
	
	/**
	 * Resolve the given request key to a SOAPAction. Request key has the same
	 * semantics as the argument of the same name passed to
	 * {@link #retrieveAndProcessData(String, ImsSyncContext)} and 
	 * {@link #invokeWebServiceTemplate(Document, String)}
	 * 
	 * @param requestKey identifies which request is called.
	 * @return a SOAPAction string. Not validated as a URI. <code>null</code>
	 *   converted to ""
	 */
	private String getSoapActionForRequestKey(String requestKey) {
		String soapAction = soapActionMap.get(requestKey);
		return soapAction == null ? "" : soapAction;
	}
	
	
	/**
	 * In the first the the request is created and populated with the request values.
	 * The request is then sent to the web service endpoint and the returned document is handed
	 * to the urlHandlingTemplate for further processing.
	 *  
	 * @param requestKey - identifies which request is called
	 * @param syncContext - request specific information
	 */
	private void retrieveAndProcessData(String requestKey, ImsSyncContext syncContext) {
		InputStream resourceStream = null;
		try {
			resourceStream = loadClassPathResource(requestDocumentMap.get(requestKey));			
	
			if (resourceStream != null) {
				
				// read the document and replace set the update fromSavePoint if such and element exists.
				Document source = new SAXBuilder().build(resourceStream);
				XPath fromSavePointPath  = XPath.newInstance(getDocumentInfoMap().get(FROM_SAVEPOINT_XPATH_KEY));
				Element fromSavePoint = (Element)fromSavePointPath.selectSingleNode(source);
				if (fromSavePoint != null) {
					String lastSavePoint = syncContext.getLastToken();
					fromSavePoint.setText(lastSavePoint == null ? DEFAULT_SAVE_POINT : lastSavePoint);
				}	
				
				if (log.isDebugEnabled()) {
					log.debug("Calling webservice to retrieve file URL @ " + saipEndpoint);
					if (fromSavePoint != null) {
						log.debug("Incremental call for save point" + fromSavePoint.getText() );
					} else { 
						log.debug("Snapshot call, no save point");
					}
				}
				
				Document document = invokeWebServiceTemplate(source, requestKey);
				if (document != null) {
					//This code will change when we get real Oracle WSDL/XSD
					XPath datafilePath = XPath.newInstance(getDocumentInfoMap().get(DATAFILE_XPATH_KEY));
					String fileUrl = datafilePath.valueOf(document);
					if (log.isDebugEnabled()) {
						log.debug("Loading file from URL: " + fileUrl);
					}
					
					XPath savePointPath  = XPath.newInstance(getDocumentInfoMap().get(SAVEPOINT_XPATH_KEY));
					String savePoint = savePointPath.valueOf(document);
					
					if (log.isDebugEnabled()) {
						if (savePoint == null || savePoint.trim().length() == 0) {
							log.debug("save point: No Save Point");
						} else {
							log.debug("save point: " + savePoint);							
						}
					}
					
					if (fileUrl == null || fileUrl.trim().length() == 0) {
						// we did not receive a snapshot file
						log.info("File URL has not been sent.");
					} else {
						//In the future we would extract the url and send the url onto our handler
						//Depending on the feedstype we would also extract the save point and assign it to the ImsSyncContext passed in
						urlHandlingTemplate.handleURL(fileUrl, xmlStreamHandlingCallback);
						
						// setting the new save point, so that the quartz job can persist it.
						if (savePoint != null && savePoint.trim().length() != 0) {
							syncContext.setNewToken(savePoint);
						}
	
						if (log.isDebugEnabled()) {
							log.debug("File " + fileUrl + " has been loaded.");
						}
					}
				} else{
					log.error("Unable to parse null Document object.");
				}
			} else {
				log.error("Unable to read class path resource with key: " + requestDocumentMap.get(requestKey));
			}			
		} catch (IOException ioe) {
			log.error("Unable to build Document object from resourcestream.", ioe);
		} catch (JDOMException jde) {
			log.error("Unable to apply Xpath expressions to Document object.", jde);
		} finally {
			if (resourceStream != null) {
				try {resourceStream.close();} catch (IOException ignore) {};
			}
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.unicon.sakora.api.ws.ImsEsSyncService#readCourseTemplates(net.unicon.sakora.api.ws.ImsSyncContext)
	 */
	public void readCourseTemplates(ImsSyncContext syncContext) {
		String requestKey = null;
		if (syncContext.getFeedType() == ImsSyncContext.FeedType.SNAPSHOT) {
			requestKey = READ_COURSE_TEMPLATES_KEY;
		} else if (syncContext.getFeedType() == ImsSyncContext.FeedType.INCREMENTAL) {
			requestKey = READ_COURSE_TEMPLATES_FROM_SAVE_POINT_KEY;
		}

		retrieveAndProcessData(requestKey, syncContext);
	}

	/*
	 * (non-Javadoc)
	 * @see net.unicon.sakora.api.ws.ImsEsSyncService#readCourseOfferings(net.unicon.sakora.api.ws.ImsSyncContext)
	 */
	public void readCourseOfferings(ImsSyncContext syncContext) {
		String requestKey = null;
		if (syncContext.getFeedType() == ImsSyncContext.FeedType.SNAPSHOT) {
			requestKey = READ_COURSE_OFFERINGS_KEY;
		} else if (syncContext.getFeedType() == ImsSyncContext.FeedType.INCREMENTAL) {
			requestKey = READ_COURSE_OFFERINGS_FROM_SAVE_POINT_KEY;
		}
		retrieveAndProcessData(requestKey, syncContext);
	}

	/*
	 * (non-Javadoc)
	 * @see net.unicon.sakora.api.ws.ImsEsSyncService#readCourseSections(net.unicon.sakora.api.ws.ImsSyncContext)
	 */
	public void readCourseSections(ImsSyncContext syncContext) {
		String requestKey = null;
		if (syncContext.getFeedType() == ImsSyncContext.FeedType.SNAPSHOT) {
			requestKey = READ_COURSE_SECTIONS_KEY;
		} else if (syncContext.getFeedType() == ImsSyncContext.FeedType.INCREMENTAL) {
			requestKey = READ_COURSE_SECTIONS_FROM_SAVE_POINT_KEY;
		}
		retrieveAndProcessData(requestKey, syncContext);
	}

	/*
	 * (non-Javadoc)
	 * @see net.unicon.sakora.api.ws.ImsEsSyncService#readGroups(net.unicon.sakora.api.ws.ImsSyncContext)
	 */
	public void readGroups(ImsSyncContext syncContext) {
		String requestKey = null;
		if (syncContext.getFeedType() == ImsSyncContext.FeedType.SNAPSHOT) {
			requestKey = READ_GROUPS_KEY;
		} else if (syncContext.getFeedType() == ImsSyncContext.FeedType.INCREMENTAL) {
			requestKey = READ_GROUPS_FROM_SAVE_POINT_KEY;
		}
		retrieveAndProcessData(requestKey, syncContext);
	}

	/*
	 * (non-Javadoc)
	 * @see net.unicon.sakora.api.ws.ImsEsSyncService#readMemberships(net.unicon.sakora.api.ws.ImsSyncContext)
	 */
	public void readMemberships(ImsSyncContext syncContext) {
		String requestKey = null;
		if (syncContext.getFeedType() == ImsSyncContext.FeedType.SNAPSHOT) {
			requestKey = READ_MEMBERSHIPS_KEY;
		} else if (syncContext.getFeedType() == ImsSyncContext.FeedType.INCREMENTAL) {
			requestKey = READ_MEMBERSHIPS_FROM_SAVE_POINT_KEY;
		}
		retrieveAndProcessData(requestKey, syncContext);
	}

	/*
	 * (non-Javadoc)
	 * @see net.unicon.sakora.api.ws.ImsEsSyncService#readPersons(net.unicon.sakora.api.ws.ImsSyncContext)
	 */
	public void readPersons(ImsSyncContext syncContext) {
		String requestKey = null;
		if (syncContext.getFeedType() == ImsSyncContext.FeedType.SNAPSHOT) {
			requestKey = READ_PERSONS_KEY;
		} else if (syncContext.getFeedType() == ImsSyncContext.FeedType.INCREMENTAL) {
			requestKey = READ_PERSONS_FROM_SAVE_POINT_KEY;
		}
		retrieveAndProcessData(requestKey, syncContext);		
	}

	/*
	 * (non-Javadoc)
	 * @see net.unicon.sakora.api.ws.ImsEsSyncService#readSectionAssociations(net.unicon.sakora.api.ws.ImsSyncContext)
	 */
	public void readSectionAssociations(ImsSyncContext syncContext) {
		String requestKey = null;
		if (syncContext.getFeedType() == ImsSyncContext.FeedType.SNAPSHOT) {
			requestKey = READ_SECTION_ASSOCIATIONS_KEY;
		} else if (syncContext.getFeedType() == ImsSyncContext.FeedType.INCREMENTAL) {
			requestKey = READ_SECTION_ASSOCIATIONS_FROM_SAVE_POINT_KEY;
		}
		retrieveAndProcessData(requestKey, syncContext);
	}
	

	/**
	 * Read in initial xml request documents to be sent to the web service. 
	 * These documents are stored on the class path.
	 */	
	private InputStream loadClassPathResource(String resourcePath) {		
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(resourcePath);
		if (is != null) {
			if (log.isDebugEnabled() && false) {	// I think that debug stuff is really useless and should be removed. Disabled it for now.
				// for debug purposes we log the content of the stream, close it and reopen it. 
				StringBuffer buff = new StringBuffer();
				byte [] buffer = new byte[512];
				try {
					while (is.read(buffer) != -1) {
						buff.append(new String(buffer)+"\n");						
					}
				} catch (IOException ioe) {
					log.debug("Error logging stream content for debug purposes");
				} finally {
					try {is.close();}  catch (IOException ioe) {}
				}
				log.debug("Resource value:");
				log.debug(buff.toString());
				//read the resource in again so it can be returned
				is = this.getClass().getClassLoader().getResourceAsStream(resourcePath);
			}
		}			
		return is;
	}
	
	public Map<String,String> getRequestDocumentMap() {
		return requestDocumentMap;
	}

	public void setRequestDocumentMap(Map<String, String> requestDocumentMap) {
		this.requestDocumentMap = requestDocumentMap;
	}

	public Map<String,String> getDocumentInfoMap() {
		return documentInfoMap;
	}

	public void setDocumentInfoMap(Map<String, String> documentInfoMap) {
		this.documentInfoMap = documentInfoMap;
	}
	
	public Map<String, String> getSoapActionMap() {
		return soapActionMap;
	}
	
	public void setSoapActionMap(Map<String, String> soapActionMap) {
		this.soapActionMap = soapActionMap;
	}

	public String getSaipEndpoint() {
		return saipEndpoint;
	}

	public void setSaipEndpoint(String saipEndpoint) {
		this.saipEndpoint = saipEndpoint;
	}
	
	public URLHandlingTemplate getUrlHandlingTemplate() {
		return urlHandlingTemplate;
	}

	public void setUrlHandlingTemplate(URLHandlingTemplate urlHandlingTemplate) {
		this.urlHandlingTemplate = urlHandlingTemplate;
	}

	public XMLStreamHandlingCallback getXmlStreamHandlingCallback() {
		return xmlStreamHandlingCallback;
	}

	public void setXmlStreamHandlingCallback(
			XMLStreamHandlingCallback xmlStreamHandlingCallback) {
		this.xmlStreamHandlingCallback = xmlStreamHandlingCallback;
	}	
	
	public void setWebServiceTemplate(WebServiceTemplate webServiceTemplate) {
		this.webServiceTemplate = webServiceTemplate;
	}

}

