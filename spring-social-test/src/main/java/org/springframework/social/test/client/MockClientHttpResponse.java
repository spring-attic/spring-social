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
package org.springframework.social.test.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;

/**
 * Mock implementation of {@link ClientHttpResponse}.
 * 
 * @author Arjen Poutsma
 * @author Lukas Krecan
 * @author Craig Walls
 */
public class MockClientHttpResponse implements ClientHttpResponse {
	private InputStream bodyStream;
	private byte[] body;
	private final HttpHeaders headers;
	private final HttpStatus statusCode;
	private final String statusText;

	public MockClientHttpResponse(String body, HttpHeaders headers, HttpStatus statusCode, String statusText) {
		this(stringToInputStream(body), headers, statusCode, statusText);
	}

	public MockClientHttpResponse(InputStream bodyStream, HttpHeaders headers, HttpStatus statusCode, String statusText) {
		this.bodyStream = bodyStream;
		this.headers = headers;
		this.statusCode = statusCode;
		this.statusText = statusText;
	}

	public InputStream getBody() throws IOException {
		if (body == null) {
			body = FileCopyUtils.copyToByteArray(bodyStream);
		}
		return new ByteArrayInputStream(body);
	}

	public HttpHeaders getHeaders() {
		return headers;
	}

	public int getRawStatusCode() throws IOException {
		return statusCode.value();
	}

	public HttpStatus getStatusCode() throws IOException {
		return statusCode;
	}

	public String getStatusText() throws IOException {
		return statusText;
	}

	public void close() {
	}

	private static InputStream stringToInputStream(String in) {
		try {
			return new ByteArrayInputStream(in.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException shouldntHappen) {
			return null;
		}
	}
}
