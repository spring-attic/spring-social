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
package org.springframework.social.showcase.facebook;

import java.security.Principal;

import javax.inject.Inject;

import org.springframework.social.facebook.FacebookOperations;
import org.springframework.social.facebook.FacebookProfile;
import org.springframework.social.facebook.connect.FacebookServiceProvider;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class FacebookShowcaseController {
	private final FacebookServiceProvider facebookProvider;

	@Inject
	public FacebookShowcaseController(FacebookServiceProvider facebookServiceProvider) {
		this.facebookProvider = facebookServiceProvider;
	}

	@RequestMapping(value = "/facebook", method = RequestMethod.GET)
	public String home(Principal user, Model model) {
		if (facebookProvider.isConnected(user.getName())) {
			FacebookProfile userProfile = getFacebookApi(user).getUserProfile();
			model.addAttribute("fbUser", userProfile);
			return "facebook/facebook";
		}
		return "redirect:/connect/facebook";
	}

	@RequestMapping(value = "/facebook/wall", method = RequestMethod.POST)
	public String postToWall(Principal user, String message) {
		getFacebookApi(user).updateStatus(message);
		return "redirect:/facebook";
	}

	private FacebookOperations getFacebookApi(Principal user) {
		return facebookProvider.getConnections(user.getName()).get(0).getServiceApi();
	}
}
