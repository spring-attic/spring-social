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
package org.springframework.social.connect.web;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.social.ApiException;
import org.springframework.social.InsufficientPermissionException;
import org.springframework.social.NotAuthorizedException;
import org.springframework.social.OperationNotPermittedException;
import org.springframework.social.UserIdSource;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

/**
 * <p>Servlet filter that intercepts Spring Social {@link ApiException}s thrown in the course of a request and attempts to reconcile any connection-related
 * problems by deleting the stale/revoked connection and walking the user through the connection process to obtain a new connection.</p>
 * 
 * <p>This filter handles the exceptions via the following flow:</p>
 * 
 * <ul>
 * <li>If an exception is thrown, redirects to /connect/{provider ID}?reconnect=true</li>
 * <li>Handles its own redirect to /connect/{provider ID}?reconnect=true and converts the request to a POST request to {@link ConnectController} to kick of the authorization flow.</li>
 * </ul>
 * 
 * @since 1.1.0
 * 
 * @author Craig Walls
 */
public class ReconnectFilter extends GenericFilterBean {

	private final static Log logger = LogFactory.getLog(ReconnectFilter.class);

	private ThrowableAnalyzer throwableAnalyzer = new ThrowableAnalyzer();

	private UsersConnectionRepository usersConnectionRepository;
	
	private UserIdSource userIdSource;

	/**
	 * Creates an instance of {@link ReconnectFilter}.
	 * @param usersConnectionRepository a {@link UsersConnectionRepository} used to create a {@link ConnectionRepository} for the current user.
	 * @param userIdSource an instance of {@link UserIdSource} to obtain the current user's ID used to create a {@link ConnectionFactory}.
	 */
	public ReconnectFilter(UsersConnectionRepository usersConnectionRepository, UserIdSource userIdSource) {
		Assert.notNull(usersConnectionRepository, "UsersConnectionRepository cannot be null");
		Assert.notNull(userIdSource, "UserIdSource cannot be null");
		this.usersConnectionRepository = usersConnectionRepository;
		this.userIdSource = userIdSource;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		if (shouldPerformRefreshPostRequest(httpRequest)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Removing stale/revoked connection.");
			}
			String providerId = getProviderIdFromRequest(httpRequest);
			String currentUserId = userIdSource.getUserId();
			usersConnectionRepository.createConnectionRepository(currentUserId).removeConnections(providerId);
			if (logger.isDebugEnabled()) {
				logger.debug("Initiating refresh request.");
			}
			HttpServletRequest newRequest = new ReconnectionPostRequest(httpRequest);
			chain.doFilter(newRequest, httpResponse);
		} else {
			// Pass request through filter chain and handle any exceptions that come out of it.
			try {
				if (logger.isDebugEnabled()) {
					logger.debug("Processing request");
				}
				chain.doFilter(httpRequest, httpResponse);
			} catch (IOException e) {
				if (logger.isDebugEnabled()) {
					logger.debug("IOException: " + e.getMessage());
				}
				throw e;
			} catch (Exception e) {
				handleExceptionFromFilterChain(e, httpRequest, httpResponse);
			}
		}
	}
	
	// subclassing hooks
	/**
	 * Returns the URL to redirect to if it is determined that a connection needs to be renewed.
	 * By default, the filter will redirect to /connect/{provider ID} with a "reconnect" query parameter.
	 * This filter also handles GET requests to that same path before submitting a POST request to {@link ConnectController} for authorization.
	 * May be overridden by a subclass to handle other flows, such as redirecting to a page that informs the user that a new connection is needed. 
	 * @param request The HTTP request that triggered the exception.
	 * @param apiException The {@link ApiException}.
	 * @return the URL to redirect to if a connection needs to be renewed.
	 */
	protected String getRefreshUrl(HttpServletRequest request, ApiException apiException) {
		String scopeNeeded = getRequiredScope(apiException);
		StringBuilder sb = new StringBuilder(request.getContextPath() + CONNECT_PATH + apiException.getProviderId())
			.append(RECONNECT_PARAMETER_EQUALS_TRUE);
		if (scopeNeeded != null) {
			sb.append(SCOPE_PARAMETER_EQUALS + scopeNeeded);
		}
		return sb.toString();
	}

	/**
	 * Determines whether or not the handled request should be converted to a POST request to {@link ConnectController} for authorization.
	 * By default, will return true if the request is a GET request for /connect/{provider ID} and there is a "reconnect" query parameter.
	 * May be overridden by a subclass to consider other criteria in deciding whether or not to convert the request.
	 * @param request the handled request.
	 * @return true if the request should be converted to a POST request to {@link ConnectController}.
	 */
	protected boolean shouldPerformRefreshPostRequest(HttpServletRequest request) {
		String servletPath = request.getServletPath();
		return request.getMethod().equalsIgnoreCase(GET) && servletPath != null && servletPath.startsWith(CONNECT_PATH) && request.getParameter(RECONNECT_PARAMETER) != null;
	}

	// private helpers
	private String getRequiredScope(ApiException apiException) {
		return apiException instanceof InsufficientPermissionException ? ((InsufficientPermissionException) apiException).getRequiredPermission() : null;
	}

	private String getProviderIdFromRequest(HttpServletRequest httpRequest) {
		return httpRequest.getServletPath().substring(CONNECT_PATH_LENGTH).replace("/", "");
	}
	
	private void handleExceptionFromFilterChain(Exception e, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException, ServletException {
		RuntimeException ase = (ApiException) throwableAnalyzer.getFirstThrowableOfType(ApiException.class, throwableAnalyzer.determineCauseChain(e));
		if (ase != null && ase instanceof ApiException) {
			ApiException apiException = (ApiException) ase;
			if (logger.isDebugEnabled()) {
				logger.debug("API Exception: " + e.getMessage());
			}
			if (apiException instanceof NotAuthorizedException || apiException instanceof OperationNotPermittedException) {
				if (logger.isDebugEnabled()) {
					logger.debug("Redirecting for refresh of " + apiException.getProviderId() + " connection.");
				}
				httpResponse.sendRedirect(getRefreshUrl(httpRequest, apiException));
				return;
			}
		}

		if (e instanceof ServletException) {
			throw (ServletException) e;
		}
		else if (e instanceof RuntimeException) {
			throw (RuntimeException) e;
		}

		// Wrap other Exceptions in a generic RuntimeException. This should never happen because
		// we've already covered all the possibilities for doFilter
		throw new RuntimeException(e);
	}

	/*
	 * Request wrapper that converts existing request into a POST request to ConnectController
	 */
	private final class ReconnectionPostRequest extends HttpServletRequestWrapper {
		private ReconnectionPostRequest(HttpServletRequest request) {
			super(request);
		}
		
		@Override
		public String getMethod() {
			return POST;
		}
	}
	
	private static final String CONNECT_PATH = "/connect/";

	private static final int CONNECT_PATH_LENGTH = CONNECT_PATH.length();

	private static final String RECONNECT_PARAMETER = "reconnect";

	private static final String RECONNECT_PARAMETER_EQUALS_TRUE = "?" + RECONNECT_PARAMETER +"=true";

	private static final String SCOPE_PARAMETER_EQUALS = "&scope=";
	
	private static final String POST = "POST";
	
	private static final String GET = "GET";

}
