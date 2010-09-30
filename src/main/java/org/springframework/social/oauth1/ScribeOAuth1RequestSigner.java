package org.springframework.social.oauth1;

import java.util.Map;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import org.springframework.http.HttpMethod;
import org.springframework.social.oauth.OAuthClientRequestSigner;

/**
 * Implementation of an {@link OAuthClientRequestSigner} that uses Scribe to
 * calculate the Authorization header.
 * 
 * @author Craig Walls
 * 
 */
public class ScribeOAuth1RequestSigner extends OAuth1ClientRequestSigner {
	private final OAuthService service;
	private final String accessToken;
	private final String accessTokenSecret;

	/**
	 * Create a new instance of ScribeOAuth1RequestSigner.
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
	public ScribeOAuth1RequestSigner(String apiKey, String apiSecret, String accessToken, String accessTokenSecret) {
		this.accessToken = accessToken;
		this.accessTokenSecret = accessTokenSecret;
		this.service = new ServiceBuilder().provider(PreAuthorizedOAuthApi.class).apiKey(apiKey).apiSecret(apiSecret)
				.build();
	}

	protected String buildAuthorizationHeader(HttpMethod method, String url, Map<String, String> parameters) {
		String adjustedUrl = decode(url).replace("#", "%23");
		OAuthRequest request = new OAuthRequest(Verb.valueOf(method.name()), adjustedUrl);

		for (String key : parameters.keySet()) {
			request.addBodyParameter(key, decode(parameters.get(key)));
		}

		Token token = new Token(accessToken, accessTokenSecret);
		service.signRequest(token, request);
		return request.getHeaders().get("Authorization");
	}
}
