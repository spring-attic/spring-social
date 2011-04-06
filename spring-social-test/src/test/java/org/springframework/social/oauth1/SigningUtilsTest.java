package org.springframework.social.oauth1;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;

public class SigningUtilsTest {
		
	@Test
	public void buildBaseString_specificationExample() {
		/*
		 * Tests the buildBaseString() method using the example given in the OAuth 1 spec
		 * at http://tools.ietf.org/html/rfc5849#section-3.4.1 as the test data.
		 */
		Map<String, String> oauthParameters = SigningUtils.commonOAuthParameters("9djdj82h48djs9d2");
		oauthParameters.put("oauth_token", "kkk9d7dh3k39sjv7");
		LinkedMultiValueMap<String, String> additionalParameters = new LinkedMultiValueMap<String, String>();
		additionalParameters.add("b5", "=%3D");
		additionalParameters.add("a3", "a");
		additionalParameters.add("c@", "");
		additionalParameters.add("a2", "r b");
		additionalParameters.add("c2", "");
		additionalParameters.add("a3", "2 q");
		String baseString = SigningUtils.buildBaseString("https://api.twitter.com/oauth/request_token", HttpMethod.POST, oauthParameters, additionalParameters);
		
		String[] baseStringParts = baseString.split("&");
		assertEquals(3, baseStringParts.length);
		assertEquals("POST", baseStringParts[0]);
		assertEquals("https%3A%2F%2Fapi.twitter.com%2Foauth%2Frequest_token", baseStringParts[1]);
			
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
	@Ignore("Works...ignoring for now to avoid extra noise while debugging other test.")
	public void buildBaseString_twitterExample() {
		/*
		 * Tests the buildBaseString() method using the example given at http://dev.twitter.com/pages/auth#signing-requests
		 * as the test data.
		 */
		Map<String, String> oauthParameters = SigningUtils.commonOAuthParameters("GDdmIQH6jhtmLUypg82g");
		oauthParameters.put("oauth_callback", "http://localhost:3005/the_dance/process_callback?service_provider_id=11");
		String baseString = SigningUtils.buildBaseString("https://api.twitter.com/oauth/request_token", HttpMethod.POST, oauthParameters, new LinkedMultiValueMap<String, String>());
		
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
