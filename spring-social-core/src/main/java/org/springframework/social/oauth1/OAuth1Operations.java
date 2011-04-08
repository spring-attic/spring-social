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
package org.springframework.social.oauth1;

import org.springframework.util.MultiValueMap;

/**
 * A service interface for the OAuth 1 flow.
 * This interface allows you to conduct the "OAuth dance" with a service provider on behalf of a user.
 * @author Keith Donald
 */
public interface OAuth1Operations {

	/**
	 * Begin a new authorization flow by fetching a new request token from this service provider.
	 * The request token should be stored in the user's session up until the authorization callback is made and it's time to exchange it for an {@link #exchangeForAccessToken(AuthorizedRequestToken) access token}.
	 * @param callbackUrl the URL the provider should redirect to after the member authorizes the connection. Ignored for OAuth 1.0 providers.
	 * @param additionalParameters any additional query parameters to be sent when fetching the request token. Should not be encoded.
	 * @return a temporary request token use for authorization and exchanged for an access token 
	 */
	OAuthToken fetchRequestToken(String callbackUrl, MultiValueMap<String, String> additionalParameters);

	/**
	 * Construct the URL to redirect the user to for authorization.
	 * @param requestToken the request token value, to be encoded in the authorize URL.
	 * @param callbackUrl the URL the provider should redirect to after user authorization. Ignored for OAuth 1.0a providers. 
	 * @return the absolute authorize URL to redirect the user to for authorization
	 */
	String buildAuthorizeUrl(String requestToken, String callbackUrl);
	
	/**
	 * Construct the URL to redirect the user to for authentication.
	 * The authenticate URL differs from the authorizationUrl slightly in that it does not require the user to authorize the app multiple times.
	 * This provides a better user experience for "Sign in with Provider" scenarios.
	 * @param requestToken the request token value, to be encoded in the authorize URL.
	 * @param callbackUrl the URL the provider should redirect to, returned once the user has signed in and has authorized the app once. Ignored for OAuth 1.0a providers. 
	 * @return the absolute authenticate URL to redirect the user to for authorization
	 */
	String buildAuthenticateUrl(String requestToken, String callbackUrl);

	/**
	 * Exchange the authorized request token for an access token.
	 * @param requestToken an authorized request token and verifier. The verifier will be ignored for OAuth 1.0 providers.
	 * @param additionalParameters any additional query parameters to be sent when fetching the access token. Should not be encoded. 
	 * @return an access token granted by the provider
	 */
	OAuthToken exchangeForAccessToken(AuthorizedRequestToken requestToken, MultiValueMap<String, String> additionalParameters);

}
