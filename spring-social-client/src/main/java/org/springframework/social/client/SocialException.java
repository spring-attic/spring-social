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
package org.springframework.social.client;

/**
 * <p>
 * Exception class that represents a problem from performing an operation on a
 * social network provider (e.g. Twitter).
 * </p>
 * 
 * <p>
 * This exception class is abstract, as it is too generic for actual use. When a
 * SocialException is thrown, it should be one of the more specific subclasses.
 * </p>
 * 
 * @author Craig Walls
 */
public abstract class SocialException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public SocialException(String message) {
		super(message);
	}

	public SocialException(String message, Throwable cause) {
		super(message, cause);
	}
}
