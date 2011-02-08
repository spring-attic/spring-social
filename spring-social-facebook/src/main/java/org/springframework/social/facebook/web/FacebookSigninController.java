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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.social.connect.support.ConnectionRepository;
import org.springframework.social.web.connect.SignInControllerGateway;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/signin/")
public class FacebookSigninController {

	private static final String FACEBOOK_PROVIDER_ID = "facebook";

	private final ConnectionRepository connectionRepository;

	private final SignInControllerGateway signinGateway;

	private String noConnectionView = "redirect:/signup";

	private final String apiKey;

	public FacebookSigninController(ConnectionRepository connectionRepository, SignInControllerGateway signinGateway,
			String apiKey) {
		this.connectionRepository = connectionRepository;
		this.signinGateway = signinGateway;
		this.apiKey = apiKey;
	}

	/**
	 * Sets the view that will be displayed should no connection be found for the Twitter profile.
	 * 
	 * @param noConnectionView
	 *            the view to display when no connection can be found
	 */
	public void setNoConnectionView(String noConnectionView) {
		this.noConnectionView = noConnectionView;
	}

	@RequestMapping(value = FACEBOOK_PROVIDER_ID, method = POST)
	public String signin(HttpServletRequest request) {
		String accessToken = resolveAccessTokenValue(request);
		Serializable accountId = connectionRepository.findAccountIdByConnectionAccessToken(FACEBOOK_PROVIDER_ID,
				accessToken);

		if (accountId == null) {
			return noConnectionView;
		}

		signinGateway.signIn(accountId);
		return "redirect:/";
	}

	// TODO: What follows is largely duplicated from FacebookWebArgumentResolver. I put it here because...
	// 1. I don't want to force a developer to register the web argument resolver to use this controller
	// 2. The value of FacebookWebArgumentResolver and its annotations is now questionable. Those annotation truly only
	//    existed for the sake of Greenhouse's Facebook signin controller. While they may be useful outside of this
	//    controller, the service provider framework's handling of access tokens is better.
	// Therefore, consider getting rid of FacebookWebArgumentResolver and its annotations

	private String resolveAccessTokenValue(HttpServletRequest request) {
		Map<String, String> cookieData = getFacebookCookieData(request.getCookies());
		String accessToken = cookieData.get("access_token");
		if (accessToken != null) {
			accessToken = accessToken.replaceAll("\\%7C", "|");
		}

		if (accessToken != null) {
			return accessToken;
		}

		throw new IllegalStateException("FacebookSigninController cannot find an access token in the Facebook cookie. "
				+ "Ensure that Facebook authentication has taken place before arriving at this state.");
	}

	private Map<String, String> getFacebookCookieData(Cookie[] cookies) {
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("fbs_" + apiKey)) {
					return extractDataFromCookie(cookie.getValue());
				}
			}
		}

		return Collections.<String, String> emptyMap();
	}

	private Map<String, String> extractDataFromCookie(String cookieValue) {
		HashMap<String, String> data = new HashMap<String, String>();
		String[] fields = cookieValue.split("\\&");
		for (String field : fields) {
			String[] keyValue = field.split("\\=");
			data.put(keyValue[0], keyValue[1]);
		}
		return data;
	}
}
