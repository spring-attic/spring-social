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
package org.springframework.social.facebook.web;

import java.io.Serializable;

import org.springframework.social.connect.oauth2.OAuth2ServiceProvider;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.web.connect.ProviderSignInAccount;
import org.springframework.social.web.connect.ServiceProviderLocator;

public class FacebookSignInAccount implements ProviderSignInAccount {

	private final String providerId;

	private final String accessToken;

	public FacebookSignInAccount(String providerId, String accessToken) {
		this.providerId = providerId;
		this.accessToken = accessToken;
	}

	public void connect(ServiceProviderLocator serviceProviderLocator, Serializable accountId) {
		OAuth2ServiceProvider<?> provider = (OAuth2ServiceProvider<?>) serviceProviderLocator.getServiceProvider(providerId);
		provider.connect(accountId, new AccessGrant(accessToken, null));
	}

}
