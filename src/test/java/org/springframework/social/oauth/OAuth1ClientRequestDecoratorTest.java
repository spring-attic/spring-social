package org.springframework.social.oauth;

import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

public class OAuth1ClientRequestDecoratorTest {
	private OAuth1ClientRequestDecorator decorator;

	@Before
	public void setup() throws Exception {
		OAuthTemplate oauthTemplate = mock(OAuthTemplate.class);
		when(oauthTemplate.buildAuthorizationHeader(any(HttpMethod.class), any(URL.class), any(Map.class))).thenReturn(
				"AUTHORIZATION_HEADER");

		decorator = new OAuth1ClientRequestDecorator(oauthTemplate);
	}

	@Test
	public void decorate() throws Exception {
		ClientHttpRequest requestIn = new TestingClientHttpRequest(new ByteArrayOutputStream(), new HttpHeaders(),
				HttpMethod.GET, new URI("http://foo.com/bar"));
		ClientHttpRequest requestOut = decorator.decorate(requestIn);
		HttpHeaders headers = requestOut.getHeaders();
		assertEquals(asList("AUTHORIZATION_HEADER"), headers.get("Authorization"));
	}

	@Test
	public void extractParametersFromRequest_noParameters() throws Exception {
		ClientHttpRequest requestIn = new TestingClientHttpRequest(new ByteArrayOutputStream(), new HttpHeaders(),
				HttpMethod.GET, new URI("http://foo.com/bar"));
		assertEquals(emptyMap(), decorator.extractParametersFromRequest(requestIn));
	}

	@Test
	public void extractParametersFromRequest_queryParameters() throws Exception {
		ClientHttpRequest requestIn = new TestingClientHttpRequest(new ByteArrayOutputStream(), new HttpHeaders(),
				HttpMethod.GET, new URI("http://foo.com/bar?a=1&b=x&c"));

		Map<String, String> expectedParameters = new HashMap<String, String>();
		expectedParameters.put("a", "1");
		expectedParameters.put("b", "x");
		expectedParameters.put("c", null);
		assertEquals(expectedParameters, decorator.extractParametersFromRequest(requestIn));
	}



	private class TestingClientHttpRequest implements ClientHttpRequest {
		private final OutputStream body;
		private final HttpHeaders headers;
		private final HttpMethod method;
		private final URI uri;

		private TestingClientHttpRequest(OutputStream body, HttpHeaders headers, HttpMethod method, URI uri) {
			this.body = body;
			this.headers = headers;
			this.method = method;
			this.uri = uri;

		}

		@Override
		public OutputStream getBody() throws IOException {
			return body;
		}

		@Override
		public HttpHeaders getHeaders() {
			return headers;
		}

		@Override
		public HttpMethod getMethod() {
			return method;
		}

		@Override
		public URI getURI() {
			return uri;
		}

		@Override
		public ClientHttpResponse execute() throws IOException {
			return null; // do nothing
		}

	}

}
