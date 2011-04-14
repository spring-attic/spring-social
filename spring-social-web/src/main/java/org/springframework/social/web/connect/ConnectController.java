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
package org.springframework.social.web.connect;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.social.connect.ServiceProviderConnection;
import org.springframework.social.connect.ServiceProviderConnectionFactory;
import org.springframework.social.connect.ServiceProviderConnectionFactoryLocator;
import org.springframework.social.connect.ServiceProviderConnectionKey;
import org.springframework.social.connect.ServiceProviderConnectionRepository;
import org.springframework.social.connect.support.OAuth1ServiceProviderConnectionFactory;
import org.springframework.social.connect.support.OAuth2ServiceProviderConnectionFactory;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

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
@RequestMapping("/connect/")
public class ConnectController  {
	
	private String baseCallbackUrl;
	
	private MultiValueMap<Class<?>, ConnectInterceptor<?>> interceptors;

	private ServiceProviderConnectionFactoryLocator connectionFactoryLocator;
	
	private ServiceProviderConnectionRepository connectionRepository;
	
	/**
	 * Constructs a ConnectController.
	 * @param applicationUrl the base secure URL for this application, used to construct the callback URL passed to the service providers at the beginning of the connection process.
	 */
	@Inject
	public ConnectController(String applicationUrl, ServiceProviderConnectionFactoryLocator connectionFactoryLocator, ServiceProviderConnectionRepository connectionRepository) {
		this.baseCallbackUrl = applicationUrl + AnnotationUtils.findAnnotation(getClass(), RequestMapping.class).value()[0];
		this.connectionFactoryLocator = connectionFactoryLocator;
		this.connectionRepository = connectionRepository;
		this.interceptors = new LinkedMultiValueMap<Class<?>, ConnectInterceptor<?>>();
	}

	/**
	 * Configure the list of interceptors that should receive callbacks during the connection process.
	 */
	public void setInterceptors(List<ConnectInterceptor<?>> interceptors) {
		for (ConnectInterceptor<?> interceptor : interceptors) {
			addInterceptor(interceptor);
		}
	}

	/**
	 * Adds a ConnectInterceptor to receive callbacks during the connection process.
	 */
	public void addInterceptor(ConnectInterceptor<?> interceptor) {
		Class<?> serviceApiType = GenericTypeResolver.resolveTypeArgument(interceptor.getClass(), ConnectInterceptor.class);
		interceptors.add(serviceApiType, interceptor);
	}

	/**
	 * Render the connect form for the service provider identified by {name} to the member as HTML in their web browser.
	 */
	@RequestMapping(value="{providerId}", method=RequestMethod.GET)
	public String connect(@PathVariable String providerId, Model model) {
		List<ServiceProviderConnection<?>> connections = connectionRepository.findConnectionsToProvider(providerId);
		if (connections.isEmpty()) {
			return baseViewPath(providerId) + "Connect";
		} else {
			model.addAttribute("connections", connections);
			return baseViewPath(providerId) + "Connected";			
		}
	}

	/**
	 * Process a connect form submission by commencing the process of establishing a connection to the provider on behalf of the member.
	 * Fetches a new request token from the provider, temporarily stores it in the session, then redirects the member to the provider's site for authorization.
	 */
	@RequestMapping(value="{providerId}", method=RequestMethod.POST)
	public String connect(@PathVariable String providerId, @RequestParam(required=false) String scope, WebRequest request) {
		ServiceProviderConnectionFactory<?> connectionFactory = connectionFactoryLocator.getConnectionFactory(providerId);
		preConnect(connectionFactory, request);
		if (connectionFactory instanceof OAuth1ServiceProviderConnectionFactory) {
			OAuth1Operations oauth1Ops = ((OAuth1ServiceProviderConnectionFactory<?>) connectionFactory).getOAuthOperations();
			OAuthToken requestToken = oauth1Ops.fetchRequestToken(callbackUrl(providerId), null);
			request.setAttribute(OAUTH_TOKEN_ATTRIBUTE, requestToken, WebRequest.SCOPE_SESSION);
			return "redirect:" + oauth1Ops.buildAuthorizeUrl(requestToken.getValue(), callbackUrl(providerId));
		} else if (connectionFactory instanceof OAuth2ServiceProviderConnectionFactory) {
			return "redirect:" + ((OAuth2ServiceProviderConnectionFactory<?>) connectionFactory).getOAuthOperations().buildAuthorizeUrl(callbackUrl(providerId), scope, null, GrantType.AuthorizationCode, null);
		} else {
			throw new IllegalStateException("Connections to provider '" + providerId + "' not supported");
		}
	}

