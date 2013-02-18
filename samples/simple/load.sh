#!/bin/bash
# Load up a simple set of data (1 session, 1 course, 1 instructor, 1 student, 1 section)
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
