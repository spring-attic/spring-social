package org.springframework.web.client.test;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;

public class MockHttpRequest implements HttpRequest {

	private final HttpHeaders headers = new HttpHeaders();

	private HttpMethod method;
	
	private URI uri;

	public MockHttpRequest(String uri) {
		this(HttpMethod.GET, uri);
	}

	public MockHttpRequest(HttpMethod method, String uri) {
		try {
			this.uri = new URI(uri);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Invalid uri: '"+ uri + "'", e);
		}
		this.method = method;
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

}