package org.springframework.social.oauth1;

import org.springframework.util.ClassUtils;

public class OAuth1RequestSignerFactory {
	private static final boolean s2OAuthPresent = ClassUtils
			.isPresent("org.springframework.security.oauth.consumer.CoreOAuthConsumerSupport",
			OAuth1RequestSignerFactory.class.getClassLoader());

	private static final boolean scribePresent = ClassUtils.isPresent(
			"org.scribe.builder.ServiceBuilder", OAuth1RequestSignerFactory.class.getClassLoader());

	public static OAuth1ClientRequestSigner getRequestSigner(String apiKey, String apiSecret, String accessToken,
			String accessTokenSecret) {
		System.out.println(s2OAuthPresent + " :: " + scribePresent);
		if (s2OAuthPresent) {
			return new S2OAuth1RequestSigner(apiKey, apiSecret, accessToken, accessTokenSecret);
		} else if (scribePresent) {
			return new ScribeOAuth1RequestSigner(apiKey, apiSecret, accessToken, accessTokenSecret);
		}

		throw new MissingOAuthLibraryException(
				"No suitable OAuth library can be found. Spring Social needs S2OAuth or Scribe to be able to sign requests.");
	}
}
