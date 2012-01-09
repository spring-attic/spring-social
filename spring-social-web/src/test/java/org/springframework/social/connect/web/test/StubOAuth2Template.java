package org.springframework.social.connect.web.test;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.social.connect.web.test.StubOAuthTemplateBehavior.*;

import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

public class StubOAuth2Template extends OAuth2Template {

	private final StubOAuthTemplateBehavior behavior;

	public StubOAuth2Template(String clientId, String clientSecret, String authorizeUrl, String accessTokenUrl, StubOAuthTemplateBehavior behavior) {
		super(clientId, clientSecret, authorizeUrl, null, accessTokenUrl);
		this.behavior = behavior;
	}
	
	@Override
	public AccessGrant exchangeForAccess(String authorizationCode, String redirectUri, MultiValueMap<String, String> additionalParameters) {
		if (behavior == THROW_EXCEPTION) {
			throw new HttpClientErrorException(BAD_REQUEST);
		}
		
		return new AccessGrant("accessToken");
	}
}
