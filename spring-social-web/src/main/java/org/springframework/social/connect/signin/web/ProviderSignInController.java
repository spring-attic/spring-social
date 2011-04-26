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
package org.springframework.social.connect.signin.web;

import java.io.NotSerializableException;

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
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuth1Version;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

/**
 * Spring MVC Controller for handling the provider user sign-in flow.
 * <ul>
 * <li>POST /signin/{providerId}  - Initiate user sign-in with {providerId}.</li>
 * <li>GET /signin/{providerId}?oauth_token||code - Receive {providerId} authentication callback and establish the connection.</li>
 * </ul>
 * @author Keith Donald
 */
@Controller
@RequestMapping("/signin")
public class ProviderSignInController {

	private final Provider<ServiceProviderConnectionFactoryLocator> connectionFactoryLocatorProvider;

	private final MultiUserServiceProviderConnectionRepository usersConnectionRepository;
	
	private final Provider<ServiceProviderConnectionRepository> currentUserConnectionRepositoryProvider;
	
	private final String baseCallbackUrl;
	
	private final SignInService signInService;

	private String signupUrl = "/signup";
	
	/**
	 * Creates a new provider sign-in controller.
	 * @param applicationUrl the base secure URL for this application, used to construct the callback URL passed to the service providers at the beginning of the sign-in process.
	 * @param connectionFactoryLocatorProvider a provider for the locator of {@link ServiceProviderConnectionFactory connection factories} that can be used for sign-in;
	 * A JSR330 Provider is injected here instead of the actual locator object to support the fact {@link ProviderSignInAttempt} objects are session-scoped and thus require a Serializable reference to a {@link ServiceProviderConnectionFactoryLocator}.
	 * The injected Provider should be Serializable, otherwise {@link NotSerializableException} instances could occur during the provider sign-in flow.
	 * @param usersConnectionRepository the global store for service provider connections across all local user accounts
	 * @param currentUserConnectionRepositoryProvider a provider for the current user's {@link ServiceProviderConnectionRepository} instance;
	 * A JSR 330 Provider is injected here instead of the actual repository object because repository instances are request-scoped and resolved based on the currently authenticated user.
	 * @param signInService an adapter between this controller and the local application's user sign-in system.
	 */
	@Inject
	public ProviderSignInController(String applicationUrl, Provider<ServiceProviderConnectionFactoryLocator> connectionFactoryLocatorProvider, MultiUserServiceProviderConnectionRepository usersConnectionRepository,
			Provider<ServiceProviderConnectionRepository> currentUserConnectionRepositoryProvider, SignInService signInService) {
		this.connectionFactoryLocatorProvider = connectionFactoryLocatorProvider;
		this.usersConnectionRepository = usersConnectionRepository;
		this.currentUserConnectionRepositoryProvider = currentUserConnectionRepositoryProvider;
		this.signInService = signInService;
		this.baseCallbackUrl = applicationUrl + AnnotationUtils.findAnnotation(getClass(), RequestMapping.class).value()[0];
	}

	/**
	 * Process a sign-in form submission by commencing the process of establishing a connection to the provider on behalf of the user.
	 * For OAuth1, fetches a new request token from the provider, temporarily stores it in the session, then redirects the user to the provider's site for authentication authorization.
	 * For OAuth2, redirects the user to the provider's site for authentication authorization.
	 */
	@RequestMapping(value="/{providerId}", method=RequestMethod.POST)
	public String signin(@PathVariable String providerId, WebRequest request) {
		ServiceProviderConnectionFactory<?> connectionFactory = getConnectionFactoryLocator().getConnectionFactory(providerId);
		if (connectionFactory instanceof OAuth1ServiceProviderConnectionFactory) {
			OAuth1Operations oauth1Ops = ((OAuth1ServiceProviderConnectionFactory<?>) connectionFactory).getOAuthOperations();
			OAuthToken requestToken = oauth1Ops.fetchRequestToken(callbackUrl(providerId), null);
			request.setAttribute(OAUTH_TOKEN_ATTRIBUTE, requestToken, WebRequest.SCOPE_SESSION);
			return "redirect:" + oauth1Ops.buildAuthenticateUrl(requestToken.getValue(), oauth1Ops.getVersion() == OAuth1Version.CORE_10 ? new OAuth1Parameters(callbackUrl(providerId)) : OAuth1Parameters.NONE);
		} else if (connectionFactory instanceof OAuth2ServiceProviderConnectionFactory) {
			return "redirect:" + ((OAuth2ServiceProviderConnectionFactory<?>) connectionFactory).getOAuthOperations().buildAuthenticateUrl(GrantType.AUTHORIZATION_CODE, new OAuth2Parameters(callbackUrl(providerId)));
		} else {
			throw new IllegalStateException("Sign in using provider '" + providerId + "' not supported");
		}
	}

