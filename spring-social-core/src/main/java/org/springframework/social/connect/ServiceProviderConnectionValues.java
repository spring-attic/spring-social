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
 * A configuration interface used to set values on a {@link ServiceProviderConnection} from a specific ServiceProvider API.
 * {@link #setProviderUserId(String)} maps to {@link ServiceProviderConnectionKey#getProviderUserId()}
 * {@link #setDisplayName(String)} maps to {@link ServiceProviderConnection#getDisplayName()}
 * {@link #setProfileUrl(String)} maps to {@link ServiceProviderConnection#getProfileUrl()}
 * {@link #setImageUrl(String)} maps to {@link ServiceProviderConnection#getImageUrl()}
 * @author Keith Donald
 * @see ServiceApiAdapter#setConnectionValues(Object, ServiceProviderConnectionValues)
 */
public interface ServiceProviderConnectionValues {

	/**
	 * Sets value mapped to {@link ServiceProviderConnectionKey#getProviderUserId()}.
	 */
	public void setProviderUserId(String providerUserId);

	/**
	 * Sets value mapped to {@link ServiceProviderConnection#getDisplayName()}.
	 */
	public void setDisplayName(String displayName);
	
	/**
	 * Sets value mapped to {@link ServiceProviderConnection#getProfileUrl()}
	 */
	public void setProfileUrl(String profileUrl);

	/**
	 * Sets value mapped to {@link ServiceProviderConnection#getImageUrl()}
	 */
	public void setImageUrl(String imageUrl);

}