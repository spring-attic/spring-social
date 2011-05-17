package org.springframework.social.security.provider;

import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.gowalla.api.Gowalla;

public class GowallaAuthenticationService extends OAuth2AuthenticationService<Gowalla> {
	
	public GowallaAuthenticationService() {
		super();
	}

	public GowallaAuthenticationService(OAuth2ConnectionFactory<Gowalla> connectionFactory) {
		super(connectionFactory);
	}

}
