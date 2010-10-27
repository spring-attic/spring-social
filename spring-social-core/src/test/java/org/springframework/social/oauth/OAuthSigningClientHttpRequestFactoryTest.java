package org.springframework.social.oauth;

import static org.junit.Assert.*;
import static org.springframework.http.HttpMethod.*;

import java.net.URI;

import org.junit.Test;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

public class OAuthSigningClientHttpRequestFactoryTest {
	@Test
	public void createRequest() throws Exception {
		OAuthSigningClientHttpRequestFactory factory = new OAuthSigningClientHttpRequestFactory(
				new SimpleClientHttpRequestFactory(), new FakeSigner());

		ClientHttpRequest request = factory.createRequest(new URI("http://www.springsource.com/test"), GET);
		assertTrue(request instanceof OAuthSigningClientHttpRequest);
		assertEquals(new URI("http://www.springsource.com/test"), request.getURI());
		assertEquals(GET, request.getMethod());
	}
}
