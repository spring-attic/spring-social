package org.springframework.social.oauth1;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.CommonsClientHttpRequestFactory;

public class OAuth1ClientRequestSignerTest {
	private OAuth1ClientRequestSigner signer;

	@Before
	public void setup() throws Exception {
		signer = new StubbedOAuth1ClientRequestAuthorizer();
	}

	@Test
	public void authorize_postRequest() throws Exception {
		Map<String, String> bodyParameters = Collections.singletonMap("status", "some status message");
		ClientHttpRequest request = new CommonsClientHttpRequestFactory().createRequest(new URI("http://bar.com/foo"),
				HttpMethod.POST);
		signer.sign(request, bodyParameters);
		assertEquals("POST_AUTHORIZATION_HEADER", request.getHeaders().get("Authorization").get(0));
	}


	// stub buildAuthorizationHeader(), since that's not what we're
	// testing here anyway. We're just checking that the signer puts the
	// authorization into the request...not that the value is good
	private class StubbedOAuth1ClientRequestAuthorizer extends OAuth1ClientRequestSigner {
		protected String buildAuthorizationHeader(HttpMethod method, URI url, Map<String, String> parameters) {
			if (method.equals(HttpMethod.POST) && url.toString().equals("http://bar.com/foo")
					&& parameters.equals(Collections.singletonMap("status", "some status message"))) {
				return "POST_AUTHORIZATION_HEADER";
			}
			return null;
		}
	}
}
