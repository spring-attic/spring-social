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
package org.springframework.social.twitter.web;

import java.io.Serializable;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.social.connect.ServiceProvider;
import org.springframework.social.connect.oauth1.OAuth1ServiceProvider;
import org.springframework.social.connect.support.ConnectionRepository;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.web.connect.ConnectController;
import org.springframework.social.web.connect.ServiceProviderLocator;
import org.springframework.social.web.connect.SignInService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

/**
 * Controller that performs Twitter's "sign in with Twitter" OAuth flow, as described at http://dev.twitter.com/pages/sign_in_with_twitter.
 * Upon completing the signin flow, looks up the application account ID connected with the Twitter profile and signs in.
 * If no connection can be found, the controller goes to a no-connection view, "redirect:/signup" by default.  
 * @author Craig Walls
 */
@Controller
@RequestMapping("/signin/")
public class TwitterSigninController implements BeanFactoryAware {

	private ServiceProviderLocator serviceProviderLocator;

	private String baseCallbackUrl;
	
	private String baseConnectControllerUrl;

	private final ConnectionRepository connectionRepository;

	private final SignInService signinService;

	private String noConnectionView = "redirect:/signup";

	/**
	 * Constructs the Twitter sign in controller.
	 * 
	 * @param connectionRepository
	 *            a connection repository used to lookup the account ID connected to the Twitter profile.
	 * @param signinService
	 *            the signin strategy used to authenticate the user with the application.
	 * @param applicationUrl
	 *            the base secure URL for this application, used to construct the callback URL passed to the service
	 *            providers at the beginning of the connection process.
	 */
	public TwitterSigninController(ConnectionRepository connectionRepository, SignInService signinService,
			String applicationUrl) {
		this.connectionRepository = connectionRepository;
		this.signinService = signinService;
		this.baseCallbackUrl = applicationUrl + AnnotationUtils.findAnnotation(getClass(), RequestMapping.class).value()[0];
		this.baseConnectControllerUrl = applicationUrl + AnnotationUtils.findAnnotation(ConnectController.class, RequestMapping.class).value()[0];
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.serviceProviderLocator = new ServiceProviderLocator((ListableBeanFactory) beanFactory);
	}

	/**
	 * Sets the view that will be displayed should no connection be found for the Twitter profile.
	 * @param noConnectionView the view to display when no connection can be found
	 */
	public void setNoConnectionView(String noConnectionView) {
		this.noConnectionView = noConnectionView;
	}

	/**
	 * Initiates the sign-in with Twitter flow by fetching a request token and redirecting to Twitter's authentication URL.
	 */
	@RequestMapping(value = TWITTER_PROVIDER_ID, method = RequestMethod.POST)
	public String signin(WebRequest request) {
		// TODO: Address the duplication between this controller and ConnectController
		ServiceProvider<?> provider = getServiceProvider(TWITTER_PROVIDER_ID);
		OAuth1Operations oauth1Ops = ((OAuth1ServiceProvider<?>) provider).getOAuthOperations();
		OAuthToken requestToken = oauth1Ops.fetchNewRequestToken(callbackUrl());
		request.setAttribute(OAUTH_TOKEN_ATTRIBUTE, requestToken, WebRequest.SCOPE_SESSION);
		return "redirect:https://api.twitter.com/oauth/authenticate?oauth_token=" + requestToken.getValue();
	}

	/**
	 * Processes the authentication callback from Twitter after the user authenticates with Twitter. Exchanges the
	 * request token and given verifier for an access token. Uses that access token to lookup a connected account ID to signin with.
	 * If there is no connection for access token, the flow will transition to the no-connection view, "redirect:/signup" by default.
	 */
	@RequestMapping(value = TWITTER_PROVIDER_ID, method = RequestMethod.GET, params = "oauth_token")
	public String oauth1Callback(@RequestParam("oauth_token") String token,
			@RequestParam(value = "oauth_verifier") String verifier, WebRequest request) {
		// TODO: Address the duplication between this controller and ConnectController
		OAuth1ServiceProvider<?> provider = (OAuth1ServiceProvider<?>) getServiceProvider(TWITTER_PROVIDER_ID);
		AuthorizedRequestToken authorizedRequestToken = new AuthorizedRequestToken(extractCachedRequestToken(request), verifier);
		OAuthToken accessToken = provider.getOAuthOperations().exchangeForAccessToken(authorizedRequestToken);
		Serializable accountId = connectionRepository.findAccountIdByConnectionAccessToken(TWITTER_PROVIDER_ID, accessToken.getValue());

		if (accountId == null) {
			Properties deferredConnectionDetails = new Properties();
			deferredConnectionDetails.setProperty("accessToken", accessToken.getValue());
			deferredConnectionDetails.setProperty("accessTokenSecret", accessToken.getSecret());
			deferredConnectionDetails.setProperty("providerId", TWITTER_PROVIDER_ID);
			request.setAttribute(ConnectController.DEFERRED_CONNECTION_DETAILS_ATTRIBUTE, deferredConnectionDetails, WebRequest.SCOPE_SESSION);
			return noConnectionView;
		}

		signinService.signIn(accountId);
		return "redirect:/";
	}

	private ServiceProvider getServiceProvider(String providerId) {
		return serviceProviderLocator.getServiceProvider(providerId);
	}

	private String callbackUrl() {
		return baseCallbackUrl + TWITTER_PROVIDER_ID;
	}
	
	private String deferredConnectionUrl() {
		return baseConnectControllerUrl + TWITTER_PROVIDER_ID + "?deferred";
	}

	private OAuthToken extractCachedRequestToken(WebRequest request) {
		OAuthToken requestToken = (OAuthToken) request.getAttribute(OAUTH_TOKEN_ATTRIBUTE, WebRequest.SCOPE_SESSION);
		request.removeAttribute(OAUTH_TOKEN_ATTRIBUTE, WebRequest.SCOPE_SESSION);
		return requestToken;
	}

	private static final String OAUTH_TOKEN_ATTRIBUTE = "oauthToken";

	private static final String TWITTER_PROVIDER_ID = "twitter";

}
