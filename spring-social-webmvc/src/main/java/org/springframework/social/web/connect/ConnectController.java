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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.GenericTypeResolver;
import org.springframework.security.oauth.client.oauth1.AuthorizedRequestToken;
import org.springframework.security.oauth.client.oauth1.OAuthToken;
import org.springframework.security.oauth.client.oauth2.AccessGrant;
import org.springframework.social.provider.AuthorizationProtocol;
import org.springframework.social.provider.ServiceProvider;
import org.springframework.social.provider.ServiceProviderConnection;
import org.springframework.social.provider.ServiceProviderFactory;
import org.springframework.social.provider.oauth1.OAuth1ServiceProvider;
import org.springframework.social.provider.oauth2.OAuth2ServiceProvider;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

/**
 * <p>Generic UI controller for managing the account connection process.  Supported connection flow for OAuth 1 providers:</p>
 * <ul>
 * GET /connect/{name}  - Get a web page showing Account connection status to provider {name}.<br/>
 * POST /connect/{name} - Initiate an Account connection with provider {name}.<br/>
 * GET /connect/{name}?oauth_token - Receive provider {name} authorization callback and complete Account connection.<br/>
 * DELETE /connect/{name} - Disconnect Account from provider {name}.<br/>
 * </ul>
 * 
 * <p>The connection flow for OAuth 2 providers is subtly different:</p>
 * <ul>
 * GET /connect/{name}  - Get a web page showing Account connection status to provider {name}.<br/>
 * POST /connect/{name} - Initiate an Account connection with provider {name}.<br/>
 * GET /connect/{name}?code - Receive provider {name} authorization callback and complete Account connection.<br/>
 * DELETE /connect/{name} - Disconnect Account from provider {name}.<br/>
 * </ul>
 * 
 * <p>Also supports a register-then-connect flow. This flow is similar to the connect flows, except that after
 * receiving an access token from the provider, the flow breaks away to an application registration screen, allowing
 * a new user to register with the application with data retrieved from their provider profile. After registration,
 * the application may resume the flow so that a connection may be completed between the member account and their
 * provider profile. The register-then-connect flow is:</p>
 * 
 * <p>The connection flow for OAuth 2 providers is subtly different:</p>
 * <ul>
 * GET /connect/{name}  - Get a web page showing Account connection status to provider {name}.<br/>
 * POST /connect/{name}/register - Initiate an Account connection with registration flow with provider {name}.<br/>
 * GET /connect/{name}?oauth_token or GET /connect/{name}?code - Receive provider {name} authorization callback and complete Account connection.
 *    The flow breaks away to the application registration view at this point.<br/>
 * GET /connect/{name}/register - Resumes the connection flow, establishing the connection.<br/>
 * DELETE /connect/{name} - Disconnect Account from provider {name}.<br/>
 * </ul>
 * 
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
		// TODO: It seems that since we always configure a service provider as a
		// bean in Spring, whether it be using the namespace or by invoking
		// getServiceProvider() on the JDBC service provider factory, that
		// wiring the service provider factory into ConnectController isn't
		// necessary. Instead, ConnectController just needs a way of finding one
		// of those service provider beans by its name. If so, then the SPF
		// should be removed as a constructor argument. This would not only
		// simplify configuration, but would also prevent 2 instances of SPs
		// from being created (one created when ConnectController asks for it
		// and one created in the Spring context).
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
	public String connect(@PathVariable String name, WebRequest request,
			@RequestParam(required = false, defaultValue = "") String scope) {
		ServiceProvider<?> provider = getServiceProvider(name);
		preConnect(provider, request);
		Map<String, String> authorizationParameters = new HashMap<String, String>();
		if (provider.getAuthorizationProtocol() == AuthorizationProtocol.OAUTH_1) {
			OAuth1ServiceProvider<?> oauth1Provider = (OAuth1ServiceProvider<?>) provider;
			OAuthToken requestToken = oauth1Provider.getOAuth1Operations().fetchNewRequestToken(baseCallbackUrl + name);
			request.setAttribute(OAUTH_TOKEN_ATTRIBUTE, requestToken, WebRequest.SCOPE_SESSION);
			authorizationParameters.put("requestToken", requestToken.getValue());
			return "redirect:" + oauth1Provider.getOAuth1Operations().buildAuthorizeUrl(requestToken.getValue());
		} else {
			OAuth2ServiceProvider<?> oauth2Provider = (OAuth2ServiceProvider<?>) provider;
			return "redirect:" + oauth2Provider.getOAuth2Operations().buildAuthorizeUrl(baseCallbackUrl + name, scope);
		}
	}

	/**
	 * Initiates a registration/connection flow. Commences the process of
	 * establishing a connection to the provider on behalf of the member, just
	 * as with the regular connection flow. The main difference here is that
	 * once an access token is retrieved, the flow will break away, offering the
	 * user an application registration screen. Once the user has registered
	 * with the application, the flow can be resumed and the connection will be
	 * created.
	 */
	@RequestMapping(value = "/connect/{name}/register", method = RequestMethod.POST)
	public String register(@PathVariable String name, WebRequest request,
			@RequestParam(required = false, defaultValue = "") String scope) {
		request.setAttribute(REGISTRATION_FLOW_ATTRIBUTE, true, WebRequest.SCOPE_SESSION);
		return connect(name, request, scope);
	}

	/**
	 * Process the authorization callback from an OAuth 1 service provider.
	 * Called after the member authorizes the connection, generally done by
	 * having he or she click "Allow" in their web browser at the provider's
	 * site. On authorization verification, connects the member's local account
	 * to the account they hold at the service provider. Removes the request
	 * token from the session since it is no longer valid after the connection
	 * is established.
	 */
	@RequestMapping(value="/connect/{name}", method=RequestMethod.GET, params="oauth_token")
	public String authorizeCallback(@PathVariable String name, @RequestParam("oauth_token") String token,
			@RequestParam(value = "oauth_verifier", defaultValue = "verifier") String verifier, WebRequest request) {
		OAuthToken requestToken = (OAuthToken) request.getAttribute(OAUTH_TOKEN_ATTRIBUTE, WebRequest.SCOPE_SESSION);
		if (requestToken == null) {
			return "connect/" + name + "Connect";
		}
		request.removeAttribute(OAUTH_TOKEN_ATTRIBUTE, WebRequest.SCOPE_SESSION);
		AuthorizedRequestToken authorizedRequestToken = new AuthorizedRequestToken(requestToken, verifier);
		OAuth1ServiceProvider<?> provider = (OAuth1ServiceProvider<?>) getServiceProvider(name);
		OAuthToken accessToken = provider.getOAuth1Operations().exchangeForAccessToken(authorizedRequestToken);
		// TODO : Come back to reimplement the registration flow
//		if (request.getAttribute(REGISTRATION_FLOW_ATTRIBUTE, WebRequest.SCOPE_SESSION) != null) {
//			return holdAccessGrantAndGoToRegistration(name, request, provider, accessToken);
//		}
		provider.connect(accountIdResolver.resolveAccountId(), accessToken);
		postConnect(provider, request);
		// FlashMap.setSuccessMessage("Your account is now connected to your "
		// + provider.getDisplayName() + " account!");
		return "redirect:/connect/" + name;
	}

	/**
	 * Process the authorization callback from an OAuth 2 service provider.
	 * Called after the member authorizes the connection, generally done by
	 * having he or she click "Allow" in their web browser at the provider's
	 * site. On authorization verification, connects the member's local account
	 * to the account they hold at the service provider.
	 */
	@RequestMapping(value = "/connect/{name}", method = RequestMethod.GET, params = "code")
	public String authorizeCallback(@PathVariable String name, @RequestParam("code") String code, WebRequest request) {
		OAuth2ServiceProvider<?> provider = (OAuth2ServiceProvider<?>) getServiceProvider(name);
		String redirectUri = baseCallbackUrl + name;
		AccessGrant accessGrant = provider.getOAuth2Operations().exchangeForAccess(code, redirectUri);
		// TODO : Come back to reimplement the registration flow
//		if (request.getAttribute(REGISTRATION_FLOW_ATTRIBUTE, WebRequest.SCOPE_SESSION) != null) {
//			return holdAccessGrantAndGoToRegistration(name, request, provider, accessGrant);
//		}
		provider.connect(accountIdResolver.resolveAccountId(), accessGrant);
		postConnect(provider, request);
		return "redirect:/connect/" + name;
	}

	// TODO : Come back to reimplement the registration flow
