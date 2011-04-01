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
package org.springframework.social.connect;

import java.io.Serializable;

import org.springframework.social.connect.spi.ProviderProfile;
import org.springframework.social.connect.spi.ServiceApiAdapter;

final class ServiceProviderConnectionImpl<S> implements ServiceProviderConnection<S> {

	private final Long id;
	
	private final Serializable accountId;
	
	private final String providerId;
	
	private ProviderProfile profile;

	private final Object profileMonitor = new Object();
	
	private final boolean allowSignIn;
	
	private final ApiTokens apiTokens;
	
	private final S serviceApi;
	
	private final ServiceApiAdapter<S> serviceApiAdapter;
	
 	public ServiceProviderConnectionImpl(String providerId, boolean allowSignIn, ApiTokens apiTokens, S serviceApi, ServiceApiAdapter<S> serviceApiAdapter) {
 		this(null, null, providerId, null, allowSignIn, apiTokens, serviceApi, serviceApiAdapter);
 	}
 	
	public ServiceProviderConnectionImpl(ServiceProviderConnectionMemento connectionMemento, S serviceApi, ServiceApiAdapter<S> serviceApiAdapter) {
		this.id = connectionMemento.getId();
		this.accountId = connectionMemento.getAccountId();
		this.providerId = connectionMemento.getProviderId();
		this.profile = new ProviderProfile(connectionMemento.getProviderAccountId(), connectionMemento.getProfileName(), connectionMemento.getProfileUrl(), connectionMemento.getProfilePictureUrl());
		this.allowSignIn = connectionMemento.isAllowSignIn();
		this.apiTokens = new ApiTokens(connectionMemento.getAccessToken(), connectionMemento.getSecret(), connectionMemento.getRefreshToken());
		this.serviceApi = serviceApi;
		this.serviceApiAdapter = serviceApiAdapter;
	}
 	
	public Long getId() {
		return id;
	}

	public Serializable getAccountId() {
		return accountId;
	}

	public String getProviderId() {
		return providerId;
	}

	public String getProviderAccountId() {
		return getProviderProfile().getId();
	}

	public String getProfileName() {
		return getProviderProfile().getName();
	}

	public String getProfileUrl() {
		return getProviderProfile().getUrl();
	}

	public String getProfilePictureUrl() {
		return getProviderProfile().getPictureUrl();
	}

	public boolean allowSignIn() {
		return allowSignIn;
	}

	public boolean test() {
		return serviceApiAdapter.test(serviceApi);
	}

	public void updateStatus(String message) {
		serviceApiAdapter.updateStatus(serviceApi, message);
	}

	public void sync() {
		synchronized (profileMonitor) {
			profile = fetchProfile();
		}
	}

	public S getServiceApi() {
		return serviceApi;
	}

	public ServiceProviderConnection<S> assignAccountId(Serializable accountId) {
		return new ServiceProviderConnectionImpl<S>(id, accountId, providerId, profile, allowSignIn, apiTokens, serviceApi, serviceApiAdapter);
	}
	
	public ServiceProviderConnection<S> assignId(Long id) {
		return new ServiceProviderConnectionImpl<S>(id, accountId, providerId, profile, allowSignIn, apiTokens, serviceApi, serviceApiAdapter);
	}

	public ServiceProviderConnectionMemento createMemento() {
		ProviderProfile profile = getProviderProfile();
		return new ServiceProviderConnectionMemento(id, accountId, providerId, profile.getId(), profile.getName(), profile.getUrl(), profile.getPictureUrl(),
				allowSignIn, apiTokens.getAccessToken(), apiTokens.getSecret(), apiTokens.getRefreshToken());
	}

	// identity

	@SuppressWarnings("rawtypes")
	public boolean equals(Object o) {
		if (!(o instanceof ServiceProviderConnectionImpl)) {
			return false;
		}
		ServiceProviderConnectionImpl other = (ServiceProviderConnectionImpl) o;
		if (id != null && other.id != null) {
			return accountId.equals(other.accountId) && providerId.equals(other.providerId) && id.equals(other.id);
		} else {
			String providerAccountId = getProviderAccountId();
			String otherProviderAccountId = other.getProviderAccountId();
			if (id == null && other.id == null && providerAccountId != null && otherProviderAccountId != null) {
				return providerId.equals(other.providerId) && providerAccountId.equals(otherProviderAccountId);
			} else {
				return this == other;
			}
		}
	}
	
	public int hashCode() {
		if (id != null) {
			return accountId.hashCode() + providerId.hashCode() + id.hashCode();
		} else {
			String providerAccountId = getProviderAccountId();
			if (providerAccountId != null) {
				return providerId.hashCode() + providerAccountId.hashCode();
			} else {
				return providerId.hashCode();
			}			
		}
	}
	
	// internal helpers

 	private ServiceProviderConnectionImpl(Long id, Serializable accountId, String providerId, ProviderProfile profile,
 			boolean allowSignIn, ApiTokens apiTokens, S serviceApi, ServiceApiAdapter<S> serviceApiAdapter) {
		this.id = id;
		this.accountId = accountId;
		this.providerId = providerId;
		this.profile = profile;
		this.allowSignIn = allowSignIn;
		this.apiTokens = apiTokens;
		this.serviceApi = serviceApi;
		this.serviceApiAdapter = serviceApiAdapter;
	}
 	
	private ProviderProfile getProviderProfile() {
		synchronized (profileMonitor) {
			if (profile == null) {
				profile = fetchProfile();
			}
			return profile;
		}
	}
	
	private ProviderProfile fetchProfile() {
		return serviceApiAdapter.getProfile(serviceApi);
	}

}