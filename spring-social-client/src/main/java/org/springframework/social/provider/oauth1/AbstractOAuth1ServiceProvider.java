package org.springframework.social.provider.oauth1;

import java.io.Serializable;

import org.springframework.social.provider.AuthorizationProtocol;
import org.springframework.social.provider.ServiceProviderConnection;
import org.springframework.social.provider.support.AbstractServiceProvider;
import org.springframework.social.provider.support.ConnectionRepository;

public class AbstractOAuth1ServiceProvider<S> extends AbstractServiceProvider<S> implements OAuth1ServiceProvider<S> {

	private final String consumerKey;
	
	private final String consumerSecret;
	
	private final String requestTokenUrl;
	
	private final String authorizeUrl;
	
	private final String accessTokenUrl;
	
	public AbstractOAuth1ServiceProvider(String id, String displayName, ConnectionRepository connectionRepository, String consumerKey, String consumerSecret,
			String  requestTokenUrl, String authorizeUrl, String accessTokenUrl) {
		super(id, displayName, connectionRepository);
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.requestTokenUrl = requestTokenUrl;
		this.authorizeUrl = authorizeUrl;
		this.accessTokenUrl = accessTokenUrl;
	}

	public AuthorizationProtocol getAuthorizationProtocol() {
		return AuthorizationProtocol.OAUTH_1;
	}

	public OAuthToken fetchNewRequestToken(String callbackUrl) {
		return null;
	}

	public String buildAuthorizeUrl(String requestToken) {
		return null;
	}

	public OAuthToken exchangeForAccessToken(AuthorizedRequestToken requestToken) {
		return null;
	}

	public ServiceProviderConnection<S> connect(Serializable accountId, OAuthToken accessToken) {
		return null;
	}
	
}
