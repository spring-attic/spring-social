/*
 * Copyright 2010 the original author or authors.
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.security.provider.SocialAuthenticationService;
import org.springframework.social.security.provider.SocialAuthenticationService.AuthenticationMode;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

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

	/**
	 * testing only
	 * 
	 * @param session may be null
	 * @param data may be null
	 * @return true if new attempt was added to session
	 */
	static boolean addSignInAttempt(HttpSession session, ConnectionData data) {
		return session == null || data == null ? null : SignInAttempts.add(session, data);
	}
	
	/**
	 * @param session may be null
	 * @return list of ConnectionData for sign-in attempts using unknown connections
	 */
	public static List<ConnectionData> getSignInAttempts(HttpSession session) {
		if (session == null) {
			return new ArrayList<ConnectionData>(0);
		}
		return new ArrayList<ConnectionData>(SignInAttempts.get(session));
	}
	
	/**
	 * clear list of sign-in attempts (after registration)
	 * @param session may be null
	 */
	public static void clearSignInAttempts(HttpSession session) {
		if (session != null) {
			SignInAttempts.clear(session);
		}
	}
	
	public SocialAuthenticationFilter() {
	}

	@Override
	protected void initFilterBean() throws ServletException {
		Assert.notNull(getAuthManager(), "authManager must be set");
		Assert.notNull(getUserIdExtractor(), "userIdExtractor must be set");
		Assert.notNull(getUsersConnectionRepository(), "usersConnectionRepository must be set");
		Assert.notNull(getAuthServiceLocator(), "authServiceLocator must be configured");
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
			response.sendRedirect(e.getRedirectUrl());
			return;
		}

		if (!response.isCommitted()) {
			chain.doFilter(request, response);
		}
	}

	protected Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException, SocialAuthenticationRedirectException {

		Set<String> authProviders = authServiceLocator.registeredAuthenticationProviderIds();
		
		if (authProviders.isEmpty()) {
			return null;
		}

		String explicitAuthProviderId = getRequestedProviderId(request);

		if (explicitAuthProviderId != null) {
			if (!authServiceLocator.registeredAuthenticationProviderIds().contains(explicitAuthProviderId)) {
				// simply ignore unknown id and let chain handle request
				return null;
			} else {
				// auth explicitly required
				SocialAuthenticationService<?> authService = authServiceLocator.getAuthenticationService(explicitAuthProviderId);
				if (authService.getAuthenticationMode() == AuthenticationMode.IMPLICIT) {
					// unknown service id
					return null;
				}
				return attemptAuthService(authService, AuthenticationMode.EXPLICIT, request, response);
			}
		} else if (!isAuthenticated()) {
			// implicitly only if not logged in already

			Authentication auth = null;
			AuthenticationException authEx = null;
			for (final String authProvider : authProviders) {

				SocialAuthenticationService<?> authService = authServiceLocator.getAuthenticationService(authProvider);
				
				if (authService .getAuthenticationMode() == AuthenticationMode.EXPLICIT) {
					continue;
				}

				try {
					auth = attemptAuthService(authService, AuthenticationMode.IMPLICIT, request, response);
					if (auth != null && auth.isAuthenticated()) {
						break;
					}
				} catch (AuthenticationException e) {
					authEx = toAuthException(authEx, e, authService);
				}
			}

			if (auth != null && auth.isAuthenticated()) {
				// ignore exception if fallback succeeded
				return auth;
			} else if (authEx != null) {
				throw authEx;
			} else {
				return null;
			}
		}

		return null;
	}

	protected final boolean isAuthenticated() {
		Authentication auth = getAuthentication();
		return auth != null && auth.isAuthenticated();
	}

	protected Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

	protected Authentication attemptAuthService(final SocialAuthenticationService<?> authService,
			AuthenticationMode authMode, final HttpServletRequest request, final HttpServletResponse response)
			throws SocialAuthenticationRedirectException {

		final SocialAuthenticationToken token = authService.getAuthToken(authMode, request, response);
		if (token != null) {
			Assert.isInstanceOf(ConnectionData.class, token.getPrincipal(), "unexpected principle type");
			
			Authentication auth = getAuthentication();
			if (auth == null || !auth.isAuthenticated()) {
				if (!authService.getConnectionCardinality().isAuthenticatePossible()) {
					return null;
				}
				token.setDetails(getAuthDetailsSource().buildDetails(request));
				try {
					Authentication success = getAuthManager().authenticate(token);
					Assert.isInstanceOf(SocialUserDetails.class, success.getPrincipal(), "unexpected principle type");
					
					// success, now update existing data if necessary
					if (isUpdateConnections()) {
						String userId = ((SocialUserDetails)success.getPrincipal()).getUserId();
						ConnectionData data = (ConnectionData) token.getPrincipal();
						
						Connection<?> connection = authService.getConnectionFactory().createConnection(data);
						ConnectionRepository repo = getUsersConnectionRepository().createConnectionRepository(userId);
						repo.updateConnection(connection);
					}
					
					return success;
				} catch (BadCredentialsException e) {
					// connection unknown, register new user?
					if (getSignupUrl() == null) {
						throw e;
					} else {
						// store ConnectionData in session and redirect to register page
						addSignInAttempt(request.getSession(), (ConnectionData) token.getPrincipal());
						throw new SocialAuthenticationRedirectException(getSignupUrl());
					}
				}
			} else {
				// already authenticated - add connection instead
				String userId = userIdExtractor.extractUserId(auth);
				Object principal = token.getPrincipal();
				if (userId != null && principal instanceof ConnectionData) {
					Connection<?> connection = addConnection(authService, userId, (ConnectionData) principal);
					if(connection != null) {
						String redirectUrl = authService.getConnectionAddedRedirectUrl(request, connection);
						if (redirectUrl == null) {
							// use default instead
							redirectUrl = getConnectionAddedRedirectUrl();
						}
						throw new SocialAuthenticationRedirectException(redirectUrl);
					} else {
						return null;
					}
				}
			}
		}

		return null;
	}

	protected Connection<?> addConnection(final SocialAuthenticationService<?> authService, String userId, final ConnectionData data) {

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

	protected String getRequestedProviderId(HttpServletRequest request) {
		String uri = request.getRequestURI();
		int pathParamIndex = uri.indexOf(';');

		if (pathParamIndex > 0) {
			// strip everything after the first semi-colon
			uri = uri.substring(0, pathParamIndex);
		}

		String providerId = uri;
		if (!uri.startsWith(request.getContextPath())) {
			return null;
		}
		providerId = providerId.substring(request.getContextPath().length());

		if (!uri.startsWith(getFilterProcessesUrl())) {
			return null;
		}
		providerId = providerId.substring(getFilterProcessesUrl().length());

		if (providerId.startsWith("/")) {
			return providerId.substring(1);
		} else {
			return null;
		}
	}

	/**
	 * override to change exception handling strategy. Raise current exception
	 * to fail fast, return exception that will be raised at the end or simply
	 * return null to ignore.
	 * 
	 * @param previous
	 *            previously returned value or null
	 * @param current
	 *            current exception
	 * @param authService
	 *            service that caused current exception
	 * @return exception that should be thrown at the end
	 * @throws AuthenticationException
	 *             if no further services should be tried
	 */
	protected AuthenticationException toAuthException(AuthenticationException previous,
			AuthenticationException current, SocialAuthenticationService<?> authService) throws AuthenticationException {
		// return previous == null ? current : previous;
		return null; // no exception for implicit auth
	}

	/**
	 * Default behaviour for successful authentication. <ol> <li>Sets the
	 * successful <tt>Authentication</tt> object on the
	 * {@link SecurityContextHolder}</li> <li>Invokes the configured
	 * {@link SessionAuthenticationStrategy} to handle any session-related
	 * behaviour (such as creating a new session to protect against
	 * session-fixation attacks).</li> <li>Informs the configured
	 * <tt>RememberMeServices</tt> of the successful login</li> <li>Fires an
	 * {@link InteractiveAuthenticationSuccessEvent} via the configured
	 * <tt>ApplicationEventPublisher</tt></li> <li>Delegates additional
	 * behaviour to the {@link AuthenticationSuccessHandler}.</li> </ol>
	 * 
	 * @param authResult
	 *            the object returned from the <tt>attemptAuthentication</tt>
	 *            method.
	 */
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			Authentication authResult) throws IOException, ServletException {

		if (logger.isDebugEnabled()) {
			logger.debug("Authentication success. Updating SecurityContextHolder to contain: " + authResult);
		}

		SecurityContextHolder.getContext().setAuthentication(authResult);

		getRememberMeServices().loginSuccess(request, response, authResult);

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
	 * Default behaviour for unsuccessful authentication. <ol> <li>Clears the
	 * {@link SecurityContextHolder}</li> <li>Stores the exception in the
	 * session (if it exists or <tt>allowSesssionCreation</tt> is set to
	 * <tt>true</tt>)</li> <li>Informs the configured
	 * <tt>RememberMeServices</tt> of the failed login</li> <li>Delegates
	 * additional behaviour to the {@link AuthenticationFailureHandler}.</li>
	 * </ol>
	 */
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		SecurityContextHolder.clearContext();

		if (logger.isDebugEnabled()) {
			logger.debug("Authentication request failed: " + failed.toString());
			logger.debug("Updated SecurityContextHolder to contain null Authentication");
		}

		getRememberMeServices().loginFail(request, response);
		
		getFailureHandler().onAuthenticationFailure(request, response, failed);
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

	public void setAuthManager(AuthenticationManager authManager) {
		this.authManager = authManager;
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

	public void setUserIdExtractor(UserIdExtractor userIdExtractor) {
		this.userIdExtractor = userIdExtractor;
	}

	public UsersConnectionRepository getUsersConnectionRepository() {
		return usersConnectionRepository;
	}

	public void setUsersConnectionRepository(UsersConnectionRepository usersConnectionRepository) {
		this.usersConnectionRepository = usersConnectionRepository;
	}

	public SocialAuthenticationServiceLocator getAuthServiceLocator() {
		return authServiceLocator;
	}

	public void setAuthServiceLocator(SocialAuthenticationServiceLocator authServiceLocator) {
		this.authServiceLocator = authServiceLocator;
	}

	private static class SignInAttempts {
		
		private static final String ATTR_SIGN_IN_ATTEMPT = SignInAttempts.class.getName();
		
		private Map<ConnectionKey, ConnectionData> attempts = new HashMap<ConnectionKey, ConnectionData>();
		
		/**
		 * @return always <code>true</code>
		 */
		private static boolean add(HttpSession session, ConnectionData data) {
			SignInAttempts signInAttempts = (SignInAttempts) session.getAttribute(ATTR_SIGN_IN_ATTEMPT);
			if (signInAttempts == null) {
				session.setAttribute(ATTR_SIGN_IN_ATTEMPT, signInAttempts = new SignInAttempts()); 
			}
			return signInAttempts.addAttempt(data);
		}
		
		/**
		 * @return unmodifiable list
		 */
		private static List<ConnectionData> get(HttpSession session) {
			SignInAttempts signInAttempts = (SignInAttempts) session.getAttribute(ATTR_SIGN_IN_ATTEMPT);
			if(signInAttempts == null) {
				return Collections.emptyList();
			} else {
				return signInAttempts.getAttempts();
			}
		}

		private static void clear(HttpSession session) {
			session.removeAttribute(ATTR_SIGN_IN_ATTEMPT);
		}
		
		private SignInAttempts() {
		}
		
		/**
		 * @return <code>true</code> if previous connection was replaced
		 */
		private boolean addAttempt(ConnectionData data) {
			return attempts.put(key(data), data) != null;
		}
		
		private List<ConnectionData> getAttempts() {
			return new ArrayList<ConnectionData>(attempts.values());
		}
		
		private ConnectionKey key(ConnectionData data) {
			return new ConnectionKey(data.getProviderId(), data.getProviderUserId());
		}
	}
}
