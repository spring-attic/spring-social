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
package org.springframework.social.twitter.support.extractors;

import java.util.Date;
import java.util.Map;

import org.springframework.social.twitter.types.SavedSearch;

public class SavedSearchResponseExtractor extends AbstractResponseExtractor<SavedSearch> {

	public SavedSearch extractObject(Map<String, Object> item) {
		long id = Long.valueOf(String.valueOf(item.get("id")));
		String name = String.valueOf(item.get("name"));
		String query = String.valueOf(item.get("query"));
		Object positionValue = item.get("position");
		int position = positionValue == null ? 0 : Integer.valueOf(String.valueOf(positionValue));
		Date createdAt = toSavedSearchDate(String.valueOf(item.get("created_at")));
		return new SavedSearch(id, name, query, position, createdAt);
	}
	
}
