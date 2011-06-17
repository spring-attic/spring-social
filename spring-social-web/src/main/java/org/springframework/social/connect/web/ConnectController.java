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

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.core.GenericTypeResolver;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.DuplicateConnectionException;
import org.springframework.social.connect.support.OAuth1ConnectionFactory;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Generic UI controller for managing the account-to-service-provider connection flow.
 * <ul>
 * <li>GET /connect/{providerId}  - Get a web page showing connection status to {providerId}.</li>
 * <li>POST /connect/{providerId} - Initiate an connection with {providerId}.</li>
 * <li>GET /connect/{providerId}?oauth_verifier||code - Receive {providerId} authorization callback and establish the connection.</li>
 * <li>DELETE /connect/{providerId} - Disconnect from {providerId}.</li>
 * </ul>
 * @author Keith Donald
 * @author Craig Walls
 * @author Roy Clarkson
 */
@Controller
@RequestMapping("/connect")
public class ConnectController  {
	
	private final ConnectionFactoryLocator connectionFactoryLocator;
	
	private final ConnectionRepository connectionRepository;

	private final MultiValueMap<Class<?>, ConnectInterceptor<?>> interceptors = new LinkedMultiValueMap<Class<?>, ConnectInterceptor<?>>();

	private final ConnectSupport webSupport = new ConnectSupport();
	
	/**
	 * Constructs a ConnectController.
	 * @param connectionFactoryLocator the locator for {@link ConnectionFactory} instances needed to establish connections
	 * @param connectionRepository the current user's {@link ConnectionRepository} needed to persist connections; must be a proxy to a request-scoped bean
	 */
	@Inject
	public ConnectController(ConnectionFactoryLocator connectionFactoryLocator, ConnectionRepository connectionRepository) {
		this.connectionFactoryLocator = connectionFactoryLocator;
		this.connectionRepository = connectionRepository;
	}

	/**
	 * Configure the list of interceptors that should receive callbacks during the connection process.
	 * Convenient when an instance of this class is configured using a tool that supports JavaBeans-based configuration.
	 * @param interceptors the connect interceptors to add
	 */
	public void setInterceptors(List<ConnectInterceptor<?>> interceptors) {
		for (ConnectInterceptor<?> interceptor : interceptors) {
			addInterceptor(interceptor);
		}
	}

	/**
	 * Configures the base secure URL for the application this controller is being used in e.g. <code>https://myapp.com</code>. Defaults to null.
	 * If specified, will be used to generate OAuth callback URLs.
	 * If not specified, OAuth callback URLs are generated from web request info. 
	 * You may wish to set this property if requests into your application flow through a proxy to your application server.
	 * In this case, the request URI may contain a scheme, host, and/or port value that points to an internal server not appropriate for an external callback URL.
	 * If you have this problem, you can set this property to the base external URL for your application and it will be used to construct the callback URL instead.
	 * @param applicationUrl the application URL value
	 */
	public void setApplicationUrl(URL applicationUrl) {
		webSupport.setApplicationUrl(applicationUrl);
	}
	
	/**
	 * Adds a ConnectInterceptor to receive callbacks during the connection process.
	 * Useful for programmatic configuration.
	 * @param interceptor the connect interceptor to add
	 */
	public void addInterceptor(ConnectInterceptor<?> interceptor) {
		Class<?> serviceApiType = GenericTypeResolver.resolveTypeArgument(interceptor.getClass(), ConnectInterceptor.class);
		interceptors.add(serviceApiType, interceptor);
	}

	/**
	 * Render the status of connections across all providers to the user as HTML in their web browser.
	 */
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String connectionStatus(WebRequest request, Model model) {
		Map<String, List<Connection<?>>> connections = connectionRepository.findAllConnections();
		model.addAttribute("providerIds", connectionFactoryLocator.registeredProviderIds());		
		model.addAttribute("connectionMap", connections);
		return connectView("status");
	}

	/**
	 * Render the status of the connections to the service provider to the user as HTML in their web browser.
	 */
	@RequestMapping(value="/{providerId}", method=RequestMethod.GET)
	public String connectionStatus(@PathVariable String providerId, WebRequest request, Model model) {
		processFlash(request, model);
		List<Connection<?>> connections = connectionRepository.findConnections(providerId);
		if (connections.isEmpty()) {
			return connectView(providerId + "Connect"); 
		} else {
			model.addAttribute("connections", connections);
			return connectView(providerId + "Connected");			
		}
	}

