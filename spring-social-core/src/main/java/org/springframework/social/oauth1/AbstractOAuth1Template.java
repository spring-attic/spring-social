package org.springframework.social.oauth1;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

public class AbstractOAuth1Template {
	protected final String consumerKey;

	protected final String consumerSecret;

	protected final String requestTokenUrl;

	protected final UriTemplate authorizeUrlTemplate;

	protected final String accessTokenUrl;

	protected final RestTemplate restTemplate = new RestTemplate();

	public AbstractOAuth1Template(String consumerKey, String consumerSecret, String requestTokenUrl,
			String authorizeUrl, String accessTokenUrl) {
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.requestTokenUrl = requestTokenUrl;
		this.authorizeUrlTemplate = new UriTemplate(authorizeUrl);
		this.accessTokenUrl = accessTokenUrl;
	}

	// internal helpers
	
	protected OAuthToken getTokenFromProvider(String tokenUrl, Map<String, String> tokenRequestParameters,
			Map<String, String> additionalParameters, String tokenSecret) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", getAuthorizationHeaderValue(tokenUrl, tokenRequestParameters, additionalParameters, tokenSecret));
		MultiValueMap<String, String> bodyParameters = new LinkedMultiValueMap<String, String>();
		bodyParameters.setAll(additionalParameters);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(bodyParameters, headers);
		@SuppressWarnings("rawtypes")
		ResponseEntity<MultiValueMap> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, MultiValueMap.class);
		@SuppressWarnings("unchecked")
		MultiValueMap<String, String> responseMap = response.getBody();
		return new OAuthToken(responseMap.getFirst("oauth_token"), responseMap.getFirst("oauth_token_secret"));
	}

	protected String getAuthorizationHeaderValue(String tokenUrl, Map<String, String> tokenRequestParameters,
			Map<String, String> additionalParameters, String tokenSecret) {
		Map<String, String> oauthParameters = SigningUtils.commonOAuthParameters(consumerKey);
		oauthParameters.putAll(tokenRequestParameters);
		return SigningUtils.buildAuthorizationHeaderValue(tokenUrl, oauthParameters, additionalParameters,
				HttpMethod.POST, consumerSecret, tokenSecret);
	}
	
	// testing hooks
	RestTemplate getRestTemplate() {
		return restTemplate;
	}
}
