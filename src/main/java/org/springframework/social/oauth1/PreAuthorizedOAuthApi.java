package org.springframework.social.oauth1;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.extensions.linkedin.LinkedInBaseStringExtractorImpl;
import org.scribe.extractors.BaseStringExtractor;

/**
 * Generic Scribe API class used internally by {@link ScribeOAuth1RequestSigner}
 * for request-signing purposes only.
 * 
 * Assumes that the user is pre-authenticated and does not know or care about
 * request or access token endpoints. Thus, it can be used generically to sign
 * requests regardless of the provider, but cannot be used to authenticate a
 * user.
 * 
 * @author Craig Walls
 */
public class PreAuthorizedOAuthApi extends DefaultApi10a {
	/**
	 * @return null, as this API implementation won't be used to obtain an
	 *         access token.
	 */
	public String getAccessTokenEndpoint() {
		return null;
	}

	/**
	 * @return null, as this API implementation won't be used to obtain a
	 *         request token.
	 */
	public String getRequestTokenEndpoint() {
		return null;
	}

	/**
	 * Overridden to return a {@link LinkedInBaseStringExtractorImpl}, so that
	 * tildes (~) will be escaped. LinkedIn's API requires this and there is no
	 * apparent harm in using it with the other APIs.
	 * 
	 * @return an instance of {@link LinkedInBaseStringExtractorImpl}.
	 */
	public BaseStringExtractor getBaseStringExtractor() {
		return new LinkedInBaseStringExtractorImpl();
	}
}
