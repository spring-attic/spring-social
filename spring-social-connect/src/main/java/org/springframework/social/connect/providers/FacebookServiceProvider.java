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
package org.springframework.social.connect.providers;

import java.util.Map;

import org.springframework.social.connect.AbstractOAuth2ServiceProvider;
import org.springframework.social.connect.AccountConnectionRepository;
import org.springframework.social.connect.OAuth2Tokens;
import org.springframework.social.connect.OAuthToken;
import org.springframework.social.connect.ServiceProviderParameters;
import org.springframework.social.facebook.FacebookOperations;
import org.springframework.social.facebook.FacebookTemplate;
import org.springframework.web.client.RestTemplate;

/**
 * Facebook ServiceProvider implementation.
 * @author Keith Donald
 */
public final class FacebookServiceProvider extends AbstractOAuth2ServiceProvider<FacebookOperations> {
	
	public FacebookServiceProvider(ServiceProviderParameters parameters,
			AccountConnectionRepository connectionRepository) {
		super(parameters, connectionRepository);
	}

	/*
	 * This method is necessary because Facebook fails to properly adhere to the
	 * latest OAuth 2 specification draft (draft 11). In particular Facebook
	 * differs from the spec in the following 2 ways:
	 * 
	 * 1. The OAuth 2 spec says requests for access tokens should be made via
	 * POST requests. Facebook only supports GET requests.
	 * 
	 * 2. The OAuth 2 spec limits the access token response to application/json
	 * content type. Facebook responds with what appears to be form-encoded data
	 * but with Content-Type set to "text/plain".
	 * 
	 * In addition, the OAuth 2 spec speaks of a refresh token that may be
	 * returned so that the client can refresh the access token after it
	 * expires. Facebook does not support refresh tokens.
	 */
	@Override
	protected OAuth2Tokens fetchOAuth2AccessToken(Map<String, String> tokenRequestParameters) {
		String response = new RestTemplate().getForObject(parameters.getAccessTokenUrl() + ACCESS_TOKEN_QUERY_PARAMETERS,
				String.class, tokenRequestParameters);
		String[] nameValuePairs = response.split("\\&");
		for (String nameValuePair : nameValuePairs) {
			String[] nameAndValue = nameValuePair.split("=");
			if (nameAndValue[0].equals("access_token")) {
				return new OAuth2Tokens(new OAuthToken(nameAndValue[1]), null);
			}
		}

		return new OAuth2Tokens(null, null);
	}

	protected FacebookOperations createServiceOperations(OAuthToken accessToken) {
		if (accessToken == null) {
			throw new IllegalStateException("Cannot access Facebook without an access token");
		}
		return new FacebookTemplate(accessToken.getValue());
	}

	protected String fetchProviderAccountId(FacebookOperations facebook) {
		return facebook.getProfileId();
	}

	protected String buildProviderProfileUrl(String facebookId, FacebookOperations facebook) {
		return "http://www.facebook.com/profile.php?id=" + facebookId;
	}

	private static final String ACCESS_TOKEN_QUERY_PARAMETERS = "?client_id={client_id}&client_secret={client_secret}&code={code}&redirect_uri={redirect_uri}";

}