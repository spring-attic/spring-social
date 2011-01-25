package org.springframework.social.provider.oauth1;

import org.springframework.security.oauth.client.oauth1.AuthorizedRequestToken;
import org.springframework.security.oauth.client.oauth1.OAuth1Operations;
import org.springframework.security.oauth.client.oauth1.OAuthToken;

class StubOAuth1Operations implements OAuth1Operations {

	public OAuthToken fetchNewRequestToken(String callbackUrl) {
		return new OAuthToken("12345", "23456");
	}

	public String buildAuthorizeUrl(String requestToken) {
		return "http://springsource.org/oauth/authorize?request_token=" + requestToken;
	}

	public OAuthToken exchangeForAccessToken(AuthorizedRequestToken requestToken) {
		return new OAuthToken("34567", "45678");
	}
	
}