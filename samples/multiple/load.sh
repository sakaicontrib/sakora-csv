#!/bin/bash
# Run this script from this directory after Sakai is already running
# Load up a larger set of data (3 sessions, 5 courses, 6 instructors, 20 students, 10 sections)
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
