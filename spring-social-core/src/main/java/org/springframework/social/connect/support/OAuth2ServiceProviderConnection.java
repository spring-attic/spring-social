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
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2ServiceProvider;

public class OAuth2ServiceProviderConnection<S> extends AbstractServiceProviderConnection<S> {

	private OAuth2ServiceProvider<S> serviceProvider;
	
	private String accessToken;
	
	private String refreshToken;
	
	private Long expireTime;
	
	public OAuth2ServiceProviderConnection(String providerId, String providerUserId, OAuth2ServiceProvider<S> serviceProvider,
			String accessToken, String refreshToken, Long expiresTime, ServiceApiAdapter<S> serviceApiAdapter) {
		super(providerId, providerUserId, serviceProvider.getServiceApi(accessToken), serviceApiAdapter);
		init(serviceProvider, accessToken, refreshToken, expireTime);
	}

	public OAuth2ServiceProviderConnection(ServiceProviderConnectionMemento memento, OAuth2ServiceProvider<S> serviceProvider, ServiceApiAdapter<S> serviceApiAdapter) {
		super(memento, serviceProvider.getServiceApi(memento.getAccessToken()), serviceApiAdapter);
		init(serviceProvider, memento.getAccessToken(), memento.getRefreshToken(), memento.getExpireTime());		
	}
	
	// subclassing hooks

	@Override
	protected S doRefresh() {
		AccessGrant accessGrant = serviceProvider.getOAuthOperations().refreshAccessToken(refreshToken);
		setAccessFields(accessGrant.getAccessToken(), accessGrant.getRefreshToken(), /* accessGrant.getExpireTime() */ null);
		return serviceProvider.getServiceApi(accessToken);
	}
	
	@Override
	public ServiceProviderConnectionMemento createMemento() {
		return new ServiceProviderConnectionMemento(getKey().getProviderId(), getKey().getProviderUserId(),
				getProfileName(), getProfileUrl(), getProfilePictureUrl(), accessToken, /* accessGrant.getExpireTime() */ null, refreshToken, expireTime);
	}

	// internal helpers

	private void init(OAuth2ServiceProvider<S> serviceProvider, String accessToken, String refreshToken, Long expireTime) {
		this.serviceProvider = serviceProvider;
		setAccessFields(accessToken, refreshToken, expireTime);
	}
	
	private void setAccessFields(String accessToken, String refreshToken, Long expireTime) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.expireTime = expireTime;
	}

}
