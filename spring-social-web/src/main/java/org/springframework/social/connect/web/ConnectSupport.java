/*
 * Copyright 2011 the original author or authors.
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
package org.springframework.social.connect.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.support.OAuth1ConnectionFactory;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuth1Version;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

/**
 * Provides common connect support and utilities for Java web/servlet environments.
 * Used by {@link ConnectController} and {@link ProviderSignInController}.
 * @author Keith Donald
 */
public class ConnectSupport {

	private boolean useAuthenticateUrl;

	private String applicationUrl;

	/**
	 * Flag indicating if this instance will support OAuth-based authentication instead of the traditional user authorization.
	 * Some providers expose a special "authenticateUrl" the user should be redirected to as part of an OAuth-based authentication attempt.
	 * Setting this flag to true has {@link #buildOAuthUrl(ConnectionFactory, NativeWebRequest) oauthUrl} return this authenticate URL.
	 * @param useAuthenticateUrl whether to use the authenticat url or not
	 * @see OAuth1Operations#buildAuthenticateUrl(String, OAuth1Parameters)
	 * @see OAuth2Operations#buildAuthenticateUrl(GrantType, OAuth2Parameters)
	 */
	public void setUseAuthenticateUrl(boolean useAuthenticateUrl) {
		this.useAuthenticateUrl = useAuthenticateUrl;
	}

	/**
	 * Configures the base secure URL for the application this controller is being used in e.g. <code>https://myapp.com</code>. Defaults to null.
	 * If specified, will be used to generate OAuth callback URLs.
	 * If not specified, OAuth callback URLs are generated from {@link HttpServletRequest HttpServletRequests}. 
	 * You may wish to set this property if requests into your application flow through a proxy to your application server.
	 * In this case, the HttpServletRequest URI may contain a scheme, host, and/or port value that points to an internal server not appropriate for an external callback URL.
	 * If you have this problem, you can set this property to the base external URL for your application and it will be used to construct the callback URL instead.
	 * @param applicationUrl the application URL value
	 */
	public void setApplicationUrl(String applicationUrl) {
		this.applicationUrl = applicationUrl;
	}

	/**
	 * Builds the provider URL to redirect the user to for connection authorization.
	 * @param connectionFactory the service provider's connection factory e.g. FacebookConnectionFactory
	 * @param request the current web request
	 * @return the URL to redirect the user to for authorization
	 * @throws IllegalArgumentException if the connection factory is not OAuth1 based.
	 */
	public String buildOAuthUrl(ConnectionFactory<?> connectionFactory, NativeWebRequest request) {
		return buildOAuthUrl(connectionFactory, request, null);
	}
	
	/**
	 * Builds the provider URL to redirect the user to for connection authorization.
	 * @param connectionFactory the service provider's connection factory e.g. FacebookConnectionFactory
	 * @param request the current web request
	 * @param additionalParameters parameters to add to the authorization URL.
	 * @return the URL to redirect the user to for authorization
	 * @throws IllegalArgumentException if the connection factory is not OAuth1 based.
	 */
	public String buildOAuthUrl(ConnectionFactory<?> connectionFactory, NativeWebRequest request, MultiValueMap<String, String> additionalParameters) {
		if (connectionFactory instanceof OAuth1ConnectionFactory) {
			return buildOAuth1Url((OAuth1ConnectionFactory<?>) connectionFactory, request, additionalParameters);
		} else if (connectionFactory instanceof OAuth2ConnectionFactory) {
			return buildOAuth2Url((OAuth2ConnectionFactory<?>) connectionFactory, request, additionalParameters);
		} else {
			throw new IllegalArgumentException("ConnectionFactory not supported");
		}		
	}

	/**
	 * Complete the connection to the OAuth1 provider.
	 * @param connectionFactory the service provider's connection factory e.g. FacebookConnectionFactory
	 * @param request the current web request
	 * @return a new connection to the service provider
	 */
	public Connection<?> completeConnection(OAuth1ConnectionFactory<?> connectionFactory, NativeWebRequest request) {
		String verifier = request.getParameter("oauth_verifier"); 
		AuthorizedRequestToken requestToken = new AuthorizedRequestToken(extractCachedRequestToken(request), verifier);
		OAuthToken accessToken = connectionFactory.getOAuthOperations().exchangeForAccessToken(requestToken, null);
		return connectionFactory.createConnection(accessToken);
	}

