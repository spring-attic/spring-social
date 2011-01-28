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
package org.springframework.social.samples.twitter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.social.connect.ServiceProviderConnection;
import org.springframework.social.twitter.TwitterOperations;
import org.springframework.social.twitter.provider.TwitterServiceProvider;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class TweetController {

	private final TwitterServiceProvider twitterProvider;

	@Inject
	public TweetController(TwitterServiceProvider twitterProvider) {
		this.twitterProvider = twitterProvider;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Model model) {
		List<ServiceProviderConnection<TwitterOperations>> connections = twitterProvider.getConnections(1);
		List<String> connectionNames = new ArrayList<String>();
		for (ServiceProviderConnection<TwitterOperations> serviceProviderConnection : connections) {
			connectionNames.add(serviceProviderConnection.getServiceApi().getProfileId());
		}

		if (connections.size() > 0) {
			model.addAttribute("connections", connectionNames);
			model.addAttribute(new TweetForm());
			return "tweet";
		}

		return "redirect:/connect/twitter";
	}

	@RequestMapping(value = "/tweet", method = RequestMethod.POST)
	public String postTweet(TweetForm tweetForm) {
		List<ServiceProviderConnection<TwitterOperations>> connections = twitterProvider.getConnections(1);
		for (ServiceProviderConnection<TwitterOperations> connection : connections) {
			TwitterOperations twitter = connection.getServiceApi();
			if (tweetForm.isTweetToAll() || twitter.getProfileId().equals(tweetForm.getScreenName())) {
				twitter.updateStatus(tweetForm.getMessage());
			}
		}

		return "redirect:/";
	}
}
