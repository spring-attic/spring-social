package org.springframework.social.gowalla.connect;

import org.springframework.social.connect.oauth2.AbstractOAuth2ServiceProvider;
import org.springframework.social.connect.support.ConnectionRepository;
import org.springframework.social.gowalla.GowallaOperations;
import org.springframework.social.gowalla.GowallaTemplate;
import org.springframework.social.oauth2.OAuth2Template;

public class GowallaServiceProvider extends AbstractOAuth2ServiceProvider<GowallaOperations> {

	public GowallaServiceProvider(String clientId, String clientSecret, ConnectionRepository connectionRepository) {
		super("gowalla", connectionRepository, new OAuth2Template(clientId, clientSecret,
				"https://gowalla.com/api/oauth/new?client_id={client_id}&redirect_uri={redirect_uri}&scope={scope}", "https://gowalla.com/api/oauth/token"));
	}

	protected GowallaOperations getApi(String accessToken) {
		return new GowallaTemplate(accessToken);
	}
	
}
