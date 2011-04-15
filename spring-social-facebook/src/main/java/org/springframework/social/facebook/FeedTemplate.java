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
package org.springframework.social.facebook;

import java.util.List;

import org.springframework.social.facebook.support.json.FeedEntryList;
import org.springframework.social.facebook.types.FacebookLink;
import org.springframework.social.facebook.types.FeedEntry;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

class FeedTemplate implements FeedOperations {

	private final GraphApi graphApi;

	public FeedTemplate(GraphApi graphApi) {
		this.graphApi = graphApi;
	}

	public List<FeedEntry> getFeed() {
		return getFeed("me");
	}

	public List<FeedEntry> getFeed(String ownerId) {
		return graphApi.fetchConnections(ownerId, "feed", FeedEntryList.class).getList();
	}

	public List<FeedEntry> getHomeFeed() {
		return getHomeFeed("me");
	}

	public List<FeedEntry> getHomeFeed(String userId) {
		return graphApi.fetchConnections(userId, "home", FeedEntryList.class).getList();
	}
	
	public List<FeedEntry> getStatuses() {
		return getStatuses("me");
	}
	
	public List<FeedEntry> getStatuses(String userId) {
		return graphApi.fetchConnections(userId, "statuses", FeedEntryList.class).getList();
	}
	
	public List<FeedEntry> getLinks() {
		return getLinks("me");
	}
	
	public List<FeedEntry> getLinks(String ownerId) {
		return graphApi.fetchConnections(ownerId, "links", FeedEntryList.class).getList();
	}

	public FeedEntry getNote(String noteId) {
		return graphApi.fetchObject(noteId, FeedEntry.class);
	}
	
	public List<FeedEntry> getNotes() {
		return getNotes("me");
	}
	
	public List<FeedEntry> getNotes(String ownerId) {
		return graphApi.fetchConnections(ownerId, "notes", FeedEntryList.class).getList();
	}
	
	public List<FeedEntry> getPosts() {
		return getPosts("me");
	}
	public List<FeedEntry> getPosts(String ownerId) {
		return graphApi.fetchConnections(ownerId, "posts", FeedEntryList.class).getList();
	}
	
	public FeedEntry getFeedEntry(String entryId) {
		return graphApi.fetchObject(entryId, FeedEntry.class);
	}

	public String updateStatus(String message) {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.set("message", message);
		return graphApi.publish("me", "feed", map);
	}

	public String postLink(String message, FacebookLink link) {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.set("link", link.getLink());
		map.set("name", link.getName());
		map.set("caption", link.getCaption());
		map.set("description", link.getDescription());
		map.set("message", message);
		return graphApi.publish("me", "feed", map);
	}

	public void deleteFeedEntry(String id) {
		graphApi.delete(id);
	}

}
