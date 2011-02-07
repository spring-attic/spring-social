package org.springframework.social.twitter.connect;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.social.oauth1.OAuth1Template;
import org.springframework.social.oauth1.OAuthToken;

public class TwitterOAuth1Template extends OAuth1Template {

	public TwitterOAuth1Template(String consumerKey, String consumerSecret, String requestTokenUrl,
			String authorizeUrl, String accessTokenUrl) {
		super(consumerKey, consumerSecret, requestTokenUrl, authorizeUrl, accessTokenUrl);
	}

	public OAuthToken exchangeForAccessToken(String bridgeCode) {
		Map<String, String> requestParameters = new HashMap<String, String>();
		requestParameters.put("oauth_bridge_code", bridgeCode);
		return getTokenFromProvider(getAccessTokenUrl(), Collections.<String, String> emptyMap(), requestParameters, null);
	}
}
