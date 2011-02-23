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

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.social.connect.support.ConnectionRepository;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.web.connect.ConnectController;
import org.springframework.social.web.connect.SignInControllerService;
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

	private final SignInControllerService signinService;

	private String noConnectionView = "redirect:/signup";

	private final String apiKey;

	private final String appSecret;

	private String baseConnectControllerUrl;

	/**
	 * Constructs the FacebookSigninController.
	 * 
	 * @param connectionRepository
	 *            a connection repository used to lookup the account ID connected to the Facebook profile.
	 * @param signinService
	 *            the signin strategy used to authenticate the user with the application.
	 * @param apiKey
	 *            the Facebook API key used to retrieve the Facebook cookie containing the access token.
	 */
	public FacebookSigninController(ConnectionRepository connectionRepository, SignInControllerService signinService,
			String applicationUrl, String apiKey, String appSecret) {
		this.connectionRepository = connectionRepository;
		this.signinService = signinService;
		// TODO: The Facebook service provider could be looked up here and could expose its API key as a
		// property. Then this controller could just get the API key and app secret from the provider.
		this.apiKey = apiKey;
		this.appSecret = appSecret;
		this.baseConnectControllerUrl = applicationUrl + AnnotationUtils.findAnnotation(ConnectController.class, RequestMapping.class).value()[0];
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
			request.getSession().setAttribute(ConnectController.DEFERRED_CONNECTION_ACCESS_TOKEN_ATTRIBUTE, new AccessGrant(accessToken, null));
			return noConnectionView + "?deferredConnectionUrl=" + deferredConnectionUrl();
		}

		signinService.signIn(accountId);
		return "redirect:/";
	}
	
	private String deferredConnectionUrl() {
		return baseConnectControllerUrl + FACEBOOK_PROVIDER_ID + "?deferred";
	}

	private String resolveAccessTokenValue(HttpServletRequest request) {
		Map<String, String> cookieData = FacebookCookieParser.getFacebookCookieData(request.getCookies(), apiKey,
				appSecret);
		String accessToken = cookieData.get("access_token");
		if (accessToken != null) {
			return accessToken;
		}

		throw new IllegalStateException("FacebookSigninController cannot find an access token in the Facebook cookie.");
	}

	private static final String FACEBOOK_PROVIDER_ID = "facebook";

}
