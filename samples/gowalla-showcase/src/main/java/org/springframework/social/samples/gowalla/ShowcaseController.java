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
package org.springframework.social.samples.gowalla;

import javax.inject.Inject;

import org.springframework.social.gowalla.GowallaOperations;
import org.springframework.social.gowalla.provider.GowallaServiceProvider;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ShowcaseController {
	private final GowallaServiceProvider gowallaProvider;

	@Inject
	public ShowcaseController(GowallaServiceProvider gowallaProvider) {
		this.gowallaProvider = gowallaProvider;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Model model) {
		if (gowallaProvider.isConnected(1)) {
			GowallaOperations gowalla = gowallaProvider.getConnections(1).get(0).getServiceApi();
			String gowallaId = gowalla.getProfileId();
			model.addAttribute("gowallaId", gowallaId);
			model.addAttribute("topCheckins", gowalla.getTopCheckins(gowallaId));
			return "home";
		}
		return "redirect:/connect/gowalla";
	}
}
