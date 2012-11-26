/*
 * Copyright 2012 the original author or authors.
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

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.security.provider.SocialAuthenticationService;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Filter for handling the provider sign-in flow within the Spring Security filter chain.
 * Should be injected into the chain at or before the PRE_AUTH_FILTER location.
 * 
 * @author Stefan Fussenegger
 * @author Craig Walls
 */
public class SocialAuthenticationFilter extends GenericFilterBean {

	private AuthenticationManager authManager;

	private SocialAuthenticationServiceLocator authServiceLocator;

	private AuthenticationDetailsSource<HttpServletRequest, ?> authDetailsSource = new WebAuthenticationDetailsSource();

	private ApplicationEventPublisher eventPublisher;

	private RememberMeServices rememberMeServices = null;

	private String filterProcessesUrl = "/auth";

	private String signupUrl = "/signup";

	private String connectionAddedRedirectUrl = "/";

	private boolean updateConnections = true;

	private SessionAuthenticationStrategy sessionStrategy = new NullAuthenticatedSessionStrategy();

	private AuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();

	private AuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();

	private UserIdExtractor userIdExtractor;

	private UsersConnectionRepository usersConnectionRepository;

	public SocialAuthenticationFilter(AuthenticationManager authManager, UserIdExtractor userIdExtractor, 
			UsersConnectionRepository usersConnectionRepository, SocialAuthenticationServiceLocator authServiceLocator) {
		this.authManager = authManager;
		this.userIdExtractor = userIdExtractor;
		this.usersConnectionRepository = usersConnectionRepository;
		this.authServiceLocator = authServiceLocator;
	}

