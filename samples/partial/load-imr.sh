#!/bin/bash
# Run this script from this directory after Sakai is already running
# Should have already loaded the 'multiple' data set previously
# Load up a partial data set with ignoreMembershipRemovals enabled
curl -F "username=admin" -F "password=admin" \
 -F "runJob=true" -F "ignoreMembershipRemovals=true" \
 -F "sessions=@sessions.csv" \
 -F "courseSets=@courseSets.csv" \
 -F "courses=@courses.csv" \
 -F "courseOfferings=@courseOfferings.csv" \
 -F "courseSections=@courseSections.csv" \
 -F "people=@people.csv" \
 -F "courseMembership=@courseMembership.csv" \
 -F "sectionMembership=@sectionMembership.csv" \
 http://localhost:8080/sakai-sakora-csv-tool/csv
