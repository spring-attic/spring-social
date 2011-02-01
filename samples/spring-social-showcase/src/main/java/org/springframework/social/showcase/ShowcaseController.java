package org.springframework.social.showcase;

import java.security.Principal;
import java.util.List;

import javax.inject.Inject;

import org.springframework.social.connect.ServiceProvider;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ShowcaseController {
	private final List<ServiceProvider<?>> serviceProviders;

	@Inject
	public ShowcaseController(List<ServiceProvider<?>> serviceProviders) {
		this.serviceProviders = serviceProviders;
	}

	@RequestMapping("/")
	public String home(Principal user, Model model) {
		for (ServiceProvider<?> serviceProvider : serviceProviders) {
			model.addAttribute(serviceProvider.getId() + "_status", serviceProvider.getConnections(user.getName())
					.size() > 0 ? "Yes" : "No");
		}
		return "home";
	}
}
