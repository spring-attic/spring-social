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
package org.springframework.social.web.signin;

import javax.inject.Inject;
import javax.inject.Provider;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.social.connect.MultiUserServiceProviderConnectionRepository;
import org.springframework.social.connect.ServiceProviderConnection;
import org.springframework.social.connect.ServiceProviderConnectionFactory;
import org.springframework.social.connect.ServiceProviderConnectionFactoryLocator;
import org.springframework.social.connect.ServiceProviderConnectionRepository;
import org.springframework.social.connect.support.OAuth1ServiceProviderConnectionFactory;
import org.springframework.social.connect.support.OAuth2ServiceProviderConnectionFactory;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

@Controller
@RequestMapping("/signin/")
public class ProviderSignInController {

	private final ServiceProviderConnectionFactoryLocator connectionFactoryLocator;

	private final MultiUserServiceProviderConnectionRepository usersConnectionRepository;
	
	private final Provider<ServiceProviderConnectionRepository> currentUserConnectionRepositoryProvider;
	
	private final String baseCallbackUrl;
	
	private final SignInService signInService;

	private String signupUrl = "/signup";
	
	@Inject
	public ProviderSignInController(String applicationUrl, ServiceProviderConnectionFactoryLocator connectionFactoryLocator, MultiUserServiceProviderConnectionRepository usersConnectionRepository,
			Provider<ServiceProviderConnectionRepository> currentUserConnectionRepositoryProvider, SignInService signInService) {
		this.connectionFactoryLocator = connectionFactoryLocator;
		this.usersConnectionRepository = usersConnectionRepository;
		this.currentUserConnectionRepositoryProvider = currentUserConnectionRepositoryProvider;
		this.signInService = signInService;
		this.baseCallbackUrl = applicationUrl + AnnotationUtils.findAnnotation(getClass(), RequestMapping.class).value()[0];
	}
	
	@RequestMapping(value="{providerId}", method=RequestMethod.POST)
	public String signin(@PathVariable String providerId, WebRequest request) {
		ServiceProviderConnectionFactory<?> connectionFactory = connectionFactoryLocator.getConnectionFactory(providerId);
		if (connectionFactory instanceof OAuth1ServiceProviderConnectionFactory) {
			OAuth1Operations oauth1Ops = ((OAuth1ServiceProviderConnectionFactory<?>) connectionFactory).getOAuthOperations();
			OAuthToken requestToken = oauth1Ops.fetchRequestToken(callbackUrl(providerId), null);
			request.setAttribute(OAUTH_TOKEN_ATTRIBUTE, requestToken, WebRequest.SCOPE_SESSION);
			return "redirect:" + oauth1Ops.buildAuthenticateUrl(requestToken.getValue(), callbackUrl(providerId));
		} else if (connectionFactory instanceof OAuth2ServiceProviderConnectionFactory) {
			return "redirect:" + ((OAuth2ServiceProviderConnectionFactory<?>) connectionFactory).getOAuthOperations().buildAuthenticateUrl(callbackUrl(providerId), null, GrantType.AuthorizationCode, null);
		} else {
			throw new IllegalStateException("Sign in using provider '" + providerId + "' not supported");
		}
	}
	
	@RequestMapping(value="{providerId}", method=RequestMethod.GET, params="oauth_token")
	public String oauth1Callback(@PathVariable String providerId, @RequestParam("oauth_token") String token, @RequestParam(value="oauth_verifier", required=false) String verifier, WebRequest request) {
		OAuth1ServiceProviderConnectionFactory<?> connectionFactory = (OAuth1ServiceProviderConnectionFactory<?>) connectionFactoryLocator.getConnectionFactory(providerId);
		OAuthToken accessToken = connectionFactory.getOAuthOperations().exchangeForAccessToken(new AuthorizedRequestToken(extractCachedRequestToken(request), verifier), null);
		ServiceProviderConnection<?> connection = connectionFactory.createConnection(accessToken);
		return handleSignIn(connection, request);
	}
	
	@RequestMapping(value="{providerId}", method=RequestMethod.GET, params="code")
	public String oauth2Callback(@PathVariable String providerId, @RequestParam("code") String code, WebRequest request) {
		OAuth2ServiceProviderConnectionFactory<?> connectionFactory = (OAuth2ServiceProviderConnectionFactory<?>) connectionFactoryLocator.getConnectionFactory(providerId);
		AccessGrant accessGrant = connectionFactory.getOAuthOperations().exchangeForAccess(code, callbackUrl(providerId), null);
		ServiceProviderConnection<?> connection = connectionFactory.createConnection(accessGrant);
		return handleSignIn(connection, request);
	}

	// internal helpers
	
	private String callbackUrl(String providerId) {
		return baseCallbackUrl + providerId;
	}
	
	private OAuthToken extractCachedRequestToken(WebRequest request) {
		OAuthToken requestToken = (OAuthToken) request.getAttribute(OAUTH_TOKEN_ATTRIBUTE, WebRequest.SCOPE_SESSION);
		request.removeAttribute(OAUTH_TOKEN_ATTRIBUTE, WebRequest.SCOPE_SESSION);
		return requestToken;
	}

	private String handleSignIn(ServiceProviderConnection<?> connection, WebRequest request) {
		String localUserId = usersConnectionRepository.findLocalUserIdConnectedTo(connection.getKey());
		if (localUserId == null) {
			ProviderSignInAttempt signInAttempt = new ProviderSignInAttempt(connection, currentUserConnectionRepositoryProvider);
			request.setAttribute(ProviderSignInAttempt.SESSION_ATTRIBUTE, signInAttempt, WebRequest.SCOPE_SESSION);
			return "redirect:" + signupUrl;
		} else {
			signIn(localUserId);
			return "redirect:/";
		}		
	}
	
	private void signIn(String localUserId) {
		signInService.signIn(localUserId);		
	}
	
	private static final String OAUTH_TOKEN_ATTRIBUTE = "oauthToken";
}
