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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.security.provider.SocialAuthenticationService;

public class SocialAuthenticationServiceRegistry extends ConnectionFactoryRegistry implements SocialAuthenticationServiceLocator {

	private Map<String, SocialAuthenticationService<?>> authenticationServices = new HashMap<String, SocialAuthenticationService<?>>();

	public SocialAuthenticationService<?> getAuthenticationService(String providerId) {
		SocialAuthenticationService<?> authenticationService = authenticationServices.get(providerId);
		if (authenticationService == null) {
			throw new IllegalArgumentException("No authentication service for service provider '" + providerId + "' is registered");
		}
		return authenticationService;
	}

	/**
	 * Add a {@link SocialAuthenticationService} to this registry.
	 * @param authenticationService a SocialAuthenticationService to register
	 */
	public void addAuthenticationService(SocialAuthenticationService<?> authenticationService) {
		addConnectionFactory(authenticationService.getConnectionFactory());
		authenticationServices.put(authenticationService.getConnectionFactory().getProviderId(), authenticationService);
	}

	/**
	 * Set the group of {@link SocialAuthenticationService}s registered in this registry. 
	 * JavaBean setter that allows for this object to be more easily configured by tools. 
	 * For programmatic configuration, prefer {@link #addAuthenticationService(SocialAuthenticationService)}.
	 * @param authenticationServices the set of social authentication services to register
	 */
	public void setAuthenticationServices(Iterable<SocialAuthenticationService<?>> authenticationServices) {
		for (SocialAuthenticationService<?> authenticationService : authenticationServices) {
			addAuthenticationService(authenticationService);
		}
	}

	public Set<String> registeredAuthenticationProviderIds() {
		return authenticationServices.keySet();
	}

}
