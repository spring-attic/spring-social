package org.springframework.social.oauth1;

import static org.hamcrest.core.IsInstanceOf.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class OAuth1RequestSignerFactoryTest {
	@Test(expected = MissingOAuthLibraryException.class)
	public void getRequestSigner_neitherLibraryInClasspath() {
		OAuth1RequestSignerFactory.scribePresent = false;
		OAuth1RequestSignerFactory.s2OAuthPresent = false;

		OAuth1RequestSignerFactory.getRequestSigner("api_key", "api_secret", "access_token", "access_token_secret");
	}

	@Test
	public void getRequestSigner_onlyScribeInClasspath() {
		OAuth1RequestSignerFactory.scribePresent = true;
		OAuth1RequestSignerFactory.s2OAuthPresent = false;

		OAuth1ClientRequestSigner requestSigner = OAuth1RequestSignerFactory.getRequestSigner("api_key", "api_secret",
				"access_token", "access_token_secret");
		assertThat(requestSigner, instanceOf(ScribeOAuth1RequestSigner.class));
	}

	@Test
	public void getRequestSigner_onlyS2OAuthInClasspath() {
		OAuth1RequestSignerFactory.scribePresent = false;
		OAuth1RequestSignerFactory.s2OAuthPresent = true;

		OAuth1ClientRequestSigner requestSigner = OAuth1RequestSignerFactory.getRequestSigner("api_key", "api_secret",
				"access_token", "access_token_secret");
		assertThat(requestSigner, instanceOf(S2OAuth1RequestSigner.class));
	}

	@Test
	public void getRequestSigner_bothScribeAndS2OAuthInClasspath() {
		OAuth1RequestSignerFactory.scribePresent = true;
		OAuth1RequestSignerFactory.s2OAuthPresent = true;

		OAuth1ClientRequestSigner requestSigner = OAuth1RequestSignerFactory.getRequestSigner("api_key", "api_secret",
				"access_token", "access_token_secret");
		assertThat(requestSigner, instanceOf(S2OAuth1RequestSigner.class));
	}
}
