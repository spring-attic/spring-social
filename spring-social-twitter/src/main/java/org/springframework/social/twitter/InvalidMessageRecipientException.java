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
package org.springframework.social.twitter;

import org.springframework.social.client.SocialException;

/**
 * Exception thrown when an attempt is made to send a direct message to an
 * invalid recipient; that is, a recipient who is not following the
 * authenticated user.
 * 
 * @author Craig Walls
 */
public class InvalidMessageRecipientException extends SocialException {
	private static final long serialVersionUID = 1L;

	public InvalidMessageRecipientException(String message) {
		super(message);
	}

	public InvalidMessageRecipientException(String message, Throwable cause) {
		super(message, cause);
	}
}
