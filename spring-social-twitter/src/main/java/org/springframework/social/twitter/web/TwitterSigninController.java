package org.springframework.social.twitter.web;

import java.io.Serializable;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.social.connect.support.ConnectionRepository;
import org.springframework.social.twitter.connect.TwitterServiceProvider;
import org.springframework.social.web.connect.ServiceProviderLocator;
import org.springframework.social.web.connect.SignInControllerGateway;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class TwitterSigninController implements BeanFactoryAware {
	private ServiceProviderLocator serviceProviderLocator;
	private final ConnectionRepository connectionRepository;
	private final SignInControllerGateway signinGateway;

	public TwitterSigninController(ConnectionRepository connectionRepository, SignInControllerGateway signinGateway) {
		this.connectionRepository = connectionRepository;
		this.signinGateway = signinGateway;
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.serviceProviderLocator = new ServiceProviderLocator((ListableBeanFactory) beanFactory);
	}

	@RequestMapping(value = "/signin/twitter", method = RequestMethod.POST)
	public String signInWithProviderBridgeCode(String bridgeCode) {
		TwitterServiceProvider serviceProvider = getServiceProvider("twitter");
		String accessToken = serviceProvider.exchangeBridgeCodeForAccessToken(bridgeCode);
		Serializable accountId = connectionRepository.findAccountIdByConnectionAccessToken("twitter", accessToken);

		if (accountId == null) {
			// TODO: Handle no matching account case
			return "redirect:/";
		}

		signinGateway.signIn(accountId);
		return "redirect:/";
	}

	private TwitterServiceProvider getServiceProvider(String providerId) {
		return (TwitterServiceProvider) serviceProviderLocator.getServiceProvider(providerId);
	}
}
