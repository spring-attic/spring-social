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
package org.springframework.social.facebook;

import java.util.List;

import org.springframework.util.MultiValueMap;

/**
 * Interface specifying a basic set of operations for interacting with Facebook.
 * Implemented by {@link FacebookTemplate}. Not often used directly, but a
 * useful option to enhance testability, as it can easily be mocked or stubbed.
 * <p>
 * Many of the methods contained in this interface require an access token from
 * Facebook. When a method's description speaks of the "current user", it is
 * referring to the user for whom the access token has been issued.
 * </p>
 * @author Craig Walls
 */
public interface FacebookApi {
	
	/**
	 * Retrieves the user's Facebook profile ID.
	 * @return the user's Facebook profile ID.
	 */
	String getProfileId();

	/**
	 * Retrieve the current user's Facebook profile information.
	 * @return the user's profile information.
	 */
	FacebookProfile getUserProfile();

	/**
	 * Retrieves the Facebook profile information for a given user ID.
	 * 
	 * @param facebookId
	 *            the Facebook ID to retrieve profile data for.
	 * @return the user's profile information.
	 */
	FacebookProfile getUserProfile(String facebookId);

	/**
	 * Retrieve the URL to the user's Facebook profile.
	 * 
	 * @return the URL to the user's Facebook profile.
	 */
	String getProfileUrl();

	/**
	 * Get a list of the user's friends.
	 * @return a list of <code>String</code>s where each entry is the Facebook ID of one of the user's friends.
	 */
	List<String> getFriendIds();

	/**
	 * Posts a message to the current user's wall.
	 * @param status The message to post
	 */
	void updateStatus(String status);

	/**
	 * Posts a message to the current user's wall along with a link.
	 * @param message The message to post
	 * @param link A link to be included in the status update
	 */
	void updateStatus(String message, FacebookLink link);

	/**
	 * Low-level publish-to-Facebook method for publishing any type of object supported by Facebook's API.
	 * @param object The ID of the object to publish to.
	 * @param connection The connection to be published.
	 * @param data The data to be published.
	 */
	void publish(String object, String connection, MultiValueMap<String, String> data);
}
