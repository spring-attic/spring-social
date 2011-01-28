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
 * <p>
 * Indicates an HTTP 403 (Forbidden) response from making a call to a social
 * network's API.
 * </p>
 * 
 * <p>
 * In the case of Twitter, this often means that you are attempting to post a
 * duplicate tweet or have reached an update limit.
 * </p>
 * 
 * @author Craig Walls
 */
public class OperationNotPermittedException extends SocialException {
	private static final long serialVersionUID = 1L;

	public OperationNotPermittedException(String message) {
		super(message);
	}

	public OperationNotPermittedException(String message, Throwable cause) {
		super(message, cause);
	}

}
