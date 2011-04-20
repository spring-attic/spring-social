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

import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.social.facebook.api.GraphApi;
import org.springframework.social.facebook.api.ImageType;
import org.springframework.social.facebook.api.UserOperations;

class UserTemplate implements UserOperations {

	private final GraphApi graphApi;

	public UserTemplate(GraphApi graphApi) {
		this.graphApi = graphApi;
	}

	public FacebookProfile getUserProfile() {
		return getUserProfile("me");
	}

	public FacebookProfile getUserProfile(String facebookId) {
		return graphApi.fetchObject(facebookId, FacebookProfile.class);
	}

	public byte[] getUserProfileImage() {
		return getUserProfileImage("me", ImageType.NORMAL);
	}
	
	public byte[] getUserProfileImage(String userId) {
		return getUserProfileImage(userId, ImageType.NORMAL);
	}

	public byte[] getUserProfileImage(ImageType imageType) {
		return getUserProfileImage("me", imageType);
	}
	
	public byte[] getUserProfileImage(String userId, ImageType imageType) {
		return graphApi.fetchImage(userId, "picture", imageType);
	}
}
