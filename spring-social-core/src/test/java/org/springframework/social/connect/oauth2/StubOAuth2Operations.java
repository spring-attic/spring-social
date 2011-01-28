package org.springframework.social.connect.oauth2;

import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;

class StubOAuth2Operations implements OAuth2Operations {

	public String buildAuthorizeUrl(String redirectUri, String scope) {
		return "http://springsource.org/oauth/authorize?scope=" + scope;
	}

	public AccessGrant exchangeForAccess(String authorizationGrant, String redirectUri) {
		return new AccessGrant("12345", "23456");
	}
	
}