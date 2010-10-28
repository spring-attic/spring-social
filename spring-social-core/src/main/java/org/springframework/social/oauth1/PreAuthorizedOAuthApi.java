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
package org.springframework.social.oauth1;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.extensions.linkedin.LinkedInBaseStringExtractorImpl;
import org.scribe.extractors.BaseStringExtractor;

/**
 * <p>
 * Generic Scribe API class used internally by {@link ScribeOAuth1RequestSigner}
 * for request-signing purposes only.
 * </p>
 * 
 * <p>
 * Assumes that the user is pre-authenticated and does not know or care about
 * request or access token endpoints. Thus, it can be used generically to sign
 * requests regardless of the provider, but cannot be used to authenticate a
 * user.
 * </p>
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
