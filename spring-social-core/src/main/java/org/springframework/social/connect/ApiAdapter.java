/*
 * Copyright 2015 the original author or authors.
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

import org.springframework.social.ApiException;

/**
 * An adapter that bridges between the uniform {@link Connection} model and a specific provider API model.
 * @author Keith Donald
 * @param <A> the service provider's API interface
 */
public interface ApiAdapter<A> {
	
	/**
	 * Implements {@link Connection#test()} for connections to the given API.
	 * @param api the API binding
	 * @return true if the API is functional, false if not
	 */
	boolean test(A api);
	
	/**
	 * Sets values for {@link ConnectionKey#getProviderUserId()}, {@link Connection#getDisplayName()},
	 * {@link Connection#getProfileUrl()}, and {@link Connection#getImageUrl()} for connections to the given API.
	 * @param api the API binding
	 * @param values the connection values to set
	 * @throws ApiException if there is a problem fetching connection information from the provider.
	 */
	void setConnectionValues(A api, ConnectionValues values);
	
	/**
	 * Implements {@link Connection#fetchUserProfile()} for connections to the given API.
	 * Should never return null.
	 * If the provider's API does not expose user profile data, this method should return {@link UserProfile#EMPTY}. 
	 * @param api the API binding
	 * @return the service provider user profile
	 * @throws ApiException if there is a problem fetching a user profile from the provider.
	 * @see UserProfileBuilder
	 */
	UserProfile fetchUserProfile(A api);
	
	/**
	 * Implements {@link Connection#updateStatus(String)} for connections to the given API.
	 * If the provider does not have a status concept calling this method should have no effect.
	 * @param api the API binding
	 * @param message the status message
	 * @throws ApiException if there is a problem updating the user's status on the provider.
	 */
	void updateStatus(A api, String message);
	
}
