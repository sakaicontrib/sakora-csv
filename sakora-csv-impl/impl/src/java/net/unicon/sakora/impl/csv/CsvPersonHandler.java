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
package net.unicon.sakora.impl.csv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.unicon.sakora.api.csv.CsvSyncContext;
import net.unicon.sakora.api.csv.model.Person;
import net.unicon.sakora.api.csv.model.SakoraLog;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.genericdao.api.search.Restriction;
import org.sakaiproject.genericdao.api.search.Search;
import org.sakaiproject.id.api.IdManager;
import org.sakaiproject.user.api.UserAlreadyDefinedException;
import org.sakaiproject.user.api.UserEdit;
import org.sakaiproject.user.api.UserIdInvalidException;
import org.sakaiproject.user.api.UserLockedException;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.api.UserPermissionException;

/**
 * Reads in Person definitions from csv extracts, expect format is:
 * Eid, Last Name, First Name, Email, Password, Type[, optional]
 * 
 * <p>Optional fields are written in to user properties using a
 * configurable list of property names. Optional fields without
 * a configured property name are discarded.</p>
 * 
 * <p>The optional field name {@link #ID_FIELD_NAME} is a special case. 
 * When an optional field appears in an position given that name and 
 * the user does not already exist in the Sakai directory, the user 
 * will be created using that value as its primary key. If the user 
 * already exists, that field is ignored.</p>
 * 
 * @author Joshua Ryan
 *
 */
@SuppressWarnings("serial")
public class CsvPersonHandler extends CsvHandlerBase {
	static final Log log = LogFactory.getLog(CsvPersonHandler.class);

	private static final String ID_FIELD_NAME = "id";

	private IdManager idManager;
	private List<String> optionalFieldNames = new ArrayList<String>() {{
		add(ID_FIELD_NAME);
	}};
	private String suspended = "suspended";

	public CsvPersonHandler() {
	}

    @Override
    public String getName() {
        return "Person";
    }

	@Override
	protected void readInputLine(CsvSyncContext context, String[] line) {

		final int minFieldCount = 6;

		if (line != null && line.length >= minFieldCount) {
			line = trimAll(line);

			// for clarity
			String eid = line[0];
			String lastName = line[1];
			String firstName = line[2];
			String email = line[3];
			String pw = line[4];
			String type = line[5];
			Map<String,String> optionalFields = getOptionalFields(line, 6);

			String existingId = null;
			String newId = null;

			// why doesn't UserDirectoryService have a userExists type method?
			try {
				existingId = userDirService.getUserId(eid);
			} catch (UserNotDefinedException unde) {
			    // empty on purpose
			}

			try {
				UserEdit edit = null;

				boolean changed = false;
				if (existingId == null || "".equals(existingId)) {
					if ( optionalFields.containsKey(ID_FIELD_NAME) ) {
						newId = optionalFields.get(ID_FIELD_NAME);
					}
					if ( newId == null || "".equals(newId) ) {
						newId = idManager.createUuid();
					}
					edit = userDirService.addUser(newId, eid);
					newId = edit.getId();
					edit.setFirstName(firstName);
					edit.setLastName(lastName);
					edit.setEmail(email);
					edit.setPassword(pw);
					edit.setType(type);
					changed = true;
				} else {
					edit = userDirService.editUser(existingId);
					// check if the user has changed, only update if the record has changed
					if (!StringUtils.equals(firstName, edit.getFirstName())) {
					    edit.setFirstName(firstName);
					    changed = true;
					}
					if (!StringUtils.equals(lastName, edit.getLastName())) {
					    edit.setLastName(lastName);
					    changed = true;
					}
					if (!StringUtils.equals(email, edit.getEmail())) {
					    edit.setEmail(email);
					    changed = true;
					}
					if (!edit.checkPassword(pw)) {
					    edit.setPassword(pw);
					    changed = true;
					}
					if (!StringUtils.equals(type, edit.getFirstName())) {
					    edit.setType(type);
					    changed = true;
					}
				}

				if ( !(optionalFields.isEmpty()) ) {
					log.debug("Processing optional fields for user with eid [" + eid  + "]: " + optionalFields);
					for ( String fieldName: optionalFields.keySet() ) {
						if ( ID_FIELD_NAME.equals(fieldName) ) {
							continue;
						}
						String fieldValue = optionalFields.get(fieldName);
						if ( fieldValue == null || "".equals(fieldValue) ) {
						    if (edit.getPropertiesEdit().getProperty(fieldName) != null) {
						        edit.getPropertiesEdit().removeProperty(fieldName);
						        changed = true;
						    }
						} else {
						    String currentVal = edit.getPropertiesEdit().getProperty(fieldName);
						    if (currentVal == null || fieldValue.equals(currentVal)) {
						        edit.getPropertiesEdit().addProperty(fieldName, fieldValue);
						        changed = true;
						    }
						}
					}
				}

				if (changed) {
				    userDirService.commitEdit(edit);
				    if (existingId == null) {
				        adds++;
				    } else {
				        updates++;
				    }
				}
			}
			catch(UserIdInvalidException uiie) {
				dao.create(new SakoraLog(this.getClass().toString(), uiie.getLocalizedMessage()));
				log.error("CsvPersonHandler: " + uiie.getMessage());
				errors++;
			}
			catch(UserNotDefinedException unde) {
				dao.create(new SakoraLog(this.getClass().toString(), unde.getLocalizedMessage()));
				log.error("CsvPersonHandler: " + unde.getMessage());
				errors++;
			}
			catch(UserAlreadyDefinedException uade) {
				// This will happen too often to care about
				//dao.create(new SakoraLog(this.getClass().toString(), uade.getLocalizedMessage()));
				//log.error("CsvPersonHandler: " + uade.getMessage());
			}
			catch(UserLockedException ule) {
				dao.create(new SakoraLog(this.getClass().toString(), ule.getLocalizedMessage()));
				log.error("CsvPersonHandler: " + ule.getMessage());
				errors++;
			}
			catch(UserPermissionException upe) {
				dao.create(new SakoraLog(this.getClass().toString(), upe.getLocalizedMessage()));
				log.error("CsvPersonHandler: " + upe.getMessage());
				errors++;
			}

			// Log users read in for delta calculation, update existing and create new
			// dao.save(new Person(eid, (existingId == null ? newId : existingId), time));
			Person p = dao.findById(Person.class, eid);
			if (p == null) {
			    dao.create(new Person(eid, newId, time));
			} else {
			    p.setInputTime(time);
			    p.setUserId( (existingId == null ? newId : existingId) );
			    dao.update(p);
			}
		} else {
			log.error("Skipping short line (expected at least [" + minFieldCount + 
					"] fields): [" + (line == null ? null : Arrays.toString(line)) + "]");
			errors++;
		}
	}

