package org.springframework.social.oauth;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth.common.signature.SharedConsumerSecret;
import org.springframework.security.oauth.consumer.BaseProtectedResourceDetails;
import org.springframework.security.oauth.consumer.OAuthConsumerSupport;
import org.springframework.security.oauth.consumer.ProtectedResourceDetails;
import org.springframework.security.oauth.consumer.token.OAuthConsumerToken;

public class SSOAuthClientRequestAuthorizerTest {
	private SSOAuthClientRequestAuthorizer authorizer;

	@Before
	public void setup() throws Exception {
		OAuthTemplate oauthTemplate = mock(OAuthTemplate.class);
		when(oauthTemplate.buildAuthorizationHeader(any(HttpMethod.class), any(URL.class), any(Map.class))).thenReturn(
				"AUTHORIZATION_HEADER");

		OAuthConsumerToken accessToken = new OAuthConsumerToken();
		accessToken.setValue("value");
		accessToken.setSecret("secret");
		accessToken.setResourceId("provider");

		OAuthConsumerSupport oauthSupport = mockOAuthConsumerSupport(accessToken);

		BaseProtectedResourceDetails twitterDetails = new BaseProtectedResourceDetails();
		twitterDetails.setId("provider");
		twitterDetails.setConsumerKey("twitterKey");
		twitterDetails.setSharedSecret(new SharedConsumerSecret("twitterSecret"));

		SSOAuthAccessTokenServices tokenServices = mock(SSOAuthAccessTokenServices.class);
		when(tokenServices.getToken(eq("provider"), any(Object.class))).thenReturn(accessToken);

		authorizer = new SSOAuthClientRequestAuthorizer(oauthSupport, twitterDetails, tokenServices);

		Authentication authentication = new TestingAuthenticationToken("principal", "credentials");
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	@After
	public void cleanup() {
		SecurityContextHolder.getContext().setAuthentication(null);
	}

	@Test
	public void authorize() throws Exception {

		ClientHttpRequest requestIn = new FakeClientHttpRequest(new ByteArrayOutputStream(), new HttpHeaders(),
				HttpMethod.GET, new URI("http://foo.com/bar"));
		ClientHttpRequest requestOut = authorizer.authorize(requestIn);
		HttpHeaders headers = requestOut.getHeaders();
		assertEquals(asList("AUTHORIZATION_HEADER"), headers.get("Authorization"));
	}

	@Test
	public void extractParametersFromRequest_noParameters() throws Exception {
		ClientHttpRequest requestIn = new FakeClientHttpRequest(new ByteArrayOutputStream(), new HttpHeaders(),
				HttpMethod.GET, new URI("http://foo.com/bar"));
		assertEquals(emptyMap(), authorizer.extractParametersFromRequest(requestIn));
	}

	@Test
	public void extractParametersFromRequest_queryParameters() throws Exception {
		ClientHttpRequest requestIn = new FakeClientHttpRequest(new ByteArrayOutputStream(), new HttpHeaders(),
				HttpMethod.GET, new URI("http://foo.com/bar?a=1&b=x&c"));

		Map<String, String> expectedParameters = new HashMap<String, String>();
		expectedParameters.put("a", "1");
		expectedParameters.put("b", "x");
		expectedParameters.put("c", null);
		assertEquals(expectedParameters, authorizer.extractParametersFromRequest(requestIn));
	}

	private OAuthConsumerSupport mockOAuthConsumerSupport(OAuthConsumerToken accessToken) {
		OAuthConsumerSupport oauthSupport = mock(OAuthConsumerSupport.class);
		when(
				oauthSupport.getAuthorizationHeader(any(ProtectedResourceDetails.class), eq(accessToken),
						any(URL.class), eq("GET"), any(Map.class))).thenReturn("AUTHORIZATION_HEADER");
		return oauthSupport;
	}
}
