package org.springframework.social.oauth1;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
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
		authorizer = new StubbedOAuth1ClientRequestAuthorizer();
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

	// stub buildAuthorizationHeader(), since that's not what we're
	// testing here anyway.
	private class StubbedOAuth1ClientRequestAuthorizer extends OAuth1ClientRequestAuthorizer {
		protected String buildAuthorizationHeader(HttpMethod method, URL url, Map<String, String> parameters) {
			try {
				if (method.equals(HttpMethod.POST) && url.equals(new URL("http://foo.com/bar"))
						&& parameters.equals(Collections.emptyMap())) {
					return "POST_AUTHORIZATION_HEADER";
				}

				Map<String, String> params = new HashMap<String, String>();
				params.put("a", "1");
				params.put("b", "2");
				if (method.equals(HttpMethod.GET) && url.equals(new URL("http://bar.com/foo?b=2&a=1"))
						&& parameters.equals(params)) {
					return "GET_AUTHORIZATION_HEADER";
				}

				return null;
			} catch (MalformedURLException willNotHappen) {
				return null;
			}
		}
	}
}
