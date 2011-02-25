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

/**
 * Allows for setting up responses and additional expectations. Implementations of this interface are returned by
 * {@link MockWebServiceServer#expect(RequestMatcher)}.
 * 
 * @author Arjen Poutsma
 * @author Lukas Krecan
 * @author Craig Walls
 */
public interface ResponseActions {
	/**
	 * Allows for further expectations to be set on the request.
	 *
	 * @return the request expectations
	 */
	ResponseActions andExpect(RequestMatcher requestMatcher);

	/**
	 * Sets the {@link ResponseCreator} for this mock.
	 *
	 * @param responseCreator the response creator
	 */
	void andRespond(ResponseCreator responseCreator);
}
