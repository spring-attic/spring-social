/*
 * Copyright 2010 the original author or authors.
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
package org.springframework.social;

/**
 * Indicates a security-related error occurred during the invocation of a service provider API operation.
 * For example, updating a Facebook user's status with a revoked/expired access token or when invoking an operation that require authorization without providing credentials.
 * @author Craig Walls
 */
@SuppressWarnings("serial")
public class NotAuthorizedException extends ApiException {

	public NotAuthorizedException(String message) {
		super(message);
	}

	public NotAuthorizedException(String message, Throwable cause) {
		super(message, cause);
	}

}
