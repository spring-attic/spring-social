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
package org.springframework.social.oauth2;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.social.support.ClientHttpRequestFactorySelector;
import org.springframework.social.support.FormMapHttpMessageConverter;
import org.springframework.social.support.LoggingErrorHandler;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * OAuth2Operations implementation that uses REST-template to make the OAuth calls.
 * @author Keith Donald
 * @author Roy Clarkson
 */
public class OAuth2Template implements OAuth2Operations {

	private final String clientId;
	
	private final String clientSecret;

	private final String accessTokenUrl;

	private final String authorizeUrl;

	private String authenticateUrl;
	
	private RestTemplate restTemplate;
	
	private boolean useParametersForClientAuthentication; 

	/**
	 * Constructs an OAuth2Template for a given set of client credentials. 
	 * Assumes that the authorization URL is the same as the authentication URL.
	 * @param clientId the client ID
	 * @param clientSecret the client secret
	 * @param authorizeUrl the base URL to redirect to when doing authorization code or implicit grant authorization
	 * @param accessTokenUrl the URL at which an authorization code, refresh token, or user credentials may be exchanged for an access token.
	 */
	public OAuth2Template(String clientId, String clientSecret, String authorizeUrl, String accessTokenUrl) {
		this(clientId, clientSecret, authorizeUrl, null, accessTokenUrl);
	}

	/**
	 * Constructs an OAuth2Template for a given set of client credentials. 
	 * @param clientId the client ID
	 * @param clientSecret the client secret
	 * @param authorizeUrl the base URL to redirect to when doing authorization code or implicit grant authorization
	 * @param authenticateUrl the URL to redirect to when doing authentication via authorization code grant
	 * @param accessTokenUrl the URL at which an authorization code, refresh token, or user credentials may be exchanged for an access token
	 */
	public OAuth2Template(String clientId, String clientSecret, String authorizeUrl, String authenticateUrl, String accessTokenUrl) {
		Assert.notNull(clientId, "The clientId property cannot be null");
		Assert.notNull(clientSecret, "The clientSecret property cannot be null");
		Assert.notNull(authorizeUrl, "The authorizeUrl property cannot be null");
		Assert.notNull(accessTokenUrl, "The accessTokenUrl property cannot be null");
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		String clientInfo = "?client_id=" + formEncode(clientId);
		this.authorizeUrl = authorizeUrl + clientInfo;
		if (authenticateUrl != null) {
			this.authenticateUrl = authenticateUrl + clientInfo;
		} else {
			this.authenticateUrl = null;
		}
		this.accessTokenUrl = accessTokenUrl;
	}
	
	/**
	 * Set to true to pass client credentials to the provider as parameters instead of using HTTP Basic authentication.
	 * @param useParametersForClientAuthentication true if the client credentials should be passed as parameters; false if passed via HTTP Basic
	 */
	public void setUseParametersForClientAuthentication(boolean useParametersForClientAuthentication) {
		this.useParametersForClientAuthentication = useParametersForClientAuthentication;
	}

	/**
	 * Set the request factory on the underlying RestTemplate.
	 * This can be used to plug in a different HttpClient to do things like configure custom SSL settings.
	 * @param requestFactory the request factory used by the underlying RestTemplate
	 */
	public void setRequestFactory(ClientHttpRequestFactory requestFactory) {
		Assert.notNull(requestFactory, "The requestFactory property cannot be null");
		getRestTemplate().setRequestFactory(requestFactory);
	}

	public String buildAuthorizeUrl(OAuth2Parameters parameters) {
		return buildAuthUrl(authorizeUrl, GrantType.AUTHORIZATION_CODE, parameters);
	}

	public String buildAuthorizeUrl(GrantType grantType, OAuth2Parameters parameters) {
		return buildAuthUrl(authorizeUrl, grantType, parameters);
	}

	public String buildAuthenticateUrl(OAuth2Parameters parameters) {
		return authenticateUrl != null ? buildAuthUrl(authenticateUrl, GrantType.AUTHORIZATION_CODE, parameters) : buildAuthorizeUrl(GrantType.AUTHORIZATION_CODE, parameters);
	}

