package org.springframework.social.oauth;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpMethod;

public class FakeClientRequest implements ClientRequest {
	private final URI uri;
	private final HttpMethod httpMethod;
	private final Map<String, String> headers;
	private final Map<String, String> queryParameters;

	public FakeClientRequest(URI uri, HttpMethod httpMethod, Map<String, String> queryParameters) {
		this.uri = uri;
		this.httpMethod = httpMethod;
		this.queryParameters = queryParameters;
		this.headers = new HashMap<String, String>();
	}

	public void addHeader(String headerName, String headerValue) {
		headers.put(headerName, headerValue);
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setParameter(String parameterName, String parameterValue) {
		queryParameters.put(parameterName, parameterValue);
	}

	public Map<String, String> getQueryParameters() {
		return queryParameters;
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public URI getURI() {
		return uri;
	}

}