	/**
	 * Complete the connection to the OAuth2 provider.
	 * @param connectionFactory the service provider's connection factory e.g. FacebookConnectionFactory
	 * @param request the current web request
	 * @return a new connection to the service provider
	 */
	public Connection<?> completeConnection(OAuth2ConnectionFactory<?> connectionFactory, NativeWebRequest request) {
		String code = request.getParameter("code");
		AccessGrant accessGrant = connectionFactory.getOAuthOperations().exchangeForAccess(code, callbackUrl(request), null);
		return connectionFactory.createConnection(accessGrant);		
	}

	// internal helpers
	
	private String buildOAuth1Url(OAuth1ConnectionFactory<?> connectionFactory, NativeWebRequest request, MultiValueMap<String, String> additionalParameters) {
		OAuth1Operations oauthOperations = connectionFactory.getOAuthOperations();
		OAuth1Parameters parameters = new OAuth1Parameters(additionalParameters);
		if (oauthOperations.getVersion() == OAuth1Version.CORE_10) {
			parameters.setCallbackUrl(callbackUrl(request));
		}
		OAuthToken requestToken = fetchRequestToken(request, oauthOperations);
		request.setAttribute(OAUTH_TOKEN_ATTRIBUTE, requestToken, RequestAttributes.SCOPE_SESSION);
		return buildOAuth1Url(oauthOperations, requestToken.getValue(), parameters);
	}

	private OAuthToken fetchRequestToken(NativeWebRequest request, OAuth1Operations oauthOperations) {
		if (oauthOperations.getVersion() == OAuth1Version.CORE_10_REVISION_A) {
			return oauthOperations.fetchRequestToken(callbackUrl(request), null);
		}
		return oauthOperations.fetchRequestToken(null, null);				
	}

	private String buildOAuth2Url(OAuth2ConnectionFactory<?> connectionFactory, NativeWebRequest request, MultiValueMap<String, String> additionalParameters) {
		OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
		OAuth2Parameters parameters = getOAuth2Parameters(request, additionalParameters);
		if (useAuthenticateUrl) { 
			return oauthOperations.buildAuthenticateUrl(GrantType.AUTHORIZATION_CODE, parameters);						
		} else {
			return oauthOperations.buildAuthorizeUrl(GrantType.AUTHORIZATION_CODE, parameters);			
		}
	}

	private OAuth2Parameters getOAuth2Parameters(NativeWebRequest request, MultiValueMap<String, String> additionalParameters) {
		OAuth2Parameters parameters = new OAuth2Parameters(additionalParameters);
		parameters.setRedirectUri(callbackUrl(request));
		String scope = request.getParameter("scope");
		if (scope != null) {
			parameters.setScope(scope);
		}
		return parameters;
	}

	private String callbackUrl(NativeWebRequest request) {
		HttpServletRequest nativeRequest = request.getNativeRequest(HttpServletRequest.class);
		if (applicationUrl != null) {
			return applicationUrl + connectPath(nativeRequest);
		} else {
			return nativeRequest.getRequestURL().toString();
		}
	}

	private String connectPath(HttpServletRequest request) {
		String pathInfo = request.getPathInfo();
		return request.getServletPath() + (pathInfo != null ? pathInfo : "");
	}

	private String buildOAuth1Url(OAuth1Operations oauthOperations, String requestToken, OAuth1Parameters parameters) {
		if (useAuthenticateUrl) {
			return oauthOperations.buildAuthenticateUrl(requestToken, parameters);			
		} else {
			return oauthOperations.buildAuthorizeUrl(requestToken, parameters);
		}
	}

	private OAuthToken extractCachedRequestToken(WebRequest request) {
		OAuthToken requestToken = (OAuthToken) request.getAttribute(OAUTH_TOKEN_ATTRIBUTE, RequestAttributes.SCOPE_SESSION);
		request.removeAttribute(OAUTH_TOKEN_ATTRIBUTE, RequestAttributes.SCOPE_SESSION);
		return requestToken;
	}
	
	private static final String OAUTH_TOKEN_ATTRIBUTE = "oauthToken";

}