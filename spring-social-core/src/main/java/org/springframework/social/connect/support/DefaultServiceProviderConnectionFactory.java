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

import java.util.Map;

import org.springframework.core.GenericTypeResolver;
import org.springframework.social.ServiceProvider;
import org.springframework.social.connect.ServiceProviderConnection;
import org.springframework.social.connect.ServiceProviderConnectionFactory;
import org.springframework.social.connect.ServiceProviderConnectionMemento;
import org.springframework.social.connect.ServiceProviderRegistry;
import org.springframework.social.connect.spi.ProviderProfile;
import org.springframework.social.connect.spi.ServiceApiAdapter;
import org.springframework.social.oauth1.OAuth1ServiceProvider;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2ServiceProvider;

public class DefaultServiceProviderConnectionFactory implements ServiceProviderConnectionFactory {

	private ServiceProviderRegistry serviceProviderRegistry;
	
	private Map<ServiceProvider<?>, ServiceApiAdapter<?>> serviceApiAdapters;
	
	public void addServiceApiAdapter(ServiceApiAdapter<?> serviceApiAdapter) {
		Class<?> serviceApiType = GenericTypeResolver.resolveTypeArgument(serviceApiAdapter.getClass(), ServiceApiAdapter.class);
		serviceApiAdapters.put(serviceProviderRegistry.getServiceProviderByApi(serviceApiType), serviceApiAdapter);
	}
	
	public <S> ServiceProviderConnection<S> createOAuth1Connection(OAuth1ServiceProvider<S> provider, OAuthToken accessToken) {
		return new ServiceProviderConnectionImpl<S>(serviceProviderRegistry.providerId(provider), true,
				new ApiTokens(accessToken.getValue(), accessToken.getSecret(), null),
				provider.getServiceApi(accessToken.getValue(), accessToken.getSecret()),
				getServiceApiAdapter(provider));
	}

	public <S> ServiceProviderConnection<S> createOAuth2Connection(OAuth2ServiceProvider<S> provider, AccessGrant accessGrant) {
		return new ServiceProviderConnectionImpl<S>(serviceProviderRegistry.providerId(provider), true,
				new ApiTokens(accessGrant.getAccessToken(), null, accessGrant.getRefreshToken()),
				provider.getServiceApi(accessGrant.getAccessToken()),
				getServiceApiAdapter(provider));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ServiceProviderConnection<?> createConnection(ServiceProviderConnectionMemento connectionMemento) {
		if (connectionMemento.getSecret() != null) {
			OAuth1ServiceProvider<?> provider = serviceProviderRegistry.getServiceProviderById(connectionMemento.getProviderId(), OAuth1ServiceProvider.class);
			return new ServiceProviderConnectionImpl(connectionMemento,
					provider.getServiceApi(connectionMemento.getAccessToken(), connectionMemento.getSecret()),
					getServiceApiAdapter(provider));
		} else {
			OAuth2ServiceProvider<?> provider = serviceProviderRegistry.getServiceProviderById(connectionMemento.getProviderId(), OAuth2ServiceProvider.class);
			return new ServiceProviderConnectionImpl(connectionMemento,
					provider.getServiceApi(connectionMemento.getAccessToken()),
					getServiceApiAdapter(provider));	
		}
	}

	// internal helpers
	
	@SuppressWarnings("unchecked")
	private <S> ServiceApiAdapter<S> getServiceApiAdapter(ServiceProvider<S> provider) {
		if (provider instanceof ServiceApiAdapter) {
			return (ServiceApiAdapter<S>) provider;
		}
		ServiceApiAdapter<S> apiAdapter = (ServiceApiAdapter<S>) serviceApiAdapters.get(provider);
		return apiAdapter != null ? apiAdapter : (ServiceApiAdapter<S>) DEFAULT_SERVICE_API_ADAPTER;
	}

	private static final ServiceApiAdapter<Object> DEFAULT_SERVICE_API_ADAPTER = new ServiceApiAdapter<Object>() {

		public boolean test(Object serviceApi) {
			return true;
		}

		public ProviderProfile getProfile(Object serviceApi) {
			return EMPTY_PROFILE;
		}

		public void updateStatus(Object serviceApi, String message) {
		}
		
	};
	
	private static final ProviderProfile EMPTY_PROFILE = new ProviderProfile(null, null, null, null);
	
}