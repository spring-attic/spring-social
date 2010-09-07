package org.springframework.social.oauth;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

public class FakeClientHttpRequest implements ClientHttpRequest {
	private final OutputStream body;
	private final HttpHeaders headers;
	private final HttpMethod method;
	private final URI uri;

	public FakeClientHttpRequest(OutputStream body, HttpHeaders headers, HttpMethod method, URI uri) {
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
