/*
 * Copyright 2013 the original author or authors.
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
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Servlet filter that intercepts Spring Social {@link ApiException}s thrown in the course of a request and attempts to reconcile any connection-related
 * problems by deleting the stale/revoked connection and walking the user through the connection process to obtain a new connection.
 * @author Craig Walls
 */
public class ApiExceptionHandlingFilter extends GenericFilterBean {

	// TODO: There's a lot of URL manipulation in this case that could probably be broken down into reusable bits
	
	private final static Log logger = LogFactory.getLog(ApiExceptionHandlingFilter.class);

	private ThrowableAnalyzer throwableAnalyzer = new ThrowableAnalyzer();

	private UsersConnectionRepository usersConnectionRepository;
	
	private String filterRefreshUrl = "/refresh";

	private UserIdSource userIdSource;

	public ApiExceptionHandlingFilter(UsersConnectionRepository usersConnectionRepository, UserIdSource userIdSource) {
		this.usersConnectionRepository = usersConnectionRepository;
		this.userIdSource = userIdSource;
	}

	/**
	 * The URL path that this filter will intercept to trigger a reconnect flow.
	 * Defaults to "/refresh".
	 */
	public void setFilterRefreshUrl(String filterRefreshUrl) {
		this.filterRefreshUrl = filterRefreshUrl;
	}
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		if (shouldPerformRefreshPostRequest(httpRequest)) {
			logger.info("Removing stale/revoked connection.");			
			String providerId = getProviderIdFromRequest(httpRequest);
			String currentUserId = userIdSource.getUserId();
			usersConnectionRepository.createConnectionRepository(currentUserId).removeConnections(providerId); 
			logger.info("Initiating refresh request.");
			HttpServletRequest newRequest = new ReconnectionPostRequest(httpRequest);
			chain.doFilter(newRequest, httpResponse);
		} else {
			// Pass request through filter chain and handle any exceptions that come out of it.
			try {
				logger.info("Processing request");
				chain.doFilter(httpRequest, httpResponse);
			} catch (IOException e) {
				logger.info("IOException: " + e.getMessage());
				throw e;
			} catch (Exception e) {
				handleExceptionFromFilterChain(e, httpRequest, httpResponse);
			}
		}
	}
	
	// subclassing hooks
	/**
	 * Returns the URL to redirect to if it is determined that a connection needs to be renewed.
	 * By default, the URL will be based on {@link #filterRefreshUrl}, to immediately trigger a connection flow with {@link ConnectController}.
	 * May be overridden by a subclass to handle other flows, such as redirecting to a page that informs the user that a new connection is needed. 
	 * @param request The HTTP request that triggered the exception.
	 * @param apiException The {@link ApiException}.
	 * @return the URL to redirect to if a connection needs to be renewed.
	 */
	protected String getRefreshUrl(HttpServletRequest request, ApiException apiException) {
		String scopeNeeded = getRequiredScope(apiException);				
		return request.getContextPath() + filterRefreshUrl + "/" + apiException.getProviderId() + (scopeNeeded != null ? "?scope=" + scopeNeeded : "");
	}

	// private helpers
	private String getRequiredScope(ApiException apiException) {
		return apiException instanceof InsufficientPermissionException ? ((InsufficientPermissionException) apiException).getRequiredPermission() : null;
	}

	private String getProviderIdFromRequest(HttpServletRequest httpRequest) {
		return httpRequest.getServletPath().substring(filterRefreshUrl.length()+1);  // TODO: Seems hackish
	}
	
	private boolean shouldPerformRefreshPostRequest(HttpServletRequest request) {
		String servletPath = request.getServletPath();
		return servletPath != null && servletPath.startsWith(filterRefreshUrl) && request.getMethod().equalsIgnoreCase("GET");
	}

	private void handleExceptionFromFilterChain(Exception e, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException, ServletException {
		// Try to extract a SpringSecurityException from the stacktrace
		Throwable[] causeChain = throwableAnalyzer.determineCauseChain(e);
		RuntimeException ase = (ApiException) throwableAnalyzer.getFirstThrowableOfType(ApiException.class, causeChain);
		if (ase != null && ase instanceof ApiException) {
			ApiException apiException = (ApiException) ase;
			logger.info("API Exception: " + e.getMessage());
			if (apiException instanceof NotAuthorizedException || apiException instanceof OperationNotPermittedException) {
				logger.info("Redirecting for refresh of " + apiException.getProviderId() + " connection.");
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

		// Wrap other Exceptions. This shouldn't actually happen
		// as we've already covered all the possibilities for doFilter
		throw new RuntimeException(e);
	}

	private final class ReconnectionPostRequest extends HttpServletRequestWrapper {

		private ReconnectionPostRequest(HttpServletRequest request) {
			super(request);
		}
		
		@Override
		public String getMethod() {
			return "POST";
		}
		
		@Override
		public String getRequestURI() {
			return getRequestURL().toString();
		}
		
		@Override
		public StringBuffer getRequestURL() {
			HttpServletRequest request = (HttpServletRequest) getRequest();
			return convertRefreshPathToConnectPath(request.getRequestURL());
		}

		@Override
		public String getServletPath() {
			HttpServletRequest request = (HttpServletRequest) getRequest();
			return convertRefreshPathToConnectPath(new StringBuffer(request.getServletPath())).toString();
		}
		
		private StringBuffer convertRefreshPathToConnectPath(StringBuffer refreshPath) {
			int filterPathIndex = refreshPath.indexOf(filterRefreshUrl);
			return refreshPath.replace(filterPathIndex, filterPathIndex + filterRefreshUrl.length(), "/connect");
		}

	}

}
