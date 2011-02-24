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

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.social.connect.support.ConnectionRepository;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.twitter.connect.TwitterServiceProvider;
import org.springframework.social.web.signin.AbstractProviderSigninController;
import org.springframework.social.web.signin.OAuth1ProviderSignInAttempt;
import org.springframework.social.web.signin.ProviderSignInAttempt;
import org.springframework.social.web.signin.SignInService;
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
@RequestMapping("/signin/twitter")
public class TwitterSigninController extends AbstractProviderSigninController {

	private final TwitterServiceProvider serviceProvider;
	
	private final String callbackUrl;
	
	/**
	 * Constructs the Twitter sign in controller.
	 */
	public TwitterSigninController(TwitterServiceProvider serviceProvider, ConnectionRepository connectionRepository, SignInService signInService, String applicationUrl) {
		super(connectionRepository, signInService);
		this.serviceProvider = serviceProvider;
		this.callbackUrl = applicationUrl + AnnotationUtils.findAnnotation(getClass(), RequestMapping.class).value()[0];
	}

	/**
	 * Initiates the sign-in with Twitter flow by fetching a request token and redirecting to Twitter's authentication URL.
	 */
	@RequestMapping(method=RequestMethod.POST)
	public String signin(WebRequest request) {
		OAuthToken requestToken = serviceProvider.getOAuthOperations().fetchNewRequestToken(callbackUrl);
		request.setAttribute(OAUTH_TOKEN_ATTRIBUTE, requestToken, WebRequest.SCOPE_SESSION);
		return "redirect:https://api.twitter.com/oauth/authenticate?oauth_token=" + requestToken.getValue();
	}

	/**
	 * Processes the authentication callback from Twitter after the user authenticates with Twitter. Exchanges the
	 * request token and given verifier for an access token. Uses that access token to lookup a connected account ID to signin with.
	 * If there is no connection for access token, the flow will transition to the no-connection view, "redirect:/signup" by default.
	 */
	@RequestMapping(method=RequestMethod.GET, params="oauth_token")
	public String oauth1Callback(@RequestParam("oauth_token") String token, @RequestParam(value = "oauth_verifier") String verifier, WebRequest request) {
		AuthorizedRequestToken authorizedRequestToken = new AuthorizedRequestToken(extractCachedRequestToken(request), verifier);
		OAuthToken accessToken = serviceProvider.getOAuthOperations().exchangeForAccessToken(authorizedRequestToken);
		Serializable accountId = getConnectionRepository().findAccountIdByConnectionAccessToken(serviceProvider.getId(), accessToken.getValue());
		if (accountId == null) {
			OAuth1ProviderSignInAttempt signInAttempt = new OAuth1ProviderSignInAttempt(serviceProvider, accessToken.getValue(), accessToken.getSecret());
			request.setAttribute(ProviderSignInAttempt.SESSION_ATTRIBUTE, signInAttempt, WebRequest.SCOPE_SESSION);
			return "redirect:" + getSignupUrl();
		}
		getSignInService().signIn(accountId);
		return "redirect:/";
	}

	private OAuthToken extractCachedRequestToken(WebRequest request) {
		OAuthToken requestToken = (OAuthToken) request.getAttribute(OAUTH_TOKEN_ATTRIBUTE, WebRequest.SCOPE_SESSION);
		request.removeAttribute(OAUTH_TOKEN_ATTRIBUTE, WebRequest.SCOPE_SESSION);
		return requestToken;
	}

	private static final String OAUTH_TOKEN_ATTRIBUTE = "oauthToken";

}
