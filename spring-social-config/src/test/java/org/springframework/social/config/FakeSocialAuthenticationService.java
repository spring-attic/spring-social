package org.springframework.social.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.security.SocialAuthenticationRedirectException;
import org.springframework.social.security.SocialAuthenticationToken;
import org.springframework.social.security.provider.SocialAuthenticationService;

public class FakeSocialAuthenticationService implements SocialAuthenticationService<Fake> {

	private FakeConnectionFactory connectionFactory;

	public FakeSocialAuthenticationService(String appId, String appSecret) {
		connectionFactory = new FakeConnectionFactory(appId, appSecret);
	}
	
	public org.springframework.social.security.provider.SocialAuthenticationService.ConnectionCardinality getConnectionCardinality() {
		return null;
	}

	public ConnectionFactory<Fake> getConnectionFactory() {
		return connectionFactory;
	}

	public SocialAuthenticationToken getAuthToken(HttpServletRequest request, HttpServletResponse response) throws SocialAuthenticationRedirectException {
		return null;
	}

	public String getConnectionAddedRedirectUrl(HttpServletRequest request, Connection<?> connection) {
		return null;
	}

}
