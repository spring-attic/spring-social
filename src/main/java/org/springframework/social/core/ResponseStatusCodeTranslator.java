package org.springframework.social.core;

import org.springframework.http.ResponseEntity;

public interface ResponseStatusCodeTranslator {
	SocialException translate(ResponseEntity<?> responseEntity);
}
