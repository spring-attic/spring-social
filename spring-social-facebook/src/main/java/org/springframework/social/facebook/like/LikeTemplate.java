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
package org.springframework.social.facebook.like;

import java.util.List;

import org.springframework.social.facebook.GraphApi;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class LikeTemplate implements LikeOperations {

	private final GraphApi graphApi;

	public LikeTemplate(GraphApi graphApi) {
		this.graphApi = graphApi;
	}

	public List<UserLike> getLikes() {
		return getLikes("me");
	}

	public List<UserLike> getLikes(String userId) {
		return graphApi.fetchConnections(userId, "likes", UserLikeList.class).getList();
	}
	
	public void like(String objectId) {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		graphApi.post(objectId, "likes", map);
	}

	public void unlike(String objectId) {
		graphApi.delete(objectId, "likes");
	}

	public List<UserLike> getBooks() {
		return getMovies("me");
	}

	public List<UserLike> getBooks(String userId) {
		return graphApi.fetchConnections(userId, "books", UserLikeList.class).getList();
	}

	public List<UserLike> getMovies() {
		return getMovies("me");
	}

	public List<UserLike> getMovies(String userId) {
		return graphApi.fetchConnections(userId, "movies", UserLikeList.class).getList();
	}

	public List<UserLike> getMusic() {
		return getMovies("me");
	}

	public List<UserLike> getMusic(String userId) {
		return graphApi.fetchConnections(userId, "music", UserLikeList.class).getList();
	}

	public List<UserLike> getTelevision() {
		return getMovies("me");
	}

	public List<UserLike> getTelevision(String userId) {
		return graphApi.fetchConnections(userId, "television", UserLikeList.class).getList();
	}

	public List<UserLike> getActivities() {
		return getActivities("me");
	}

	public List<UserLike> getActivities(String userId) {
		return graphApi.fetchConnections(userId, "activities", UserLikeList.class).getList();
	}

	public List<UserLike> getInterests() {
		return getInterests("me");
	}

	public List<UserLike> getInterests(String userId) {
		return graphApi.fetchConnections(userId, "interests", UserLikeList.class).getList();
	}
}
