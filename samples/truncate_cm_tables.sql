-- clears out all the CM data from the default set of CM tables AND sakora data from the sakora tables
-- WARNING: DO NOT run this on a production system database or it will purge out all CM and Sakora data and history
-- The foreign_key_checks part is mysql specific but required since there are circular refs between cm_enrollment_set_t and cm_member_container_t
SET foreign_key_checks = 0;
TRUNCATE sakora_membership;
TRUNCATE sakora_person;
TRUNCATE sakora_session;
TRUNCATE sakora_log;
TRUNCATE cm_meeting_t;
TRUNCATE cm_membership_t;
TRUNCATE cm_course_set_canon_assoc_t;
TRUNCATE cm_course_set_offering_assoc_t;
TRUNCATE cm_enrollment_t;
TRUNCATE cm_official_instructors_t;
TRUNCATE cm_enrollment_set_t;
TRUNCATE cm_member_container_t;
TRUNCATE cm_cross_listing_t;
TRUNCATE cm_academic_session_t;
SET foreign_key_checks = 1;