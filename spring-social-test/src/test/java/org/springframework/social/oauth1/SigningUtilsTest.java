package org.springframework.social.oauth1;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.Map;

import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;

public class SigningUtilsTest {

	@Test
	public void buildAuthorizationHeader_URI() throws Exception {
		Map<String, String> oauthParameters = SigningUtils.commonOAuthParameters("9djdj82h48djs9d2");
		oauthParameters.put("oauth_token", "kkk9d7dh3k39sjv7");
		LinkedMultiValueMap<String, String> additionalParameters = new LinkedMultiValueMap<String, String>();
		additionalParameters.add("b5", "=%3D");
		additionalParameters.add("a3", "a");
		additionalParameters.add("c@", "");
		additionalParameters.add("a2", "r b");
		additionalParameters.add("c2", "");
		additionalParameters.add("a3", "2 q");
		String authorizationHeader = SigningUtils.buildAuthorizationHeaderValue(HttpMethod.POST, new URI("http://example.com/request"), oauthParameters, additionalParameters, "consumer_secret", "token_secret");
		assertAuthorizationHeader(authorizationHeader);
	}

	@Test
	public void buildAuthorizationHeader_HttpRequest() throws Exception {
		HttpRequest request = new SimpleClientHttpRequestFactory().createRequest(new URI("http://example.com/request?b5=%3D%253D&a3=a&c%40=&a2=r%20b"), HttpMethod.POST);
		String authorizationHeader = SigningUtils.buildAuthorizationHeaderValue(request, "c2&a3=2+q".getBytes(), "9djdj82h48djs9d2", "consumer_secret", "kkk9d7dh3k39sjv7", "token_secret");
		assertAuthorizationHeader(authorizationHeader);
	}
	
	@Test
	public void spring30buildAuthorizationHeaderValue() throws Exception {
		ClientHttpRequest request = new SimpleClientHttpRequestFactory().createRequest(new URI("http://example.com/request?b5=%3D%253D&a3=a&c%40=&a2=r%20b"), HttpMethod.POST);
		String authorizationHeader = SigningUtils.spring30buildAuthorizationHeaderValue(request, "c2&a3=2+q".getBytes(), "9djdj82h48djs9d2", "consumer_secret", "kkk9d7dh3k39sjv7", "token_secret");
		assertAuthorizationHeader(authorizationHeader);
	}

	private void assertAuthorizationHeader(String authorizationHeader) {
		// TODO: Only tests that the elements of the header are present and that most values are correct.
		//       Testing the nonce, timestamp, and signature values is tricky, so those values aren't asserted.
		String[] headerElements = authorizationHeader.split(", ");
		assertEquals("OAuth oauth_version=\"1.0\"", headerElements[0]);
		assertTrue(headerElements[1].matches("oauth_nonce=\"(.*)\"")); // TODO: Test nonce better
		assertEquals("oauth_signature_method=\"HMAC-SHA1\"", headerElements[2]);		
		assertEquals("oauth_consumer_key=\"9djdj82h48djs9d2\"", headerElements[3]);
		assertEquals("oauth_token=\"kkk9d7dh3k39sjv7\"", headerElements[4]);
		assertTrue(headerElements[5].matches("oauth_timestamp=\"(.*)\""));	// TODO: Test timestamp better
		assertTrue(headerElements[6].matches("oauth_signature=\"(.*)\""));	// TODO: Test signature better	
	}

	@Test
	public void buildBaseString_specificationExample() {
		/*
		 * Tests the buildBaseString() method using the example given in the OAuth 1 spec
		 * at http://tools.ietf.org/html/rfc5849#section-3.4.1 as the test data.
		 */
		Map<String, String> oauthParameters = SigningUtils.commonOAuthParameters("9djdj82h48djs9d2");
		oauthParameters.put("oauth_token", "kkk9d7dh3k39sjv7");
		LinkedMultiValueMap<String, String> collectedParameters = new LinkedMultiValueMap<String, String>();
		collectedParameters.add("b5", "=%3D");
		collectedParameters.add("a3", "a");
		collectedParameters.add("c@", "");
		collectedParameters.add("a2", "r b");
		collectedParameters.add("c2", "");
		collectedParameters.add("a3", "2 q");
		collectedParameters.setAll(oauthParameters);
		String baseString = SigningUtils.buildBaseString(HttpMethod.POST, "http://example.com/request", collectedParameters);
		
		String[] baseStringParts = baseString.split("&");
		assertEquals(3, baseStringParts.length);
		assertEquals("POST", baseStringParts[0]);
		assertEquals("http%3A%2F%2Fexample.com%2Frequest", baseStringParts[1]);
			
		String[] parameterParts = baseStringParts[2].split("%26");

		assertEquals(12, parameterParts.length);
		assertEquals("a2%3Dr%2520b", parameterParts[0]);
		assertEquals("a3%3D2%2520q", parameterParts[1]);
		assertEquals("a3%3Da", parameterParts[2]);
		assertEquals("b5%3D%253D%25253D", parameterParts[3]);
		assertEquals("c%2540%3D", parameterParts[4]);
		assertEquals("c2%3D", parameterParts[5]);
		assertEquals("oauth_consumer_key%3D9djdj82h48djs9d2", parameterParts[6]);
		assertTrue(parameterParts[7].startsWith("oauth_nonce%3D"));
		assertEquals("oauth_signature_method%3DHMAC-SHA1", parameterParts[8]);
		assertTrue(parameterParts[9].startsWith("oauth_timestamp%3D"));
		assertEquals("oauth_token%3Dkkk9d7dh3k39sjv7", parameterParts[10]);
		assertEquals("oauth_version%3D1.0", parameterParts[11]);
	}
	
	@Test
	public void buildBaseString_twitterExample() {
		/*
		 * Tests the buildBaseString() method using the example given at http://dev.twitter.com/pages/auth#signing-requests
		 * as the test data.
		 */
		Map<String, String> oauthParameters = SigningUtils.commonOAuthParameters("GDdmIQH6jhtmLUypg82g");
		oauthParameters.put("oauth_callback", "http://localhost:3005/the_dance/process_callback?service_provider_id=11");
		LinkedMultiValueMap<String, String> collectedParameters = new LinkedMultiValueMap<String, String>();
		collectedParameters.setAll(oauthParameters);
		String baseString = SigningUtils.buildBaseString(HttpMethod.POST, "https://api.twitter.com/oauth/request_token", collectedParameters);
		
		String[] baseStringParts = baseString.split("&");
		assertEquals(3, baseStringParts.length);
		assertEquals("POST", baseStringParts[0]);
		assertEquals("https%3A%2F%2Fapi.twitter.com%2Foauth%2Frequest_token", baseStringParts[1]);
		
		String[] parameterParts = baseStringParts[2].split("%26");
		assertEquals(6, parameterParts.length);
		assertEquals("oauth_callback%3Dhttp%253A%252F%252Flocalhost%253A3005%252Fthe_dance%252Fprocess_callback%253Fservice_provider_id%253D11", parameterParts[0]);
		assertEquals("oauth_consumer_key%3DGDdmIQH6jhtmLUypg82g", parameterParts[1]);
		assertTrue(parameterParts[2].startsWith("oauth_nonce%3D"));
		assertEquals("oauth_signature_method%3DHMAC-SHA1", parameterParts[3]);
		assertTrue(parameterParts[4].startsWith("oauth_timestamp%3D"));
		assertEquals("oauth_version%3D1.0", parameterParts[5]);
	}
	
}
