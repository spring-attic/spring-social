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
package org.springframework.social.provider.oauth2;

import java.io.Serializable;

import org.springframework.security.oauth.client.oauth2.AccessCredentials;
import org.springframework.security.oauth.client.oauth2.OAuth2Operations;
import org.springframework.social.provider.ServiceProvider;
import org.springframework.social.provider.ServiceProviderConnection;

/**
 * A ServiceProvider that uses the OAuth 2.0 protocol.
 * @author Keith Donald
 * @param <S> The service API
 */
public interface OAuth2ServiceProvider<S> extends ServiceProvider<S> {

	/**
	 * The service interface for invoking OAuth2 operations against this provider.
	 */
	OAuth2Operations getOAuth2Operations();

	/**
	 * Establishes a connection between a user account and this provider.
	 * @param accountId the user account identifier
	 * @param accessToken the access token returned from {@link OAuth2Operations#exchangeForAccess(String, String)}
	 */	
	ServiceProviderConnection<S> connect(Serializable accountId, AccessCredentials accessToken);

}