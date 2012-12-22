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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.security.provider.SocialAuthenticationService;
import org.springframework.util.Assert;

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

	private boolean updateConnections = true;

	private UserIdExtractor userIdExtractor;

	private UsersConnectionRepository usersConnectionRepository;

	public SocialAuthenticationFilter(AuthenticationManager authManager, UserIdExtractor userIdExtractor, 
			UsersConnectionRepository usersConnectionRepository, SocialAuthenticationServiceLocator authServiceLocator) {
		super("/auth");
		setAuthenticationManager(authManager);
		this.userIdExtractor = userIdExtractor;
		this.usersConnectionRepository = usersConnectionRepository;
		this.authServiceLocator = authServiceLocator;
	}

    /**
     * Indicates whether this filter should attempt to process a social network login request for the current invocation.
     * <p>
     * Check if request URL matches filterProcessesUrl with valid providerId. 
     * The URL must be like {filterProcessesUrl}/{providerId}. 
     *
     * @return <code>true</code> if the filter should attempt authentication, <code>false</code> otherwise.
     */
	@Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String providerId = getRequestedProviderId(request);
        if (providerId != null){
            Set<String> authProviders = authServiceLocator.registeredAuthenticationProviderIds();
            return authProviders.contains(providerId);
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter#attemptAuthentication
     * (javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        Authentication auth = null;
        Set<String> authProviders = authServiceLocator.registeredAuthenticationProviderIds();
        String providerId = getRequestedProviderId(request);
        if (!authProviders.isEmpty() && providerId != null &&authProviders.contains(providerId)) {
            SocialAuthenticationService<?> authService = authServiceLocator.getAuthenticationService(providerId);
            auth = attemptAuthService(authService, request, response);
            if (auth == null) {
                throw new AuthenticationServiceException("authentication failed");
            }
        }
        return auth;
    }

    /**
     * Override to handle redirect exception.
     * 
     * @see org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter#unsuccessfulAuthentication
     * (javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, 
     *  org.springframework.security.core.AuthenticationException)
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {
        if (failed instanceof SocialAuthenticationRedirectException){
            response.sendRedirect(((SocialAuthenticationRedirectException)failed).getRedirectUrl()); 
            return;
        }
        super.unsuccessfulAuthentication(request, response, failed);
    }

    // private helpers
    
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

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /*
     * Call SocialAuthenticationService.getAuthToken() to get SocialAuthenticationToken:
     *     If first phase, throw AuthenticationRedirectException to redirect to provider website.
     *     If second phase, get token/code from request parameter and call provider API to get accessToken/accessGrant.
     * Check Authentication object in spring security context, if null or not authenticated,  call doAuthentication()
     * Otherwise, it is already authenticated, add this connection.
     */
    private Authentication attemptAuthService(final SocialAuthenticationService<?> authService, final HttpServletRequest request, 
            HttpServletResponse response) throws SocialAuthenticationRedirectException, AuthenticationException {

        final SocialAuthenticationToken token = authService.getAuthToken(request, response);
        if (token == null) return null;
        
        Assert.notNull(token.getConnection());
        
        Authentication auth = getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return doAuthentication(authService, request, token);
        } else {
            addConnection(authService, request, token, auth);
            return null;
        }       
    }   

    /*
     * Will pass token with Connection object to SocialAuthenticationProvider.authenticate(), 
     * and it may signup a new user by UsersConnectionRepository implementation class automatically.
     * Returned Authentication object is a new SocialAuthenticationToken object with principal set to
     * a SocialUserDetails object.
     */
    private Authentication doAuthentication(SocialAuthenticationService<?> authService, HttpServletRequest request, 
            SocialAuthenticationToken token) {
        try {
            if (!authService.getConnectionCardinality().isAuthenticatePossible()) return null;
            token.setDetails(authenticationDetailsSource.buildDetails(request));
            Authentication success = getAuthenticationManager().authenticate(token);
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

    /*
     * Call ConnectionRepository.updateConnection() to update stored connection related data.
     */
    private void updateConnections(SocialAuthenticationService<?> authService, SocialAuthenticationToken token, Authentication success) {
        if (isUpdateConnections()) {
            String userId = ((SocialUserDetails)success.getPrincipal()).getUserId();
            Connection<?> connection = token.getConnection();
            ConnectionRepository repo = getUsersConnectionRepository().createConnectionRepository(userId);
            repo.updateConnection(connection);
        }
    }

    /*
     *  already authenticated - add connection instead if not in UsersConnectionRepository.
     */
    private void addConnection(final SocialAuthenticationService<?> authService, HttpServletRequest request, 
            SocialAuthenticationToken token, Authentication auth) {
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

	protected Connection<?> addConnection(SocialAuthenticationService<?> authService, String userId, ConnectionData data) {
		HashSet<String> userIdSet = new HashSet<String>();
		userIdSet.add(data.getProviderUserId());
		Set<String> connectedUserIds = usersConnectionRepository.findUserIdsConnectedTo(data.getProviderId(), userIdSet);
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
			throw new IllegalStateException("can't set postLoginUrl on unknown successHandler, type is " 
			                + successHandler.getClass().getName());
		}
	}

	public void setPostFailureUrl(String postFailureUrl) {
		AuthenticationFailureHandler failureHandler = getFailureHandler();
		if (failureHandler instanceof SimpleUrlAuthenticationFailureHandler) {
			SimpleUrlAuthenticationFailureHandler h = (SimpleUrlAuthenticationFailureHandler) failureHandler;
			h.setDefaultFailureUrl(postFailureUrl);
		} else {
			throw new IllegalStateException("can't set postFailureUrl on unknown failureHandler, type is " 
			                + failureHandler.getClass().getName());
		}
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
