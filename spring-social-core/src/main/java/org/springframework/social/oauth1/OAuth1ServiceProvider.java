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
package org.springframework.social.oauth1;

import org.springframework.social.ServiceProvider;

/**
 * A ServiceProvider that uses the OAuth 1.0 protocol.
 * @author Keith Donald
 * @param <A> The service provider's API type
 */
public interface OAuth1ServiceProvider<A> extends ServiceProvider<A> {

	/**
	 * Get the service interface for carrying out the "OAuth dance" with this provider.
	 * The result of the OAuth dance is an access token that can be used to obtain a {@link #getApi(String, String) API binding}.
	 * @return the service interface for carrying out the "OAuth dance" with this provider.
	 */
	OAuth1Operations getOAuthOperations();

	/**
	 * Returns an API interface allowing the client application to access protected resources on behalf of a user.
	 * @param accessToken the API access token
	 * @param secret the access token secret
	 * @return the binding to the service provider's API
	 */
	A getApi(String accessToken, String secret);

}
