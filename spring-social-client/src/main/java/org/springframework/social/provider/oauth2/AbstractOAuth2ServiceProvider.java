package org.springframework.social.provider.oauth2;

import java.io.Serializable;

import org.springframework.social.provider.AuthorizationProtocol;
import org.springframework.social.provider.ServiceProviderConnection;
import org.springframework.social.provider.support.AbstractServiceProvider;
import org.springframework.social.provider.support.ConnectionRepository;

public class AbstractOAuth2ServiceProvider<S> extends AbstractServiceProvider<S> implements OAuth2ServiceProvider<S> {

	private String clientId;
	
	private String clientSecret;
	
	public AbstractOAuth2ServiceProvider(String id, String displayName, ConnectionRepository connectionRepository, String clientId, String clientSecret) {
		super(id, displayName, connectionRepository);
		this.clientId = clientId;
		this.clientSecret = clientSecret;
	}

	public AuthorizationProtocol getAuthorizationProtocol() {
		return AuthorizationProtocol.OAUTH_2;
	}

	public String buildAuthorizeUrl(String redirectUri, String scope) {
		return null;
	}

	public AccessToken exchangeForAccessToken(String redirectUri, String authorizationCode) {
		return null;
	}

	public ServiceProviderConnection<S> connect(Serializable accountId, AccessToken accessToken) {
		return null;
	}
	
}
