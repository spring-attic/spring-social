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
package org.springframework.social.connect.support;

import org.springframework.social.connect.ServiceProviderConnectionMemento;
import org.springframework.social.connect.spi.ServiceApiAdapter;
import org.springframework.social.oauth1.OAuth1ServiceProvider;

public class OAuth1ServiceProviderConnection<S> extends AbstractServiceProviderConnection<S> {

	private String accessToken;
	
	private String secret;
	
	public OAuth1ServiceProviderConnection(String providerId, String providerUserId, OAuth1ServiceProvider<S> serviceProvider,
			String accessToken, String secret, ServiceApiAdapter<S> serviceApiAdapter) {
		super(providerId, providerUserId, serviceProvider.getServiceApi(accessToken, secret), serviceApiAdapter);
		init(accessToken, secret);
	}

	public OAuth1ServiceProviderConnection(ServiceProviderConnectionMemento memento, OAuth1ServiceProvider<S> serviceProvider, ServiceApiAdapter<S> serviceApiAdapter) {
		super(memento, serviceProvider.getServiceApi(memento.getAccessToken(), memento.getSecret()), serviceApiAdapter);
		init(memento.getAccessToken(), memento.getSecret());
	}

	// subclassing hooks
	
	@Override
	public ServiceProviderConnectionMemento createMemento() {
		return new ServiceProviderConnectionMemento(getKey().getProviderId(), getKey().getProviderUserId(),
				getProfileName(), getProfileUrl(), getProfilePictureUrl(), accessToken, secret, null, null);
	}

	// internal helpers
	
	private void init(String accessToken, String secret) {
		this.accessToken = accessToken;
		this.secret = secret;
	}

}
