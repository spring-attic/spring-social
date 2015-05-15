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
package org.springframework.social.security;

import java.util.Set;

import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.security.provider.SocialAuthenticationService;

public interface SocialAuthenticationServiceLocator extends ConnectionFactoryLocator {

	/**
	 * Lookup a {@link SocialAuthenticationService} by providerId; for example, "facebook".
	 * @param providerId the provider ID used to find an authentication service
	 * @return a SocialAuthenticationServer for the given provider ID
	 */
	SocialAuthenticationService<?> getAuthenticationService(String providerId);

	/**
	 * Returns the set of providerIds for which a {@link SocialAuthenticationService} is registered; for example, <code>{"twitter", "facebook", "foursquare" }</code>.
	 * Elements in this set can be passed to {@link #getAuthenticationService(String)} to fetch a specific authentication service.
	 * @return a Set of provider IDs for which a SocialAuthenticationService has been registered with this provider.
	 */
	Set<String> registeredAuthenticationProviderIds();

}
