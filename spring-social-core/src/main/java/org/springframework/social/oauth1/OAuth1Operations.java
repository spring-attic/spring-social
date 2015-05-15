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

import org.springframework.util.MultiValueMap;

/**
 * A service interface for the OAuth 1 flow.
 * This interface allows you to conduct the "OAuth dance" with a service provider on behalf of a user.
 * @author Keith Donald
 */
public interface OAuth1Operations {

	/**
	 * The version of OAuth1 implemented by this operations instance.
	 * @see OAuth1Version#CORE_10
	 * @see OAuth1Version#CORE_10_REVISION_A
	 * @return The version of OAuth1 implemented by this operations instance.
	 */
	OAuth1Version getVersion();
	
	/**
	 * Begin a new authorization flow by fetching a new request token from this service provider.
	 * The request token should be stored in the user's session up until the authorization callback is made
	 * and it's time to exchange it for an {@link #exchangeForAccessToken(AuthorizedRequestToken, MultiValueMap) access token}.
	 * @param callbackUrl the URL the provider should redirect to after the member authorizes the connection. Ignored for OAuth 1.0 providers.
	 * @param additionalParameters any additional query parameters to be sent when fetching the request token. Should not be encoded.
	 * @return a temporary request token use for authorization and exchanged for an access token 
	 */
	OAuthToken fetchRequestToken(String callbackUrl, MultiValueMap<String, String> additionalParameters);

	/**
	 * Construct the URL to redirect the user to for authorization.
	 * @param requestToken the request token value, to be encoded in the authorize URL.
	 * @param parameters parameters to pass to the provider in the authorize URL. Should never be null; if there are no parameters to pass, set this argument value to {@link OAuth1Parameters#NONE}.
	 * @return the absolute authorize URL to redirect the user to for authorization
	 */
	String buildAuthorizeUrl(String requestToken, OAuth1Parameters parameters);
	
	/**
	 * Construct the URL to redirect the user to for authentication.
	 * For some provider, the authenticate URL differs from the authorizationUrl slightly in that it does not require the user to authorize the app multiple times.
	 * This provides a better user experience for "Sign in with Provider" scenarios.
	 * @param requestToken the request token value, to be encoded in the authorize URL.
	 * @param parameters parameters to pass to the provider in the authenticate URL. Should never be null; if there are no parameters to pass, set this argument value to {@link OAuth1Parameters#NONE}.
	 * @return the absolute authenticate URL to redirect the user to for authentication
	 */
	String buildAuthenticateUrl(String requestToken, OAuth1Parameters parameters);

	/**
	 * Exchange the authorized request token for an access token.
	 * @param requestToken an authorized request token and verifier. The verifier will be ignored for OAuth 1.0 providers.
	 * @param additionalParameters any additional query parameters to be sent when fetching the access token. Should not be encoded. 
	 * @return an access token granted by the provider
	 */
	OAuthToken exchangeForAccessToken(AuthorizedRequestToken requestToken, MultiValueMap<String, String> additionalParameters);

}
