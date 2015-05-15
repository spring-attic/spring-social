/*
 * Copyright 2015 the original author or authors.
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
package org.springframework.social.oauth2;


/**
 * Base class for ServiceProviders that use the OAuth2 protocol.
 * OAuth2-based ServiceProvider implementations should extend and implement {@link #getApi(String)}.
 * @author Keith Donald
 * @param <S> the service API type
 */
public abstract class AbstractOAuth2ServiceProvider<S> implements OAuth2ServiceProvider<S> {

	private final OAuth2Operations oauth2Operations;
	
	/**
	 * Create a new {@link OAuth2ServiceProvider}.
	 * @param oauth2Operations the OAuth2Operations template for conducting the OAuth 2 flow with the provider.
	 */
	public AbstractOAuth2ServiceProvider(OAuth2Operations oauth2Operations) {
		this.oauth2Operations = oauth2Operations;
	}

	// implementing OAuth2ServiceProvider
	
	public final OAuth2Operations getOAuthOperations() {
		return oauth2Operations;
	}

	public abstract S getApi(String accessToken);

}
