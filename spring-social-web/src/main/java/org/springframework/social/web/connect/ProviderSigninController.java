package org.springframework.social.web.connect;

import java.io.Serializable;

import org.springframework.social.connect.support.ConnectionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ProviderSigninController {
	private final SignInControllerGateway signinGateway;
	private final ConnectionRepository connectionRepository;

	public ProviderSigninController(ConnectionRepository connectionRepository, SignInControllerGateway signinGateway) {
		this.connectionRepository = connectionRepository;
		this.signinGateway = signinGateway;
	}

	@RequestMapping(value = "/signin/{provider}", method = RequestMethod.POST, params = "accessToken")
	public String signInWithProviderAccessToken(@PathVariable("provider") String provider, String accessToken) {
		Serializable accountId = connectionRepository.findAccountIdByConnectionAccessToken(provider, accessToken);

		if (accountId == null) {
			// TODO: Handle no matching account case
			return "redirect:/";
		}

		signinGateway.signIn(accountId);
		return "redirect:/";
	}

}