	public String buildAuthenticateUrl(GrantType grantType, OAuth2Parameters parameters) {
		return authenticateUrl != null ? buildAuthUrl(authenticateUrl, grantType, parameters) : buildAuthorizeUrl(grantType, parameters);
	}

	public AccessGrant exchangeForAccess(String authorizationCode, String redirectUri, MultiValueMap<String, String> additionalParameters) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		if (useParametersForClientAuthentication) {
			params.set("client_id", clientId);
			params.set("client_secret", clientSecret);
		}
		params.set("code", authorizationCode);
		params.set("redirect_uri", redirectUri);
		params.set("grant_type", "authorization_code");
		if (additionalParameters != null) {
			params.putAll(additionalParameters);
		}
		return postForAccessGrant(accessTokenUrl, params);
	}
	

	public AccessGrant exchangeCredentialsForAccess(String username, String password, MultiValueMap<String, String> additionalParameters) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		if (useParametersForClientAuthentication) {
			params.set("client_id", clientId);
			params.set("client_secret", clientSecret);
		}
		params.set("username", username);
		params.set("password", password);
		params.set("grant_type", "password");
		if (additionalParameters != null) {
			params.putAll(additionalParameters);
		}
		return postForAccessGrant(accessTokenUrl, params);
	}

	@Deprecated
	public AccessGrant refreshAccess(String refreshToken, String scope, MultiValueMap<String, String> additionalParameters) {
		additionalParameters.set("scope", scope);
		return refreshAccess(refreshToken, additionalParameters);
	}
	
	public AccessGrant refreshAccess(String refreshToken, MultiValueMap<String, String> additionalParameters) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		if (useParametersForClientAuthentication) {
			params.set("client_id", clientId);
			params.set("client_secret", clientSecret);
		}
		params.set("refresh_token", refreshToken);
		params.set("grant_type", "refresh_token");
		if (additionalParameters != null) {
			params.putAll(additionalParameters);
		}
		return postForAccessGrant(accessTokenUrl, params);
	}

	public AccessGrant authenticateClient() {
		return authenticateClient(null);
	}
	
	public AccessGrant authenticateClient(String scope) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		if (useParametersForClientAuthentication) {
			params.set("client_id", clientId);
			params.set("client_secret", clientSecret);
		}
		params.set("grant_type", "client_credentials");
		if (scope != null) {
			params.set("scope", scope);
		}
		return postForAccessGrant(accessTokenUrl, params);
	}

    // subclassing hooks
	
	/**
	 * Creates the {@link RestTemplate} used to communicate with the provider's OAuth 2 API.
	 * This implementation creates a RestTemplate with a minimal set of HTTP message converters ({@link FormHttpMessageConverter} and {@link MappingJackson2HttpMessageConverter}).
	 * May be overridden to customize how the RestTemplate is created.
	 * For example, if the provider returns data in some format other than JSON for form-encoded, you might override to register an appropriate message converter.
	 * @return a {@link RestTemplate} used to communicate with the provider's OAuth 2 API 
	 */
	protected RestTemplate createRestTemplate() {
		ClientHttpRequestFactory requestFactory = ClientHttpRequestFactorySelector.getRequestFactory();
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>(2);
		converters.add(new FormHttpMessageConverter());
		converters.add(new FormMapHttpMessageConverter());
		converters.add(new MappingJackson2HttpMessageConverter());
		restTemplate.setMessageConverters(converters);
		restTemplate.setErrorHandler(new LoggingErrorHandler());
		if (!useParametersForClientAuthentication) {
			List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
			if (interceptors == null) {   // defensively initialize list if it is null. (See SOCIAL-430)
				interceptors = new ArrayList<ClientHttpRequestInterceptor>();
				restTemplate.setInterceptors(interceptors);
			}
			interceptors.add(new PreemptiveBasicAuthClientHttpRequestInterceptor(clientId, clientSecret));
		}
		return restTemplate;
	}

	/**
	 * Posts the request for an access grant to the provider.
	 * The default implementation uses RestTemplate to request the access token and expects a JSON response to be bound to a Map. The information in the Map will be used to create an {@link AccessGrant}.
	 * Since the OAuth 2 specification indicates that an access token response should be in JSON format, there's often no need to override this method.
	 * If all you need to do is capture provider-specific data in the response, you should override createAccessGrant() instead.
	 * However, in the event of a provider whose access token response is non-JSON, you may need to override this method to request that the response be bound to something other than a Map.
	 * For example, if the access token response is given as form-encoded, this method should be overridden to call RestTemplate.postForObject() asking for the response to be bound to a MultiValueMap (whose contents can then be used to create an AccessGrant).
	 * @param accessTokenUrl the URL of the provider's access token endpoint.
	 * @param parameters the parameters to post to the access token endpoint.
	 * @return the access grant.
	 */
	@SuppressWarnings("unchecked")
	protected AccessGrant postForAccessGrant(String accessTokenUrl, MultiValueMap<String, String> parameters) {
		return extractAccessGrant(getRestTemplate().postForObject(accessTokenUrl, parameters, Map.class));
	}
	
	/**
	 * Creates an {@link AccessGrant} given the response from the access token exchange with the provider.
	 * May be overridden to create a custom AccessGrant that captures provider-specific information from the access token response. 
	 * @param accessToken the access token value received from the provider
	 * @param scope the scope of the access token
	 * @param refreshToken a refresh token value received from the provider
	 * @param expiresIn the time (in seconds) remaining before the access token expires.
	 * @param response all parameters from the response received in the access token exchange.
	 * @return an {@link AccessGrant}
	 */
	protected AccessGrant createAccessGrant(String accessToken, String scope, String refreshToken, Long expiresIn, Map<String, Object> response) {
		return new AccessGrant(accessToken, scope, refreshToken, expiresIn);
	}
		
	// testing hooks
	
	protected RestTemplate getRestTemplate() {
		// Lazily create RestTemplate to make sure all parameters have had a chance to be set.
		// Can't do this InitializingBean.afterPropertiesSet() because it will often be created directly and not as a bean.
		if (restTemplate == null) {
			restTemplate = createRestTemplate();
		}
		return restTemplate;
	}
	
	// internal helpers

	private String buildAuthUrl(String baseAuthUrl, GrantType grantType, OAuth2Parameters parameters) {
		StringBuilder authUrl = new StringBuilder(baseAuthUrl);
		if (grantType == GrantType.AUTHORIZATION_CODE) {
			authUrl.append('&').append("response_type").append('=').append("code");
		} else if (grantType == GrantType.IMPLICIT_GRANT) {
			authUrl.append('&').append("response_type").append('=').append("token");
		}
		for (Iterator<Entry<String, List<String>>> additionalParams = parameters.entrySet().iterator(); additionalParams.hasNext();) {
			Entry<String, List<String>> param = additionalParams.next();
			String name = formEncode(param.getKey());
			for (Iterator<String> values = param.getValue().iterator(); values.hasNext();) {
				authUrl.append('&').append(name);
				String value = values.next();
				if (StringUtils.hasLength(value)) {
					authUrl.append('=').append(formEncode(value));
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
	
	private AccessGrant extractAccessGrant(Map<String, Object> result) {
		return createAccessGrant((String) result.get("access_token"), (String) result.get("scope"), (String) result.get("refresh_token"), getIntegerValue(result, "expires_in"), result);
	}

	// Retrieves object from map into an Integer, regardless of the object's actual type. Allows for flexibility in object type (eg, "3600" vs 3600).
	private Long getIntegerValue(Map<String, Object> map, String key) {
		try {
			return Long.valueOf(String.valueOf(map.get(key))); // normalize to String before creating integer value;			
		} catch (NumberFormatException e) {
			return null;
		}
	}

}
