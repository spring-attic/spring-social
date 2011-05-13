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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.security.provider.SocialAuthenticationService;
import org.springframework.social.security.provider.SocialAuthenticationService.AuthenticationMode;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.filter.GenericFilterBean;

public class SocialAuthenticationFilter extends GenericFilterBean {

	private AuthenticationManager authManager;
	private Map<String, SocialAuthenticationService<?>> authServices = new HashMap<String, SocialAuthenticationService<?>>();
	private AuthenticationDetailsSource<HttpServletRequest, ?> authDetailsSource = new WebAuthenticationDetailsSource();
	private ApplicationEventPublisher eventPublisher;
	private RememberMeServices rememberMeServices = null;

	private String filterProcessesUrl = "/j_spring_social_security_check";

	private SessionAuthenticationStrategy sessionStrategy = new NullAuthenticatedSessionStrategy();

	private AuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
	private UserIdExtractor userIdExtractor;

	private UsersConnectionRepository usersConnectionRepository;

	public SocialAuthenticationFilter() {
	}

	@Override
	protected void initFilterBean() throws ServletException {
		Assert.notNull(getAuthManager(), "authManager must be set");
		Assert.notNull(getUserIdExtractor(), "userIdExtractor must be set");
		Assert.notNull(getUsersConnectionRepository(), "usersConnectionRepository must be set");
	}

	public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain)
			throws IOException, ServletException {

		// only authenticate previously unauthenticated sessions

		final HttpServletRequest request = (HttpServletRequest) req;
		final HttpServletResponse response = (HttpServletResponse) res;

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

		if (!res.isCommitted()) {
			chain.doFilter(req, res);
		}
	}

	protected Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException, SocialAuthenticationRedirectException {

		if (CollectionUtils.isEmpty(authServices)) {
			return null;
		}

		String explicitAuthProviderId = getRequestedProviderId(request);

		if (explicitAuthProviderId != null) {
			// auth explicitly required
			SocialAuthenticationService<?> authService = authServices.get(explicitAuthProviderId);
			if (authService == null || authService.getAuthenticationMode() == AuthenticationMode.IMPLICIT) {
				// unknown service id
				return null;
			}
			return attemptAuthService(authService, AuthenticationMode.EXPLICIT, request, response);
		} else if (!isAuthenticated()) {
			// implicitly only if not logged in already

			Authentication auth = null;
			AuthenticationException authEx = null;
			for (final SocialAuthenticationService<?> authService : authServices.values()) {

				if (authService.getAuthenticationMode() == AuthenticationMode.EXPLICIT) {
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
			Authentication auth = getAuthentication();
			if (auth == null || !auth.isAuthenticated()) {
				token.setDetails(getAuthDetailsSource().buildDetails(request));
				return getAuthManager().authenticate(token);
			} else {
				// already authenticated - add connection instead
				String userId = userIdExtractor.extractUserId(auth);
				Object principal = token.getPrincipal();
				if (userId != null && principal instanceof ConnectionData) {
					Connection<?> connection = authService.getConnectionFactory().createConnection(
							(ConnectionData) principal);
					connection.sync();
					usersConnectionRepository.createConnectionRepository(userId).addConnection(connection);

					throw new SocialAuthenticationRedirectException(authService.getConnectionAddedRedirectUrl(request,
							connection));
				}
				return null;
			}
		}

		return null;
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

		getSuccessHandler().onAuthenticationSuccess(request, response, authResult);
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
	}

	public void setAuthServices(final List<SocialAuthenticationService<?>> authServices) {
		this.authServices = new HashMap<String, SocialAuthenticationService<?>>();
		for (SocialAuthenticationService<?> authService : authServices) {
			addAuthService(authService);
		}
	}

	public void addAuthService(SocialAuthenticationService<?> authService) {
		authServices.put(authService.getConnectionFactory().getProviderId(), authService);
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
		this.successHandler = successHandler;
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

}
