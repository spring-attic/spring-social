package org.springframework.social.oauth1;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

public class OAuth1ClientRequestSignerTest {
	private OAuth1ClientRequestSigner signer;

	@Before
	public void setup() throws Exception {
		signer = new StubbedOAuth1ClientRequestAuthorizer();
	}

	@Test
	public void authorize_postRequest() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		Map<String, String> bodyParameters = Collections.singletonMap("status", "some status message");
		signer.sign(HttpMethod.POST, headers, "http://bar.com/foo", bodyParameters);
		assertEquals("POST_AUTHORIZATION_HEADER", headers.get("Authorization").get(0));
	}

	@Test
	public void authorize_getRequest() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		Map<String, String> params = new HashMap<String, String>();
		params.put("a", "1");
		params.put("b", "2");
		signer.sign(HttpMethod.GET, headers, "http://bar.com/foo", params);
		assertEquals("GET_AUTHORIZATION_HEADER", headers.get("Authorization").get(0));
	}

	// stub buildAuthorizationHeader(), since that's not what we're
	// testing here anyway. We're just checking that the signer puts the
	// authorization into the request...not that the value is good
	private class StubbedOAuth1ClientRequestAuthorizer extends OAuth1ClientRequestSigner {
		protected String buildAuthorizationHeader(HttpMethod method, String url, Map<String, String> parameters) {
			if (method.equals(HttpMethod.POST) && url.equals("http://bar.com/foo")
					&& parameters.equals(Collections.singletonMap("status", "some status message"))) {
				return "POST_AUTHORIZATION_HEADER";
			}

			Map<String, String> params = new HashMap<String, String>();
			params.put("a", "1");
			params.put("b", "2");
			if (method.equals(HttpMethod.GET) && url.equals("http://bar.com/foo") && parameters.equals(params)) {
				return "GET_AUTHORIZATION_HEADER";
			}

			return null;
		}
	}
}
