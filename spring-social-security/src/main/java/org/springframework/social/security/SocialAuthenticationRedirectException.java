/*
 * Copyright 2015 the original author or authors.
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
package org.springframework.social.security;

import java.net.URL;

import org.springframework.security.core.AuthenticationException;

/**
 * Indicates the need to perform a redirect in the course of authenticating with a social provider. 
 * @author Stefan Fusseneger
 */
@SuppressWarnings("serial")
public class SocialAuthenticationRedirectException extends AuthenticationException {

	private final String redirectUrl;

	public SocialAuthenticationRedirectException(URL redirectUrl) {
	    this(redirectUrl.toString());
	}

	public SocialAuthenticationRedirectException(String redirectUrl) {
	    super("");
		this.redirectUrl = redirectUrl;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

}
