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

import javax.inject.Inject;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.view.RedirectView;

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

	private final ConnectionFactoryLocator connectionFactoryLocator;

	private final UsersConnectionRepository usersConnectionRepository;
	
	private final ConnectionRepository connectionRepository;
	
	private final SignInAdapter signInAdapter;

	private String signUpUrl = "/signup";

	private String postSignInUrl = "/";

	private final String controllerCallbackUrl;
	
	/**
	 * Creates a new provider sign-in controller.
	 * @param applicationUrl the base secure URL for this application, used to construct the callback URL passed to the service providers at the beginning of the sign-in process.
	 * @param connectionFactoryLocator the locator of {@link ConnectionFactory connection factories} that can be used for sign-in; should be a serializable proxy to a singleton bean.
	 * This is because {@link ProviderSignInAttempt} objects are session-scoped and thus require a Serializable reference.
	 * @param usersConnectionRepository the global store for service provider connections across all local user accounts
	 * @param connectionRepository the current user's {@link ConnectionRepository} instance; must be a serializable proxy to a request-scoped bean.
	 */
	@Inject
	public ProviderSignInController(String applicationUrl, ConnectionFactoryLocator connectionFactoryLocator, UsersConnectionRepository usersConnectionRepository,
			ConnectionRepository connectionRepository, SignInAdapter signInAdapter) {
		this.connectionFactoryLocator = connectionFactoryLocator;
		this.usersConnectionRepository = usersConnectionRepository;
		this.connectionRepository = connectionRepository;
		this.signInAdapter = signInAdapter;
		this.controllerCallbackUrl = applicationUrl + AnnotationUtils.findAnnotation(getClass(), RequestMapping.class).value()[0];
	}

	/**
	 * Sets the URL to redirect the user to if no local user account can be mapped when signing in using a provider.
	 * Defaults to "/signup". 
	 * @param signUpUrl the URL of the sign up page.
	 */
	public void setSignUpUrl(String signUpUrl) {
		this.signUpUrl = signUpUrl; 
	}

	/**
 	 * Sets the URL to redirect the user to after signing in using a provider.
 	 * Defaults to "/".
	 * @param postSignInUrl the postSignIn URL
	 */
	public void setPostSignInUrl(String postSignInUrl) {
		this.postSignInUrl = postSignInUrl;
	}

	/**
	 * Process a sign-in form submission by commencing the process of establishing a connection to the provider on behalf of the user.
	 * For OAuth1, fetches a new request token from the provider, temporarily stores it in the session, then redirects the user to the provider's site for authentication authorization.
	 * For OAuth2, redirects the user to the provider's site for authentication authorization.
	 */
	@RequestMapping(value="/{providerId}", method=RequestMethod.POST)
	public RedirectView signIn(@PathVariable String providerId, WebRequest request) {
		ConnectionFactory<?> connectionFactory = connectionFactoryLocator.getConnectionFactory(providerId);
		if (connectionFactory instanceof OAuth1ConnectionFactory) {
			return new RedirectView(oauth1Url((OAuth1ConnectionFactory<?>) connectionFactory, request));
		} else if (connectionFactory instanceof OAuth2ConnectionFactory) {
			return new RedirectView(oauth2Url((OAuth2ConnectionFactory<?>) connectionFactory, request));
		} else {
			return new RedirectView(customAuthUrl(connectionFactory, request));
		}
	}

	/**
	 * Process the authentication callback from an OAuth 1 service provider.
	 * Called after the member authorizes the authentication, generally done once by having he or she click "Allow" in their web browser at the provider's site.
	 * Handles the provider sign-in callback by first determining if a local user account is associated with the connected provider account.
	 * If so, signs the local user in by delegating to {@link SignInAdapter#signIn(String)}.
	 * If not, redirects the user to a signup page to create a new account with {@link ProviderSignInAttempt} context exposed in the HttpSession.
	 * @see ProviderSignInAttempt
	 * @see ProviderSignInUtils 
	 */
	@RequestMapping(value="/{providerId}", method=RequestMethod.GET, params="oauth_token")
	public RedirectView oauth1Callback(@PathVariable String providerId, @RequestParam("oauth_token") String token, @RequestParam(value="oauth_verifier", required=false) String verifier, WebRequest request) {
		OAuth1ConnectionFactory<?> connectionFactory = (OAuth1ConnectionFactory<?>) connectionFactoryLocator.getConnectionFactory(providerId);
		OAuthToken accessToken = connectionFactory.getOAuthOperations().exchangeForAccessToken(new AuthorizedRequestToken(extractCachedRequestToken(request), verifier), null);
		Connection<?> connection = connectionFactory.createConnection(accessToken);
		return handleSignIn(connection, request);
	}

	/**
	 * Process the authentication callback from an OAuth 2 service provider.
	 * Called after the user authorizes the authentication, generally done once by having he or she click "Allow" in their web browser at the provider's site.
	 * Handles the provider sign-in callback by first determining if a local user account is associated with the connected provider account.
	 * If so, signs the local user in by delegating to {@link SignInAdapter#signIn(String)}.
	 * If not, redirects the user to a signup page to create a new account with {@link ProviderSignInAttempt} context exposed in the HttpSession.
	 * @see ProviderSignInAttempt
	 * @see ProviderSignInUtils 
	 */
	@RequestMapping(value="/{providerId}", method=RequestMethod.GET, params="code")
	public RedirectView oauth2Callback(@PathVariable String providerId, @RequestParam("code") String code, WebRequest request) {
		OAuth2ConnectionFactory<?> connectionFactory = (OAuth2ConnectionFactory<?>) connectionFactoryLocator.getConnectionFactory(providerId);
		AccessGrant accessGrant = connectionFactory.getOAuthOperations().exchangeForAccess(code, callbackUrl(providerId, request), null);
		Connection<?> connection = connectionFactory.createConnection(accessGrant);
		return handleSignIn(connection, request);
	}

	// subclassing hooks
	
	/**
	 * Hook method subclasses may override to sign-in with providers of custom types other than OAuth1 or OAuth2.
	 * Default implementation throws an {@link UnsupportedOperationException} indicating the custom {@link ConnectionFactory} is not supported.
	 */
	protected String customAuthUrl(ConnectionFactory<?> connectionFactory, WebRequest request) {
		throw new UnsupportedOperationException("Sign in using provider '" + connectionFactory.getProviderId() + "' not supported");		
	}
	
	// internal helpers

	private String oauth1Url(OAuth1ConnectionFactory<?> connectionFactory, WebRequest request) {
		OAuth1Operations oauth1Ops = ((OAuth1ConnectionFactory<?>) connectionFactory).getOAuthOperations();
		OAuthToken requestToken;
		String authenticateUrl;
		if (oauth1Ops.getVersion() == OAuth1Version.CORE_10_REVISION_A) {
			requestToken = oauth1Ops.fetchRequestToken(callbackUrl(connectionFactory.getProviderId(), request), null);				
			authenticateUrl = oauth1Ops.buildAuthenticateUrl(requestToken.getValue(), OAuth1Parameters.NONE);
		} else {
			requestToken = oauth1Ops.fetchRequestToken(null, null);				
			authenticateUrl = oauth1Ops.buildAuthenticateUrl(requestToken.getValue(), new OAuth1Parameters(callbackUrl(connectionFactory.getProviderId(), request)));
		}
		request.setAttribute(OAUTH_TOKEN_ATTRIBUTE, requestToken, WebRequest.SCOPE_SESSION);
		return authenticateUrl;
	}

	private String oauth2Url(OAuth2ConnectionFactory<?> connectionFactory, WebRequest request) {
		OAuth2Operations oauth2Ops = ((OAuth2ConnectionFactory<?>) connectionFactory).getOAuthOperations();
		String authenticateUrl = oauth2Ops.buildAuthenticateUrl(GrantType.AUTHORIZATION_CODE, new OAuth2Parameters(callbackUrl(connectionFactory.getProviderId(), request), request.getParameter("scope")));
		return authenticateUrl;
	}

	private String callbackUrl(String providerId, WebRequest request) {
		return controllerCallbackUrl + "/" + providerId;
	}
	
	private OAuthToken extractCachedRequestToken(WebRequest request) {
		OAuthToken requestToken = (OAuthToken) request.getAttribute(OAUTH_TOKEN_ATTRIBUTE, WebRequest.SCOPE_SESSION);
		request.removeAttribute(OAUTH_TOKEN_ATTRIBUTE, WebRequest.SCOPE_SESSION);
		return requestToken;
	}

	private RedirectView handleSignIn(Connection<?> connection, WebRequest request) {
		String localUserId = usersConnectionRepository.findUserIdWithConnection(connection);
		if (localUserId == null) {
			ProviderSignInAttempt signInAttempt = new ProviderSignInAttempt(connection, connectionFactoryLocator, connectionRepository);
			request.setAttribute(ProviderSignInAttempt.SESSION_ATTRIBUTE, signInAttempt, WebRequest.SCOPE_SESSION);
			return redirect(signUpUrl);
		} else {
			signInAdapter.signIn(localUserId);		
			return redirect(postSignInUrl);
		}
	}

	private RedirectView redirect(String url) {
		return new RedirectView(url, true);
	}
	
	private static final String OAUTH_TOKEN_ATTRIBUTE = "oauthToken";
}