	/**
	 * Process a connect form submission by commencing the process of establishing a connection to the provider on behalf of the member.
	 * For OAuth1, fetches a new request token from the provider, temporarily stores it in the session, then redirects the member to the provider's site for authorization.
	 * For OAuth2, redirects the user to the provider's site for authorization.
	 */
	@RequestMapping(value="/{providerId}", method=RequestMethod.POST)
	public RedirectView connect(@PathVariable String providerId, NativeWebRequest request) {
		ConnectionFactory<?> connectionFactory = connectionFactoryLocator.getConnectionFactory(providerId);
		preConnect(connectionFactory, request);
		return new RedirectView(webSupport.buildOAuthUrl(connectionFactory, request));
	}

	/**
	 * Process the authorization callback from an OAuth 1 service provider.
	 * Called after the user authorizes the connection, generally done by having he or she click "Allow" in their web browser at the provider's site.
	 * On authorization verification, connects the user's local account to the account they hold at the service provider
	 * Removes the request token from the session since it is no longer valid after the connection is established.
	 */
	@RequestMapping(value="/{providerId}", method=RequestMethod.GET, params="oauth_token")
	public RedirectView oauth1Callback(@PathVariable String providerId, NativeWebRequest request) {
		OAuth1ConnectionFactory<?> connectionFactory = (OAuth1ConnectionFactory<?>) connectionFactoryLocator.getConnectionFactory(providerId);
		Connection<?> connection = webSupport.completeConnection(connectionFactory, request);
		addConnection(connection, connectionFactory, request);
		return connectionStatusRedirect(providerId);
	}

	/**
	 * Process the authorization callback from an OAuth 2 service provider.
	 * Called after the user authorizes the connection, generally done by having he or she click "Allow" in their web browser at the provider's site.
	 * On authorization verification, connects the user's local account to the account they hold at the service provider.
	 */
	@RequestMapping(value="/{providerId}", method=RequestMethod.GET, params="code")
	public RedirectView oauth2Callback(@PathVariable String providerId, NativeWebRequest request) {
		OAuth2ConnectionFactory<?> connectionFactory = (OAuth2ConnectionFactory<?>) connectionFactoryLocator.getConnectionFactory(providerId);
		Connection<?> connection = webSupport.completeConnection(connectionFactory, request);
		addConnection(connection, connectionFactory, request);
		return connectionStatusRedirect(providerId);
	}

	/**
	 * Remove all provider connections for a user account.
	 * The user has decided they no longer wish to use the service provider from this application.
	 * Note: requires {@link HiddenHttpMethodFilter} to be registered with the '_method' request parameter set to 'DELETE' to convert web browser POSTs to DELETE requests.
	 */
	@RequestMapping(value="/{providerId}", method=RequestMethod.DELETE)
	public RedirectView removeConnections(@PathVariable String providerId) {
		connectionRepository.removeConnections(providerId);
		return connectionStatusRedirect(providerId);
	}

	/**
	 * Remove a single provider connection associated with a user account.
	 * The user has decided they no longer wish to use the service provider account from this application.
	 * Note: requires {@link HiddenHttpMethodFilter} to be registered with the '_method' request parameter set to 'DELETE' to convert web browser POSTs to DELETE requests.
	 */
	@RequestMapping(value="/{providerId}/{providerUserId}", method=RequestMethod.DELETE)
	public RedirectView removeConnection(@PathVariable String providerId, @PathVariable String providerUserId) {
		connectionRepository.removeConnection(new ConnectionKey(providerId, providerUserId));
		return connectionStatusRedirect(providerId);
	}

	// internal helpers

	private String getControllerPath() {
		return "/connect";
	}

	private String getViewPath() {
		return "connect/";
	}
	
	private String connectView(String name) {
		return getViewPath() + name;		
	}

	private void addConnection(Connection<?> connection, ConnectionFactory<?> connectionFactory, WebRequest request) {
		try {
			connectionRepository.addConnection(connection);
			postConnect(connectionFactory, connection, request);
		} catch (DuplicateConnectionException e) {
			request.setAttribute(DUPLICATE_CONNECTION_ATTRIBUTE, e, RequestAttributes.SCOPE_SESSION);
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
	
	private void processFlash(WebRequest request, Model model) {
		DuplicateConnectionException exception = (DuplicateConnectionException) request.getAttribute(DUPLICATE_CONNECTION_ATTRIBUTE, RequestAttributes.SCOPE_SESSION);
		if (exception != null) {
			model.addAttribute(DUPLICATE_CONNECTION_ATTRIBUTE, Boolean.TRUE);
			request.removeAttribute(DUPLICATE_CONNECTION_ATTRIBUTE, RequestAttributes.SCOPE_SESSION);			
		}
	}

	private RedirectView connectionStatusRedirect(String providerId) {
		return new RedirectView(getControllerPath() + "/" + providerId, true);
	}

	private static final String DUPLICATE_CONNECTION_ATTRIBUTE = "social.addConnection.duplicate";

}