package org.springframework.social.web.signin;

import java.io.Serializable;

import org.springframework.social.connect.oauth2.OAuth2ServiceProvider;
import org.springframework.social.oauth2.AccessGrant;

public class OAuth2ProviderSignInAccount implements ProviderSignInAccount {

	private OAuth2ServiceProvider<?> serviceProvider;
	
	private String accessToken;
	
	public OAuth2ProviderSignInAccount(OAuth2ServiceProvider<?> serviceProvider, String accessToken) {
		this.serviceProvider = serviceProvider;
	}

	public void connect(Serializable accountId) {
		serviceProvider.connect(accountId, new AccessGrant(accessToken));		
	}

}
