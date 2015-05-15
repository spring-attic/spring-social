/*
 * Copyright 2015 the original author or authors.
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

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.social.support.ClientHttpRequestFactorySelector;
import org.springframework.social.support.LoggingErrorHandler;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * OAuth10Operations implementation that uses REST-template to make the OAuth calls.
 * @author Keith Donald
 */
public class OAuth1Template implements OAuth1Operations {

	private final String consumerKey;

	private final String consumerSecret;

	private final URI requestTokenUrl;

	private final String authenticateUrl;
	
	private final String authorizeUrl;

	private final URI accessTokenUrl;

	private final RestTemplate restTemplate;

	private final OAuth1Version version;
	
	private final SigningSupport signingUtils;

	public OAuth1Template(String consumerKey, String consumerSecret, String requestTokenUrl, String authorizeUrl, String accessTokenUrl) {
		this(consumerKey, consumerSecret, requestTokenUrl, authorizeUrl, accessTokenUrl, OAuth1Version.CORE_10_REVISION_A);
	}

	public OAuth1Template(String consumerKey, String consumerSecret, String requestTokenUrl, String authorizeUrl, String accessTokenUrl, OAuth1Version version) {
		this(consumerKey, consumerSecret, requestTokenUrl, authorizeUrl, null, accessTokenUrl, version);
	}

	public OAuth1Template(String consumerKey, String consumerSecret, String requestTokenUrl, String authorizeUrl, String authenticateUrl, String accessTokenUrl) {
		this(consumerKey, consumerSecret, requestTokenUrl, authorizeUrl, authenticateUrl, accessTokenUrl, OAuth1Version.CORE_10_REVISION_A);
	}
	
	public OAuth1Template(String consumerKey, String consumerSecret, String requestTokenUrl, String authorizeUrl, String authenticateUrl, String accessTokenUrl, OAuth1Version version) {
		Assert.notNull(consumerKey, "The consumerKey property cannot be null");
		Assert.notNull(consumerSecret, "The consumerSecret property cannot be null");
		Assert.notNull(requestTokenUrl, "The requestTokenUrl property cannot be null");
		Assert.notNull(authorizeUrl, "The authorizeUrl property cannot be null");
		Assert.notNull(accessTokenUrl, "The accessTokenUrl property cannot be null");
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.requestTokenUrl = encodeTokenUri(requestTokenUrl);
		this.authorizeUrl = authorizeUrl;
		this.authenticateUrl = authenticateUrl;
		this.accessTokenUrl = encodeTokenUri(accessTokenUrl);
		this.version = version;
		this.restTemplate = createRestTemplate();
		this.signingUtils = new SigningSupport();
	}

	/**
	 * Set the request factory on the underlying RestTemplate.
	 * This can be used to plug in a different HttpClient to do things like configure custom SSL settings.
	 * @param requestFactory the request factory on the underlying RestTemplate.
	 */
	public void setRequestFactory(ClientHttpRequestFactory requestFactory) {
		Assert.notNull(requestFactory, "The requestFactory property cannot be null");
		restTemplate.setRequestFactory(requestFactory);
	}
	
	// implementing OAuth1Operations
	
	public OAuth1Version getVersion() {
		return version;
	}
	
	public OAuthToken fetchRequestToken(String callbackUrl, MultiValueMap<String, String> additionalParameters) {
		Map<String, String> oauthParameters = new HashMap<String, String>(1, 1);
		if (version == OAuth1Version.CORE_10_REVISION_A) {
			oauthParameters.put("oauth_callback", callbackUrl);
		}
		return exchangeForToken(requestTokenUrl, oauthParameters, additionalParameters, null);
	}

	public String buildAuthorizeUrl(String requestToken, OAuth1Parameters parameters) {
		return buildAuthUrl(authorizeUrl, requestToken, parameters);
	}
	
	public String buildAuthenticateUrl(String requestToken, OAuth1Parameters parameters) {
		return authenticateUrl != null ? buildAuthUrl(authenticateUrl, requestToken, parameters) : buildAuthorizeUrl(requestToken, parameters);
	}

	public OAuthToken exchangeForAccessToken(AuthorizedRequestToken requestToken, MultiValueMap<String, String> additionalParameters) {
		Map<String, String> tokenParameters = new HashMap<String, String>(2, 1);
		tokenParameters.put("oauth_token", requestToken.getValue());
		if (version == OAuth1Version.CORE_10_REVISION_A) {
			tokenParameters.put("oauth_verifier", requestToken.getVerifier());
		}
		return exchangeForToken(accessTokenUrl, tokenParameters, additionalParameters, requestToken.getSecret());
	}

