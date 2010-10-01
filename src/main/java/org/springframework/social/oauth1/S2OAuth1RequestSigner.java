package org.springframework.social.oauth1;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.security.oauth.common.signature.SharedConsumerSecret;
import org.springframework.security.oauth.consumer.BaseProtectedResourceDetails;
import org.springframework.security.oauth.consumer.CoreOAuthConsumerSupport;
import org.springframework.security.oauth.consumer.token.OAuthConsumerToken;
import org.springframework.social.oauth.OAuthClientRequestSigner;

/**
 * Implementation of an {@link OAuthClientRequestSigner} that uses S2-OAuth to
 * calculate the Authorization header.
 * 
 * @author Craig Walls
 */
public class S2OAuth1RequestSigner extends OAuth1ClientRequestSigner {
	private final BaseProtectedResourceDetails providerDetails;
	private final OAuthConsumerToken consumerToken;

	/**
	 * Create a new instance of S2OAuth1RequestSigner.
	 * 
	 * @param accessToken
	 *            the access token value
	 * @param accessTokenSecret
	 *            the access token secret
	 * @param apiKey
	 *            the API key assigned by the provider
	 * @param apiSecret
	 *            the API secret assigned by the provider
	 */
	public S2OAuth1RequestSigner(String apiKey, String apiSecret, String accessToken, String accessTokenSecret) {
		providerDetails = new BaseProtectedResourceDetails();
		providerDetails.setAcceptsAuthorizationHeader(true);
		providerDetails.setConsumerKey(apiKey);
		providerDetails.setSharedSecret(new SharedConsumerSecret(apiSecret));
		providerDetails.setUse10a(true);
		providerDetails.setSignatureMethod("HMAC-SHA1");

		consumerToken = new OAuthConsumerToken();
		consumerToken.setAccessToken(true);
		consumerToken.setValue(accessToken);
		consumerToken.setSecret(accessTokenSecret);
	}

	protected String buildAuthorizationHeader(HttpMethod method, URI url, Map<String, String> parameters) {
		try {
			return new CoreOAuthConsumerSupport().getAuthorizationHeader(providerDetails, consumerToken, new URL(
					decode(url.toString())), method.name(), decodeBodyParameters(parameters));
		} catch (MalformedURLException e) {
			return null;
		}
	}

	private Map<String, String> decodeBodyParameters(Map<String, String> parameters) {
		Map<String, String> decodedParameters = new HashMap<String, String>();
		for (String key : parameters.keySet()) {
			decodedParameters.put(key, decode(parameters.get(key)));
		}
		return decodedParameters;
	}
}