//	private String holdAccessGrantAndGoToRegistration(String name, WebRequest request, ServiceProvider<?> provider,
//			AccessGrant accessGrant) {
//		request.removeAttribute(REGISTRATION_FLOW_ATTRIBUTE, WebRequest.SCOPE_SESSION);
//		request.setAttribute(name + "UserProfile", provider.getProviderUserProfile(accessGrant),
//				WebRequest.SCOPE_REQUEST);
//		request.setAttribute(ACCESS_TOKEN_ATTRIBUTE + name, accessGrant, WebRequest.SCOPE_SESSION);
//		return "connect/" + name + "Register";
//	}

	/**
	 * Completes the connection process after registration. After the
	 * application successfully registers a user, it should redirect to this
	 * handler method's URL to create the connection between the application
	 * account and the provider account.
	 */
	@RequestMapping(value = "/connect/{name}/register", method = RequestMethod.GET)
	public String completeRegistrationConnection(@PathVariable String name, WebRequest request) {
		Object storedToken = request.getAttribute(ACCESS_TOKEN_ATTRIBUTE + name, WebRequest.SCOPE_SESSION);
		if (storedToken != null) {
			ServiceProvider<?> provider = getServiceProvider(name);
			if (provider.getAuthorizationProtocol() == AuthorizationProtocol.OAUTH_1) {
				OAuth1ServiceProvider<?> oauth1Provider = (OAuth1ServiceProvider<?>) provider;
				OAuthToken accessToken = (OAuthToken) storedToken;
				oauth1Provider.connect(accountIdResolver.resolveAccountId(), accessToken);
			} else if (provider.getAuthorizationProtocol() == AuthorizationProtocol.OAUTH_2) {
				OAuth2ServiceProvider<?> oauth2Provider = (OAuth2ServiceProvider<?>) provider;
				AccessGrant accessGrant = (AccessGrant) storedToken;
				oauth2Provider.connect(accountIdResolver.resolveAccountId(), accessGrant);
			}
			postConnect(provider, request);
		}
		return "redirect:/connect/" + name;
	}

	/**
	 * Disconnect from the provider.
	 * The member has decided they no longer wish to use the service provider from this application.
	 */
	@RequestMapping(value="/connect/{name}", method=RequestMethod.DELETE)
	public String disconnect(@PathVariable String name) {
		ServiceProvider<?> serviceProvider = getServiceProvider(name);
		List<?> connections = serviceProvider.getConnections(accountIdResolver.resolveAccountId());
		for (Object object : connections) {
			((ServiceProviderConnection<?>) object).disconnect();
		}
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
	private static final String REGISTRATION_FLOW_ATTRIBUTE = "registrationFlow";
	private static final String ACCESS_TOKEN_ATTRIBUTE = "accessToken_";
}