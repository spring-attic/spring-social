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
package org.springframework.social.samples.github;

import javax.inject.Inject;

import org.springframework.social.github.GitHubOperations;
import org.springframework.social.github.GitHubUserProfile;
import org.springframework.social.github.provider.GitHubServiceProvider;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ShowcaseController {
	private final GitHubServiceProvider gitHubProvider;

	@Inject
	public ShowcaseController(GitHubServiceProvider gitHubProvider) {
		this.gitHubProvider = gitHubProvider;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Model model) {
		if (gitHubProvider.isConnected(1)) {
			String gitHubId = gitHubProvider.getProviderAccountId(1);
			model.addAttribute("gitHubId", gitHubId);

			GitHubOperations gitHub = gitHubProvider.getServiceOperations(1);
			gitHub.getProfileId();

			GitHubUserProfile user = gitHub.getUserProfile();
			model.addAttribute("gitHubUser", user);

			return "home";
		}
		return "redirect:/connect/github";
	}

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public String register(ShowcaseUser user) {
		// In a more complete example, the user data would be persisted somehow
		System.out.println("Pretending to register the user: " + user.getUsername());

		// Redirect to the ConnectController to complete the post-registration
		// connection
		return "redirect:/connect/github/register";
	}
}
