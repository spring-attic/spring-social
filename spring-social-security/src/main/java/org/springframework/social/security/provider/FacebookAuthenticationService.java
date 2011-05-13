package org.springframework.social.security.provider;

import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.facebook.api.Facebook;

public class FacebookAuthenticationService extends OAuth2AuthenticationService<Facebook> {
	
	public FacebookAuthenticationService() {
		super();
	}

	public FacebookAuthenticationService(OAuth2ConnectionFactory<Facebook> connectionFactory) {
		super(connectionFactory);
	}

}
