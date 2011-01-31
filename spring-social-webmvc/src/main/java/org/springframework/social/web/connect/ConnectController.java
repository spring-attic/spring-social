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
package org.springframework.social.web.connect;

import java.util.Collections;
import java.util.List;

import org.springframework.core.GenericTypeResolver;
import org.springframework.social.connect.ServiceProvider;
import org.springframework.social.connect.ServiceProviderConnection;
import org.springframework.social.connect.ServiceProviderFactory;
import org.springframework.social.connect.oauth1.OAuth1ServiceProvider;
import org.springframework.social.connect.oauth2.OAuth2ServiceProvider;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.stereotype.Controller;
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
 * GET /connect/{providerId}  - Get a web page showing connection status to {providerId}.<br/>
 * POST /connect/{providerId} - Initiate an connection with {providerId}.<br/>
 * GET /connect/{providerId}?oauth_token||code - Receive {providerId} authorization callback and establish the connection.<br/>
 * DELETE /connect/{providerId} - Disconnect from {providerId}.<br/>
 * </ul>
 * @author Keith Donald
 * @author Craig Walls
 */
@Controller
public class ConnectController {
	
	private final ServiceProviderFactory serviceProviderFactory;

	private final AccountIdResolver accountIdResolver;

	private final String baseCallbackUrl;
	
	private MultiValueMap<Class<?>, ConnectInterceptor<?>> interceptors;

	/**
	 * Constructs a ConnectController.
	 * @param serviceProviderFactory the factory that loads the ServiceProviders members wish to connect to
	 * @param applicationUrl the base secure URL for this application, used to construct the callback URL passed to the service providers at the beginning of the connection process.
	 */
	public ConnectController(ServiceProviderFactory serviceProviderFactory, AccountIdResolver accountIdResolver, String applicationUrl) {
		this.serviceProviderFactory = serviceProviderFactory;
		this.accountIdResolver = accountIdResolver;
		this.baseCallbackUrl = applicationUrl + "/connect/";
		this.interceptors = new LinkedMultiValueMap<Class<?>, ConnectInterceptor<?>>();
	}

	/**
	 * Configure the list of interceptors that should receive callbacks during the connection process.
	 */
	public void setInterceptors(List<ConnectInterceptor<?>> interceptors) {
		for (ConnectInterceptor<?> interceptor : interceptors) {
			Class<?> providerType = GenericTypeResolver.resolveTypeArgument(interceptor.getClass(),  ConnectInterceptor.class);
			this.interceptors.add(providerType, interceptor);
		}
	}

	/**
	 * Render the connect form for the service provider identified by {name} to the member as HTML in their web browser.
	 */
	@RequestMapping(value="/connect/{providerId}", method=RequestMethod.GET)
	public String connect(@PathVariable String providerId) {
		String baseViewPath = "connect/" + providerId;
		if (getServiceProvider(providerId).isConnected(accountIdResolver.resolveAccountId())) {
			return baseViewPath + "Connected";
		} else {
			return baseViewPath + "Connect";
		}
	}

	/**
	 * Process a connect form submission by commencing the process of establishing a connection to the provider on behalf of the member.
	 * Fetches a new request token from the provider, temporarily stores it in the session, then redirects the member to the provider's site for authorization.
	 */
	@RequestMapping(value="/connect/{providerId}", method=RequestMethod.POST)
	public String connect(@PathVariable String providerId, @RequestParam(required=false) String scope,  WebRequest request) {
		ServiceProvider<?> provider = getServiceProvider(providerId);
		preConnect(provider, request);
		if (provider instanceof OAuth1ServiceProvider) {
			OAuth1Operations oauth1Ops = ((OAuth1ServiceProvider<?>) provider).getOAuth1Operations();
			OAuthToken requestToken = oauth1Ops.fetchNewRequestToken(baseCallbackUrl + providerId);
			request.setAttribute(OAUTH_TOKEN_ATTRIBUTE, requestToken, WebRequest.SCOPE_SESSION);
			return "redirect:" + oauth1Ops.buildAuthorizeUrl(requestToken.getValue());
		} else {
			return "redirect:" + ((OAuth2ServiceProvider<?>) provider).getOAuth2Operations().buildAuthorizeUrl(baseCallbackUrl + providerId, scope);
		}
	}

