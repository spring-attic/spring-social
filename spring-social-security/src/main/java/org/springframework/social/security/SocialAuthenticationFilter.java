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
package org.springframework.social.security;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.social.UserIdSource;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.ProviderSignInAttempt;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.social.security.provider.SocialAuthenticationService;
import org.springframework.util.Assert;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Filter for handling the provider sign-in flow within the Spring Security filter chain.
 * Should be injected into the chain at or before the PRE_AUTH_FILTER location.
 * 
 * @author Stefan Fussenegger
 * @author Craig Walls
 * @author Yuan Ji
 */
public class SocialAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	private SocialAuthenticationServiceLocator authServiceLocator;

	private String signupUrl = "/signup";

	private String connectionAddedRedirectUrl = "/";

	private String connectionAddingFailureRedirectUrl = "/";

	private boolean updateConnections = true;

	private UserIdSource userIdSource;

	private UsersConnectionRepository usersConnectionRepository;

	private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();	

	private String filterProcessesUrl = DEFAULT_FILTER_PROCESSES_URL;

	private AuthenticationTrustResolverImpl authenticationTrustResolver = new AuthenticationTrustResolverImpl();

	public SocialAuthenticationFilter(AuthenticationManager authManager, UserIdSource userIdSource, UsersConnectionRepository usersConnectionRepository, SocialAuthenticationServiceLocator authServiceLocator) {
		super(DEFAULT_FILTER_PROCESSES_URL);
		setAuthenticationManager(authManager);
		this.userIdSource = userIdSource;
		this.usersConnectionRepository = usersConnectionRepository;
		this.authServiceLocator = authServiceLocator;
		super.setAuthenticationFailureHandler(new SocialAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler(DEFAULT_FAILURE_URL)));
	}
	
	/**
	 * Sets the signup URL; the URL to redirect to if authentication fails so that the user can register with the application.
	 * May be fully-qualified URL (e.g., "http://somehost/somepath/signup") or a path relative to application's servlet context path (e.g., "/signup").
	 * @param signupUrl The signup URL
	 */
	public void setSignupUrl(String signupUrl) {
		this.signupUrl = signupUrl;
	}
	
	public void setConnectionAddedRedirectUrl(String connectionAddedRedirectUrl) {
		this.connectionAddedRedirectUrl = connectionAddedRedirectUrl;
	}

	/**
	 * redirect the user after an attempt to add an additional authentication failed. After the failure
	 * the user is still authenticated, so redirecting to the default failure URL might
	 * not make sense
	 * @param connectionAddingFailureRedirectUrl The URL to redirect to after a failing connection following authorization
	 */
	public void setConnectionAddingFailureRedirectUrl(String connectionAddingFailureRedirectUrl) {
		this.connectionAddingFailureRedirectUrl = connectionAddingFailureRedirectUrl;
	}

	public void setUpdateConnections(boolean updateConnections) {
		this.updateConnections = updateConnections;
	}

	public void setPostLoginUrl(String postLoginUrl) {
		AuthenticationSuccessHandler successHandler = getSuccessHandler();
		if (successHandler instanceof AbstractAuthenticationTargetUrlRequestHandler) {
			AbstractAuthenticationTargetUrlRequestHandler h = (AbstractAuthenticationTargetUrlRequestHandler) successHandler;
			h.setDefaultTargetUrl(postLoginUrl);
		} else {
			throw new IllegalStateException("can't set postLoginUrl on unknown successHandler, type is " + successHandler.getClass().getName());
		}
	}

	public void setAlwaysUsePostLoginUrl(boolean alwaysUsePostLoginUrl) {
		AuthenticationSuccessHandler successHandler = getSuccessHandler();
		if (successHandler instanceof AbstractAuthenticationTargetUrlRequestHandler) {
			AbstractAuthenticationTargetUrlRequestHandler h = (AbstractAuthenticationTargetUrlRequestHandler) successHandler;
			h.setAlwaysUseDefaultTargetUrl(alwaysUsePostLoginUrl);
		} else {
			throw new IllegalStateException("can't set alwaysUsePostLoginUrl on unknown successHandler, type is " + successHandler.getClass().getName());
		}
	}

	/**
	 * The URL to redirect to if authentication fails or if authorization is denied by the user.
	 * @param postFailureUrl The failure URL. Defaults to "/signin" (relative to the servlet context).
	 */
	public void setPostFailureUrl(String postFailureUrl) {
		AuthenticationFailureHandler failureHandler = getFailureHandler();

		if (failureHandler instanceof SocialAuthenticationFailureHandler) {
			failureHandler = ((SocialAuthenticationFailureHandler)failureHandler).getDelegate();
		}

		if (failureHandler instanceof SimpleUrlAuthenticationFailureHandler) {
			SimpleUrlAuthenticationFailureHandler h = (SimpleUrlAuthenticationFailureHandler) failureHandler;
			h.setDefaultFailureUrl(postFailureUrl);
		} else {
			throw new IllegalStateException("can't set postFailureUrl on unknown failureHandler, type is " + failureHandler.getClass().getName());
		}
	}
	
	/**
	 * Sets a strategy to use when persisting information that is to survive past the boundaries of a request.
	 * The default strategy is to set the data as attributes in the HTTP Session.
	 * @param sessionStrategy the session strategy.
	 */
	public void setSessionStrategy(SessionStrategy sessionStrategy) {
		this.sessionStrategy = sessionStrategy;
	}

	public UsersConnectionRepository getUsersConnectionRepository() {
		return usersConnectionRepository;
	}

	public SocialAuthenticationServiceLocator getAuthServiceLocator() {
		return authServiceLocator;
	}
	
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		if (detectRejection(request)) {
			if (logger.isDebugEnabled()) {
				logger.debug("A rejection was detected. Failing authentication.");
			}
			throw new SocialAuthenticationException("Authentication failed because user rejected authorization.");
		}
		
		Set<String> authProviders = authServiceLocator.registeredAuthenticationProviderIds();
		String authProviderId = getRequestedProviderId(request);

		if (!authProviders.isEmpty() && authProviderId != null && authProviders.contains(authProviderId)) {
			try {
				SocialAuthenticationService<?> authService = authServiceLocator.getAuthenticationService(authProviderId);
				return attemptAuthService(authService, request, response);
			} catch (SocialAuthenticationRedirectException redirect) {
				try {
					response.sendRedirect(redirect.getRedirectUrl());
				} catch (IOException e) {
					throw new SocialAuthenticationException("failed to send redirect from SocialAuthenticationRedirectException", e);
				}
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * Detects a callback request after a user rejects authorization to prevent a never-ending redirect loop.
	 * Default implementation detects a rejection as a request that has one or more parameters
	 * (except 'state' parameter which can be used by application), but none of the expected parameters (oauth_token, code, scope).
	 * May be overridden to customize rejection detection.
	 * @param request the request to check for rejection.
	 * @return true if the request appears to be the result of a rejected authorization; false otherwise.
	 */
	protected boolean detectRejection(HttpServletRequest request) {
		Set<?> parameterKeys = request.getParameterMap().keySet();
		if ((parameterKeys.size() == 1) && (parameterKeys.contains("state"))) {
			return false;
		}
		return parameterKeys.size() > 0 
				&& !parameterKeys.contains("oauth_token") 
				&& !parameterKeys.contains("code") 
				&& !parameterKeys.contains("scope");
	}

	/**
	 * Indicates whether this filter should attempt to process a social network login request for the current invocation.
	 * <p>Check if request URL matches filterProcessesUrl with valid providerId. 
	 * The URL must be like {filterProcessesUrl}/{providerId}. 
	 * @return <code>true</code> if the filter should attempt authentication, <code>false</code> otherwise.
	 */
	@Deprecated
	protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
		String providerId = getRequestedProviderId(request);
		if (providerId != null){
			Set<String> authProviders = authServiceLocator.registeredAuthenticationProviderIds();
			return authProviders.contains(providerId);
		}
		return false;
	}

	protected Connection<?> addConnection(SocialAuthenticationService<?> authService, String userId, ConnectionData data) {
		HashSet<String> userIdSet = new HashSet<String>();
		userIdSet.add(data.getProviderUserId());
		Set<String> connectedUserIds = usersConnectionRepository.findUserIdsConnectedTo(data.getProviderId(), userIdSet);
		if (connectedUserIds.contains(userId)) {
			// providerUserId already connected to userId
			return null;
		} else if (!authService.getConnectionCardinality().isMultiUserId() && !connectedUserIds.isEmpty()) {
			// providerUserId already connected to different userId and no multi user allowed
			return null;
		}

		ConnectionRepository repo = usersConnectionRepository.createConnectionRepository(userId);

		if (!authService.getConnectionCardinality().isMultiProviderUserId()) {
			List<Connection<?>> connections = repo.findConnections(data.getProviderId());
			if (!connections.isEmpty()) {
				// TODO maybe throw an exception to allow UI feedback?
				return null;
			}
		}

		// add new connection
		Connection<?> connection = authService.getConnectionFactory().createConnection(data);
		connection.sync();
		repo.addConnection(connection);
		return connection;
	}
	
	@Override
	public void setFilterProcessesUrl(String filterProcessesUrl) {
		super.setFilterProcessesUrl(filterProcessesUrl);
		this.filterProcessesUrl = filterProcessesUrl;
	}

	// private helpers
	private Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	/**
	 * @return the authenticated user token, or null if authentication is incomplete.
	 * @throws AuthenticationException if authentication fails.
	 * @see AbstractAuthenticationProcessingFilter#attemptAuthentication(HttpServletRequest, HttpServletResponse)
	 */
	private Authentication attemptAuthService(final SocialAuthenticationService<?> authService, final HttpServletRequest request, HttpServletResponse response)
			throws SocialAuthenticationRedirectException, AuthenticationException {

		/*
		 * Call SocialAuthenticationService.getAuthToken() to get SocialAuthenticationToken:
		 *     If first phase, throw AuthenticationRedirectException to redirect to provider website.
		 *     If second phase, get token/code from request parameter and call provider API to get accessToken/accessGrant.
		 * Check Authentication object in spring security context, if null or not authenticated,  call doAuthentication()
		 * Otherwise, it is already authenticated, add this connection.
		 */

		final SocialAuthenticationToken token = authService.getAuthToken(request, response);
		if (token == null) return null;
		
		Assert.notNull(token.getConnection(), "Token connection must not be null.");
		
		Authentication auth = getAuthentication();
		// Check if not already authenticated or is already logged in anonymous.
		if (auth == null || !auth.isAuthenticated() || authenticationTrustResolver.isAnonymous(auth)) {
			return doAuthentication(authService, request, token);
		} else {
			addConnection(authService, request, token, auth);
			return auth;
		}		
	}	
	
	private String getRequestedProviderId(HttpServletRequest request) {
		String uri = request.getRequestURI();
		int pathParamIndex = uri.indexOf(';');

		if (pathParamIndex > 0) {
			// strip everything after the first semi-colon
			uri = uri.substring(0, pathParamIndex);
		}

		// uri must start with context path
		uri = uri.substring(request.getContextPath().length());

		// remaining uri must start with filterProcessesUrl
		if (!uri.startsWith(filterProcessesUrl)) {
			return null;
		}
		uri = uri.substring(filterProcessesUrl.length());

		// expect /filterprocessesurl/provider, not /filterprocessesurlproviderr
		if (uri.startsWith("/")) {
			return uri.substring(1);
		} else {
			return null;
		}
	}

	private void addConnection(final SocialAuthenticationService<?> authService, HttpServletRequest request, SocialAuthenticationToken token, Authentication auth) {
		// already authenticated - add connection instead
		String userId = userIdSource.getUserId();
		Connection<?> connection = token.getConnection();
		if (userId == null || connection == null) {
			logger.warn("can't add connection without userId or connection");
			return;
		}
		
		connection = addConnection(authService, userId, connection.createData());
		if(connection != null) {
			String redirectUrl = authService.getConnectionAddedRedirectUrl(request, connection);
			if (redirectUrl == null) {
				// use default instead
				redirectUrl = connectionAddedRedirectUrl;
			}
			throw new SocialAuthenticationRedirectException(redirectUrl);
		} else {
			throw new SocialAuthenticationRedirectException(connectionAddingFailureRedirectUrl);
		}
	}

	private Authentication doAuthentication(SocialAuthenticationService<?> authService, HttpServletRequest request, SocialAuthenticationToken token) {
		try {
			if (!authService.getConnectionCardinality().isAuthenticatePossible()) return null;
			token.setDetails(authenticationDetailsSource.buildDetails(request));
			Authentication success = getAuthenticationManager().authenticate(token);
			Assert.isInstanceOf(SocialUserDetails.class, success.getPrincipal(), "unexpected principle type");
			updateConnections(authService, token, success);			
			return success;
		} catch (BadCredentialsException e) {
			// connection unknown, register new user?
			if (signupUrl != null) {
				// store ConnectionData in session and redirect to register page
				sessionStrategy.setAttribute(new ServletWebRequest(request), ProviderSignInAttempt.SESSION_ATTRIBUTE, new ProviderSignInAttempt(token.getConnection()));
				throw new SocialAuthenticationRedirectException(buildSignupUrl(request));
			}
			throw e;
		}
	}

	private String buildSignupUrl(HttpServletRequest request) {
		if (signupUrl.startsWith("http://") || signupUrl.startsWith("https://"))  {
			return signupUrl;
		}
		if (!signupUrl.startsWith("/")) {
			return ServletUriComponentsBuilder.fromContextPath(request).path("/" + signupUrl).build().toUriString();
		}
		return ServletUriComponentsBuilder.fromContextPath(request).path(signupUrl).build().toUriString();
	}

	private void updateConnections(SocialAuthenticationService<?> authService, SocialAuthenticationToken token, Authentication success) {
		if (updateConnections) {
			String userId = ((SocialUserDetails)success.getPrincipal()).getUserId();
			Connection<?> connection = token.getConnection();
			ConnectionRepository repo = getUsersConnectionRepository().createConnectionRepository(userId);
			repo.updateConnection(connection);
		}
	}

	private static final String DEFAULT_FAILURE_URL = "/signin";
	
	private static final String DEFAULT_FILTER_PROCESSES_URL = "/auth";

}
