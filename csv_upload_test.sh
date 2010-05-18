# Licensed to the Sakai Foundation under one or more contributor
# license agreements. See the NOTICE file distributed with this work
# for additional information regarding copyright ownership.  The Sakai
# Foundation licenses this file to you under the Apache License,
# Version 2.0 (the "License"); you may not use this file except
# in compliance with the License. You may obtain a copy of the
# License at:
# 
# http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on
# an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied. See the License for the
# specific language governing permissions and limitations
# under the License.

#
# This is an example of the work flow needed to update your SIS info via the CSV Sakora service.
#
# This could be put on a cron job or triggered based on some external event. Depending on the size of your
# data this could present your Sakai cluster with a significant load, so you might want to primarily
# run this at non peak usage times.
#
#

#
# Here is where you could cause some action that generates your updated csv exports of SIS data.
# 
# For this example I'm just using the included unit test testing data.
#



#
# Calling the service is rather straight forward, the only non obvious bit is that you need to
# pass the username and password parameters before the csv files because the files will be streamed
# up to the server and we don't want to waste system resources on potentially unauthorized requests.
#
# The optional parameter 'runJob' tells the service if it should cause the csv files to be processed
# after the upload is finished, if you do not pass this you will need to trigger the sync job
# in some other way, most likely as a scheduled Quartz job.
#
# Also worth reminding, you NEVER want to call this service or any other action that passes passwords
# with out using https.
#

curl -F "username=admin" -F "password=admin" \
 -F "sessions=@sakora-impl/impl/resources/test/webapp/data/sessions.csv" \
 -F "courseSections=@sakora-impl/impl/resources/test/webapp/data/courseSections.csv" \
 -F "courseMembership=@sakora-impl/impl/resources/test/webapp/data/courseMembership.csv" \
 -F "courseOfferings=@sakora-impl/impl/resources/test/webapp/data/courseOfferings.csv" \
 -F "courses=@sakora-impl/impl/resources/test/webapp/data/courses.csv" \
 -F "courseSets=@sakora-impl/impl/resources/test/webapp/data/courseSets.csv" \
 -F "enrollmentSets=@sakora-impl/impl/resources/test/webapp/data/enrollmentSets.csv" \
 -F "meetings=@sakora-impl/impl/resources/test/webapp/data/meetings.csv" \
 -F "people=@sakora-impl/impl/resources/test/webapp/data/people.csv" \
 -F "sectionMembership=@sakora-impl/impl/resources/test/webapp/data/sectionMembership.csv" \
 -F "runJob=true" \
 http://localhost:8080/sakai-sakora-tool/csv


