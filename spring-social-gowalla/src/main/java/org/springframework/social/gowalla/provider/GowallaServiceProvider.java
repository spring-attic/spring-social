package org.springframework.social.gowalla.provider;

import org.springframework.social.gowalla.GowallaOperations;
import org.springframework.social.gowalla.GowallaTemplate;
import org.springframework.social.provider.oauth2.AbstractOAuth2ServiceProvider;
import org.springframework.social.provider.oauth2.OAuth2Template;
import org.springframework.social.provider.support.ConnectionRepository;

public class GowallaServiceProvider extends AbstractOAuth2ServiceProvider<GowallaOperations> {

	public GowallaServiceProvider(String clientId, String clientSecret, ConnectionRepository connectionRepository) {
		super("gowalla", "Gowalla", connectionRepository, new OAuth2Template(clientId, clientSecret));
	}

	protected GowallaOperations getApi(String accessToken) {
		return new GowallaTemplate(accessToken);
	}
	
}
