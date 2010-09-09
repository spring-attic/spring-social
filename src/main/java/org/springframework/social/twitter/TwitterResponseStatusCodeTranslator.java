package org.springframework.social.twitter;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.core.ForbiddenSocialOperationException;
import org.springframework.social.core.ResponseStatusCodeTranslator;
import org.springframework.social.core.SocialException;
import org.springframework.social.core.SocialSecurityException;

public class TwitterResponseStatusCodeTranslator implements ResponseStatusCodeTranslator {

	public SocialException translate(ResponseEntity<?> responseEntity) {
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
		// longer work.
		if (statusCode.equals(HttpStatus.FORBIDDEN)) {
			if (errorText.equals("Status is a duplicate.")) {
				return new DuplicateTweetException(errorText);
			} else {
				return new ForbiddenSocialOperationException(errorText);
			}
		} else if (statusCode.equals(HttpStatus.UNAUTHORIZED)) {
			return new SocialSecurityException(errorText);
		}

		return null;
	}

}
