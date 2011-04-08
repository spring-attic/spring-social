/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.social.facebook.support.extractors;

import java.util.Map;

import org.springframework.social.facebook.types.WorkEntry;

public class WorkResponseExtractor extends AbstractResponseExtractor<WorkEntry> {

	@SuppressWarnings("unchecked")
	public WorkEntry extractObject(Map<String, Object> workEntryMap) {
		return new WorkEntry(extractReferenceFromMap((Map<String, Object>) workEntryMap.get("employer")),
				(String) workEntryMap.get("start_date"), (String) workEntryMap.get("end_date"));
	}

}
