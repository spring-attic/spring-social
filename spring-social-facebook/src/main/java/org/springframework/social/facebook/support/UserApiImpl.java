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
import org.springframework.social.facebook.Reference;
import org.springframework.social.facebook.UserApi;
import org.springframework.social.facebook.UserEvent;
import org.springframework.social.facebook.support.extractors.ResponseExtractors;
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
		return getObjectConnection(userId, "events", ResponseExtractors.USER_EVENT_EXTRACTOR);
	}

	public List<Album> getAlbums() {
		return getAlbums("me");
	}

	public List<Album> getAlbums(String userId) {
		return getObjectConnection(userId, "albums", ResponseExtractors.ALBUM_EXTRACTOR);
	}

	public List<Reference> getFriendLists() {
		return getFriendLists("me");
	}

	public List<Reference> getFriendLists(String userId) {
		return getObjectConnection(userId, "friendlists", ResponseExtractors.REFERENCE_EXTRACTOR);
	}

	public List<Reference> getFriends() {
		return getFriends("me");
	}

	public List<Reference> getFriends(String userId) {
		return getObjectConnection(userId, "friends", ResponseExtractors.REFERENCE_EXTRACTOR);
	}
}
