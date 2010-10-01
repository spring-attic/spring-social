package org.springframework.social.oauth1;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.CommonsClientHttpRequestFactory;

public abstract class AbstractOAuth1RequestSignerTest {
	private OAuth1ClientRequestSigner signer;

	@Before
	public void setup() {
		signer = getSigner();
	}
	
	protected abstract OAuth1ClientRequestSigner getSigner();

	@Test
	public void sign_get() throws Exception {
		ClientHttpRequest request = new CommonsClientHttpRequestFactory().createRequest(new URI(
				"http://foo.com/bar?boo=ya"), HttpMethod.POST);
		Map<String, String> params = Collections.emptyMap();
		signer.sign(request, params);

		String authorization = request.getHeaders().get("Authorization").get(0);
		Map<String, String> authEntries = splitAuthorizationHeader(authorization);
		assertEquals("\"API_KEY\"", authEntries.get("oauth_consumer_key"));
		assertEquals("\"TOKEN_VALUE\"", authEntries.get("oauth_token"));
	}

	@Test
	public void sign_post() throws Exception {
		ClientHttpRequest request = new CommonsClientHttpRequestFactory().createRequest(new URI(
				"http://foo.com/bar?boo=ya"), HttpMethod.GET);
		Map<String, String> params = new HashMap<String, String>();
		params.put("status", "#willitwork Who knows?");
		signer.sign(request, params);

		String authorization = request.getHeaders().get("Authorization").get(0);
		Map<String, String> authEntries = splitAuthorizationHeader(authorization);
		assertEquals("\"API_KEY\"", authEntries.get("oauth_consumer_key"));
		assertEquals("\"TOKEN_VALUE\"", authEntries.get("oauth_token"));
	}

	private Map<String, String> splitAuthorizationHeader(String authorization) {
		String[] entryPairs = authorization.split("\\s|,");
		Map<String, String> authEntries = new HashMap<String, String>();
		for (String entryPair : entryPairs) {
			String[] keyValue = entryPair.split("=");
			authEntries.put(keyValue[0], keyValue.length > 1 ? keyValue[1] : "");
		}
		return authEntries;
	}
}