	/**
	 * Process the authentication callback from an OAuth 1 service provider.
	 * Called after the member authorizes the authentication, generally done once by having he or she click "Allow" in their web browser at the provider's site.
	 * Handles the provider sign-in callback by first determining if a local user account is associated with the connected provider account.
	 * If so, signs the local user in by delegating to {@link SignInService#signIn(String)}.
	 * If not, redirects the user to a signup page to create a new account with {@link ProviderSignInAttempt} context exposed in the HttpSession.
	 * @see ProviderSignInAttempt
	 * @see ProviderSignInUtils 
	 */
	@RequestMapping(value="/{providerId}", method=RequestMethod.GET, params="oauth_token")
	public String oauth1Callback(@PathVariable String providerId, @RequestParam("oauth_token") String token, @RequestParam(value="oauth_verifier", required=false) String verifier, WebRequest request) {
		OAuth1ServiceProviderConnectionFactory<?> connectionFactory = (OAuth1ServiceProviderConnectionFactory<?>) getConnectionFactoryLocator().getConnectionFactory(providerId);
		OAuthToken accessToken = connectionFactory.getOAuthOperations().exchangeForAccessToken(new AuthorizedRequestToken(extractCachedRequestToken(request), verifier), null);
		ServiceProviderConnection<?> connection = connectionFactory.createConnection(accessToken);
		return handleSignIn(connection, request);
	}

	/**
	 * Process the authentication callback from an OAuth 2 service provider.
	 * Called after the user authorizes the authentication, generally done once by having he or she click "Allow" in their web browser at the provider's site.
	 * Handles the provider sign-in callback by first determining if a local user account is associated with the connected provider account.
	 * If so, signs the local user in by delegating to {@link SignInService#signIn(String)}.
	 * If not, redirects the user to a signup page to create a new account with {@link ProviderSignInAttempt} context exposed in the HttpSession.
	 * @see ProviderSignInAttempt
	 * @see ProviderSignInUtils 
	 */
	@RequestMapping(value="/{providerId}", method=RequestMethod.GET, params="code")
	public String oauth2Callback(@PathVariable String providerId, @RequestParam("code") String code, WebRequest request) {
		OAuth2ServiceProviderConnectionFactory<?> connectionFactory = (OAuth2ServiceProviderConnectionFactory<?>) getConnectionFactoryLocator().getConnectionFactory(providerId);
		AccessGrant accessGrant = connectionFactory.getOAuthOperations().exchangeForAccess(code, callbackUrl(providerId), null);
		ServiceProviderConnection<?> connection = connectionFactory.createConnection(accessGrant);
		return handleSignIn(connection, request);
	}

	/**
	 * Overrides the default URL of the application's signup page ("/signup").
	 * ProviderSignInController will redirect to this URL if no matching connection can be found after signing into the provider. 
	 * @param signupUrl the URL of the signup page.
	 */
	public void setSignupUrl(String signupUrl) {
		this.signupUrl = signupUrl; 
	}
	
	// internal helpers

	private ServiceProviderConnectionFactoryLocator getConnectionFactoryLocator() {
		return connectionFactoryLocatorProvider.get();
	}
	
	private String callbackUrl(String providerId) {
		return baseCallbackUrl + "/" + providerId;
	}
	
	private OAuthToken extractCachedRequestToken(WebRequest request) {
		OAuthToken requestToken = (OAuthToken) request.getAttribute(OAUTH_TOKEN_ATTRIBUTE, WebRequest.SCOPE_SESSION);
		request.removeAttribute(OAUTH_TOKEN_ATTRIBUTE, WebRequest.SCOPE_SESSION);
		return requestToken;
	}

	private String handleSignIn(ServiceProviderConnection<?> connection, WebRequest request) {
		String localUserId = usersConnectionRepository.findLocalUserIdWithConnection(connection);
		if (localUserId == null) {
			ProviderSignInAttempt signInAttempt = new ProviderSignInAttempt(connection, connectionFactoryLocatorProvider, currentUserConnectionRepositoryProvider);
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
