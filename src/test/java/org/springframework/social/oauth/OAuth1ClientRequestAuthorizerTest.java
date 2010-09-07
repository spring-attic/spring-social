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

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;

public class OAuth1ClientRequestAuthorizerTest {
	private OAuth1ClientRequestAuthorizer authorizer;

	@Before
	public void setup() throws Exception {
		OAuthTemplate oauthTemplate = mock(OAuthTemplate.class);
		when(oauthTemplate.buildAuthorizationHeader(any(HttpMethod.class), any(URL.class), any(Map.class))).thenReturn(
				"AUTHORIZATION_HEADER");

		authorizer = new OAuth1ClientRequestAuthorizer(oauthTemplate);
	}

	@Test
	public void decorate() throws Exception {
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
}
