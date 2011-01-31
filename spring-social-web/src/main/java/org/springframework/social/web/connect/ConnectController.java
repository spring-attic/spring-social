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

import java.io.Serializable;
import java.security.Principal;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.social.connect.ServiceProvider;
import org.springframework.social.connect.ServiceProviderConnection;
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
 * <li>GET /connect/{providerId}  - Get a web page showing connection status to {providerId}.</li>
 * <li>POST /connect/{providerId} - Initiate an connection with {providerId}.</li>
 * <li>GET /connect/{providerId}?oauth_token||code - Receive {providerId} authorization callback and establish the connection.</li>
 * <li>DELETE /connect/{providerId} - Disconnect from {providerId}.</li>
 * </ul>
 * @author Keith Donald
 * @author Craig Walls
 */
@Controller
@RequestMapping("/connect/")
public class ConnectController implements BeanFactoryAware {
	
	private ServiceProviderLocator serviceProviderLocator;

	private String baseCallbackUrl;
	
	private MultiValueMap<Class<?>, ConnectInterceptor<?>> interceptors;

	/**
	 * Constructs a ConnectController.
	 * @param serviceProviderLocator the factory that loads the ServiceProviders members wish to connect to
	 * @param applicationUrl the base secure URL for this application, used to construct the callback URL passed to the service providers at the beginning of the connection process.
	 */
	public ConnectController(String applicationUrl) {
		this.baseCallbackUrl = applicationUrl + AnnotationUtils.findAnnotation(getClass(), RequestMapping.class).value()[0];
		this.interceptors = new LinkedMultiValueMap<Class<?>, ConnectInterceptor<?>>();
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.serviceProviderLocator = new ServiceProviderLocator((ListableBeanFactory) beanFactory);
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
	@RequestMapping(value="{providerId}", method=RequestMethod.GET)
	public String connect(@PathVariable String providerId, Principal user) {
		String baseViewPath = "connect/" + providerId;
		if (getServiceProvider(providerId).isConnected(accountId(user))) {
			return baseViewPath + "Connected";
		} else {
			return baseViewPath + "Connect";
		}
	}

	/**
	 * Process a connect form submission by commencing the process of establishing a connection to the provider on behalf of the member.
	 * Fetches a new request token from the provider, temporarily stores it in the session, then redirects the member to the provider's site for authorization.
	 */
	@RequestMapping(value="{providerId}", method=RequestMethod.POST)
	public String connect(@PathVariable String providerId, @RequestParam(required=false) String scope,  WebRequest request) {
		ServiceProvider<?> provider = getServiceProvider(providerId);
		preConnect(provider, request);
		if (provider instanceof OAuth1ServiceProvider) {
			OAuth1Operations oauth1Ops = ((OAuth1ServiceProvider<?>) provider).getOAuth1Operations();
			OAuthToken requestToken = oauth1Ops.fetchNewRequestToken(callbackUrl(providerId));
			request.setAttribute(OAUTH_TOKEN_ATTRIBUTE, requestToken, WebRequest.SCOPE_SESSION);
			return "redirect:" + oauth1Ops.buildAuthorizeUrl(requestToken.getValue());
		} else {
			return "redirect:" + ((OAuth2ServiceProvider<?>) provider).getOAuth2Operations().buildAuthorizeUrl(callbackUrl(providerId), scope);
		}
	}

	/**
	 * Process the authorization callback from an OAuth 1 service provider.
	 * Called after the member authorizes the connection, generally done by having he or she click "Allow" in their web browser at the provider's site.
	 * On authorization verification, connects the member's local account to the account they hold at the service provider
	 * Removes the request token from the session since it is no longer valid after the connection is established.
	 */
	@RequestMapping(value="{providerId}", method=RequestMethod.GET, params="oauth_token")
	public String oauth1Callback(@PathVariable String providerId, @RequestParam("oauth_token") String token, @RequestParam(value="oauth_verifier") String verifier, WebRequest request) {
		OAuth1ServiceProvider<?> provider = (OAuth1ServiceProvider<?>) getServiceProvider(providerId);
		AuthorizedRequestToken authorizedRequestToken = new AuthorizedRequestToken(extractCachedRequestToken(request), verifier);
		ServiceProviderConnection<?> connection = provider.connect(accountId(request.getUserPrincipal()), provider.getOAuth1Operations().exchangeForAccessToken(authorizedRequestToken));
		postConnect(provider, connection, request);
		return "redirect:/connect/" + providerId;
	}

	/**
	 * Process the authorization callback from an OAuth 2 service provider.
	 * Called after the member authorizes the connection, generally done by having he or she click "Allow" in their web browser at the provider's site.
	 * On authorization verification, connects the member's local account to the account they hold at the service provider.
	 */
	@RequestMapping(value="{providerId}", method=RequestMethod.GET, params="code")
	public String oauth2Callback(@PathVariable String providerId, @RequestParam("code") String code, WebRequest request) {
		OAuth2ServiceProvider<?> provider = (OAuth2ServiceProvider<?>) getServiceProvider(providerId);
		AccessGrant accessGrant = provider.getOAuth2Operations().exchangeForAccess(code, callbackUrl(providerId));
		ServiceProviderConnection<?> connection = provider.connect(accountId(request.getUserPrincipal()), accessGrant);
		postConnect(provider, connection, request);
		return "redirect:/connect/" + providerId;
	}

	/**
	 * Disconnect from the provider.
	 * The member has decided they no longer wish to use the service provider from this application.
	 */
	@RequestMapping(value="{providerId}", method=RequestMethod.DELETE)
	public String disconnect(@PathVariable String providerId, Principal user) {
		ServiceProvider provider = getServiceProvider(providerId);
		List<ServiceProviderConnection> connections = provider.getConnections(accountId(user));
		for (ServiceProviderConnection connection : connections) {
			connection.disconnect();
		}
		return "redirect:/connect/" + providerId;
	}

	// internal helpers

	private ServiceProvider getServiceProvider(String providerId) {
		return serviceProviderLocator.getServiceProvider(providerId);
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

	private Serializable accountId(Principal user) {
		return user.getName();
	}
	
	private String callbackUrl(String providerId) {
		return baseCallbackUrl + providerId;
	}

	private OAuthToken extractCachedRequestToken(WebRequest request) {
		OAuthToken requestToken = (OAuthToken) request.getAttribute(OAUTH_TOKEN_ATTRIBUTE, WebRequest.SCOPE_SESSION);
		request.removeAttribute(OAUTH_TOKEN_ATTRIBUTE, WebRequest.SCOPE_SESSION);
		return requestToken;
	}
	
	private static final String OAUTH_TOKEN_ATTRIBUTE = "oauthToken";
	
}