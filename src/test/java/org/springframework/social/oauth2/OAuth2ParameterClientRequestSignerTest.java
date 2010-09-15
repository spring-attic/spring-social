package org.springframework.social.oauth2;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.social.oauth.AccessToken;
import org.springframework.social.oauth.FakeClientRequest;

public class OAuth2ParameterClientRequestSignerTest {
	private OAuth2ParameterClientRequestSigner signer;

	@Before
	public void setup() throws Exception {
		signer = new OAuth2ParameterClientRequestSigner() {
			// stub, since this isn't what's being tested here anyway
			public AccessToken resolveAccessToken() {
				return new AccessToken("ACCESS_TOKEN", "Twitter");
			}
		};
	}

	@Test
	public void sign() throws Exception {
		Map<String, String> queryParameters = new HashMap<String, String>();
		queryParameters.put("a", "b");
		queryParameters.put("c", "1");
		FakeClientRequest request = new FakeClientRequest(new URI("http://foo.com/bar?a=b&c=1"), HttpMethod.GET,
				queryParameters);

		signer.sign(request);
		assertEquals("ACCESS_TOKEN", request.getQueryParameters().get("oauth_token"));
	}

	@Test
	public void sign_customParameter() throws Exception {
		Map<String, String> queryParameters = new HashMap<String, String>();
		queryParameters.put("a", "b");
		queryParameters.put("c", "1");
		FakeClientRequest request = new FakeClientRequest(new URI("http://foo.com/bar?a=b&c=1"), HttpMethod.GET,
				queryParameters);

		signer.setParameterName("the_token");
		signer.sign(request);
		assertEquals("ACCESS_TOKEN", request.getQueryParameters().get("the_token"));
	}
	
}
