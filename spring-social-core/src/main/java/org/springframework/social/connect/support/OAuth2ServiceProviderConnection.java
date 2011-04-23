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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.springframework.core.GenericTypeResolver;
import org.springframework.social.ServiceProvider;
import org.springframework.social.connect.ServiceApiAdapter;
import org.springframework.social.connect.ServiceProviderConnection;
import org.springframework.social.connect.ServiceProviderConnectionData;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2ServiceProvider;

/**
 * An OAuth2-based ServiceProviderConnection implementation.
 * In general, this implementation is expected to be suitable for all OAuth2-based providers and should not require subclassing.
 * Subclasses of {@link OAuth2ServiceProviderConnectionFactory} should be favored to encapsulate details specific to an OAuth2-based provider.
 * @author Keith Donald
 * @param <S> the service API type
 * @see OAuth2ServiceProviderConnectionFactory
 */
public class OAuth2ServiceProviderConnection<S> extends AbstractServiceProviderConnection<S> {

	private final OAuth2ServiceProvider<S> serviceProvider;

	private String accessToken;
	
	private String refreshToken;
	
	private Long expireTime;

	private S serviceApi;
	
	private S serviceApiProxy;

	/**
	 * Creates a new {@link OAuth2ServiceProviderConnection} from a access grant response.
	 * Designed to be called to establish a new {@link OAuth2ServiceProviderConnection} after receiving an access grant successfully.
	 * The providerUserId may be null in this case: if so, this constructor will try to resolve it using the service API obtained from the {@link OAuth2ServiceProvider}.
	 * @param providerId the provider id e.g. "facebook".
	 * @param providerUserId the provider user id (may be null if not returned as part of the access grant)
	 * @param accessToken the granted access token
	 * @param refreshToken the granted refresh token
	 * @param expireTime the access token expiration time
	 * @param serviceProvider the OAuth2-based ServiceProvider
	 * @param serviceApiAdapter the ServiceApiAdapter for the ServiceProvider
	 */
	public OAuth2ServiceProviderConnection(String providerId, String providerUserId, String accessToken, String refreshToken, Long expireTime,
			OAuth2ServiceProvider<S> serviceProvider, ServiceApiAdapter<S> serviceApiAdapter) {
		super(serviceApiAdapter);
		this.serviceProvider = serviceProvider;
		initAccessTokens(accessToken, refreshToken, expireTime);
		initServiceApi();
		initServiceApiProxy();
		initKey(providerId, providerUserId);
	}
	
	/**
	 * Creates a new {@link OAuth2ServiceProviderConnection} from the data provided.
	 * Designed to be called when re-constituting an existing {@link ServiceProviderConnection} from {@link ServiceProviderConnectionData}.
	 * @param data the data holding the state of this service provider connection
	 * @param serviceProvider the OAuth2-based ServiceProvider
	 * @param serviceApiAdapter the ServiceApiAdapter for the ServiceProvider
	 */
	public OAuth2ServiceProviderConnection(ServiceProviderConnectionData data, OAuth2ServiceProvider<S> serviceProvider, ServiceApiAdapter<S> serviceApiAdapter) {
		super(data, serviceApiAdapter);
		this.serviceProvider = serviceProvider;
		initAccessTokens(data.getAccessToken(), data.getRefreshToken(), data.getExpireTime());
		initServiceApi();
		initServiceApiProxy();
	}

	// implementing ServiceProviderConnection

	public boolean hasExpired() {
		synchronized (getMonitor()) {
			return expireTime != null && System.currentTimeMillis() >= expireTime;
		}
	}

	public void refresh() {
		synchronized (getMonitor()) {
			AccessGrant accessGrant = serviceProvider.getOAuthOperations().refreshAccess(refreshToken, null, null);
			initAccessTokens(accessGrant.getAccessToken(), accessGrant.getRefreshToken(), accessGrant.getExpireTime());
			initServiceApi();
		}
	}

	public S getServiceApi() {
		if (serviceApiProxy != null) {
			return serviceApiProxy;
		} else {
			synchronized (getMonitor()) {
				return serviceApi;
			}
		}
	}

	public ServiceProviderConnectionData createData() {
		synchronized (getMonitor()) {
			return new ServiceProviderConnectionData(getKey().getProviderId(), getKey().getProviderUserId(), getDisplayName(), getProfileUrl(), getImageUrl(), accessToken, null, refreshToken, expireTime);
		}
	}

	// internal helpers
	
	private void initAccessTokens(String accessToken, String refreshToken, Long expireTime) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.expireTime = expireTime;		
	}
	
	private void initServiceApi() {
		serviceApi = serviceProvider.getServiceApi(accessToken);
	}
	
	@SuppressWarnings("unchecked")
	private void initServiceApiProxy() {
		Class<?> serviceApiType = GenericTypeResolver.resolveTypeArgument(serviceProvider.getClass(), ServiceProvider.class);
		if (serviceApiType.isInterface()) {
			serviceApiProxy = (S) Proxy.newProxyInstance(serviceApiType.getClassLoader(), new Class[] { serviceApiType }, new ServiceApiInvocationHandler());
		}		
	}
	
	private class ServiceApiInvocationHandler implements InvocationHandler {

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			synchronized (getMonitor()) {
				if (OAuth2ServiceProviderConnection.this.hasExpired()) {
					throw new IllegalStateException("This OAuth2-based ServiceProviderConnection has expired: it is not possible to invoke the service API");
				}
				return method.invoke(OAuth2ServiceProviderConnection.this.serviceApi, args);				
			}
		}
	}

}