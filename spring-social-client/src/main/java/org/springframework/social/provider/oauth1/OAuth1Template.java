package org.springframework.social.provider.oauth1;

public class OAuth1Template implements OAuth1Operations {

	private final String consumerKey;
	
	private final String consumerSecret;
	
	private final String requestTokenUrl;
	
	private final String authorizeUrl;
	
	private final String accessTokenUrl;
	
	public OAuth1Template(String consumerKey, String consumerSecret, String requestTokenUrl, String authorizeUrl, String accessTokenUrl) {
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.requestTokenUrl = requestTokenUrl;
		this.authorizeUrl = authorizeUrl;
		this.accessTokenUrl = accessTokenUrl;
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

}
