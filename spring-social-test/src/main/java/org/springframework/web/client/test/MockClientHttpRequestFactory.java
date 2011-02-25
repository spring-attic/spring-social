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
package org.springframework.web.client.test;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.util.Assert;

/**
 * Mock implementation of {@link ClientHttpRequestFactory}. Contains a list of expected {@link MockClientHttpRequest}s,
 * and iterates over those.
 * 
 * @author Arjen Poutsma
 * @author Lukas Krecan
 * @author Craig Walls
 */
public class MockClientHttpRequestFactory implements ClientHttpRequestFactory {

	private final List<MockClientHttpRequest> expectedRequests = new LinkedList<MockClientHttpRequest>();

	private Iterator<MockClientHttpRequest> requestIterator;

	public MockClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
		Assert.notNull(uri, "'uri' must not be null");
		Assert.notNull(httpMethod, "'httpMethod' must not be null");

		if (requestIterator == null) {
			requestIterator = expectedRequests.iterator();
		}
		if (!requestIterator.hasNext()) {
			throw new AssertionError("No further requests expected");
		}

		MockClientHttpRequest currentRequest = requestIterator.next();
		currentRequest.setUri(uri);
		currentRequest.setHttpMethod(httpMethod);
		return currentRequest;
	}

	MockClientHttpRequest expectNewRequest() {
		Assert.state(requestIterator == null, "Can not expect another request, the test is already underway");
		MockClientHttpRequest request = new MockClientHttpRequest();
		expectedRequests.add(request);
		return request;
	}

	void verifyRequests() {
		if (expectedRequests.isEmpty()) {
			return;
		}
		if (requestIterator == null || requestIterator.hasNext()) {
			throw new AssertionError("Further request(s) expected");
		}
	}
}
