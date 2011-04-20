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

import org.springframework.social.facebook.api.FacebookLink;
import org.springframework.social.facebook.api.FeedOperations;
import org.springframework.social.facebook.api.GraphApi;
import org.springframework.social.facebook.api.LinkPost;
import org.springframework.social.facebook.api.NotePost;
import org.springframework.social.facebook.api.Post;
import org.springframework.social.facebook.api.StatusPost;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class FeedTemplate implements FeedOperations {

	private final GraphApi graphApi;

	public FeedTemplate(GraphApi graphApi) {
		this.graphApi = graphApi;
	}

	public List<Post> getFeed() {
		return getFeed("me");
	}

	public List<Post> getFeed(String ownerId) {
		return graphApi.fetchConnections(ownerId, "feed", PostList.class).getList();
	}

	public List<Post> getHomeFeed() {
		return getHomeFeed("me");
	}

	public List<Post> getHomeFeed(String userId) {
		return graphApi.fetchConnections(userId, "home", PostList.class).getList();
	}
	
	public List<StatusPost> getStatuses() {
		return getStatuses("me");
	}
	
	public List<StatusPost> getStatuses(String userId) {
		return graphApi.fetchConnections(userId, "statuses", StatusPostList.class).getList();
	}
	
	public List<LinkPost> getLinks() {
		return getLinks("me");
	}
	
	public List<LinkPost> getLinks(String ownerId) {
		return graphApi.fetchConnections(ownerId, "links", LinkPostList.class).getList();
	}

	public List<NotePost> getNotes() {
		return getNotes("me");
	}
	
	public List<NotePost> getNotes(String ownerId) {
		return graphApi.fetchConnections(ownerId, "notes", NotePostList.class).getList();
	}
	
	public List<Post> getPosts() {
		return getPosts("me");
	}
	public List<Post> getPosts(String ownerId) {
		return graphApi.fetchConnections(ownerId, "posts", PostList.class).getList();
	}
	
	public Post getFeedEntry(String entryId) {
		return graphApi.fetchObject(entryId, Post.class);
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
