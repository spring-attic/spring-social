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
package org.springframework.social.connect.spi;

/**
 * A common abstraction modeling a service provider user profile.
 * With this information, a client application can render a descriptive link to the user's profile on the provider's system.
 * @author Keith Donald
 */
public class ServiceProviderUser {

	private final String id;
	
	private final String profileName;

	private final String profileUrl;
	
	private final String profilePictureUrl;

	/**
	 * Creates a new ServiceProviderUser model.
	 * @param id the id of the user
	 * @param profileName the display name for the user's profile
	 * @param profileUrl a link to the user's profile
	 * @param profilePictureUrl a link to the user's profile picture
	 */
	public ServiceProviderUser(String id, String profileName, String profileUrl, String profilePictureUrl) {
		this.id = id;
		this.profileName = profileName;
		this.profileUrl = profileUrl;
		this.profilePictureUrl = profilePictureUrl;
	}

	/**
	 * The system-assigned id of the user.
	 * This value should ideally never change.
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