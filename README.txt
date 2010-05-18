Sakora Configuration
====================

Configuration for the Endpoints
-------------------------------

1. Configuration SAIP endpoint
   The SAIP endpoint can be configured in the properties file (preferred) or
   in the components.xml file of the Packaging module.

   saipEndpoint@net.unicon.sakora.api.ws.ImsEsSyncService=uri.to.webservice.endpoint

2. Data Retrieval of Full and Incremental Snapshots
   In general the webservice will return a full URI including the server, 
   userid and password to retrieve the document. In the case that the server
   will only return a file name, or return a URL without the credentials, 
   these missing values can be configured using the following properties.

   serverUrl@net.unicon.sakora.impl.handler.url.URLHandlingTemplateImpl=server.url.to.retrieve.file
   userId@net.unicon.sakora.impl.handler.url.URLHandlingTemplateImpl=userid
   password@net.unicon.sakora.impl.handler.url.URLHandlingTemplateImpl=password


