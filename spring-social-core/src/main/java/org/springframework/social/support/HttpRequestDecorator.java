package org.springframework.social.support;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.support.HttpRequestWrapper;

public class HttpRequestDecorator extends HttpRequestWrapper {

	private HttpHeaders httpHeaders;

	private boolean targetHeadersAdded;
	
	public HttpRequestDecorator(HttpRequest targetRequest) {
		super(targetRequest);
	}
	
	public HttpHeaders getHeaders() {
		if (!targetHeadersAdded) {
			this.httpHeaders = new HttpHeaders();
			httpHeaders.putAll(getRequest().getHeaders());
			targetHeadersAdded = true;
		}
		return httpHeaders;
	}

}