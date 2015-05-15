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
package org.springframework.social;

/**
 * Exception indicating that the authorization used during an operation invocation is rejected by the server.
 * This can occur when
 *  - an access token that is malformed or fails signature validation
 *  - an access token has been revoked
 *  - an access token has expired 
 * @author Craig Walls
 */
@SuppressWarnings("serial")
public class RejectedAuthorizationException extends NotAuthorizedException {

	public RejectedAuthorizationException(String providerId, String message) {
		super(providerId, message);
	}
	
}
