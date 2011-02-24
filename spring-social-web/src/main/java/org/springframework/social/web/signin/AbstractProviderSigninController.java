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
package org.springframework.social.web.signin;

import org.springframework.social.connect.support.ConnectionRepository;

/**
 * Convenient base class for provider sign-in controllers.
 * @author Keith Donald
 */
public class AbstractProviderSigninController {

	private final ConnectionRepository connectionRepository;

	private final SignInService signInService;

	private String signupUrl = "/signup";

	/**
	 * Constructs the Twitter sign in controller.
	 */
	public AbstractProviderSigninController(ConnectionRepository connectionRepository, SignInService signInService) {
		this.connectionRepository = connectionRepository;
		this.signInService = signInService;
	}

	public void setSignupUrl(String signupUrl) {
		this.signupUrl = signupUrl;
	}

	// subclassing hooks
	
	protected ConnectionRepository getConnectionRepository() {
		return connectionRepository;
	}
	
	protected SignInService getSignInService() {
		return signInService;
	}

	protected String getSignupUrl() {
		return signupUrl;
	}
	
}
