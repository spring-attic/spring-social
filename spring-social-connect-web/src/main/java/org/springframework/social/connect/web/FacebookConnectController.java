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
package org.springframework.social.connect.web;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.GenericTypeResolver;
import org.springframework.security.core.Authentication;
import org.springframework.social.connect.ServiceProvider;
import org.springframework.social.facebook.FacebookAccessToken;
import org.springframework.social.facebook.FacebookOperations;
import org.springframework.social.facebook.FacebookUserId;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

/**
 * Facebook-specific UI Controller for connecting member Accounts with Facebook.
 * 
 * @author Keith Donald
 * @author Craig Walls
 */
@Controller
public class FacebookConnectController {

	private final ServiceProvider<FacebookOperations> facebookProvider;

	private MultiValueMap<Class<?>, ConnectInterceptor<?>> interceptors;

	public FacebookConnectController(ServiceProvider<FacebookOperations> facebookProvider) {
		this.facebookProvider = facebookProvider;
		this.interceptors = new LinkedMultiValueMap<Class<?>, ConnectInterceptor<?>>();
	}

	/**
	 * Configure the list of interceptors that should receive callbacks during
	 * the connection process.
	 */
	public void setInterceptors(List<ConnectInterceptor<?>> interceptors) {
		for (ConnectInterceptor<?> interceptor : interceptors) {
			Class<?> providerType = GenericTypeResolver.resolveTypeArgument(interceptor.getClass(),
					ConnectInterceptor.class);
			this.interceptors.add(providerType, interceptor);
		}
	}

	@RequestMapping(value = "/connect/facebook", method = RequestMethod.GET)
	public String connectView(@FacebookUserId String facebookUserId, Model model) {
		if (facebookProvider.isConnected()) {
			model.addAttribute("facebookUserId", facebookUserId);
			return "connect/facebookConnected";
		} else {
			return "connect/facebookConnect";
		}
	}

	@RequestMapping(value = "/connect/facebook", method = RequestMethod.POST)
	public String connectAccountToFacebook(@FacebookAccessToken String accessToken,
			@FacebookUserId String facebookUserId, WebRequest request) {
		if (facebookUserId != null && accessToken != null) {
			preConnect(facebookProvider, request);
			facebookProvider.addConnection(accessToken, facebookUserId);
			postConnect(facebookProvider, request);

			// FlashMap.setSuccessMessage("Your Greenhouse account is now connected to your Facebook account!");
		}
		return "redirect:/connect/facebook";
	}

	@RequestMapping(value = "/connect/facebook", method = RequestMethod.DELETE)
	public String disconnectFacebook(HttpServletRequest request, Authentication authentication) {
		facebookProvider.disconnect();
		return "redirect:/connect/facebook";
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void preConnect(ServiceProvider<?> provider, WebRequest request) {
		for (ConnectInterceptor interceptor : interceptingConnectionsTo(provider)) {
			interceptor.preConnect(provider, request);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void postConnect(ServiceProvider<?> provider, WebRequest request) {
		for (ConnectInterceptor interceptor : interceptingConnectionsTo(provider)) {
			interceptor.postConnect(provider, request);
		}
	}

	private List<ConnectInterceptor<?>> interceptingConnectionsTo(ServiceProvider<?> provider) {
		Class<?> serviceType = GenericTypeResolver.resolveTypeArgument(provider.getClass(), ServiceProvider.class);
		List<ConnectInterceptor<?>> typedInterceptors = interceptors.get(serviceType);
		if (typedInterceptors == null) {
			typedInterceptors = Collections.emptyList();
		}
		return typedInterceptors;
	}
}