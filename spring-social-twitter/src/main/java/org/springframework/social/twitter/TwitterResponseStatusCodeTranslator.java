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

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.core.AccountNotConnectedException;
import org.springframework.social.core.OperationNotPermittedException;
import org.springframework.social.core.ResponseStatusCodeTranslator;
import org.springframework.social.core.SocialException;

/**
 * Implementation of {@link ResponseStatusCodeTranslator} that reads a Twitter
 * error response and translates it into a specific subclass of
 * {@link SocialException}.
 * 
 * Per http://apiwiki.twitter.com/HTTP-Response-Codes-and-Errors, when Twitter
 * responds with an error, the response will be a hash with two entries: request
 * and error. These entries, along with the HTTP status code, can be used to
 * determine the nature of an error and enable {@link TwitterTemplate} to throw
 * a meaningful exception.
 * 
 * @author Craig Walls
 */
public class TwitterResponseStatusCodeTranslator implements ResponseStatusCodeTranslator {

	static final String INVALID_MESSAGE_RECIPIENT_TEXT = "You cannot send messages to users who are not following you.";
	static final String DUPLICATE_STATUS_TEXT = "Status is a duplicate.";

	public SocialException translate(ResponseEntity<?> responseEntity) {
		// TODO: What happens when the response body isn't a map?
		if (!(responseEntity.getBody() instanceof Map)) {
			return null;
		}

		@SuppressWarnings("unchecked")
		Map<String, String> body = (Map<String, String>) responseEntity.getBody();

		if (!body.containsKey("error")) {
			return null;
		}

		String errorText = body.get("error");
		HttpStatus statusCode = responseEntity.getStatusCode();

		// TODO: The error text is really the only clue in the response as to
		// why the error occurred. But keying error translation off of it means
		// that any changes to Twitter's error messages will cause this to no
		// longer work. May want to externalize the error texts for easy
		// override. (See SQLErrorCodeSQLExceptionTranslator as an example of
		// how this is done for SQL error codes.)
		if (statusCode.equals(HttpStatus.FORBIDDEN)) {
			if (errorText.equals(DUPLICATE_STATUS_TEXT) || errorText.contains("You already said that")) {
				return new DuplicateTweetException(errorText);
			} else if (errorText.equals(INVALID_MESSAGE_RECIPIENT_TEXT)) {
				return new InvalidMessageRecipientException(errorText);
			} else {
				return new OperationNotPermittedException(errorText);
			}
		} else if (statusCode.equals(HttpStatus.UNAUTHORIZED)) {
			return new AccountNotConnectedException(errorText);
		}
		return null;
	}
}
