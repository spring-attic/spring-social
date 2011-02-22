package org.springframework.social.oauth.support;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;

/**
 * Represents an HTTP request message, consisting of {@linkplain #getMethod() method} and {@linkplain #getURI() uri}.
 * Concept borrowed from Spring 3.1 to support RestTemplate interceptors in Spring Social for apps using Spring 3.0.
 * This differs from Spring 3.1 in that it is a class that wraps a request and not an interface that ClientHttpRequest implements.
 * @author Craig Walls
 */
public class HttpRequest {
	private final URI uri;
	private final HttpMethod method;
	private final HttpHeaders headers;

	public HttpRequest(URI uri, HttpMethod method, HttpHeaders headers) {
		this.uri = uri;
		this.method = method;
		this.headers = new HttpHeaders();
		this.headers.putAll(headers);
	}

	public HttpRequest(ClientHttpRequest request) {
		this(request.getURI(), request.getMethod(), request.getHeaders());
	}

	public HttpRequest(HttpRequest request) {
		this(request.getURI(), request.getMethod(), request.getHeaders());
	}

	/**
	 * Return the URI of the request.
	 * @return the URI of the request
	 */
	public URI getURI() {
		return uri;
	}

	/**
	 * Return the HTTP headers of the request.
	 * @return the HTTP headers
	 */
	public HttpMethod getMethod() {
		return method;
	}

	/**
	 * Return the HTTP method of the request.
	 * @return the HTTP method as an HttpMethod enum value
	 */
	public HttpHeaders getHeaders() {
		return headers;
	}

}
