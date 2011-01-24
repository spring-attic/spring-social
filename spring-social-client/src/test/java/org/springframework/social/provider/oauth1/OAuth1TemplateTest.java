package org.springframework.social.provider.oauth1;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.security.oauth.common.signature.SharedConsumerSecret;
import org.springframework.security.oauth.consumer.BaseProtectedResourceDetails;
import org.springframework.security.oauth.consumer.OAuthConsumerSupport;
import org.springframework.security.oauth.consumer.ProtectedResourceDetails;
import org.springframework.security.oauth.consumer.token.OAuthConsumerToken;

public class OAuth1TemplateTest {

	private static final String ACCESS_TOKEN_URL = "http://www.someprovider.com/oauth/accessToken";
	private static final String REQUEST_TOKEN_URL = "https://someprovider.com/oauth/requestToken";

	@Test
	public void buildAuthorizeUrl() {
		String expected = "https://someprovider.com/oauth/authorize?oauth_token=request_token";
		String actual = buildOAuth1Template(null).buildAuthorizeUrl("request_token");
		assertEquals(expected, actual);
	}

	@Test
	public void fetchNewRequestToken() {
		OAuthConsumerSupport consumerSupport = mock(OAuthConsumerSupport.class);
		String callback = "http://www.someclient.com/connect/foo";
		BaseProtectedResourceDetails details = new BaseProtectedResourceDetails();
		details.setConsumerKey("consumer_key");
		details.setSharedSecret(new SharedConsumerSecret("consumer_secret"));
		details.setRequestTokenURL(REQUEST_TOKEN_URL);
		details.setAccessTokenURL(ACCESS_TOKEN_URL);
		OAuthConsumerToken testToken = new OAuthConsumerToken();
		testToken.setValue("request_token");
		testToken.setSecret("request_token_secret");
		when(consumerSupport.getUnauthorizedRequestToken(argThat(new ProtectedResourceDetailsMatcher(details)),
						eq(callback))).thenReturn(testToken);

		OAuthToken requestToken = buildOAuth1Template(consumerSupport).fetchNewRequestToken(callback);
		assertEquals("request_token", requestToken.getValue());
		assertEquals("request_token_secret", requestToken.getSecret());
	}

	@Test
	public void exchangeForAccessToken() {
		OAuthConsumerSupport consumerSupport = mock(OAuthConsumerSupport.class);
		BaseProtectedResourceDetails details = new BaseProtectedResourceDetails();
		details.setConsumerKey("consumer_key");
		details.setSharedSecret(new SharedConsumerSecret("consumer_secret"));
		details.setRequestTokenURL(REQUEST_TOKEN_URL);
		details.setAccessTokenURL(ACCESS_TOKEN_URL);
		OAuthConsumerToken s2RequestToken = new OAuthConsumerToken();
		s2RequestToken.setAccessToken(true);
		s2RequestToken.setValue("request_token");
		s2RequestToken.setSecret("request_token_secret");
		OAuthConsumerToken s2AccessToken = new OAuthConsumerToken();
		s2AccessToken.setValue("access_token");
		s2AccessToken.setSecret("access_token_secret");
		when(
				consumerSupport.getAccessToken(argThat(new ProtectedResourceDetailsMatcher(details)),
						argThat(new OAuthConsumerTokenMatcher(s2RequestToken)), eq("verifier"))).thenReturn(
				s2AccessToken);

		AuthorizedRequestToken requestToken = new AuthorizedRequestToken(new OAuthToken("request_token",
				"request_token_secret"), "verifier");
		OAuthToken accessToken = buildOAuth1Template(consumerSupport).exchangeForAccessToken(requestToken);
		
		assertEquals("access_token", accessToken.getValue());
		assertEquals("access_token_secret", accessToken.getSecret());
	}

	// support methods
	private OAuth1Template buildOAuth1Template(final OAuthConsumerSupport consumerSupport) {
		return new OAuth1Template("consumer_key", "consumer_secret", REQUEST_TOKEN_URL,
				"https://someprovider.com/oauth/authorize?oauth_token={request_token}", ACCESS_TOKEN_URL) {
			@Override
			protected OAuthConsumerSupport getOAuthConsumerSupport() {
				return consumerSupport;
			}
		};
	}

	private static class ProtectedResourceDetailsMatcher extends ArgumentMatcher<ProtectedResourceDetails> {
		private ProtectedResourceDetails wanted;

		public ProtectedResourceDetailsMatcher(ProtectedResourceDetails wanted) {
			this.wanted = wanted;
		}

		public boolean matches(Object actualObject) {
			ProtectedResourceDetails actual = (ProtectedResourceDetails) actualObject;
			String actualSecret = ((SharedConsumerSecret) actual.getSharedSecret()).getConsumerSecret();
			String wantedSecret = ((SharedConsumerSecret) wanted.getSharedSecret()).getConsumerSecret();
			return actual.getConsumerKey().equals(wanted.getConsumerKey())
					&& actual.getAccessTokenURL().equals(wanted.getAccessTokenURL())
					&& actual.getRequestTokenURL().equals(wanted.getRequestTokenURL())
					&& actualSecret.equals(wantedSecret);
		}
	}

	private static class OAuthConsumerTokenMatcher extends ArgumentMatcher<OAuthConsumerToken> {
		private OAuthConsumerToken wanted;

		public OAuthConsumerTokenMatcher(OAuthConsumerToken wanted) {
			this.wanted = wanted;
		}

		public boolean matches(Object actualObject) {
			OAuthConsumerToken actual = (OAuthConsumerToken) actualObject;
			return actual.getValue().equals(wanted.getValue()) && actual.getSecret().equals(wanted.getSecret());
		}
	}
}
