package org.springframework.security.oauth.client;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

public final class ProtectedResourceRequest {
	
	private final HttpHeaders headers;
	
	private final byte[] body;
	
	private final URI uri;
	
	private final HttpMethod method;

	public ProtectedResourceRequest(HttpHeaders headers, byte[] body, URI uri, HttpMethod method) {
		this.headers = headers;
		this.body = body;
		this.uri = uri;
		this.method = method;
	}

	public HttpHeaders getHeaders() {
		return headers;
	}

	public byte[] getBody() {
		return body;
	}

	public URI getUri() {
		return uri;
	}

	public HttpMethod getMethod() {
		return method;
	}
	
}