	/**
	 * Process the authorization callback from an OAuth 1 service provider.
	 * Called after the member authorizes the connection, generally done by having he or she click "Allow" in their web browser at the provider's site.
	 * On authorization verification, connects the member's local account to the account they hold at the service provider
	 * Removes the request token from the session since it is no longer valid after the connection is established.
	 */
	@RequestMapping(value="/connect/{providerId}", method=RequestMethod.GET, params="oauth_token")
	public String oauth1Callback(@PathVariable String providerId, @RequestParam("oauth_token") String token, @RequestParam(value="oauth_verifier") String verifier, WebRequest request) {
		OAuth1ServiceProvider<?> provider = (OAuth1ServiceProvider<?>) getServiceProvider(providerId);
		AuthorizedRequestToken authorizedRequestToken = new AuthorizedRequestToken(extractCachedRequestToken(request), verifier);
		ServiceProviderConnection<?> connection = provider.connect(accountIdResolver.resolveAccountId(), provider.getOAuth1Operations().exchangeForAccessToken(authorizedRequestToken));
		postConnect(provider, connection, request);
		return "redirect:/connect/" + providerId;
	}

	/**
	 * Process the authorization callback from an OAuth 2 service provider.
	 * Called after the member authorizes the connection, generally done by having he or she click "Allow" in their web browser at the provider's site.
	 * On authorization verification, connects the member's local account to the account they hold at the service provider.
	 */
	@RequestMapping(value="/connect/{providerId}", method=RequestMethod.GET, params="code")
	public String oauth2Callback(@PathVariable String providerId, @RequestParam("code") String code, WebRequest request) {
		OAuth2ServiceProvider<?> provider = (OAuth2ServiceProvider<?>) getServiceProvider(providerId);
		AccessGrant accessGrant = provider.getOAuth2Operations().exchangeForAccess(code, baseCallbackUrl + providerId);
		ServiceProviderConnection<?> connection = provider.connect(accountIdResolver.resolveAccountId(), accessGrant);
		postConnect(provider, connection, request);
		return "redirect:/connect/" + providerId;
	}

	/**
	 * Disconnect from the provider.
	 * The member has decided they no longer wish to use the service provider from this application.
	 */
	@RequestMapping(value="/connect/{providerId}", method=RequestMethod.DELETE)
	public String disconnect(@PathVariable String providerId) {
		ServiceProvider provider = getServiceProvider(providerId);
		List<ServiceProviderConnection> connections = provider.getConnections(accountIdResolver.resolveAccountId());
		for (ServiceProviderConnection connection : connections) {
			connection.disconnect();
		}
		return "redirect:/connect/" + providerId;
	}

	// internal helpers

	private ServiceProvider<?> getServiceProvider(String providerId) {
		return serviceProviderFactory.getServiceProvider(providerId);
	}

	private void preConnect(ServiceProvider<?> provider, WebRequest request) {
		for (ConnectInterceptor interceptor : interceptingConnectionsTo(provider)) {
			interceptor.preConnect(provider, request);
		}
	}

	private void postConnect(ServiceProvider<?> provider, ServiceProviderConnection<?> connection, WebRequest request) {
		for (ConnectInterceptor interceptor : interceptingConnectionsTo(provider)) {
			interceptor.postConnect(provider, connection, request);
		}
	}

	private List<ConnectInterceptor<?>> interceptingConnectionsTo(ServiceProvider<?> provider) {
		Class<?> serviceType = GenericTypeResolver.resolveTypeArgument(provider.getClass(), ServiceProvider.class);
		List<ConnectInterceptor<?>> typedInterceptors = interceptors.get(serviceType);
		if (typedInterceptors == null) {
			typedInterceptors = Collections.emptyList();
		}
		return typedInterceptors;
	}

	private OAuthToken extractCachedRequestToken(WebRequest request) {
		OAuthToken requestToken = (OAuthToken) request.getAttribute(OAUTH_TOKEN_ATTRIBUTE, WebRequest.SCOPE_SESSION);
		request.removeAttribute(OAUTH_TOKEN_ATTRIBUTE, WebRequest.SCOPE_SESSION);
		return requestToken;
	}
	
	private static final String OAUTH_TOKEN_ATTRIBUTE = "oauthToken";
	
}