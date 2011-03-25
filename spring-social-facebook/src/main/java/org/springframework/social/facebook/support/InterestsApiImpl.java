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

import org.springframework.social.facebook.InterestsApi;
import org.springframework.social.facebook.UserLike;
import org.springframework.social.facebook.support.extractors.ResponseExtractors;
import org.springframework.web.client.RestTemplate;

public class InterestsApiImpl extends AbstractFacebookApi implements InterestsApi {

	public InterestsApiImpl(RestTemplate restTemplate) {
		super(restTemplate);
	}

	public List<UserLike> getLikes() {
		return getLikes("me");
	}

	public List<UserLike> getLikes(String userId) {
		return getObjectConnection(userId, "likes", ResponseExtractors.USER_LIKE_EXTRACTOR);
	}

	public List<UserLike> getBooks() {
		return getMovies("me");
	}

	public List<UserLike> getBooks(String userId) {
		return getObjectConnection(userId, "books", ResponseExtractors.USER_LIKE_EXTRACTOR);
	}

	public List<UserLike> getMovies() {
		return getMovies("me");
	}

	public List<UserLike> getMovies(String userId) {
		return getObjectConnection(userId, "movies", ResponseExtractors.USER_LIKE_EXTRACTOR);
	}

	public List<UserLike> getMusic() {
		return getMovies("me");
	}

	public List<UserLike> getMusic(String userId) {
		return getObjectConnection(userId, "music", ResponseExtractors.USER_LIKE_EXTRACTOR);
	}

	public List<UserLike> getTelevision() {
		return getMovies("me");
	}

	public List<UserLike> getTelevision(String userId) {
		return getObjectConnection(userId, "television", ResponseExtractors.USER_LIKE_EXTRACTOR);
	}

	public List<UserLike> getActivities() {
		return getActivities("me");
	}

	public List<UserLike> getActivities(String userId) {
		return getObjectConnection(userId, "activities", ResponseExtractors.USER_LIKE_EXTRACTOR);
	}

	public List<UserLike> getInterests() {
		return getInterests("me");
	}

	public List<UserLike> getInterests(String userId) {
		return getObjectConnection(userId, "interests", ResponseExtractors.USER_LIKE_EXTRACTOR);
	}
}
