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
 * Indicates an attempt to invoke an API operation requiring authorization with no credentials.
 * For example, updating a user's status on a provider using an API binding that was constructed without an access token. 
 * @author Craig Walls
 */
@SuppressWarnings("serial")
public class AuthorizationRequiredException extends ProviderApiException {

	public AuthorizationRequiredException(String message) {
		super(message);
	}

}
