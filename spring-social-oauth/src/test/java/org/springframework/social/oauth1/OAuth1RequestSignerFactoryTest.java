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

import static org.hamcrest.core.IsInstanceOf.*;
import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Craig Walls
 */
public class OAuth1RequestSignerFactoryTest {
	@Test(expected = MissingOAuthLibraryException.class)
	public void getRequestSigner_neitherLibraryInClasspath() {
		OAuth1RequestSignerFactory.scribePresent = false;
		OAuth1RequestSignerFactory.s2OAuthPresent = false;

		OAuth1RequestSignerFactory.getRequestSigner("api_key", "api_secret", "access_token", "access_token_secret");
	}

	@Test
	public void getRequestSigner_onlyScribeInClasspath() {
		OAuth1RequestSignerFactory.scribePresent = true;
		OAuth1RequestSignerFactory.s2OAuthPresent = false;

		OAuth1ClientRequestSigner requestSigner = OAuth1RequestSignerFactory.getRequestSigner("api_key", "api_secret",
				"access_token", "access_token_secret");
		assertThat(requestSigner, instanceOf(ScribeOAuth1RequestSigner.class));
	}

	@Test
	public void getRequestSigner_onlyS2OAuthInClasspath() {
		OAuth1RequestSignerFactory.scribePresent = false;
		OAuth1RequestSignerFactory.s2OAuthPresent = true;

		OAuth1ClientRequestSigner requestSigner = OAuth1RequestSignerFactory.getRequestSigner("api_key", "api_secret",
				"access_token", "access_token_secret");
		assertThat(requestSigner, instanceOf(S2OAuth1RequestSigner.class));
	}

	@Test
	public void getRequestSigner_bothScribeAndS2OAuthInClasspath() {
		OAuth1RequestSignerFactory.scribePresent = true;
		OAuth1RequestSignerFactory.s2OAuthPresent = true;

		OAuth1ClientRequestSigner requestSigner = OAuth1RequestSignerFactory.getRequestSigner("api_key", "api_secret",
				"access_token", "access_token_secret");
		assertThat(requestSigner, instanceOf(S2OAuth1RequestSigner.class));
	}
}
