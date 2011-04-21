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
package org.springframework.social.linkedin.api;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Model class containing a user's LinkedIn profile information.
 * 
 * @author Craig Walls
 */
@XmlRootElement(name = "person")
public class LinkedInProfile implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlElement
	String id;

	@XmlElement(name = "first-name")
	String firstName;

	@XmlElement(name = "last-name")
	String lastName;

	@XmlElement
	String headline;

	@XmlElementWrapper(name = "site-standard-profile-request")
	@XmlElement(name = "url")
	String[] standardProfileUrls;

	@XmlElementWrapper(name = "site-public-profile-request")
	@XmlElement(name = "url")
	String[] publicProfileUrls;

	@XmlElement
	String industry;
	
	@XmlElement(name = "picture-url")
	String profilePictureUrl;

	/**
	 * The user's LinkedIn profile ID
	 * 
	 * @return The user's LinkedIn profile ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * The user's first name
	 * 
	 * @return The user's first name
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * The user's last name
	 * 
	 * @return The user's last name
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * The user's headline
	 * 
	 * @return The user's headline
	 */
	public String getHeadline() {
		return headline;
	}

	/**
	 * The user's industry
	 * 
	 * @return The user's industry
	 */
	public String getIndustry() {
		return industry;
	}

	/**
	 * A URL to the user's standard profile. The content shown at this profile
	 * will depend upon what the requesting user is allowed to see.
	 * 
	 * @return the URL to the user's standard profile
	 */
	public String getStandardProfileUrl() {
		return standardProfileUrls != null && standardProfileUrls.length > 0 ? standardProfileUrls[0] : null;
	}

	/**
	 * A URL to the user's public profile. The content shown at this profile is
	 * intended for public display and is determined by the user's privacy
	 * settings.
	 * 
	 * @return the URL of the user's public profile
	 */
	public String getPublicProfileUrl() {
		return publicProfileUrls != null && publicProfileUrls.length > 0 ? publicProfileUrls[0] : null;
	}
	
	/**
	 * A URL to the user's profile picture.
	 */
	public String getProfilePictureUrl() {
		return profilePictureUrl;
	}
}
