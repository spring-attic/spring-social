package org.springframework.social.provider.oauth1;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth.client.ClientRequest;
import org.springframework.security.oauth.client.oauth1.OAuth1ClientRequestInterceptor;
import org.springframework.security.oauth.client.oauth1.OAuthToken;

public class OAuth1ClientRequestInterceptorTest {
	@Test
	public void beforeExecution() throws Exception {
		OAuth1ClientRequestInterceptor interceptor = new OAuth1ClientRequestInterceptor("consumer_key",
				"consumer_secret", new OAuthToken("access_token", "token_secret"));

		byte[] body = "status=Hello+there".getBytes();
		URI uri = new URI("https://api.someprovider.com/status/update");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		ClientRequest request = new ClientRequest(headers, body, uri, HttpMethod.POST);
		interceptor.beforeExecution(request);
		String authorizationHeader = headers.getFirst("Authorization");

		// TODO: Figure out how to test this more precisely with a fixed nonce
		// and timestamp (and thus a fixed signature)
		Map<String, String> headerParameters = extractHeaderParameters(authorizationHeader);
		assertEquals("1.0", headerParameters.get("oauth_version"));
		assertTrue(headerParameters.containsKey("oauth_nonce"));
		assertEquals("HMAC-SHA1", headerParameters.get("oauth_signature_method"));
		assertEquals("consumer_key", headerParameters.get("oauth_consumer_key"));
		assertEquals("access_token", headerParameters.get("oauth_token"));
		assertTrue(headerParameters.containsKey("oauth_timestamp"));
		assertTrue(headerParameters.containsKey("oauth_signature"));
	}

	private Map<String, String> extractHeaderParameters(String authorizationHeader) {
		String[] keysAndValues = authorizationHeader.substring(6).split(",\\s");
		Map<String, String> parameters = new HashMap<String, String>();
		for (String keyAndValue : keysAndValues) {
			String[] keyValuePair = keyAndValue.split("=");
			String value = keyValuePair[1].substring(1, keyValuePair[1].length() - 1);
			parameters.put(keyValuePair[0], value);
		}
		return parameters;
	}
}
