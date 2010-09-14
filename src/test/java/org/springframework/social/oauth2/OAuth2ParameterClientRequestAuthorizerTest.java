package org.springframework.social.oauth2;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.net.URI;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.social.oauth.AccessToken;
import org.springframework.social.oauth.FakeClientHttpRequest;

public class OAuth2ParameterClientRequestAuthorizerTest {
	private OAuth2ParameterClientRequestSigner authorizer;

	@Before
	public void setup() throws Exception {
		authorizer = new OAuth2ParameterClientRequestSigner() {
			// stub, since this isn't what's being tested here anyway
			public AccessToken resolveAccessToken() {
				return new AccessToken("ACCESS_TOKEN", "Twitter");
			}
		};
	}

	@Test
	@Ignore("This approach may be flawed...ignoring for now")
	public void decorate_existingParameters() throws Exception {
		ClientHttpRequest requestIn = new FakeClientHttpRequest(new ByteArrayOutputStream(), new HttpHeaders(),
				HttpMethod.GET, new URI("http://foo.com/bar?a=b&c=1"));

		// authorizer.authorize(requestIn);
		assertEquals("http://foo.com/bar?a=b&c=1&oauth_token=ACCESS_TOKEN", requestIn.getURI().toString());
	}

	@Test
	@Ignore("This approach may be flawed...ignoring for now")
	public void decorate_noParameters() throws Exception {
		ClientHttpRequest requestIn = new FakeClientHttpRequest(new ByteArrayOutputStream(), new HttpHeaders(),
				HttpMethod.GET, new URI("http://foo.com/bar"));

		// authorizer.authorize(requestIn);
		assertEquals("http://foo.com/bar?oauth_token=ACCESS_TOKEN", requestIn.getURI().toString());
	}

	@Test
	@Ignore("This approach may be flawed...ignoring for now")
	public void decorate_customParameter() throws Exception {
		ClientHttpRequest requestIn = new FakeClientHttpRequest(new ByteArrayOutputStream(), new HttpHeaders(),
				HttpMethod.GET, new URI("http://foo.com/bar?a=b&c=1"));

		authorizer.setParameterName("the_token");
		// authorizer.authorize(requestIn);
		assertEquals("http://foo.com/bar?a=b&c=1&the_token=ACCESS_TOKEN", requestIn.getURI().toString());
	}
	
}
