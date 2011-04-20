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
 * A data transfer object used to map {@link ServiceProviderConnection} values from a ServiceProvider API.
 * {@link #getProviderUserId()} maps to {@link ServiceProviderConnectionKey#getProviderUserId()}
 * {@link #getDisplayName()} maps to {@link ServiceProviderConnection#getDisplayName()}
 * {@link #getProfileUrl()} maps to {@link ServiceProviderConnection#getProfileUrl()}
 * {@link #getImageUrl()} maps to {@link ServiceProviderConnection#getImageUrl()}
 * @author Keith Donald
 * @see ServiceApiAdapter#getConnectionValues(Object)
 */
public class ServiceProviderConnectionValues {

	private final String providerUserId;
	
	private final String displayName;

	private final String profileUrl;
	
	private final String imageUrl;

	/**
	 * Creates a new ServiceProviderConnectionValues transfer object.
	 * @param providerUserId the id of the provider user
	 * @param displayName the display name for the connection
	 * @param profileUrl a link to the user's profile
	 * @param imageUrl a link to a picture visualizing the connection
	 */
	public ServiceProviderConnectionValues(String providerUserId, String displayName, String profileUrl, String imageUrl) {
		this.providerUserId = providerUserId;
		this.displayName = displayName;
		this.profileUrl = profileUrl;
		this.imageUrl = imageUrl;
	}

	/**
	 * Value mapped to {@link ServiceProviderConnectionKey#getProviderUserId()}.
	 */
	public String getProviderUserId() {
		return providerUserId;
	}

	/**
	 * Value mapped to {@link ServiceProviderConnection#getDisplayName()}.
	 */
	public String getDisplayName() {
		return displayName;
	}
	
	/**
	 * Value mapped to {@link ServiceProviderConnection#getProfileUrl()}
	 */
	public String getProfileUrl() {
		return profileUrl;
	}

	/**
	 * Value mapped to {@link ServiceProviderConnection#getImageUrl()}
	 */
	public String getImageUrl() {
		return imageUrl;
	}

}