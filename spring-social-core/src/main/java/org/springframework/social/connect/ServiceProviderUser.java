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
package org.springframework.social.connect;

public class ServiceProviderUser {

	private final String id;
	
	private final String profileName;

	private final String profileUrl;
	
	private final String profilePictureUrl;

	public ServiceProviderUser(String id, String profileUrl, String profileName, String profilePictureUrl) {
		this.id = id;
		this.profileUrl = profileUrl;
		this.profileName = profileName;
		this.profilePictureUrl = profilePictureUrl;
	}

	/**
	 * The id of the user.
	 */
	public String getId() {
		return id;
	}

	/**
	 * The display name for the user on the provider's system.
	 * May be null if this information is not public or not provided.
	 * This information may change if the user updates his or her profile.
	 */
	public String getProfileName() {
		return profileName;
	}
	
	/**
	 * The public URL of the user's profile at the provider's site.
	 * May be null if this information is not public or not provided.
	 * This information may change if the user updates his or her profile.
	 */
	public String getProfileUrl() {
		return profileUrl;
	}

	/**
	 * A link to the user's picture at the provider's site.
	 * May be null if this information is not public or not provided.
	 * This information may change if the user updates his or her profile.
	 */
	public String getProfilePictureUrl() {
		return profilePictureUrl;
	}

}