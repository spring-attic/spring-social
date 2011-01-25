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
package org.springframework.security.oauth.client.oauth1;

import org.springframework.security.oauth.client.ProtectedResourceRequest;

/**
 * A service interface for the OAuth1 flow.
 * This interface allows you to conduct the "OAuth dance" with a service provider on behalf of a user.
 * @author Keith Donald
 */
public interface OAuth1Operations {

	/**
	 * Begin the account connection process by fetching a new request token from this service provider.
	 * The request token should be stored in the user's session up until the authorization callback is made and it's time to exchange it for an {@link #exchangeForAccessToken(AuthorizedRequestToken) access token}.
	 * @param callbackUrl the URL the provider should redirect to after the member authorizes the connection 
	 */
	OAuthToken fetchNewRequestToken(String callbackUrl);

	/**
	 * Construct the URL to redirect the user to for connection authorization.
	 * @param requestToken the request token value, to be encoded in the authorize URL.
	 * @return the absolute authorize URL to redirect the member to for authorization
	 */
	String buildAuthorizeUrl(String requestToken);

	/**
	 * Exchange the authorized request token for an access token.
	 * @param requestToken an authorized request token and verifier
	 * @return an access token granted by the provider
	 */
	OAuthToken exchangeForAccessToken(AuthorizedRequestToken requestToken);

	/**
	 * Sign the client http request with OAuth credentials.
	 * To be called before the request is executed.
	 * @param request the client http request
	 * @param accessToken the access token and secret
	 */
	void sign(ProtectedResourceRequest request, OAuthToken accessToken);
	
}
