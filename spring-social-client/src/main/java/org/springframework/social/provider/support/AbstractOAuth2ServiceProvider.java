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
package org.springframework.social.provider.support;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.springframework.social.provider.AuthorizationProtocol;
import org.springframework.social.provider.ServiceProviderConnection;
import org.springframework.social.provider.oauth2.AccessToken;
import org.springframework.social.provider.oauth2.OAuth2ServiceProvider;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

/**
 * Base class for ServiceProvider implementations that use OAuth 2
 * authorization.
 * 
 * @author Craig Walls
 * @param <S> The service API hosted by this service provider.
 */
public abstract class AbstractOAuth2ServiceProvider<S> extends AbstractServiceProvider<S> implements
		OAuth2ServiceProvider<S> {

	public AbstractOAuth2ServiceProvider(ServiceProviderParameters parameters,
			AccountConnectionRepository connectionRepository) {
		super(parameters, connectionRepository);
	}

	// OAuth2ServiceProvider
	public String buildAuthorizeUrl(String redirectUri, String scope) {
		Map<String, String> authorizationParameters = new HashMap<String, String>();
		authorizationParameters.put("clientId", getApiKey());
		authorizationParameters.put("redirectUri", redirectUri);
		authorizationParameters.put("scope", scope);
		return parameters.getAuthorizeUrl().expand(authorizationParameters).toString();
	}

	public ServiceProviderConnection<S> connect(Serializable accountId, AccessToken accessToken) {
		// TODO Auto-generated method stub
		return null;
	}

	public AccessToken exchangeForAccessToken(String redirectUri, String code) {
		Map<String, String> tokenRequestParameters = new HashMap<String, String>();
		tokenRequestParameters.put("client_id", parameters.getApiKey());
		tokenRequestParameters.put("client_secret", parameters.getSecret());
		tokenRequestParameters.put("code", code);
		tokenRequestParameters.put("redirect_uri", redirectUri);
		tokenRequestParameters.put("grant_type", "authorization_code");
		AccessToken accessToken = fetchAccessToken(tokenRequestParameters);
		return accessToken;
	}

	
	// other
	@Override
	public void refreshConnection(Serializable accountId, String providerAccountId) {
		ConnectionToken connectionToken = connectionRepository.getAccessToken(accountId, getId(), providerAccountId);
		String refreshToken = connectionToken.getRefreshToken();
		if (refreshToken == null) {
			throw new UnsupportedOperationException("Connection refresh is not supported for this provider.");
		}

		// TODO : Look into reducing some of the duplication between this method
		// and the connect() method above
		Map<String, String> tokenRequestParameters = new HashMap<String, String>();
		tokenRequestParameters.put("client_id", parameters.getApiKey());
		tokenRequestParameters.put("client_secret", parameters.getSecret());
		tokenRequestParameters.put("refresh_token", refreshToken);
		tokenRequestParameters.put("grant_type", "refresh_token");
		AccessToken accessToken = fetchAccessToken(tokenRequestParameters);
		S serviceOperations = createServiceOperations(accessToken);
		String username = fetchProviderAccountId(serviceOperations);
		connectionRepository.updateConnection(accountId, getId(), accessToken, username);
	}

	public AuthorizationProtocol getAuthorizationProtocol() {
		return AuthorizationProtocol.OAUTH_2;
	}

	protected AccessToken fetchAccessToken(Map<String, String> tokenRequestParameters) {
		@SuppressWarnings("unchecked")
		Map<String, String> result = getRestOperations().postForObject(parameters.getAccessTokenUrl(),
				tokenRequestParameters, Map.class);
		return new AccessToken(result.get("access_token"), result.get("refresh_token"));
	}

	protected RestOperations getRestOperations() {
		return new RestTemplate();
	}
}
