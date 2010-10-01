package org.springframework.social.oauth1;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

public class S2OAuth1RequestSignerTest {
	private S2OAuth1RequestSigner signer;

	@Before
	public void setup() {
		signer = new S2OAuth1RequestSigner("API_KEY", "API_SECRET", "TOKEN_VALUE", "TOKEN_SECRET");
	}

	@Test
	public void sign_get() throws Exception {
		Map<String, String> params = Collections.emptyMap();
		HttpHeaders headers = new HttpHeaders();
		signer.sign(HttpMethod.GET, headers, "http://foo.com/bar?boo=ya", params);

		String authorization = headers.get("Authorization").get(0);
		Map<String, String> authEntries = splitAuthorizationHeader(authorization);
		assertEquals("\"API_KEY\"", authEntries.get("oauth_consumer_key"));
		assertEquals("\"TOKEN_VALUE\"", authEntries.get("oauth_token"));
	}

	@Test
	public void sign_post() throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put("status", "#willitwork Who knows?");
		HttpHeaders headers = new HttpHeaders();
		signer.sign(HttpMethod.POST, headers, "http://foo.com/bar", params);

		String authorization = headers.get("Authorization").get(0);
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
