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

/**
 * An adapter that bridges between the uniform {@link ServiceProviderConnection} model and a specific provider API model.
 * @author Keith Donald
 * @param <S> the service API
 */
public interface ServiceApiAdapter<S> {
	
	/**
	 * Implements {@link ServiceProviderConnection#test()} for connections to the given service API.
	 * @param serviceApi the service API
	 * @return true if the API is functional, false if not
	 */
	boolean test(S serviceApi);
	
	/**
	 * Provides values for {@link ServiceProviderConnectionKey#getProviderUserId()}, {@link ServiceProviderConnection#getDisplayName()},
	 * {@link ServiceProviderConnection#getProfileUrl()}, and {@link ServiceProviderConnection#getImageUrl()} for connections to the given service API.
	 * @param serviceApi the service API
	 * @return the service provider connection values
	 */
	ServiceProviderConnectionValues getConnectionValues(S serviceApi);
	
	/**
	 * Implements {@link ServiceProviderConnection#fetchUserProfile()} for connections to the given service API.
	 * @param serviceApi
	 * @return the service provider user profile
	 */
	ServiceProviderUserProfile fetchUserProfile(S serviceApi);
	
	/**
	 * Implements {@link ServiceProviderConnection#updateStatus(String)} for connections to the given service API.
	 * @param serviceApi the service API
	 * @param message the status message
	 */
	void updateStatus(S serviceApi, String message);
	
}