package org.springframework.social.oauth1;


public class ScribeOAuth1RequestSignerTest extends AbstractOAuth1RequestSignerTest {
	public OAuth1ClientRequestSigner getSigner() {
		return new ScribeOAuth1RequestSigner("API_KEY", "API_SECRET", "TOKEN_VALUE", "TOKEN_SECRET");
	}
}
