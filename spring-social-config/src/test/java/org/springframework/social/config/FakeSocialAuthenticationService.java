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
package org.springframework.social.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.security.SocialAuthenticationRedirectException;
import org.springframework.social.security.SocialAuthenticationToken;
import org.springframework.social.security.provider.SocialAuthenticationService;

public class FakeSocialAuthenticationService implements SocialAuthenticationService<Fake> {

	private FakeConnectionFactory connectionFactory;

	public FakeSocialAuthenticationService(String appId, String appSecret) {
		connectionFactory = new FakeConnectionFactory(appId, appSecret);
	}
	
	public org.springframework.social.security.provider.SocialAuthenticationService.ConnectionCardinality getConnectionCardinality() {
		return null;
	}

	public ConnectionFactory<Fake> getConnectionFactory() {
		return connectionFactory;
	}

	public SocialAuthenticationToken getAuthToken(HttpServletRequest request, HttpServletResponse response) throws SocialAuthenticationRedirectException {
		return null;
	}

	public String getConnectionAddedRedirectUrl(HttpServletRequest request, Connection<?> connection) {
		return null;
	}

}
