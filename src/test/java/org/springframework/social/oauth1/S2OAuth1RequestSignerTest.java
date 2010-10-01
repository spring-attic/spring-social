package org.springframework.social.oauth1;


public class S2OAuth1RequestSignerTest extends AbstractOAuth1RequestSignerTest {
	public OAuth1ClientRequestSigner getSigner() {
		return new S2OAuth1RequestSigner("API_KEY", "API_SECRET", "TOKEN_VALUE", "TOKEN_SECRET");
	}
}
