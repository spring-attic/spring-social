/*
 * Copyright 2010 the original author or authors.
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
package org.springframework.social.provider.support;

import java.io.Serializable;
import java.util.List;

import org.springframework.social.provider.ServiceProvider;
import org.springframework.social.provider.ServiceProviderConnection;
import org.springframework.social.provider.oauth2.AccessToken;

/**
 * General-purpose base class for ServiceProvider implementations.
 * @author Keith Donald
 * @param <S> The service API hosted by this service provider.
 */
public abstract class AbstractServiceProvider<S> implements ServiceProvider<S> {
	
	protected final ServiceProviderParameters parameters;

	protected final AccountConnectionRepository connectionRepository;
	
	/**
	 * Creates a ServiceProvider.
	 * @param parameters the parameters needed to implement the behavior in this class
	 * @param connectionRepository a data access interface for managing account connection records
	 */
	public AbstractServiceProvider(ServiceProviderParameters parameters,
			AccountConnectionRepository connectionRepository) {
		this.parameters = parameters;
		this.connectionRepository = connectionRepository;
	}

	// provider meta-data
	
	public String getId() {
		return parameters.getName();
	}
	
	
	public String getApiKey() {
		return parameters.getApiKey();
	}

	public Long getAppId() {
		return parameters.getAppId();
	}
	
	// ServiceProvider
	public List<ServiceProviderConnection<S>> getConnections(Serializable accountId) {
		connectionRepository.getAccountConnections(accountId, getId());
		// TODO Auto-generated method stub
		return null;
	}

	public String getDisplayName() {
		return parameters.getDisplayName();
	}

	public boolean isConnected(Serializable accountId) {
		return connectionRepository.isConnected(accountId, getId());
	}

	// connection management
	// This addConnection() method is used only by FacebookConnectController
	// Wonder if it can go away and one of the other connection methods be used
	// instead
//	public void addConnection(Serializable accountId, String accessToken, String providerAccountId) {
//		AccessToken oauthAccessToken = new AccessToken(accessToken);
//		S serviceOperations = createServiceOperations(oauthAccessToken);
//		connectionRepository.addConnection(accountId, getId(), oauthAccessToken, providerAccountId,
//				buildProviderProfileUrl(providerAccountId, serviceOperations));
//	}


	// public void disconnect(Serializable accountId) {
	// connectionRepository.disconnect(accountId, getId());
	// }
	//
	// public void disconnect(Serializable accountId, String providerAccountId)
	// {
	// connectionRepository.disconnect(accountId, getId(), providerAccountId);
	// }

	// @Transactional
	// public S getServiceOperations(Serializable accountId) {
	// if (accountId == null || !isConnected(accountId)) {
	// return createServiceOperations(null);
	// }
	// AccessToken accessToken = connectionRepository.getAccessToken(accountId,
	// getId());
	// return createServiceOperations(accessToken);
	// }
	//
	// public S getServiceOperations(AccessToken accessToken) {
	// return createServiceOperations(accessToken);
	// }
	//
	// public S getServiceOperations(Serializable accountId, String
	// providerAccountId) {
	// AccessToken accessToken = connectionRepository.getAccessToken(accountId,
	// getId(), providerAccountId);
	// return createServiceOperations(accessToken);
	// }

	// public Collection<AccountConnection> getConnections(Serializable
	// accountId) {
	// return connectionRepository.getAccountConnections(accountId, getId());
	// }

	// public String buildAuthorizeUrl(Map<String, String>
	// authorizationParameters) {
	// Map<String, String> authParametersCopy = new HashMap<String,
	// String>(authorizationParameters);
	// authParametersCopy.put("clientId", getApiKey());
	// return
	// parameters.getAuthorizeUrl().expand(authParametersCopy).toString();
	// }

	// additional finders

	// public String getProviderAccountId(Serializable accountId) {
	// return connectionRepository.getProviderAccountId(accountId, getId());
	// }

	// subclassing hooks
	/**
	 * Construct the strongly-typed service API template that callers may use to invoke the service offered by this service provider.
	 * Subclasses should override to return their concrete service implementation.
	 * @param accessToken the granted access token needed to make authorized requests for protected resources
	 */
	protected abstract S createServiceOperations(AccessToken accessToken);

	/**
	 * Use the service API to fetch the id the member has been assigned in the provider's system.
	 * This id is stored locally to support linking to the user's connected profile page.
	 */
	protected abstract String fetchProviderAccountId(S serviceOperations);

	/**
	 * Build the URL pointing to the member's public profile on the provider's system.
	 * @param providerAccountId the id the member is known by in the provider's system.
	 * @param serviceOperations the service API
	 */
	protected abstract String buildProviderProfileUrl(String providerAccountId, S serviceOperations);

	/**
	 * The {@link #getApiKey() apiKey} secret.
	 */
	protected String getSecret() {
		return parameters.getSecret();
	}

//	public void connect(Serializable accountId, AccessToken accessToken) {
//		S serviceOperations = createServiceOperations(accessToken);
//		String providerAccountId = fetchProviderAccountId(serviceOperations);
//		connectionRepository.addConnection(accountId, getId(), accessToken, providerAccountId,
//				buildProviderProfileUrl(providerAccountId, serviceOperations));
//	}
}