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
package org.springframework.social.connect.web;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.DuplicateConnectionException;
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
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Generic UI controller for managing the account-to-service-provider connection flow.
 * <ul>
 * <li>GET /connect/{providerId}  - Get a web page showing connection status to {providerId}.</li>
 * <li>POST /connect/{providerId} - Initiate an connection with {providerId}.</li>
 * <li>GET /connect/{providerId}?oauth_token||code - Receive {providerId} authorization callback and establish the connection.</li>
 * <li>DELETE /connect/{providerId} - Disconnect from {providerId}.</li>
 * </ul>
 * @author Keith Donald
 * @author Craig Walls
 * @author Roy Clarkson
 */
@Controller
@RequestMapping("/connect")
public class ConnectController  {
	
	private String baseCallbackUrl;
	
	private MultiValueMap<Class<?>, ConnectInterceptor<?>> interceptors;

	private ConnectionFactoryLocator connectionFactoryLocator;
	
	private Provider<ConnectionRepository> connectionRepositoryProvider;
	
	private final boolean absoluteCallback;
	
	private final String trailingChar;
	
	/**
	 * Constructs a ConnectController.
	 * @param applicationUrl the base secure URL for this application, used to construct the callback URL passed to the service providers at the beginning of the connection process.
	 * @param connectionFactoryLocator the locator for {@link ConnectionFactory} instances needed to establish connections
	 * @param connectionRepositoryProvider the provider of the current user's {@link ConnectionRepository} needed to persist connections
	 */
	@Inject
	public ConnectController(String applicationUrl, ConnectionFactoryLocator connectionFactoryLocator, Provider<ConnectionRepository> connectionRepositoryProvider) {
		this.baseCallbackUrl = applicationUrl + AnnotationUtils.findAnnotation(getClass(), RequestMapping.class).value()[0];
		this.connectionFactoryLocator = connectionFactoryLocator;
		this.connectionRepositoryProvider = connectionRepositoryProvider;
		this.interceptors = new LinkedMultiValueMap<Class<?>, ConnectInterceptor<?>>();
		this.absoluteCallback = applicationUrl.matches("(?i)https?://.*");
		this.trailingChar = (!absoluteCallback && applicationUrl.indexOf("/") == 0) ? "" : "/";
	}

	/**
	 * Configure the list of interceptors that should receive callbacks during the connection process.
	 * Convenient when an instance of this class is configured using a tool that supports JavaBeans-based configuration.
	 */
	public void setInterceptors(List<ConnectInterceptor<?>> interceptors) {
		for (ConnectInterceptor<?> interceptor : interceptors) {
			addInterceptor(interceptor);
		}
	}

	/**
	 * Adds a ConnectInterceptor to receive callbacks during the connection process.
	 * Useful for programmatic configuration.
	 */
	public void addInterceptor(ConnectInterceptor<?> interceptor) {
		Class<?> serviceApiType = GenericTypeResolver.resolveTypeArgument(interceptor.getClass(), ConnectInterceptor.class);
		interceptors.add(serviceApiType, interceptor);
	}

	/**
	 * Render the connect form for the service provider identified by {name} to the member as HTML in their web browser.
	 */
	@RequestMapping(value="/{providerId}", method=RequestMethod.GET)
	public String providerPage(@PathVariable String providerId, WebRequest request, Model model) {
		handleDuplicateConnectionException(request, model);
		List<Connection<?>> connections = getConnectionRepository().findConnectionsToProvider(providerId);
		if (connections.isEmpty()) {
			return baseViewPath(providerId) + "Connect";
		} else {
			model.addAttribute("connections", connections);
			return baseViewPath(providerId) + "Connected";			
		}
	}

