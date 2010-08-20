package org.springframework.social.oauth;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth.common.signature.SharedConsumerSecret;
import org.springframework.security.oauth.consumer.BaseProtectedResourceDetails;
import org.springframework.security.oauth.consumer.OAuthConsumerSupport;
import org.springframework.security.oauth.consumer.ProtectedResourceDetails;
import org.springframework.security.oauth.consumer.ProtectedResourceDetailsService;
import org.springframework.security.oauth.consumer.token.OAuthConsumerToken;

public class OAuthSpringSecurityOAuthHelperTest {
	private OAuthSpringSecurityOAuthHelper oauthHelper;
	private OAuthConsumerToken accessToken;

	@Before
	public void setup() {
		accessToken = new OAuthConsumerToken();
		accessToken.setValue("value");
		accessToken.setSecret("secret");
		accessToken.setResourceId("resource");

		OAuthConsumerSupport oauthSupport = mockOAuthConsumerSupport(accessToken);

		BaseProtectedResourceDetails twitterDetails = new BaseProtectedResourceDetails();
		twitterDetails.setConsumerKey("twitterKey");
		twitterDetails.setSharedSecret(new SharedConsumerSecret("twitterSecret"));

		ProtectedResourceDetailsService resourceDetailsService = mock(ProtectedResourceDetailsService.class);
		when(resourceDetailsService.loadProtectedResourceDetailsById(any(String.class))).thenReturn(twitterDetails);

		oauthHelper = new OAuthSpringSecurityOAuthHelper(oauthSupport, resourceDetailsService);
	}

	@Test
	public void buildAuthorizationHeader() throws Exception {
		String authHeader = oauthHelper.buildAuthorizationHeader(new SimpleAccessTokenProvider<OAuthConsumerToken>(
				accessToken), HttpMethod.POST, "http://someurl", "provider", new HashMap<String, String>());

		Assert.assertEquals("OAuth_Header", authHeader);
	}

	@Test(expected = IllegalArgumentException.class)
	public void buildAuthorizationHeader_invalidAccessTokenType() throws Exception {
		oauthHelper.buildAuthorizationHeader(new SimpleAccessTokenProvider<String>("Bad Token"), HttpMethod.POST,
				"http://someurl", "provider", new HashMap<String, String>());
	}

	private OAuthConsumerSupport mockOAuthConsumerSupport(OAuthConsumerToken accessToken) {
		OAuthConsumerSupport oauthSupport = mock(OAuthConsumerSupport.class);
		when(oauthSupport.getAuthorizationHeader(any(ProtectedResourceDetails.class), eq(accessToken),
						any(URL.class), eq("POST"), any(Map.class))).thenReturn("OAuth_Header");
		return oauthSupport;
	}
}
