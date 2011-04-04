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
import org.springframework.social.connect.ServiceProviderUser;
import org.springframework.social.connect.spi.ServiceApiAdapter;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2ServiceProvider;

public class OAuth2ServiceProviderConnection<S> extends AbstractServiceProviderConnection<S> {

	private final OAuth2ServiceProvider<S> serviceProvider;
	
	private String accessToken;
	
	private String refreshToken;
	
	private Long expireTime;
	
	public OAuth2ServiceProviderConnection(String providerId, String providerUserId, OAuth2ServiceProvider<S> serviceProvider,
			String accessToken, String refreshToken, Long expiresTime, ServiceApiAdapter<S> serviceApiAdapter) {
		super(providerId, providerUserId, serviceProvider.getServiceApi(accessToken), serviceApiAdapter);
		this.serviceProvider = serviceProvider;
		setAccessFields(accessToken, refreshToken, expireTime);
	}

	public OAuth2ServiceProviderConnection(ServiceProviderConnectionMemento memento, OAuth2ServiceProvider<S> serviceProvider, ServiceApiAdapter<S> serviceApiAdapter) {
		super(memento, serviceProvider.getServiceApi(memento.getAccessToken()), serviceApiAdapter);
		this.serviceProvider = serviceProvider;		
		setAccessFields(memento.getAccessToken(), memento.getRefreshToken(), memento.getExpireTime());		
	}
	
	// subclassing hooks

	@Override
	protected S doRefresh() {
		AccessGrant accessGrant = serviceProvider.getOAuthOperations().refreshAccess(refreshToken);
		setAccessFields(accessGrant.getAccessToken(), accessGrant.getRefreshToken(), accessGrant.getExpireTime());
		return serviceProvider.getServiceApi(accessToken);
	}
	
	@Override
	protected ServiceProviderConnectionMemento doCreateMemento() {
		ServiceProviderUser user = getUser();		
		return new ServiceProviderConnectionMemento(getKey().getProviderId(), getKey().getProviderUserId(),
				user.getProfileName(), user.getProfileUrl(), user.getProfilePictureUrl(), accessToken, null, refreshToken, expireTime);
	}

	// internal helpers

	private void setAccessFields(String accessToken, String refreshToken, Long expireTime) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.expireTime = expireTime;
	}
	
}