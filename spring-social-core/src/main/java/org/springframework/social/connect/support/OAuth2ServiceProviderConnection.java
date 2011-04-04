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

import org.springframework.social.connect.ServiceProviderConnectionKey;
import org.springframework.social.connect.ServiceProviderConnectionData;
import org.springframework.social.connect.ServiceProviderUser;
import org.springframework.social.connect.spi.ServiceApiAdapter;

public class OAuth2ServiceProviderConnection<S> extends AbstractServiceProviderConnection<S> {

	private final OAuth2ServiceApiFactory<S> serviceApiFactory;
	
	public OAuth2ServiceProviderConnection(String providerId, String providerUserId, OAuth2ServiceApiFactory<S> serviceApiFactory, ServiceApiAdapter<S> serviceApiAdapter) {
		super(providerId, providerUserId, serviceApiFactory.createServiceApi(), serviceApiAdapter);
		this.serviceApiFactory = serviceApiFactory;
	}

	public OAuth2ServiceProviderConnection(ServiceProviderConnectionKey key, ServiceProviderUser user, OAuth2ServiceApiFactory<S> serviceApiFactory, ServiceApiAdapter<S> serviceApiAdapter) {
		super(key, user, serviceApiFactory.createServiceApi(), serviceApiAdapter);
		this.serviceApiFactory = serviceApiFactory;		
	}
	
	// subclassing hooks

	@Override
	protected S doRefresh() {
		return serviceApiFactory.refresh();
	}
	
	@Override
	public boolean hasExpired() {
		return serviceApiFactory.hasExpired();
	}

	@Override
	protected ServiceProviderConnectionData doCreateData() {
		ServiceProviderUser user = getUser();		
		return new ServiceProviderConnectionData(getKey().getProviderId(), getKey().getProviderUserId(), user.getProfileName(), user.getProfileUrl(), user.getProfilePictureUrl(),
				serviceApiFactory.getAccessToken(), null, serviceApiFactory.getRefreshToken(), serviceApiFactory.getExpireTime());
	}

}