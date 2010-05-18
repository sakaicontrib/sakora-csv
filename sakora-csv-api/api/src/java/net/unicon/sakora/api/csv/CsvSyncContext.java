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
package net.unicon.sakora.api.csv;

import java.util.Map;

public interface CsvSyncContext {
	
	/** 
	 * Arbitrary String-based name/value pairs for representing
	 * processing of a CSV batch. Be aware that these properties
	 * are typically persisted as the state of the Quartz job
	 * that kicks off batch processing. I.e. be sure to cleanup
	 * this map unless you intend for that state to affect
	 * processing of the next batch.
	 * 
	 * @return
	 */
	Map<String,String> getProperties();
	void setProperties(Map<String,String> state);

}
