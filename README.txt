See http://confluence.sakaiproject.org/display/IMSES/Simple+Sakai+SIS+Services for detailed configuration information.

This project was previously embedded in https://source.sakaiproject.org/contrib/unicon/sakora.

Configuration options (others documented on the confluence page):
# Sakora will attempt to create the directory at the specified path if it does not exist. A warning will be logged if the current process cannot find/create the director or does not have read/write permissions in it.
# Default: $CATALINA_HOME/sakai/sakora-csv/
#net.unicon.sakora.csv.batchUploadDir=/full/path/to/sakora-csv/
# Maximum size, in MB (so 1000 is 1 GB), of POST requests to https://$SAKAI_DOMAIN/sakai-sakora-csv-tool/csv
#net.unicon.sakora.csv.maxFileSize=1000
# Configure Sakora to ignore all data for academic sessions which are not included in the sessions.csv file
# If true, this basically means that older sessions will not cause the removal or updates or data related to those sessions,
# when false (legacy), all courses, users, and memberships related to sessions not included is removed or disabled
# Default: false (matches legacy behavior)
#net.unicon.sakora.csv.ignoreMissingSessions=true
