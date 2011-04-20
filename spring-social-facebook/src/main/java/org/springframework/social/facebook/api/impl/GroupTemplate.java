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
package org.springframework.social.facebook.api.impl;

import java.util.List;

import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.social.facebook.api.GraphApi;
import org.springframework.social.facebook.api.Group;
import org.springframework.social.facebook.api.GroupMemberReference;
import org.springframework.social.facebook.api.GroupOperations;
import org.springframework.social.facebook.api.ImageType;

public class GroupTemplate implements GroupOperations {
	
	private final GraphApi graphApi;

	public GroupTemplate(GraphApi graphApi) {
		this.graphApi = graphApi;
	}
	
	public Group getGroup(String groupId) {
		return graphApi.fetchObject(groupId, Group.class);
	}
	
	public byte[] getGroupImage(String groupId) {
		return getGroupImage(groupId, ImageType.NORMAL);
	}
	
	public byte[] getGroupImage(String groupId, ImageType imageType) {
		return graphApi.fetchImage(groupId, "picture", imageType);
	}
	
	public List<GroupMemberReference> getMembers(String groupId) {
		return graphApi.fetchConnections(groupId, "members", GroupMemberReferenceList.class).getList();
	}

	public List<FacebookProfile> getMemberProfiles(String groupId) {
		return graphApi.fetchConnections(groupId, "members", FacebookProfileList.class, FULL_PROFILE_FIELDS).getList();
	}

	private static final String[] FULL_PROFILE_FIELDS = {"id", "username", "name", "first_name", "last_name", "gender", "locale", "education", "work", "email", "third_party_id", "link", "timezone", "updated_time", "verified", "about", "bio", "birthday", "location", "hometown", "interested_in", "religion", "political", "quotes", "relationship_status", "significant_other", "website"};

}