	// subclassing hooks

	/**
	 * Exposes the consumer key to be read by subclasses.
	 * This may be useful when overriding {@link #addCustomAuthorizationParameters(MultiValueMap)} and the consumer key is required in the authorization request.
	 * @return the consumer key to be read by subclasses.
	 */
	protected String getConsumerKey() {
		return consumerKey;
	}
	
	/**
	 * Creates an {@link OAuthToken} given the response from the request token or access token exchange with the provider.
	 * May be overridden to create a custom {@link OAuthToken}.
	 * @param tokenValue the token value received from the provider.
	 * @param tokenSecret the token secret received from the provider.
	 * @param response all parameters from the response received in the request/access token exchange.
	 * @return an {@link OAuthToken}
	 */
	protected OAuthToken createOAuthToken(String tokenValue, String tokenSecret, MultiValueMap<String, String> response) {
		return new OAuthToken(tokenValue, tokenSecret);
	}

	/**
	 * Subclassing hook to add custom authorization parameters to the authorization URL.
	 * Default implementation adds no parameters.
	 * @param parameters custom parameters for authorization
	 */
	protected void addCustomAuthorizationParameters(MultiValueMap<String, String> parameters) {
	}
	
	// internal helpers

	private RestTemplate createRestTemplate() {
		RestTemplate restTemplate = new RestTemplate(ClientHttpRequestFactorySelector.getRequestFactory());
		List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>(1);
		converters.add(new FormHttpMessageConverter() {
			public boolean canRead(Class<?> clazz, MediaType mediaType) {
				// always read MultiValueMaps as x-www-url-formencoded even if contentType not set properly by provider				
				return MultiValueMap.class.isAssignableFrom(clazz);
			}
		});
		restTemplate.setMessageConverters(converters);
		restTemplate.setErrorHandler(new LoggingErrorHandler());
		return restTemplate;
	}
	
	private URI encodeTokenUri(String url) {
		return UriComponentsBuilder.fromUriString(url).build().toUri();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private OAuthToken exchangeForToken(URI tokenUrl, Map<String, String> tokenParameters, MultiValueMap<String, String> additionalParameters, String tokenSecret) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", buildAuthorizationHeaderValue(tokenUrl, tokenParameters, additionalParameters, tokenSecret));
		ResponseEntity<MultiValueMap> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, new HttpEntity<MultiValueMap<String, String>>(additionalParameters, headers), MultiValueMap.class);
		MultiValueMap<String, String> body = response.getBody();
		return createOAuthToken(body.getFirst("oauth_token"), body.getFirst("oauth_token_secret"), body);
	}

	private String buildAuthorizationHeaderValue(URI tokenUrl, Map<String, String> tokenParameters, MultiValueMap<String, String> additionalParameters, String tokenSecret) {
		Map<String, String> oauthParameters = signingUtils.commonOAuthParameters(consumerKey);
		oauthParameters.putAll(tokenParameters);
		if (additionalParameters == null) {
			additionalParameters = EmptyMultiValueMap.instance();
		}
		return signingUtils.buildAuthorizationHeaderValue(HttpMethod.POST, tokenUrl, oauthParameters, additionalParameters, consumerSecret, tokenSecret);
	}

	private String buildAuthUrl(String baseAuthUrl, String requestToken, OAuth1Parameters parameters) {
		StringBuilder authUrl = new StringBuilder(baseAuthUrl).append('?').append("oauth_token").append('=').append(formEncode(requestToken));
		addCustomAuthorizationParameters(parameters);
		if (parameters != null) {
			for (Iterator<Entry<String, List<String>>> additionalParams = parameters.entrySet().iterator(); additionalParams.hasNext();) {
				Entry<String, List<String>> param = additionalParams.next();
				String name = formEncode(param.getKey());
				for (Iterator<String> values = param.getValue().iterator(); values.hasNext();) {
					authUrl.append('&').append(name).append('=').append(formEncode(values.next()));
				}
			}
		}		
		return authUrl.toString();
	}
	
	private String formEncode(String data) {
		try {
			return URLEncoder.encode(data, "UTF-8");
		}
		catch (UnsupportedEncodingException ex) {
			// should not happen, UTF-8 is always supported
			throw new IllegalStateException(ex);
		}
	}
	
	// testing hooks
	RestTemplate getRestTemplate() {
		return restTemplate;
	}

}
