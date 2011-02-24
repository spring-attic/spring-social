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

import javax.servlet.http.HttpServletRequest;

import org.springframework.social.connect.support.ConnectionRepository;
import org.springframework.social.facebook.connect.FacebookServiceProvider;
import org.springframework.social.web.signin.OAuth2ProviderSignInAccount;
import org.springframework.social.web.signin.ProviderSignInAccount;
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
public class FacebookSigninController {

	private final FacebookServiceProvider serviceProvider;
	
	private final ConnectionRepository connectionRepository;

	private final SignInService signinService;

	private String signupUrl = "/signup";

	/**
	 * Constructs the FacebookSigninController.
	 * @param connectionRepository a connection repository used to lookup the account ID connected to the Facebook profile.
	 */
	public FacebookSigninController(FacebookServiceProvider serviceProvider, ConnectionRepository connectionRepository, SignInService signinService) {
		this.serviceProvider = serviceProvider;
		this.connectionRepository = connectionRepository;
		this.signinService = signinService;
	}

	public void setSignupUrl(String signupUrl) {
		this.signupUrl = signupUrl;
	}

	/**
	 * Retrieves the user's Facebook access token from a cookie written after a successful login using Facebook's &lt;fb:login-button&gt; tag.
	 * Uses that access token to lookup the connected account ID and attempts to authenticate to the application for that account.
	 * If there is no connection for access token, the flow will transition to the no-connection view, "redirect:/signup" by default.
	 */
	@RequestMapping(method=RequestMethod.POST)
	public String signin(HttpServletRequest request) {
		String accessToken = resolveAccessTokenValue(request);
		Serializable accountId = connectionRepository.findAccountIdByConnectionAccessToken(serviceProvider.getId(), accessToken);
		if (accountId == null) {
			OAuth2ProviderSignInAccount signInAccount = new OAuth2ProviderSignInAccount(serviceProvider, accessToken);
			request.getSession().setAttribute(ProviderSignInAccount.SESSION_ATTRIBUTE, signInAccount);
			return "redirect:" + signupUrl;
		}
		signinService.signIn(accountId);
		return "redirect:/";
	}
	
	private String resolveAccessTokenValue(HttpServletRequest request) {
		Map<String, String> cookieData = FacebookCookieParser.getFacebookCookieData(request.getCookies(), serviceProvider.getAppId(), serviceProvider.getAppSecret());
		String accessToken = cookieData.get("access_token");
		if (accessToken != null) {
			return accessToken;
		} else {
			throw new IllegalStateException("FacebookSigninController cannot find an access token in the Facebook cookie.");
		}
	}

}