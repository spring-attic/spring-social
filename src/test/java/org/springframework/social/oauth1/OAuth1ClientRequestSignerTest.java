package org.springframework.social.oauth1;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.social.oauth.FakeClientRequest;

public class OAuth1ClientRequestSignerTest {
	private OAuth1ClientRequestSigner signer;

	@Before
	public void setup() throws Exception {
		signer = new StubbedOAuth1ClientRequestAuthorizer();
	}

	@Test
	public void authorize_postRequest() throws Exception {
		FakeClientRequest request = new FakeClientRequest(new URI("http://bar.com/foo"), HttpMethod.POST,
				new HashMap<String, String>());
		signer.sign(request);

		assertEquals("POST_AUTHORIZATION_HEADER", request.getHeaders().get("Authorization"));
	}

	@Test
	public void authorize_getRequest() throws Exception {
		HashMap<String, String> queryParameters = new HashMap<String, String>();
		queryParameters.put("a", "1");
		queryParameters.put("b", "2");
		FakeClientRequest request = new FakeClientRequest(new URI("http://bar.com/foo?b=2&a=1"), HttpMethod.GET,
				queryParameters);
		signer.sign(request);

		assertEquals("GET_AUTHORIZATION_HEADER", request.getHeaders().get("Authorization"));
	}

	@Test
	public void canary() {

	}

	// stub buildAuthorizationHeader(), since that's not what we're
	// testing here anyway.
	private class StubbedOAuth1ClientRequestAuthorizer extends OAuth1ClientRequestSigner {
		protected String buildAuthorizationHeader(HttpMethod method, URL url, Map<String, String> parameters) {
			try {
				if (method.equals(HttpMethod.POST) && url.equals(new URL("http://bar.com/foo"))
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
