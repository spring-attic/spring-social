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
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
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
import org.springframework.social.account.ConnectedAccountNotFoundException;

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

		OAuthConsumerTokenServices tokenServices = mock(OAuthConsumerTokenServices.class);
		when(tokenServices.getToken("provider", 1L)).thenReturn(accessToken);


		oauthHelper = new OAuthSpringSecurityOAuthHelper(oauthSupport, resourceDetailsService, tokenServices);
	}

	@After
	public void cleanup() {
		SecurityContextHolder.getContext().setAuthentication(null);
	}

	@Test
	public void buildAuthorizationHeader() throws Exception {
		Account account = new Account(1L, "Art", "Names", "art@names.com", "artnames", "http://someurl");
		Authentication authentication = new TestingAuthenticationToken(account, "credentials");
		SecurityContextHolder.getContext().setAuthentication(authentication);

		String authHeader = oauthHelper.buildAuthorizationHeader(HttpMethod.POST, "http://someurl", "provider",
				new HashMap<String, String>());

		Assert.assertEquals("OAuth_Header", authHeader);
	}

	@Test(expected = AuthenticationCredentialsNotFoundException.class)
	public void resolveAccessToken_noAuthenticationInSecurityContext() throws Exception {
		oauthHelper.resolveAccessToken("resource");
	}

	@Test(expected = BadCredentialsException.class)
	public void resolveAccessToken_principalIsNotAnAccount() throws Exception {
		Authentication authentication = new TestingAuthenticationToken("String Principal", "credentials");
		SecurityContextHolder.getContext().setAuthentication(authentication);
		oauthHelper.resolveAccessToken("resource");
	}

	@Test(expected = ConnectedAccountNotFoundException.class)
	public void resolveAccessToken_notConnected() throws Exception {
		Account account = new Account(1L, "Art", "Names", "art@names.com", "artnames", "http://someurl");
		Authentication authentication = new TestingAuthenticationToken(account, "credentials");
		SecurityContextHolder.getContext().setAuthentication(authentication);
		oauthHelper.resolveAccessToken("resource");
	}

	@Test
	public void resolveAccessToken() {
		Account account = new Account(1L, "Art", "Names", "art@names.com", "artnames", "http://someurl");
		Authentication authentication = new TestingAuthenticationToken(account, "credentials");
		SecurityContextHolder.getContext().setAuthentication(authentication);
		oauthHelper.resolveAccessToken("provider");
	}

	private OAuthConsumerSupport mockOAuthConsumerSupport(OAuthConsumerToken accessToken) {
		OAuthConsumerSupport oauthSupport = mock(OAuthConsumerSupport.class);
		when(oauthSupport.getAuthorizationHeader(any(ProtectedResourceDetails.class), eq(accessToken),
						any(URL.class), eq("POST"), any(Map.class))).thenReturn("OAuth_Header");
		return oauthSupport;
	}
}
