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
package org.springframework.social.provider.oauth1;

import org.springframework.security.oauth.common.signature.SharedConsumerSecret;
import org.springframework.security.oauth.consumer.BaseProtectedResourceDetails;
import org.springframework.security.oauth.consumer.CoreOAuthConsumerSupport;
import org.springframework.security.oauth.consumer.OAuthConsumerSupport;
import org.springframework.security.oauth.consumer.ProtectedResourceDetails;
import org.springframework.security.oauth.consumer.token.OAuthConsumerToken;
import org.springframework.web.util.UriTemplate;

/**
 * OAuth1Operations implementation that uses REST-template to make the OAuth calls.
 * @author Keith Donald
 */
public class OAuth1Template implements OAuth1Operations {

	private final String consumerKey;
	
	private final String consumerSecret;
	
	private final String requestTokenUrl;
	
	private final UriTemplate authorizeUrlTemplate;
	
	private final String accessTokenUrl;

	private OAuthConsumerSupport oauthConsumerSupport;
	
	public OAuth1Template(String consumerKey, String consumerSecret, String requestTokenUrl, String authorizeUrl, String accessTokenUrl) {
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.requestTokenUrl = requestTokenUrl;
		this.authorizeUrlTemplate = new UriTemplate(authorizeUrl);
		this.accessTokenUrl = accessTokenUrl;
		this.oauthConsumerSupport = new CoreOAuthConsumerSupport();
	}

	public OAuthToken fetchNewRequestToken(String callbackUrl) {
		OAuthConsumerToken requestToken = getOAuthConsumerSupport().getUnauthorizedRequestToken(
				getProtectedResourceDetails(), callbackUrl);
		return new OAuthToken(requestToken.getValue(), requestToken.getSecret());
	}

	public String buildAuthorizeUrl(String requestToken) {
		return authorizeUrlTemplate.expand(requestToken).toString();
	}

	public OAuthToken exchangeForAccessToken(AuthorizedRequestToken requestToken) {
		OAuthConsumerToken consumerToken = new OAuthConsumerToken();
		consumerToken.setValue(requestToken.getValue());
		consumerToken.setSecret(requestToken.getSecret());
		OAuthConsumerToken accessToken = getOAuthConsumerSupport().getAccessToken(getProtectedResourceDetails(),
				consumerToken, requestToken.getVerifier());
		return new OAuthToken(accessToken.getValue(), accessToken.getSecret());
	}

	private ProtectedResourceDetails getProtectedResourceDetails() {
		BaseProtectedResourceDetails details = new BaseProtectedResourceDetails();
		details.setConsumerKey(consumerKey);
		details.setSharedSecret(new SharedConsumerSecret(consumerSecret));
		details.setRequestTokenURL(requestTokenUrl);
		details.setAccessTokenURL(accessTokenUrl);
		return details;
	}

	protected OAuthConsumerSupport getOAuthConsumerSupport() {
		return oauthConsumerSupport;
	}
}
