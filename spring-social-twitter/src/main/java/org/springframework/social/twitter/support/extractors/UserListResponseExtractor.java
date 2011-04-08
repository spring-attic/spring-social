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

import java.util.Map;

import org.springframework.social.twitter.types.UserList;

public class UserListResponseExtractor extends AbstractResponseExtractor<UserList> {

	public UserList extractObject(Map<String, Object> listMap) {
		long id = Long.valueOf(String.valueOf(listMap.get("id")));
		String fullName = String.valueOf(listMap.get("full_name"));
		String name = String.valueOf(listMap.get("name"));
		String description = String.valueOf(listMap.get("description"));
		String slug = String.valueOf(listMap.get("slug"));
		boolean isPublic = String.valueOf(listMap.get("mode")).equals("public");
		boolean isFollowing = Boolean.valueOf(String.valueOf(listMap.get("following")));
		int memberCount = Integer.valueOf(String.valueOf(listMap.get("member_count")));
		int subscriberCount = Integer.valueOf(String.valueOf(listMap.get("subscriber_count")));
		String uriPath = String.valueOf(listMap.get("uri"));
		UserList twitterList = new UserList(id, name, fullName, uriPath, description, slug, isPublic, isFollowing, memberCount, subscriberCount);
		return twitterList;
	}
	
}
