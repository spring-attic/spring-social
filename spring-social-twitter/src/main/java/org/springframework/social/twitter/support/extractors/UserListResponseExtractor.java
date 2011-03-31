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
