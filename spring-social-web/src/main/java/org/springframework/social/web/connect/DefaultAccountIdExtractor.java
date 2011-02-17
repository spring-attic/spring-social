package org.springframework.social.web.connect;

import java.io.Serializable;

import org.springframework.web.context.request.WebRequest;

/**
 * Default account ID extractor that uses the principal name as the account ID.
 */
class DefaultAccountIdExtractor implements AccountIdExtractor {

	public Serializable extractAccountId(WebRequest request) {
		return request.getUserPrincipal().getName();
	}

}
