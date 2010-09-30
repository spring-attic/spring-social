package org.springframework.social.oauth1;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class ScribeOAuth1RequestSignerTest {
	private ScribeOAuth1RequestSigner signer;

	@Before
	public void setup() {
		signer = new ScribeOAuth1RequestSigner("API_KEY", "API_SECRET", "TOKEN_VALUE", "TOKEN_SECRET");
	}

	@Test
	@Ignore("FIX THIS TO USE THE NEW SIGNER")
	public void sign_get() throws Exception {
		// Map<String, String> params = Collections.emptyMap();
		// FakeClientRequest request = new FakeClientRequest(new
		// URI("http://foo.com/bar"), HttpMethod.GET, params);
		// signer.sign(request);
		//
		// String authorization = request.getHeaders().get("Authorization");
		// Map<String, String> authEntries =
		// splitAuthorizationHeader(authorization);
		//
		// assertEquals("\"API_KEY\"", authEntries.get("oauth_consumer_key"));
		// assertEquals("\"TOKEN_VALUE\"", authEntries.get("oauth_token"));
	}

	@Test
	@Ignore("FIX THIS TO USE THE NEW SIGNER")
	public void sign_post() throws Exception {
		// Map<String, String> params = Collections.emptyMap();
		// FakeClientRequest request = new FakeClientRequest(new
		// URI("http://foo.com/bar"), HttpMethod.POST, params);
		// signer.sign(request);
		//
		// String authorization = request.getHeaders().get("Authorization");
		// Map<String, String> authEntries =
		// splitAuthorizationHeader(authorization);
		//
		// assertEquals("\"API_KEY\"", authEntries.get("oauth_consumer_key"));
		// assertEquals("\"TOKEN_VALUE\"", authEntries.get("oauth_token"));
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