	/**
	 * Process a connect form submission by commencing the process of establishing a connection to the provider on behalf of the member.
	 * For OAuth1, fetches a new request token from the provider, temporarily stores it in the session, then redirects the member to the provider's site for authorization.
	 * For OAuth2, redirects the user to the provider's site for authorization.
	 */
	@RequestMapping(value="/{providerId}", method=RequestMethod.POST)
	public RedirectView connect(@PathVariable String providerId, WebRequest request) {
		ConnectionFactory<?> connectionFactory = connectionFactoryLocator.getConnectionFactory(providerId);
		preConnect(connectionFactory, request);
		if (connectionFactory instanceof OAuth1ConnectionFactory) {
			OAuth1Operations oauth1Ops = ((OAuth1ConnectionFactory<?>) connectionFactory).getOAuthOperations();
			OAuthToken requestToken = oauth1Ops.fetchRequestToken(callbackUrl(providerId, request), null);
			request.setAttribute(OAUTH_TOKEN_ATTRIBUTE, requestToken, WebRequest.SCOPE_SESSION);
			String authorizeUrl = oauth1Ops.buildAuthorizeUrl(requestToken.getValue(), oauth1Ops.getVersion() == OAuth1Version.CORE_10 ? new OAuth1Parameters(callbackUrl(providerId, request)) : OAuth1Parameters.NONE);
			return new RedirectView(authorizeUrl);
		} else if (connectionFactory instanceof OAuth2ConnectionFactory) {
			OAuth2Operations oauth2Ops = ((OAuth2ConnectionFactory<?>) connectionFactory).getOAuthOperations();
			String authorizeUrl = oauth2Ops.buildAuthorizeUrl(GrantType.AUTHORIZATION_CODE, new OAuth2Parameters(callbackUrl(providerId, request), request.getParameter("scope")));
			return new RedirectView(authorizeUrl);
		} else {
			return handleConnectToCustomConnectionFactory(connectionFactory, request);
		}
	}

	/**
	 * Process the authorization callback from an OAuth 1 service provider.
	 * Called after the user authorizes the connection, generally done by having he or she click "Allow" in their web browser at the provider's site.
	 * On authorization verification, connects the user's local account to the account they hold at the service provider
	 * Removes the request token from the session since it is no longer valid after the connection is established.
	 */
	@RequestMapping(value="/{providerId}", method=RequestMethod.GET, params="oauth_token")
	public RedirectView oauth1Callback(@PathVariable String providerId, @RequestParam("oauth_token") String token, @RequestParam(value="oauth_verifier", required=false) String verifier, WebRequest request) {
		OAuth1ConnectionFactory<?> connectionFactory = (OAuth1ConnectionFactory<?>) connectionFactoryLocator.getConnectionFactory(providerId);
		OAuthToken accessToken = connectionFactory.getOAuthOperations().exchangeForAccessToken(new AuthorizedRequestToken(extractCachedRequestToken(request), verifier), null);
		Connection<?> connection = connectionFactory.createConnection(accessToken);
		addConnection(request, connectionFactory, connection);
		return redirectToProvider(providerId);
	}

	/**
	 * Process the authorization callback from an OAuth 2 service provider.
	 * Called after the user authorizes the connection, generally done by having he or she click "Allow" in their web browser at the provider's site.
	 * On authorization verification, connects the user's local account to the account they hold at the service provider.
	 */
	@RequestMapping(value="/{providerId}", method=RequestMethod.GET, params="code")
	public RedirectView oauth2Callback(@PathVariable String providerId, @RequestParam("code") String code, WebRequest request) {
		OAuth2ConnectionFactory<?> connectionFactory = (OAuth2ConnectionFactory<?>) connectionFactoryLocator.getConnectionFactory(providerId);
		AccessGrant accessGrant = connectionFactory.getOAuthOperations().exchangeForAccess(code, callbackUrl(providerId, request), null);
		Connection<?> connection = connectionFactory.createConnection(accessGrant);
		addConnection(request, connectionFactory, connection);
		return redirectToProvider(providerId);
	}

	/**
	 * Remove all provider connections for a user account.
	 * The user has decided they no longer wish to use the service provider from this application.
	 */
	@RequestMapping(value="/{providerId}", method=RequestMethod.DELETE)
	public RedirectView removeConnections(@PathVariable String providerId) {
		getConnectionRepository().removeConnectionsToProvider(providerId);
		return redirectToProvider(providerId);
	}