	private Map<String, String> getOptionalFields(String[] line, int startAtIdx) {
		if ( optionalFieldNames == null || optionalFieldNames.isEmpty() ) {
			return new HashMap<String,String>();
		}
		
		// this is how Sakai would typically inject an empty list
		if ( optionalFieldNames.size() == 1 && "".equals(optionalFieldNames.get(0)) ) {
			return new HashMap<String,String>();
		}
		
		// Put all names in map to preserve delete semantics for missing
		// fields even if no fields exist in line at or after startAtIdx
		Map<String,String> namedFields = new HashMap<String,String>();
		for ( String fieldName : optionalFieldNames ) {
			namedFields.put(fieldName, null);
		}
		if ( line.length <= startAtIdx ) {
			return namedFields;
		}
		for ( int i = startAtIdx, p = 0; i < line.length; i++, p++ ) {
			if ( p >= optionalFieldNames.size() ) {
				break;
			}
			String fieldName = optionalFieldNames.get(p);
			if ( fieldName != null && !("".equals(fieldName)) ) {
				namedFields.put(fieldName, line[i]);
			}
		}
		return namedFields;
	}
	
	@Override
	protected void processInternal(CsvSyncContext context) {
	    if (CsvCommonHandlerService.URM_IGNORE.equals(commonHandlerService.userRemoveMode())) {
	        if (log.isDebugEnabled()) log.debug("SakoraCSV skipping people (user removal) processing, userRemoveMode=ignore");
	    } else {
	        // process the removals
	        loginToSakai();

	        // look for all users previously input via csv but not included in this import
	        Search search = new Search();
	        search.addRestriction(new Restriction("inputTime", time, Restriction.NOT_EQUALS));
	        search.setLimit(searchPageSize);

	        boolean done = false;

	        while (!done) {
	            List<Person> people = dao.findBySearch(Person.class, search);
	            for (Person user : people) {
	                try {
	                    UserEdit target = userDirService.editUser(user.getUserId());
	                    if (CsvCommonHandlerService.URM_DELETE.equals(commonHandlerService.userRemoveMode())) {
	                        userDirService.removeUser(target);
	                    } else {
	                        // assume the default disable case (without checking)
	                        target.setType(suspended);
	                    }
	                    // commit the changes
	                    userDirService.commitEdit(target);
	                    deletes++;
	                }
	                catch(UserNotDefinedException unde) {
	                    dao.create(new SakoraLog(this.getClass().toString(), unde.getLocalizedMessage()));
	                    log.error("CsvPersonHandler: " + unde.getMessage());
	                }
	                catch(UserAlreadyDefinedException uade) {
	                    dao.create(new SakoraLog(this.getClass().toString(), uade.getLocalizedMessage()));
	                    log.error("CsvPersonHandler: " + uade.getMessage());
	                }
	                catch(UserLockedException ule) {
	                    dao.create(new SakoraLog(this.getClass().toString(), ule.getLocalizedMessage()));
	                    log.error("CsvPersonHandler: " + ule.getMessage());
	                }
	                catch(UserPermissionException upe) {
	                    dao.create(new SakoraLog(this.getClass().toString(), upe.getLocalizedMessage()));
	                    log.error("CsvPersonHandler: " + upe.getMessage());
	                }
	            }
	            if (people == null || people.size() == 0) {
	                done = true;
	            } else {
	                search.setStart(search.getStart() + searchPageSize);
	            }
	            // should we halt if a stop was requested via pleaseStop?
	        }

	        logoutFromSakai();
	    }
	    dao.create(new SakoraLog(this.getClass().toString(),
	            "Finished processing input, added " + adds + " items, updated " 
	                    + updates + " items and removed " + deletes));
	}

	public void setDeleteUsers(boolean deleteUsers) {
	    log.warn("SakoraCSV: deleteUsers is no longer supported, use userRemoveMode option instead (see docs for details)");
	}

	public String getSuspended() {
		return suspended;
	}

	public void setSuspended(String suspended) {
		this.suspended = suspended;
	}
	
	public List<String> getOptionalFieldNames() {
		return optionalFieldNames;
	}

	public void setOptionalFieldNames(List<String> optionalFieldNames) {
		this.optionalFieldNames = optionalFieldNames;
	}
	
	public IdManager getIdManager() {
		return idManager;
	}

	public void setIdManager(IdManager idManager) {
		this.idManager = idManager;
	}

}
