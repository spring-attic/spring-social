package org.springframework.social.web.signin;

import java.io.Serializable;

import org.springframework.social.connect.oauth1.OAuth1ServiceProvider;
import org.springframework.social.oauth1.OAuthToken;

public class OAuth1ProviderSignInAccount implements ProviderSignInAccount {

	private OAuth1ServiceProvider<?> serviceProvider;
	
	private String accessToken;
	
	private String accessTokenSecret;
	
	public OAuth1ProviderSignInAccount(OAuth1ServiceProvider<?> serviceProvider, String accessToken, String accessTokenSecret) {
		this.serviceProvider = serviceProvider;
		this.accessToken = accessToken;
		this.accessTokenSecret = accessTokenSecret;
	}

	public void connect(Serializable accountId) {
		serviceProvider.connect(accountId, new OAuthToken(accessToken, accessTokenSecret));		
	}

}
