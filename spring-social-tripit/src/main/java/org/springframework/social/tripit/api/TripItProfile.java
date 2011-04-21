/*
 * Copyright 2010 the original author or authors.
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
package org.springframework.social.tripit.api;

import java.io.Serializable;

/**
 * Model class containing a TripIt user's profile information.
 * 
 * @author Craig Walls
 */
@SuppressWarnings("serial")
public class TripItProfile implements Serializable {

	public TripItProfile(String id, String screenName, String publicDisplayName, String emailAddress, String homeCity, String company,
			String profilePath, String profileImageUrl) {
		this.id = id;
		this.screenName = screenName;
		this.publicDisplayName = publicDisplayName;
		this.emailAddress = emailAddress;
		this.homeCity = homeCity;
		this.company = company;
		this.profilePath = profilePath;
		this.profileImageUrl = profileImageUrl;
	}

	/**
	 * The TripIt user's profile ID
	 * 
	 * @return The TripIt user's profile ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * The TripIt user's screen name
	 * 
	 * @return The TripIt user's screen name
	 */
	public String getScreenName() {
		return screenName;
	}

	/**
	 * The TripIt user's display name
	 * 
	 * @return The TripIt user's display name
	 */
	public String getPublicDisplayName() {
		return publicDisplayName;
	}
	
	/**
	 * The TripIt user's email address
	 * 
	 * @return The TripIt user's email address
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * The TripIt user's home city
	 * 
	 * @return The TripIt user's home city
	 */
	public String getHomeCity() {
		return homeCity;
	}

	/**
	 * The company that the TripIt user works for
	 * 
	 * @return The company that the TripIt user works for
	 */
	public String getCompany() {
		return company;
	}

	/**
	 * The URL to the user's profile page at TripIt
	 * 
	 * @return The URL to the user's profile page at TripIt
	 */
	public String getProfileUrl() {
		return "http://www.tripit.com/" + profilePath;
	}
	
	/**
	 * The URL to the user's profile image at TripIt
	 * 
	 * @return The URL to the user's profile image at TripIt
	 */
	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	private final String id;
	private final String screenName;
	private final String publicDisplayName;
	private final String emailAddress;
	private final String homeCity;
	private final String company;
	private final String profilePath;
	private final String profileImageUrl;
}