	/**
	 * Remove a single provider connection associated with a user account.
	 * The user has decided they no longer wish to use the service provider account from this application.
	 */
	@RequestMapping(value="/{providerId}/{providerUserId}", method=RequestMethod.DELETE)
	public RedirectView removeConnections(@PathVariable String providerId, @PathVariable String providerUserId) {
		getConnectionRepository().removeConnection(new ConnectionKey(providerId, providerUserId));
		return redirectToProvider(providerId);
	}

	// subclassing hooks
	
	/**
	 * Hook method subclasses may override to create connections to providers of custom types other than OAuth1 or OAuth2.
	 * Default implementation throws an {@link IllegalStateException} indicating the custom {@link ConnectionFactory} is not supported.
	 */
	protected RedirectView handleConnectToCustomConnectionFactory(ConnectionFactory<?> connectionFactory, WebRequest request) {
		throw new IllegalStateException("Connections to provider '" + connectionFactory.getProviderId() + "' are not supported");		
	}
	
	// internal helpers
	private void addConnection(WebRequest request, ConnectionFactory<?> connectionFactory, Connection<?> connection) {
		try {
			getConnectionRepository().addConnection(connection);	
			postConnect(connectionFactory, connection, request);
		} catch (DuplicateConnectionException e) {
			request.setAttribute(DUPLICATE_CONNECTION_EXCEPTION_ATTRIBUTE, e, WebRequest.SCOPE_SESSION);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void preConnect(ConnectionFactory<?> connectionFactory, WebRequest request) {
		for (ConnectInterceptor interceptor : interceptingConnectionsTo(connectionFactory)) {
			interceptor.preConnect(connectionFactory, request);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void postConnect(ConnectionFactory<?> connectionFactory, Connection<?> connection, WebRequest request) {
		for (ConnectInterceptor interceptor : interceptingConnectionsTo(connectionFactory)) {
			interceptor.postConnect(connection, request);
		}
	}

	private List<ConnectInterceptor<?>> interceptingConnectionsTo(ConnectionFactory<?> connectionFactory) {
		Class<?> serviceType = GenericTypeResolver.resolveTypeArgument(connectionFactory.getClass(), ConnectionFactory.class);
		List<ConnectInterceptor<?>> typedInterceptors = interceptors.get(serviceType);
		if (typedInterceptors == null) {
			typedInterceptors = Collections.emptyList();
		}
		return typedInterceptors;
	}
	
	private String baseViewPath(String providerId) {
		return "connect/" + providerId;		
	}
	
	protected String callbackUrl(String providerId, WebRequest request) {
		if (absoluteCallback) return baseCallbackUrl + "/" + providerId;
		String proto = request.isSecure() ? "https://" : "http://";
		return proto + request.getHeader("Host") + trailingChar + baseCallbackUrl + "/" + providerId;
	}

	private OAuthToken extractCachedRequestToken(WebRequest request) {
		OAuthToken requestToken = (OAuthToken) request.getAttribute(OAUTH_TOKEN_ATTRIBUTE, WebRequest.SCOPE_SESSION);
		request.removeAttribute(OAUTH_TOKEN_ATTRIBUTE, WebRequest.SCOPE_SESSION);
		return requestToken;
	}
	
	private void handleDuplicateConnectionException(WebRequest request, Model model) {
		DuplicateConnectionException exception = (DuplicateConnectionException) request.getAttribute(DUPLICATE_CONNECTION_EXCEPTION_ATTRIBUTE, WebRequest.SCOPE_SESSION);
		request.removeAttribute(DUPLICATE_CONNECTION_EXCEPTION_ATTRIBUTE, WebRequest.SCOPE_SESSION);
		if(exception != null) {
			model.addAttribute(DUPLICATE_CONNECTION_EXCEPTION_ATTRIBUTE, Boolean.TRUE);
		}
	}

	private ConnectionRepository getConnectionRepository() {
		return connectionRepositoryProvider.get();
	}

	private RedirectView redirectToProvider(String providerId) {
		return new RedirectView(providerId, true);
	}

	private static final String OAUTH_TOKEN_ATTRIBUTE = "oauthToken";

	private static final String DUPLICATE_CONNECTION_EXCEPTION_ATTRIBUTE = "_duplicateConnectionException";

}