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

import java.util.Date;
import java.util.Map;

import org.springframework.social.facebook.types.Group;
import org.springframework.social.facebook.types.Group.Privacy;
import org.springframework.social.facebook.types.Reference;

public class GroupResponseExtractor extends AbstractResponseExtractor<Group> {

	@SuppressWarnings("unchecked")
	public Group extractObject(Map<String, Object> groupMap) {
		String id = (String) groupMap.get("id");
		Reference owner = extractReferenceFromMap((Map<String, Object>) groupMap.get("owner"));
		String name = (String) groupMap.get("name");
		Privacy privacy = Privacy.valueOf(((String) groupMap.get("privacy")).toUpperCase());
		String icon = (String) groupMap.get("icon");
		Date updatedTime = toDate((String) groupMap.get("updated_time"));
		String email = (String) groupMap.get("email");
		String description = (String) groupMap.get("description");
		return new Group(id, owner, name, privacy, icon, updatedTime, email, description);
	}

}
