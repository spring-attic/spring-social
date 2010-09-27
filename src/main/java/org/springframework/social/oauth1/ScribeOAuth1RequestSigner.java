package org.springframework.social.oauth1;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
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
		// TODO : REMOVE THE SPECIFIC API HERE...FIGURE OUT WHICH ONE WE NEED OR
		// BUILD UP THE SERVICE PROGRAMMATICALLY
		this.service = new ServiceBuilder().provider(PreAuthorizedOAuthApi.class).apiKey(apiKey).apiSecret(apiSecret)
				.callback("http://greenhouse.springsource.org").build();
	}

	protected String buildAuthorizationHeader(HttpMethod method, URL url, Map<String, String> parameters) {
		try {
			// Need to decode the URL given because Scribe is assuming that it
			// isn't UTF-8 encoded yet and will re-encode it.
			String decodedUrl = URLDecoder.decode(url.toString(), "UTF-8");
			OAuthRequest request = new OAuthRequest(Verb.valueOf(method.name()), decodedUrl);
			Token token = new Token(accessToken, accessTokenSecret);
			service.signRequest(token, request);
			return request.getHeaders().get("Authorization");
		} catch (UnsupportedEncodingException shouldntHappen) {
			return null;
		}
	}
}
