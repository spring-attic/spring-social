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

import java.util.List;

import org.springframework.social.linkedin.api.impl.LinkedInTemplate;

/**
 * <p>
 * Interface specifying a basic set of operations for interacting with LinkedIn.
 * Implemented by {@link LinkedInTemplate}.
 * </p>
 * 
 * <p>
 * Many of the methods contained in this interface require OAuth authentication
 * with LinkedIn. When a method's description speaks of the "current user", it
 * is referring to the user for whom the access token has been issued.
 * </p>
 * 
 * @author Craig Walls
 */
public interface LinkedInApi {
	/**
	 * Retrieves the user's LinkedIn profile ID.
	 * 
	 * @return the user's LinkedIn profile ID.
	 */
	String getProfileId();

	/**
	 * Retrieves a URL to the user's public profile page.
	 * 
	 * @return a URL to the user's public profile page.
	 */
	String getProfileUrl();

	/**
	 * Retrieves the current user's profile details.
	 * 
	 * @return the user's profile data.
	 */
	LinkedInProfile getUserProfile();

	/**
	 * Retrieves the 1st-degree connections from the current user's network.
	 * 
	 * @return the user's connections
	 */
	List<LinkedInProfile> getConnections();
}
