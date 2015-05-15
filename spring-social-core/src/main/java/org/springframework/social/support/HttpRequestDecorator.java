/*
 * Copyright 2015 the original author or authors.
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
package org.springframework.social.support;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Extension of HttpRequestWrapper that supports adding new HttpHeaders to the wrapped HttpRequest.
 * @author Keith Donald
 */
public class HttpRequestDecorator extends HttpRequestWrapper {

	private HttpHeaders httpHeaders;

	private boolean existingHeadersAdded;
	
	MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
	
	public HttpRequestDecorator(HttpRequest request) {
		super(request);
	}
	
	public void addParameter(String name, String value) {
		parameters.add(name, value);
	}
	
	public HttpHeaders getHeaders() {
		if (!existingHeadersAdded) {
			this.httpHeaders = new HttpHeaders();
			httpHeaders.putAll(getRequest().getHeaders());
			existingHeadersAdded = true;
		}
		return httpHeaders;
	}
	
	@Override
	public URI getURI() {
		if (parameters.isEmpty()) {
			return super.getURI();
		}
		return URIBuilder.fromUri(super.getURI()).queryParams(parameters).build();
	}

}
