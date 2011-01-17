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
package org.springframework.social.tripit;

import java.io.Serializable;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeName;

/**
 * Model class containing a TripIt user's profile information.
 * 
 * @author Craig Walls
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName("Profile")
public class TripItProfile implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * The TripIt user's profile ID
	 * 
	 * @return The TripIt user's profile ID
	 */
	public String getId() {
		return attributes.get("ref");
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

	@JsonProperty("@attributes")
	Map<String, String> attributes;

	@JsonProperty("screen_name")
	String screenName;

	@JsonProperty("public_display_name")
	String publicDisplayName;

	@JsonProperty("home_city")
	String homeCity;

	@JsonProperty("company")
	String company;

	@JsonProperty("profile_url")
	String profilePath;
}
