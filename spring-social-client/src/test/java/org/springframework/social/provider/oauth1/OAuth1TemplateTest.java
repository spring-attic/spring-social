package org.springframework.social.provider.oauth1;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class OAuth1TemplateTest {

	private static final String ACCESS_TOKEN_URL = "http://www.someprovider.com/oauth/accessToken";
	private static final String REQUEST_TOKEN_URL = "https://www.someprovider.com/oauth/requestToken";
	private OAuth1Template oauth1;

	@Before
	public void setup() {
		oauth1 = new OAuth1Template("consumer_key", "consumer_secret", REQUEST_TOKEN_URL,
				"https://www.someprovider.com/oauth/authorize?oauth_token={request_token}", ACCESS_TOKEN_URL);
	}

	@Test
	public void buildAuthorizeUrl() {
		assertEquals("https://www.someprovider.com/oauth/authorize?oauth_token=request_token", oauth1.buildAuthorizeUrl("request_token"));
	}

	@Test
	public void fetchNewRequestToken() {
		// TODO : Flesh out proper test
	}

	@Test
	public void exchangeForAccessToken() {
		// TODO : Flesh out proper test
	}

}
