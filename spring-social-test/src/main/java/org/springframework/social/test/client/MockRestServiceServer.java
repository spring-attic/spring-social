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

import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestGatewaySupport;

/**
 * <strong>Main entry point for client-side REST testing</strong>. Typically used to test a {@link RestTemplate}, set up
 * expectations on request messages, and create response messages.
 * <p/>
 * The typical usage of this class is:
 * <ol>
 * <li>Create a {@code MockRestServiceServer} instance by calling {@link #createServer(RestTemplate)}.
 * <li>Set up request expectations by calling {@link #expect(RequestMatcher)}, possibly by using the default
 * {@link RequestMatcher} implementations provided in {@link RequestMatchers} (which can be statically imported).
 * Multiple expectations can be set up by chaining {@link ResponseActions#andExpect(RequestMatcher)} calls.</li>
 * <li>Create an appropriate response message by calling {@link ResponseActions#andRespond(ResponseCreator)
 * andRespond(ResponseCreator)}, possibly by using the default {@link ResponseCreator} implementations provided in
 * {@link ResponseCreators} (which can be statically imported).</li>
 * <li>Use the {@code RestTemplate} as normal, either directly of through client code.</li>
 * <li>Call {@link #verify()}.
 * </ol>
 * Note that because of the 'fluent' API offered by this class (and related classes), you can typically use the Code
 * Completion features (i.e. ctrl-space) in your IDE to set up the mocks.
 * 
 * @author Arjen Poutsma
 * @author Lukas Krecan
 * @author Craig Walls
 */
public class MockRestServiceServer {
	private final MockClientHttpRequestFactory mockRequestFactory;

	private MockRestServiceServer(MockClientHttpRequestFactory mockRequestFactory) {
		Assert.notNull(mockRequestFactory, "'mockRequestFactory' must not be null");
		this.mockRequestFactory = mockRequestFactory;
	}

	/**
	 * Creates a {@code MockRestServiceServer} instance based on the given {@link RestTemplate}.
	 * 
	 * @param restTemplate
	 *            the RestTemplate
	 * @return the created server
	 */
	public static MockRestServiceServer createServer(RestTemplate restTemplate) {
		Assert.notNull(restTemplate, "'restTemplate' must not be null");

		MockClientHttpRequestFactory mockRequestFactory = new MockClientHttpRequestFactory();
		restTemplate.setRequestFactory(mockRequestFactory);

		return new MockRestServiceServer(mockRequestFactory);
	}

    /**
     * Creates a {@code MockRestServiceServer} instance based on the given {@link RestGatewaySupport}.
     *
     * @param gatewaySupport the client class
     * @return the created server
     */
	public static MockRestServiceServer createServer(RestGatewaySupport gatewaySupport) {
		Assert.notNull(gatewaySupport, "'gatewaySupport' must not be null");
		return createServer(gatewaySupport.getRestTemplate());
	}

	/**
	 * Records an expectation specified by the given {@link RequestMatcher}. Returns a {@link ResponseActions} object
	 * that allows for creating the response, or to set up more expectations.
	 * 
	 * @param requestMatcher
	 *            the request matcher expected
	 * @return the response actions
	 */
	public ResponseActions expect(RequestMatcher requestMatcher) {
		MockClientHttpRequest request = mockRequestFactory.expectNewRequest();
		request.addRequestMatcher(requestMatcher);
		return request;
	}

	/**
	 * Verifies that all expectations were met.
	 * 
	 * @throws AssertionError
	 *             in case of unmet expectations
	 */
	public void verify() {
		mockRequestFactory.verifyRequests();
	}
}
