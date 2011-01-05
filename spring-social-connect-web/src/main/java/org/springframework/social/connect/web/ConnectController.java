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
package org.springframework.social.connect.web;

import java.util.Collections;
import java.util.List;

import org.springframework.core.GenericTypeResolver;
import org.springframework.social.connect.AccountIdResolver;
import org.springframework.social.connect.AuthorizedRequestToken;
import org.springframework.social.connect.OAuthToken;
import org.springframework.social.connect.OAuthVersion;
import org.springframework.social.connect.ServiceProvider;
import org.springframework.social.connect.ServiceProviderFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

/**
 * Generic UI controller for managing the account connection process.  Supported flow:
 * <ul>
 * GET /connect/{name}  - Get a web page showing Account connection status to provider {name}.<br/>
 * POST /connect/{name} - Initiate an Account connection with provider {name}.<br/>
 * GET /connect/{name}?oauth_token - Receive provider {name} authorization callback and complete Account connection.<br/>
 * DELETE /connect/{name} - Disconnect Account from provider {name}.<br/>
 * </ul>
 * @author Keith Donald
 * @author Craig Walls
 */
@Controller
public class ConnectController {
	
	private final ServiceProviderFactory serviceProviderFactory;
	
	private final String baseCallbackUrl;
	
	private MultiValueMap<Class<?>, ConnectInterceptor<?>> interceptors;

	private final AccountIdResolver accountIdResolver;

	/**
	 * Constructs a ConnectController.
	 * @param serviceProviderFactory the factory that loads the ServiceProviders members wish to connect to
	 * @param applicationUrl the base secure URL for this application, used to construct the callback URL passed to the service providers at the beginning of the connection process.
	 */
	public ConnectController(ServiceProviderFactory serviceProviderFactory, String applicationUrl,
			AccountIdResolver accountIdResolver) {
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
	@RequestMapping(value="/connect/{name}", method=RequestMethod.GET)
	public String connect(@PathVariable String name) {
		String baseViewPath = "connect/" + name;
		if (getServiceProvider(name).isConnected(accountIdResolver.resolveAccountId())) {
			return baseViewPath + "Connected";
		} else {
			return baseViewPath + "Connect";
		}
	}

	/**
	 * Process a connect form submission by commencing the process of establishing a connection to the provider on behalf of the member.
	 * Fetches a new request token from the provider, temporarily stores it in the session, then redirects the member to the provider's site for authorization.
	 */
	@RequestMapping(value="/connect/{name}", method=RequestMethod.POST)
	public String connect(@PathVariable String name, WebRequest request) {
		ServiceProvider<?> provider = getServiceProvider(name);
		preConnect(provider, request);

		String authorizationUrlParameter = null;
		if (provider.getOAuthVersion() == OAuthVersion.OAUTH_1) {
			OAuthToken requestToken = provider.fetchNewRequestToken(baseCallbackUrl + name);
			request.setAttribute(OAUTH_TOKEN_ATTRIBUTE, requestToken, WebRequest.SCOPE_SESSION);
			authorizationUrlParameter = requestToken.getValue();
		} else {
			authorizationUrlParameter = baseCallbackUrl + name;
		}

		return "redirect:" + provider.buildAuthorizeUrl(authorizationUrlParameter);
	}
	
	/**
	 * Process the authorization callback from the service provider.
	 * Called after the member authorizes the connection, generally done by having he or she click "Allow" in their web browser at the provider's site.
	 * On authorization verification, connects the member's local account to the account they hold at the service provider.
	 * Removes the request token from the session since it is no longer valid after the connection is established.
	 */
	@RequestMapping(value="/connect/{name}", method=RequestMethod.GET, params="oauth_token")
	public String authorizeCallback(@PathVariable String name, @RequestParam("oauth_token") String token,
			@RequestParam(value = "oauth_verifier", defaultValue = "verifier") String verifier, WebRequest request) {
		OAuthToken requestToken = (OAuthToken) request.getAttribute(OAUTH_TOKEN_ATTRIBUTE, WebRequest.SCOPE_SESSION);
		if (requestToken == null) {
			return "connect/" + name + "Connect";
		}
		request.removeAttribute(OAUTH_TOKEN_ATTRIBUTE, WebRequest.SCOPE_SESSION);
		ServiceProvider<?> provider = getServiceProvider(name);
		provider.connect(accountIdResolver.resolveAccountId(), new AuthorizedRequestToken(requestToken, verifier));
		postConnect(provider, request);
		// FlashMap.setSuccessMessage("Your Greenhouse account is now connected to your "
		// + provider.getDisplayName() + " account!");
		return "redirect:/connect/" + name;
	}

	@RequestMapping(value = "/connect/{name}", method = RequestMethod.GET, params = "code")
	public String authorizeCallback(@PathVariable String name, @RequestParam("code") String code, WebRequest request) {
		ServiceProvider<?> provider = getServiceProvider(name);
		provider.connect(accountIdResolver.resolveAccountId(), baseCallbackUrl + name, code);
		postConnect(provider, request);
		return "redirect:/connect/" + name;
	}

	/**
	 * Disconnect from the provider.
	 * The member has decided they no longer wish to use the service provider from this application.
	 */
	@RequestMapping(value="/connect/{name}", method=RequestMethod.DELETE)
	public String disconnect(@PathVariable String name) {
		getServiceProvider(name).disconnect(accountIdResolver.resolveAccountId());
		return "redirect:/connect/" + name;
	}

	// internal helpers

	private ServiceProvider<?> getServiceProvider(String name) {
		return serviceProviderFactory.getServiceProvider(name);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void preConnect(ServiceProvider<?> provider, WebRequest request) {
		for (ConnectInterceptor interceptor : interceptingConnectionsTo(provider)) {
			interceptor.preConnect(provider, request);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void postConnect(ServiceProvider<?> provider, WebRequest request) {
		for (ConnectInterceptor interceptor : interceptingConnectionsTo(provider)) {
			interceptor.postConnect(provider, request);
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
	
	private static final String OAUTH_TOKEN_ATTRIBUTE = "oauthToken";

}