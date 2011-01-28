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

import org.springframework.http.ResponseEntity;

/**
 * Strategy interface for converting responses from a social network provider
 * into specific instances of {@link SocialException}.
 * 
 * @author Craig Walls
 */
public interface ResponseStatusCodeTranslator {
	/**
	 * Translate responseEntity into a SocialException
	 * 
	 * @param responseEntity
	 *            The response from the social network provider
	 * 
	 * @return the exception translated from the response or <code>null</code>
	 *         if the response doesn't translate into an error.
	 */
	SocialException translate(ResponseEntity<?> responseEntity);
}
