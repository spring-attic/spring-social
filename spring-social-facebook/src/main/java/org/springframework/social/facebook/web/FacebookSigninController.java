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
package org.springframework.social.facebook.web;

import java.io.Serializable;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import org.springframework.social.connect.oauth2.OAuth2ServiceProvider;
import org.springframework.social.connect.support.ConnectionRepository;
import org.springframework.social.facebook.connect.FacebookServiceProvider;
import org.springframework.social.web.signin.AbstractProviderSigninController;
import org.springframework.social.web.signin.OAuth2ProviderSignInAttempt;
import org.springframework.social.web.signin.ProviderSignInAttempt;
import org.springframework.social.web.signin.SignInService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller that enables a user to authenticate to an application by signing into Facebook.
 * @author Craig Walls
 */
@Controller
@RequestMapping("/signin/facebook")
public class FacebookSigninController extends AbstractProviderSigninController {

	private final Provider<? extends OAuth2ServiceProvider<?>> serviceProviderLocator;
	
	private final FacebookServiceProvider serviceProvider;
	
	/**
	 * Constructs the FacebookSigninController.
	 * @param connectionRepository a connection repository used to lookup the account ID connected to the Facebook profile.
	 */
	@Inject
	public FacebookSigninController(Provider<FacebookServiceProvider> serviceProviderLocator, ConnectionRepository connectionRepository, @SuppressWarnings("rawtypes") SignInService signInService) {
		super(connectionRepository, signInService);
		this.serviceProviderLocator = serviceProviderLocator;
		this.serviceProvider = serviceProviderLocator.get();
	}

	/**
	 * Retrieves the user's Facebook access token from a cookie written after a successful login using Facebook's &lt;fb:login-button&gt; tag.
	 * Uses that access token to lookup the connected account ID and attempts to authenticate to the application for that account.
	 * If there is no connection for access token, the flow will transition to the no-connection view, "redirect:/signup" by default.
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(method=RequestMethod.POST)
	public String signin(HttpServletRequest request) {
		String accessToken = getAccessTokenCookieValue(request);
		Serializable accountId = getConnectionRepository().findAccountIdByConnectionAccessToken(serviceProvider.getId(), accessToken);
		if (accountId == null) {
			OAuth2ProviderSignInAttempt signInAttempt = new OAuth2ProviderSignInAttempt(serviceProviderLocator, accessToken);
			request.getSession().setAttribute(ProviderSignInAttempt.SESSION_ATTRIBUTE, signInAttempt);
			return "redirect:" + getSignupUrl();
		}
		getSignInService().signIn(accountId);
		return "redirect:/";
	}

	// internal helpers
	
	private String getAccessTokenCookieValue(HttpServletRequest request) {
		Map<String, String> cookieData = FacebookCookieParser.getFacebookCookieData(request.getCookies(), serviceProvider.getAppId(), serviceProvider.getAppSecret());
		String accessToken = cookieData.get("access_token");
		if (accessToken != null) {
			return accessToken;
		} else {
			throw new IllegalStateException("FacebookSigninController cannot find an access token in the Facebook cookie.");
		}
	}

}