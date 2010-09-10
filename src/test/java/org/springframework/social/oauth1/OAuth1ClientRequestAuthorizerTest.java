package org.springframework.social.oauth1;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.social.oauth.FakeClientHttpRequest;

public class OAuth1ClientRequestAuthorizerTest {
	private OAuth1ClientRequestAuthorizer authorizer;

	@Before
	public void setup() throws Exception {
		OAuth1Template oauthTemplate = mock(OAuth1Template.class);
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("a", "1");
		parameters.put("b", "2");

		Map<String, String> emptyParams = Collections.emptyMap();

		when(
				oauthTemplate.buildAuthorizationHeader(eq(HttpMethod.POST), eq(new URL("http://foo.com/bar")),
						eq(emptyParams)))
				.thenReturn("POST_AUTHORIZATION_HEADER");

		when(
				oauthTemplate.buildAuthorizationHeader(eq(HttpMethod.GET), eq(new URL("http://bar.com/foo?b=2&a=1")),
						eq(parameters))).thenReturn("GET_AUTHORIZATION_HEADER");

		authorizer = new OAuth1ClientRequestAuthorizer(oauthTemplate);
	}

	@Test
	public void authorize_postRequest() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		authorizer.authorize(new FakeClientHttpRequest(new ByteArrayOutputStream(), headers, HttpMethod.POST, new URI(
				"http://foo.com/bar")));
		assertEquals("POST_AUTHORIZATION_HEADER", headers.getFirst("Authorization"));
	}

	@Test
	public void authorize_getRequest() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		authorizer.authorize(new FakeClientHttpRequest(new ByteArrayOutputStream(), headers, HttpMethod.GET, new URI(
				"http://bar.com/foo?b=2&a=1")));
		assertEquals("GET_AUTHORIZATION_HEADER", headers.getFirst("Authorization"));
	}

}
