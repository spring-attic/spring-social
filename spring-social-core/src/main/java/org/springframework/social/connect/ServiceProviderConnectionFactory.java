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

import org.springframework.social.connect.support.ServiceProviderConnectionMemento;
import org.springframework.social.oauth1.OAuth1ServiceProvider;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2ServiceProvider;

public interface ServiceProviderConnectionFactory {

	<S> ServiceProviderConnection<S> createOAuth1Connection(OAuth1ServiceProvider<S> provider, OAuthToken accessToken);
	
	<S> ServiceProviderConnection<S> createOAuth2Connection(OAuth2ServiceProvider<S> provider, AccessGrant accessGrant);

	ServiceProviderConnection<?> createConnection(ServiceProviderConnectionMemento connectionMemento);
	
}