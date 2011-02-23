/*
 * Copyright 2011 the original author or authors.
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

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.support.HttpRequestWrapper;

/**
 * Extension of HttpRequestWrapper that supports adding new HttpHeaders to the wrapped HttpRequest.
 * @author Keith Donald
 */
public class HttpRequestDecorator extends HttpRequestWrapper {

	private HttpHeaders httpHeaders;

	private boolean existingHeadersAdded;
	
	public HttpRequestDecorator(HttpRequest request) {
		super(request);
	}
	
	public HttpHeaders getHeaders() {
		if (!existingHeadersAdded) {
			this.httpHeaders = new HttpHeaders();
			httpHeaders.putAll(getRequest().getHeaders());
			existingHeadersAdded = true;
		}
		return httpHeaders;
	}

}