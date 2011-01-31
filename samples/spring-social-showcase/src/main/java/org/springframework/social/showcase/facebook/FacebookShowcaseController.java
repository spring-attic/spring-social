package org.springframework.social.showcase.facebook;

import java.security.Principal;

import javax.inject.Inject;

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
			FacebookProfile userProfile = facebookProvider.getConnections(user.getName()).get(0).getServiceApi()
					.getUserProfile();
			model.addAttribute("fbUser", userProfile);
			return "facebook/facebook";
		}
		return "redirect:/connect/facebook";
	}

	@RequestMapping(value = "/facebook/wall", method = RequestMethod.POST)
	public String postToWall(Principal user, String message) {
		facebookProvider.getConnections(user.getName()).get(0).getServiceApi().updateStatus(message);
		return "redirect:/facebook";
	}
}