	/**
	 * Process the authorization callback from an OAuth 1 service provider.
	 * Called after the member authorizes the connection, generally done by having he or she click "Allow" in their web browser at the provider's site.
	 * On authorization verification, connects the member's local account to the account they hold at the service provider
	 * Removes the request token from the session since it is no longer valid after the connection is established.
	 */
	@RequestMapping(value="{providerId}", method=RequestMethod.GET, params="oauth_token")
	public String oauth1Callback(@PathVariable String providerId, @RequestParam("oauth_token") String token, @RequestParam(value="oauth_verifier", required=false) String verifier, WebRequest request) {
		OAuth1ServiceProviderConnectionFactory<?> connectionFactory = (OAuth1ServiceProviderConnectionFactory<?>) connectionFactoryLocator.getConnectionFactory(providerId);
		OAuthToken accessToken = connectionFactory.getOAuthOperations().exchangeForAccessToken(new AuthorizedRequestToken(extractCachedRequestToken(request), verifier), null);
		ServiceProviderConnection<?> connection = connectionFactory.createConnection(accessToken);
		connectionRepository.addConnection(connection);	
		postConnect(connectionFactory, connection, request);
		return redirectToProviderConnect(providerId);
	}

	/**
	 * Process the authorization callback from an OAuth 2 service provider.
	 * Called after the member authorizes the connection, generally done by having he or she click "Allow" in their web browser at the provider's site.
	 * On authorization verification, connects the member's local account to the account they hold at the service provider.
	 */
	@RequestMapping(value="{providerId}", method=RequestMethod.GET, params="code")
	public String oauth2Callback(@PathVariable String providerId, @RequestParam("code") String code, WebRequest request) {
		OAuth2ServiceProviderConnectionFactory<?> connectionFactory = (OAuth2ServiceProviderConnectionFactory<?>) connectionFactoryLocator.getConnectionFactory(providerId);
		AccessGrant accessGrant = connectionFactory.getOAuthOperations().exchangeForAccess(code, callbackUrl(providerId), null);
		ServiceProviderConnection<?> connection = connectionFactory.createConnection(accessGrant);
		connectionRepository.addConnection(connection);
		postConnect(connectionFactory, connection, request);
		return redirectToProviderConnect(providerId);
	}

	/**
	 * Remove all provider connections for a user account.
	 * The member has decided they no longer wish to use the service provider from this application.
	 */
	@RequestMapping(value="{providerId}", method=RequestMethod.DELETE)
	public String removeConnections(@PathVariable String providerId) {
		connectionRepository.removeConnectionsToProvider(providerId);
		return redirectToProviderConnect(providerId);
	}

	/**
	 * Remove a single provider connection associated with a user account.
	 * The member has decided they no longer wish to use the service provider account from this application.
	 */
	@RequestMapping(value="{providerId}/{providerUserId}", method=RequestMethod.DELETE)
	public String removeConnections(@PathVariable String providerId, @PathVariable String providerUserId) {
		connectionRepository.removeConnection(new ServiceProviderConnectionKey(providerId, providerUserId));
		return redirectToProviderConnect(providerId);
	}
	
	// internal helpers

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void preConnect(ServiceProviderConnectionFactory<?> connectionFactory, WebRequest request) {
		for (ConnectInterceptor interceptor : interceptingConnectionsTo(connectionFactory)) {
			interceptor.preConnect(connectionFactory, request);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void postConnect(ServiceProviderConnectionFactory<?> connectionFactory, ServiceProviderConnection<?> connection, WebRequest request) {
		for (ConnectInterceptor interceptor : interceptingConnectionsTo(connectionFactory)) {
			interceptor.postConnect(connection, request);
		}
	}

	private List<ConnectInterceptor<?>> interceptingConnectionsTo(ServiceProviderConnectionFactory<?> connectionFactory) {
		Class<?> serviceType = GenericTypeResolver.resolveTypeArgument(connectionFactory.getClass(), ServiceProviderConnectionFactory.class);
		List<ConnectInterceptor<?>> typedInterceptors = interceptors.get(serviceType);
		if (typedInterceptors == null) {
			typedInterceptors = Collections.emptyList();
		}
		return typedInterceptors;
	}
	
	private String baseViewPath(String providerId) {
		return "connect/" + providerId;		
	}
	
	private String callbackUrl(String providerId) {
		return baseCallbackUrl + providerId;
	}

	private OAuthToken extractCachedRequestToken(WebRequest request) {
		OAuthToken requestToken = (OAuthToken) request.getAttribute(OAUTH_TOKEN_ATTRIBUTE, WebRequest.SCOPE_SESSION);
		request.removeAttribute(OAUTH_TOKEN_ATTRIBUTE, WebRequest.SCOPE_SESSION);
		return requestToken;
	}

	private String redirectToProviderConnect(String providerId) {
		return "redirect:/connect/" + providerId;
	}

	private static final String OAUTH_TOKEN_ATTRIBUTE = "oauthToken";

}