package org.springframework.social.oauth;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth.common.signature.SharedConsumerSecret;
import org.springframework.security.oauth.consumer.BaseProtectedResourceDetails;
import org.springframework.security.oauth.consumer.OAuthConsumerSupport;
import org.springframework.security.oauth.consumer.ProtectedResourceDetails;
import org.springframework.security.oauth.consumer.ProtectedResourceDetailsService;
import org.springframework.security.oauth.consumer.token.OAuthConsumerToken;
import org.springframework.social.account.Account;

public class OAuthSpringSecurityOAuthTemplateTest {
	private OAuthSpringSecurityOAuthTemplate oauthTemplate;
	private OAuthConsumerToken accessToken;
	private Account account;

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

		account = new Account(1L, "Art", "Names", "art@names.com", "artnames", "http://someurl");

		OAuthConsumerTokenServices tokenServices = mock(OAuthConsumerTokenServices.class);
		when(tokenServices.getToken("provider", account)).thenReturn(accessToken);

		oauthTemplate = new OAuthSpringSecurityOAuthTemplate("provider", oauthSupport, resourceDetailsService,
				tokenServices);
	}

	@After
	public void cleanup() {
		SecurityContextHolder.getContext().setAuthentication(null);
	}

	@Test
	public void buildAuthorizationHeader() throws Exception {
		Authentication authentication = new TestingAuthenticationToken(account, "credentials");
		SecurityContextHolder.getContext().setAuthentication(authentication);

		String authHeader = oauthTemplate.buildAuthorizationHeader(HttpMethod.POST, "http://someurl",
				new HashMap<String, String>());

		Assert.assertEquals("OAuth_Header", authHeader);
	}

	private OAuthConsumerSupport mockOAuthConsumerSupport(OAuthConsumerToken accessToken) {
		OAuthConsumerSupport oauthSupport = mock(OAuthConsumerSupport.class);
		when(oauthSupport.getAuthorizationHeader(any(ProtectedResourceDetails.class), eq(accessToken),
						any(URL.class), eq("POST"), any(Map.class))).thenReturn("OAuth_Header");
		return oauthSupport;
	}
}
