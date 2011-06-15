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
package org.springframework.social;

/**
 * Exception thrown when attempting to invoke an API operation requiring authorization but no authorization has been provided.
 * This can occur after constructing an API binding without providing the necessary authorization information, such as an access token.
 * @author Craig Walls
 */
@SuppressWarnings("serial")
public class MissingAuthorizationException extends NotAuthorizedException {

	public MissingAuthorizationException() {
		super("No authorization has been provided but one is required.");
	}
	
}
