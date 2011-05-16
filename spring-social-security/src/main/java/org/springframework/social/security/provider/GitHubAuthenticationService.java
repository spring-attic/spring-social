package org.springframework.social.security.provider;

import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.github.api.GitHub;

public class GitHubAuthenticationService extends OAuth2AuthenticationService<GitHub> {
	
	public GitHubAuthenticationService() {
		super();
	}

	public GitHubAuthenticationService(OAuth2ConnectionFactory<GitHub> connectionFactory) {
		super(connectionFactory);
	}

}