	public final void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) 
			throws IOException, ServletException {
		doFilter((HttpServletRequest) req, (HttpServletResponse) res, chain);
	}
	
	public void doFilter(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain) 
			throws IOException, ServletException {		
		try {
			final Authentication auth = attemptAuthentication(request, response);
			if (auth != null) {
				getSessionStrategy().onAuthentication(auth, request, response);
				successfulAuthentication(request, response, auth);

				if (logger.isDebugEnabled()) {
					logger.debug("SecurityContextHolder populated with social token: '" + getAuthentication() + "'");
				}
			}
		} catch (final AuthenticationException authenticationException) {
			if (logger.isDebugEnabled()) {
				logger.debug("SecurityContextHolder not populated with social token", authenticationException);
			}
			unsuccessfulAuthentication(request, response, authenticationException);
		} catch (SocialAuthenticationRedirectException e) {
			response.sendRedirect(e.getRedirectUrl()); // TODO: Handle in-app redirects cleaner
			return;
		}

		if (!response.isCommitted()) {
			chain.doFilter(request, response);
		}
	}

	protected Connection<?> addConnection(SocialAuthenticationService<?> authService, String userId, ConnectionData data) {
		HashSet<String> userIdSet = new HashSet<String>();
		userIdSet.add(data.getProviderUserId());
		Set<String> connectedUserIds = usersConnectionRepository
				.findUserIdsConnectedTo(data.getProviderId(), userIdSet);
		if (connectedUserIds.contains(userId)) {
			// already connected
			return null;
		} else if (!authService.getConnectionCardinality().isMultiUserId() && !connectedUserIds.isEmpty()) {
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

	/**
	 * Override to change exception handling strategy. 
	 * Raise current exception to fail fast, return exception that will be raised at the end or simply return null to ignore.
	 * @param previous previously returned value or null
	 * @param current current exception
	 * @param authService service that caused current exception
	 * @return exception that should be thrown at the end
	 * @throws AuthenticationException if no further services should be tried
	 */
	protected AuthenticationException toAuthException(AuthenticationException previous, AuthenticationException current, 
			SocialAuthenticationService<?> authService) throws AuthenticationException {
		// return previous == null ? current : previous;
		return null; // no exception for implicit auth
	}

	/**
	 * Default behaviour for successful authentication. 
	 * <ol> 
	 *   <li>Sets the successful <tt>Authentication</tt> object on the {@link SecurityContextHolder}</li> 
	 *   <li>Invokes the configured {@link SessionAuthenticationStrategy} to handle any session-related behaviour (such as creating a new session to protect against session-fixation attacks).</li> 
	 *   <li>Informs the configured <tt>RememberMeServices</tt> of the successful login</li> 
	 *   <li>Fires an {@link InteractiveAuthenticationSuccessEvent} via the configured <tt>ApplicationEventPublisher</tt></li>
	 *   <li>Delegates additional behaviour to the {@link AuthenticationSuccessHandler}.</li> 
	 * </ol>
	 * @param authResult the object returned from the <tt>attemptAuthentication</tt> method.
	 */
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) 
			throws IOException, ServletException {

		if (logger.isDebugEnabled()) {
			logger.debug("Authentication success. Updating SecurityContextHolder to contain: " + authResult);
		}

		SecurityContextHolder.getContext().setAuthentication(authResult);
		if (getRememberMeServices() != null) {
			getRememberMeServices().loginSuccess(request, response, authResult);
		}
		
		// Fire event
		ApplicationEventPublisher eventPublisher = getEventPublisher();
		if (eventPublisher != null) {
			eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, this.getClass()));
		}
		if (getRequestedProviderId(request) != null) {
			// only redirect explicit auth
			getSuccessHandler().onAuthenticationSuccess(request, response, authResult);
		}
	}

	/**
	 * Default behaviour for unsuccessful authentication. 
	 * <ol> 
	 *   <li>Clears the {@link SecurityContextHolder}</li> 
	 *   <li>Stores the exception in the session (if it exists or <tt>allowSesssionCreation</tt> is set to <tt>true</tt>)</li> 
	 *   <li>Informs the configured <tt>RememberMeServices</tt> of the failed login</li> 
	 *   <li>Delegates additional behaviour to the {@link AuthenticationFailureHandler}.</li>
	 * </ol>
	 */
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) 
			throws IOException, ServletException {
		SecurityContextHolder.clearContext();
		if (logger.isDebugEnabled()) {
			logger.debug("Authentication request failed: " + failed.toString());
			logger.debug("Updated SecurityContextHolder to contain null Authentication");
		}
		if (getRememberMeServices() != null) {
			getRememberMeServices().loginFail(request, response);
		}
		
		getFailureHandler().onAuthenticationFailure(request, response, failed);
	}

	// private helpers

	private Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response) 
			throws AuthenticationException, IOException, ServletException, SocialAuthenticationRedirectException {
		Authentication auth = null;
		Set<String> authProviders = authServiceLocator.registeredAuthenticationProviderIds();
		String authProviderId = getRequestedProviderId(request);
		if (!authProviders.isEmpty() && authProviderId != null && authServiceLocator.registeredAuthenticationProviderIds().contains(authProviderId)) {
			SocialAuthenticationService<?> authService = authServiceLocator.getAuthenticationService(authProviderId);
			auth = attemptAuthService(authService, request, response);
			if (auth == null) {
				throw new AuthenticationServiceException("authentication failed");
			}
		}
		return auth;
	}

	private Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	private Authentication attemptAuthService(final SocialAuthenticationService<?> authService, final HttpServletRequest request, 
			HttpServletResponse response) throws SocialAuthenticationRedirectException, AuthenticationException {

		final SocialAuthenticationToken token = authService.getAuthToken(request, response);
		if (token == null) return null;
		
		Assert.isInstanceOf(ConnectionData.class, token.getPrincipal(), "unexpected principle type");
		
		Authentication auth = getAuthentication();
		if (auth == null || !auth.isAuthenticated()) {
			return doAuthentication(authService, request, token);
		} else {
			addConnection(authService, request, token, auth);
			return null;
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
		if (!uri.startsWith(getFilterProcessesUrl())) {
			return null;
		}
		uri = uri.substring(getFilterProcessesUrl().length());

		// expect /filterprocessesurl/provider, not /filterprocessesurlproviderr
		if (uri.startsWith("/")) {
			return uri.substring(1);
		} else {
			return null;
		}
	}

	private void addConnection(final SocialAuthenticationService<?> authService, HttpServletRequest request, 
			SocialAuthenticationToken token, Authentication auth) {
		// already authenticated - add connection instead
		String userId = userIdExtractor.extractUserId(auth);
		Object principal = token.getPrincipal();
		if (userId == null || !(principal instanceof ConnectionData)) return;
		
		Connection<?> connection = addConnection(authService, userId, (ConnectionData) principal);
		if(connection != null) {
			String redirectUrl = authService.getConnectionAddedRedirectUrl(request, connection);
			if (redirectUrl == null) {
				// use default instead
				redirectUrl = getConnectionAddedRedirectUrl();
			}
			throw new SocialAuthenticationRedirectException(redirectUrl);
		}
	}

	private Authentication doAuthentication(SocialAuthenticationService<?> authService, HttpServletRequest request, 
			SocialAuthenticationToken token) {
		try {
			if (!authService.getConnectionCardinality().isAuthenticatePossible()) return null;
			token.setDetails(getAuthDetailsSource().buildDetails(request));
			Authentication success = getAuthManager().authenticate(token);
			Assert.isInstanceOf(SocialUserDetails.class, success.getPrincipal(), "unexpected principle type");			
			updateConnections(authService, token, success);			
			return success;
		} catch (BadCredentialsException e) {
			// connection unknown, register new user?
			if (getSignupUrl() != null) {
				// store ConnectionData in session and redirect to register page
				addSignInAttempt(request.getSession(), (ConnectionData) token.getPrincipal());
				throw new SocialAuthenticationRedirectException(getSignupUrl());
			}
			throw e;
		}
	}

	private void updateConnections(SocialAuthenticationService<?> authService, SocialAuthenticationToken token, Authentication success) {
		if (isUpdateConnections()) {
			String userId = ((SocialUserDetails)success.getPrincipal()).getUserId();
			ConnectionData data = (ConnectionData) token.getPrincipal();
			Connection<?> connection = authService.getConnectionFactory().createConnection(data);
			ConnectionRepository repo = getUsersConnectionRepository().createConnectionRepository(userId);
			repo.updateConnection(connection);
		}
	}
	
	
	public AuthenticationDetailsSource<HttpServletRequest, ?> getAuthDetailsSource() {
		return authDetailsSource;
	}

	public void setAuthDetailsSource(final AuthenticationDetailsSource<HttpServletRequest, ?> authDetailsSource) {
		this.authDetailsSource = authDetailsSource;
	}

	/**
	 * @return may be null
	 */
	public ApplicationEventPublisher getEventPublisher() {
		return eventPublisher;
	}

	public void setEventPublisher(final ApplicationEventPublisher eventPublisher) {
		this.eventPublisher = eventPublisher;
	}

	public AuthenticationManager getAuthManager() {
		return authManager;
	}

	public String getFilterProcessesUrl() {
		return filterProcessesUrl;
	}

	public void setFilterProcessesUrl(String filterProcessesUrl) {
		this.filterProcessesUrl = filterProcessesUrl;
	}

	public String getSignupUrl() {
		return signupUrl;
	}

	public void setSignupUrl(String signupUrl) {
		this.signupUrl = signupUrl;
	}

	public String getConnectionAddedRedirectUrl() {
		return connectionAddedRedirectUrl;
	}

	public void setConnectionAddedRedirectUrl(String connectionAddedRedirectUrl) {
		this.connectionAddedRedirectUrl = connectionAddedRedirectUrl;
	}

	public boolean isUpdateConnections() {
		return updateConnections;
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

	public void setPostFailureUrl(String postFailureUrl) {
		AuthenticationFailureHandler failureHandler = getFailureHandler();
		if (failureHandler instanceof SimpleUrlAuthenticationFailureHandler) {
			SimpleUrlAuthenticationFailureHandler h = (SimpleUrlAuthenticationFailureHandler) failureHandler;
			h.setDefaultFailureUrl(postFailureUrl);
		} else {
			throw new IllegalStateException("can't set postFailureUrl on unknown failureHandler, type is " + failureHandler.getClass().getName());
		}
	}
	
	public RememberMeServices getRememberMeServices() {
		return rememberMeServices;
	}

	public void setRememberMeServices(RememberMeServices rememberMeServices) {
		this.rememberMeServices = rememberMeServices;
	}

	public AuthenticationSuccessHandler getSuccessHandler() {
		return successHandler;
	}

	public void setSuccessHandler(AuthenticationSuccessHandler successHandler) {
		if (successHandler == null) {
			throw new NullPointerException("successHandler");
		}
		this.successHandler = successHandler;
	}

	public AuthenticationFailureHandler getFailureHandler() {
		return failureHandler;
	}

	public void setFailureHandler(AuthenticationFailureHandler failureHandler) {
		this.failureHandler = failureHandler;
	}

	public SessionAuthenticationStrategy getSessionStrategy() {
		return sessionStrategy;
	}

	public void setSessionStrategy(SessionAuthenticationStrategy sessionStrategy) {
		this.sessionStrategy = sessionStrategy;
	}

	public UserIdExtractor getUserIdExtractor() {
		return userIdExtractor;
	}

	public UsersConnectionRepository getUsersConnectionRepository() {
		return usersConnectionRepository;
	}

	public SocialAuthenticationServiceLocator getAuthServiceLocator() {
		return authServiceLocator;
	}

	private boolean addSignInAttempt(HttpSession session, ConnectionData data) {
		return session == null || data == null ? null : SignInAttempts.add(session, data);
	}

}
