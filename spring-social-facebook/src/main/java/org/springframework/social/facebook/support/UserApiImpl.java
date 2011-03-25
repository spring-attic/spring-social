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
package org.springframework.social.facebook.support;

import java.util.List;

import org.springframework.social.facebook.Album;
import org.springframework.social.facebook.Checkin;
import org.springframework.social.facebook.FacebookProfile;
import org.springframework.social.facebook.UserApi;
import org.springframework.social.facebook.UserEvent;
import org.springframework.social.facebook.UserLike;
import org.springframework.social.facebook.support.extractors.AlbumResponseExtractor;
import org.springframework.social.facebook.support.extractors.ResponseExtractors;
import org.springframework.social.facebook.support.extractors.UserEventResponseExtractor;
import org.springframework.social.facebook.support.extractors.UserLikeResponseExtractor;
import org.springframework.web.client.RestTemplate;

public class UserApiImpl extends AbstractFacebookApi implements UserApi {

	public UserApiImpl(RestTemplate restTemplate) {
		super(restTemplate);
	}

	public FacebookProfile getUserProfile() {
		return getUserProfile("me");
	}

	public FacebookProfile getUserProfile(String facebookId) {
		return getObject(facebookId, ResponseExtractors.PROFILE_EXTRACTOR);
	}

	public List<UserLike> getLikes() {
		return getLikes("me");
	}

	public List<UserLike> getLikes(String userId) {
		return getObjectConnection(userId, "likes", new UserLikeResponseExtractor());
	}

	public List<UserLike> getBooks() {
		return getMovies("me");
	}

	public List<UserLike> getBooks(String userId) {
		return getObjectConnection(userId, "books", new UserLikeResponseExtractor());
	}

	public List<UserLike> getMovies() {
		return getMovies("me");
	}

	public List<UserLike> getMovies(String userId) {
		return getObjectConnection(userId, "movies", new UserLikeResponseExtractor());
	}

	public List<UserLike> getMusic() {
		return getMovies("me");
	}

	public List<UserLike> getMusic(String userId) {
		return getObjectConnection(userId, "music", new UserLikeResponseExtractor());
	}

	public List<UserLike> getTelevision() {
		return getMovies("me");
	}

	public List<UserLike> getTelevision(String userId) {
		return getObjectConnection(userId, "television", new UserLikeResponseExtractor());
	}

	public List<UserLike> getActivities() {
		return getActivities("me");
	}

	public List<UserLike> getActivities(String userId) {
		return getObjectConnection(userId, "activities", new UserLikeResponseExtractor());
	}

	public List<UserLike> getInterests() {
		return getInterests("me");
	}

	public List<UserLike> getInterests(String userId) {
		return getObjectConnection(userId, "interests", new UserLikeResponseExtractor());
	}

	public List<Checkin> getCheckins() {
		return getCheckins("me");
	}

	public List<Checkin> getCheckins(String userId) {
		return getObjectConnection(userId, "checkins", ResponseExtractors.CHECKIN_EXTRACTOR);
	}

	public List<UserEvent> getEvents() {
		return getEvents("me");
	}

	public List<UserEvent> getEvents(String userId) {
		return getObjectConnection(userId, "events", new UserEventResponseExtractor());
	}

	public List<Album> getAlbums() {
		return getAlbums("me");
	}

	public List<Album> getAlbums(String userId) {
		return getObjectConnection(userId, "albums", new AlbumResponseExtractor());
	}
}
