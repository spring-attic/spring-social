/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.social.oauth;

import static org.junit.Assert.*;
import static org.junit.internal.matchers.IsCollectionContaining.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpStatus.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

/**
 * @author Craig Walls
 */
public class OAuthSigningClientHttpRequestTest {
	static final String TEST_AUTHORIZATION_HEADER = "test_authorization_header";

	@Test
	public void executeInternal() throws Exception {
		OAuthClientRequestSigner signer = new FakeSigner();
		FakeRequest request = new FakeRequest(new URI("http://www.test.com/foo"), GET, new HttpHeaders());

		OAuthSigningClientHttpRequest signingRequest = new OAuthSigningClientHttpRequest(request, signer);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Foo", "bar");
		ClientHttpResponse response = signingRequest.executeInternal(headers, "This is the request body".getBytes());

		// The signed request should have the Authorization header given by the
		// signer
		List<String> authorizationHeaders = request.getHeaders().get("Authorization");
		assertEquals(1, authorizationHeaders.size());
		assertThat(authorizationHeaders, hasItem(TEST_AUTHORIZATION_HEADER));

		// The signing request should pass the headers it is given to the signed
		// request
		List<String> fooHeaders = request.getHeaders().get("Foo");
		assertEquals(1, fooHeaders.size());
		assertThat(fooHeaders, hasItem("bar"));

		// The bytes given to the signing request should be written to the
		// signed request's body.
		assertEquals("This is the request body", new String(request.getBodyBytes()));

		// The signing request should return the response from the signed
		// request execution
		InputStream bodyInputStream = response.getBody();
		String readLine = new BufferedReader(new InputStreamReader(bodyInputStream)).readLine();
		assertEquals("This is the test response", readLine);
	}

	@Test
	public void executeInternal_withBodyParameters() throws Exception {
		OAuthClientRequestSigner signer = new FakeSigner();
		FakeRequest request = new FakeRequest(new URI("http://www.test.com/foo"), POST, new HttpHeaders());

		OAuthSigningClientHttpRequest signingRequest = new OAuthSigningClientHttpRequest(request, signer);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Foo", "bar");
		headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED.toString());
		signingRequest.executeInternal(headers, "foo=42&bar=199&baz=abc".getBytes());

		// The signed request should have the Authorization header given by the
		// signer, considering any body parameters since this is a
		// FORM_URLENCODED request
		List<String> authorizationHeaders = request.getHeaders().get("Authorization");
		assertEquals(1, authorizationHeaders.size());
		String authorizationHeader = authorizationHeaders.get(0);
		assertTrue(authorizationHeader.startsWith(TEST_AUTHORIZATION_HEADER));
		assertTrue(authorizationHeader.contains("&baz=abc"));
		assertTrue(authorizationHeader.contains("&foo=42"));
		assertTrue(authorizationHeader.contains("&bar=199"));
	}



	private static class FakeRequest implements ClientHttpRequest {
		private ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		private HttpHeaders headers;
		private HttpMethod method;
		private URI uri;

		public FakeRequest(URI uri, HttpMethod method, HttpHeaders headers) {
			this.uri = uri;
			this.method = method;
			this.headers = headers;
		}

		public OutputStream getBody() throws IOException {
			return byteStream;
		}

		public byte[] getBodyBytes() {
			return byteStream.toByteArray();
		}

		public HttpHeaders getHeaders() {
			return headers;
		}

		public HttpMethod getMethod() {
			return method;
		}

		public URI getURI() {
			return uri;
		}

		public ClientHttpResponse execute() throws IOException {
			return new FakeClientHttpResponse();
		}
	}

	private static class FakeClientHttpResponse implements ClientHttpResponse {
		public InputStream getBody() throws IOException {
			return new ByteArrayInputStream("This is the test response".getBytes());
		}

		public HttpHeaders getHeaders() {
			return null;
		}

		public HttpStatus getStatusCode() throws IOException {
			return OK;
		}

		public String getStatusText() throws IOException {
			return null;
		}

		public void close() {
		}
	}
}
