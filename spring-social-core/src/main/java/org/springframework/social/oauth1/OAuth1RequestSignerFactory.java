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

import org.springframework.util.ClassUtils;

/**
 * Factory used by OAuth 1-based templates to choose a signer implementation.
 * The selection is made based on what is available on the classpath.
 * 
 * Spring Security for OAuth (S2OAuth) is the preferred choice. However, S2OAuth
 * is currently one large module and a bit hefty for deployment into a
 * constrained environment, such as an Android device. Scribe, on the other
 * hand, has a smaller footprint and is more suitable in that circumstance.
 * 
 * Note that aside from their respective footprints, S2OAuth and Scribe
 * are/should be equivalent choices. Should S2OAuth be modularized into smaller
 * modules, there would be no reason not to choose it exclusively and this
 * factory would no longer be needed.
 * 
 * @author Craig Walls
 */
public class OAuth1RequestSignerFactory {
	static boolean s2OAuthPresent = ClassUtils
			.isPresent("org.springframework.security.oauth.consumer.CoreOAuthConsumerSupport",
			OAuth1RequestSignerFactory.class.getClassLoader());

	static boolean scribePresent = ClassUtils.isPresent(
			"org.scribe.builder.ServiceBuilder", OAuth1RequestSignerFactory.class.getClassLoader());

	/**
	 * Creates an {@link OAuth1ClientRequestSigner}. The implementation is
	 * chosen from what is available on the classpath, either
	 * {@link S2OAuth1RequestSigner} or {@link ScribeOAuth1RequestSigner},
	 * depending on whether S2OAuth or Scribe is available.
	 * 
	 * @param accessToken
	 *            the access token value
	 * @param accessTokenSecret
	 *            the access token secret
	 * @param apiKey
	 *            the API key assigned by the provider
	 * @param apiSecret
	 *            the API secret assigned by the provider
	 * 
	 * @return an {@link OAuth1ClientRequestSigner}
	 */
	public static OAuth1ClientRequestSigner getRequestSigner(String apiKey, String apiSecret, String accessToken,
			String accessTokenSecret) {
		if (s2OAuthPresent) {
			return new S2OAuth1RequestSigner(apiKey, apiSecret, accessToken, accessTokenSecret);
		} else if (scribePresent) {
			return new ScribeOAuth1RequestSigner(apiKey, apiSecret, accessToken, accessTokenSecret);
		}

		throw new MissingOAuthLibraryException(
				"No suitable OAuth library can be found. Spring Social needs S2OAuth or Scribe to be able to sign requests.");
	}
}
