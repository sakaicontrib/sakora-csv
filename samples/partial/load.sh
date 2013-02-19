#!/bin/bash
# Run this script from this directory after Sakai is already running
# Load up a partial set of data (a subset of the multiple set)
curl -F "username=admin" -F "password=admin" \
 -F "runJob=true" \
 -F "sessions=@sessions.csv" \
 -F "courseSets=@courseSets.csv" \
 -F "courses=@courses.csv" \
 -F "courseOfferings=@courseOfferings.csv" \
 -F "courseSections=@courseSections.csv" \
 -F "people=@people.csv" \
 -F "courseMembership=@courseMembership.csv" \
 -F "sectionMembership=@sectionMembership.csv" \
 http://localhost:8080/sakai-sakora-csv-tool/csv
