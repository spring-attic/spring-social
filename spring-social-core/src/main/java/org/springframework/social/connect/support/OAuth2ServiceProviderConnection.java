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
import org.springframework.social.connect.ServiceProviderConnection;
import org.springframework.social.connect.ServiceProviderConnectionData;
import org.springframework.social.connect.ServiceProviderConnectionKey;
import org.springframework.social.connect.ServiceProviderUser;
import org.springframework.social.connect.spi.ServiceApiAdapter;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2ServiceProvider;

public class OAuth2ServiceProviderConnection<S> implements ServiceProviderConnection<S> {

	private final ServiceProviderConnectionKey key;

	private final OAuth2ServiceProvider<S> serviceProvider;

	private final ServiceApiAdapter<S> serviceApiAdapter;

	private ServiceProviderUser user;

	private String accessToken;
	
	private String refreshToken;
	
	private Long expireTime;

	private S serviceApi;
	
	private S serviceApiProxy;

	private final Object monitor = new Object();

	public OAuth2ServiceProviderConnection(String providerId, String providerUserId, String accessToken, String refreshToken, Long expireTime,
			OAuth2ServiceProvider<S> serviceProvider, ServiceApiAdapter<S> serviceApiAdapter) {
		this.serviceProvider = serviceProvider;
		this.serviceApiAdapter = serviceApiAdapter;
		initAccessTokens(accessToken, refreshToken, expireTime);
		initServiceApi();
		initServiceApiProxy();
		this.key = createKey(providerId, providerUserId);
	}
	
	public OAuth2ServiceProviderConnection(ServiceProviderConnectionKey key, ServiceProviderUser user, String accessToken, String refreshToken, Long expireTime,
			OAuth2ServiceProvider<S> serviceProvider, ServiceApiAdapter<S> serviceApiAdapter) {
		this.key = key;
		this.user = user;
		this.serviceProvider = serviceProvider;
		this.serviceApiAdapter = serviceApiAdapter;
		initAccessTokens(accessToken, refreshToken, expireTime);
		initServiceApi();
		initServiceApiProxy();
	}

	public ServiceProviderConnectionKey getKey() {
		return key;
	}

	public ServiceProviderUser getUser() {
		synchronized (monitor) {
			if (user == null) {
				initUser();
			}			
			return user;
		}
	}

	public boolean test() {
		return serviceApiAdapter.test(getServiceApi());
	}

	public boolean hasExpired() {
		return System.currentTimeMillis() >= expireTime;
	}

	public void refresh() {
		AccessGrant accessGrant = serviceProvider.getOAuthOperations().refreshAccess(refreshToken);
		initAccessTokens(accessGrant.getAccessToken(), accessGrant.getRefreshToken(), accessGrant.getExpireTime());
		initServiceApi();
	}

	public void updateStatus(String message) {
		serviceApiAdapter.updateStatus(getServiceApi(), message);
	}

	public void sync() {
		synchronized (monitor) {
			initUser();
		}
	}

	public S getServiceApi() {
		if (serviceApiProxy != null) {
			return serviceApiProxy;
		} else {
			synchronized (monitor) {
				return serviceApi;
			}
		}
	}

	public ServiceProviderConnectionData createData() {
		return new ServiceProviderConnectionData(key.getProviderId(), key.getProviderUserId(), user.getProfileName(), user.getProfileUrl(), user.getProfilePictureUrl(), accessToken, null, refreshToken, expireTime);
	}

	// identity

	@SuppressWarnings("rawtypes")
	public boolean equals(Object o) {
		if (!(o instanceof OAuth2ServiceProviderConnection)) {
			return false;
		}
		OAuth2ServiceProviderConnection other = (OAuth2ServiceProviderConnection) o;
		return key.equals(other.key);
	}
	
	public int hashCode() {
		return key.hashCode();
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
			synchronized (monitor) {
				if (OAuth2ServiceProviderConnection.this.hasExpired()) {
					throw new IllegalStateException("This OAuth2-based ServiceProviderConnection has expired: not possible to invoke service API");
				}
				return method.invoke(OAuth2ServiceProviderConnection.this.serviceApi, args);				
			}
		}
	}

	private ServiceProviderConnectionKey createKey(String providerId, String providerUserId) {
		if (providerUserId == null) {
			initUser();
			providerUserId = user.getId();
		}
		return new ServiceProviderConnectionKey(providerId, providerUserId);		
	}
	
	private void initUser() {
		user = serviceApiAdapter.getUser(serviceApi);
	}

}