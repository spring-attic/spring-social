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
package org.springframework.social.showcase.signup;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.social.showcase.ShowcaseUser;
import org.springframework.social.showcase.UserRepository;
import org.springframework.social.showcase.UsernameAlreadyInUseException;
import org.springframework.social.web.connect.SignInControllerGateway;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SignupController {

	private final UserRepository userRepository;
	private final SignInControllerGateway signinGateway;

	@Inject
	public SignupController(UserRepository userRepository, SignInControllerGateway signinGateway) {
		this.userRepository = userRepository;
		this.signinGateway = signinGateway;
	}

	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public SignupForm signupForm() {
		return new SignupForm();
	}

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public String signup(@Valid SignupForm form, BindingResult formBinding) {
		if (formBinding.hasErrors()) {
			return null;
		}

		boolean result = createUser(form, formBinding);
		return result ? "redirect:/" : null;
	}

	private boolean createUser(SignupForm form, BindingResult formBinding) {
		try {
			ShowcaseUser user = new ShowcaseUser(form.getUsername(), form.getPassword(),
					form.getFirstName(), form.getLastName());
			userRepository.createUser(user);

			signinGateway.signIn(user.getUsername());
			return true;
		} catch (UsernameAlreadyInUseException e) {
			formBinding.rejectValue("username", "user.duplicateUsername", "already in use");
			return false;
		}
	}
}
