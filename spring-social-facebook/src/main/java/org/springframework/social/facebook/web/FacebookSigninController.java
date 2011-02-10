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

import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.io.Serializable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.social.connect.support.ConnectionRepository;
import org.springframework.social.web.connect.SignInControllerGateway;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller that enables a user to authenticate to an application by signing into Facebook.
 * @author Craig Walls
 */
@Controller
@RequestMapping("/signin/")
public class FacebookSigninController {

	private final ConnectionRepository connectionRepository;

	private final SignInControllerGateway signinGateway;

	private String noConnectionView = "redirect:/signup";

	private final String apiKey;

	/**
	 * Constructs the FacebookSigninController.
	 * @param connectionRepository a connection repository used to lookup the account ID connected to the Facebook profile.
	 * @param signinGateway the signin strategy used to authenticate the user with the application.
	 * @param apiKey the Facebook API key used to retrieve the Facebook cookie containing the access token.
	 */
	public FacebookSigninController(ConnectionRepository connectionRepository, SignInControllerGateway signinGateway,
			String apiKey) {
		this.connectionRepository = connectionRepository;
		this.signinGateway = signinGateway;
		// TODO: This key is used to lookup the Facebook cookie. But I wonder if it's necessary. Shouldn't there only be
		// one"fbs_*" cookie in any given application? If so, then just look for any cookie that starts with "fbs_" and
		// use it.
		// Alternatively, the Facebook service provider could be looked up here and could expose its API key as a
		// property. Then this controller could just get the API key from the provider.
		this.apiKey = apiKey;
	}

	/**
	 * Sets the view that will be displayed should no connection be found for the Twitter profile.
	 * 
	 * @param noConnectionView the view to display when no connection can be found
	 */
	public void setNoConnectionView(String noConnectionView) {
		this.noConnectionView = noConnectionView;
	}

	/**
	 * Retrieves the user's Facebook access token from a cookie written after a successful login using Facebook's &lt;fb:login-button&gt; tag.
	 * Uses that access token to lookup the connected account ID and attempts to authenticate to the application for that account.
	 * If there is no connection for access token, the flow will transition to the no-connection view, "redirect:/signup" by default.
	 */
	@RequestMapping(value = FACEBOOK_PROVIDER_ID, method = POST)
	public String signin(HttpServletRequest request) {
		String accessToken = resolveAccessTokenValue(request);
		Serializable accountId = connectionRepository.findAccountIdByConnectionAccessToken(FACEBOOK_PROVIDER_ID, accessToken);

		if (accountId == null) {
			return noConnectionView;
		}

		signinGateway.signIn(accountId);
		return "redirect:/";
	}

	private String resolveAccessTokenValue(HttpServletRequest request) {
		Map<String, String> cookieData = FacebookCookieParser.getFacebookCookieData(request.getCookies(), apiKey);
		String accessToken = cookieData.get("access_token");
		if (accessToken != null) {
			return accessToken;
		}

		throw new IllegalStateException("FacebookSigninController cannot find an access token in the Facebook cookie.");
	}

	private static final String FACEBOOK_PROVIDER_ID = "facebook";

}
