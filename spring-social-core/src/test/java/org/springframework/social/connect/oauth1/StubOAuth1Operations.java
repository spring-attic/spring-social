package org.springframework.social.connect.oauth1;

import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuthToken;

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