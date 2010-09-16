package org.springframework.social.twitter;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.core.OperationNotPermittedException;
import org.springframework.social.core.ResponseStatusCodeTranslator;
import org.springframework.social.core.SocialException;
import org.springframework.social.core.AccountNotConnectedException;

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
			if (errorText.equals(DUPLICATE_STATUS_TEXT)) {
				return new DuplicateTweetException(errorText);
			} else {
				return new OperationNotPermittedException(errorText);
			}
		} else if (statusCode.equals(HttpStatus.UNAUTHORIZED)) {
			return new AccountNotConnectedException(errorText);
		}

		return null;
	}

}
