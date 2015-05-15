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
package org.springframework.social.connect.web.test;

import static org.springframework.social.connect.web.test.StubOAuthTemplateBehavior.*;

import org.springframework.social.connect.support.OAuth2ConnectionFactory;

public class StubOAuth2ConnectionFactory extends OAuth2ConnectionFactory<TestApi2> {
	public StubOAuth2ConnectionFactory(String clientId, String clientSecret) {
		this(clientId, clientSecret, NO_EXCEPTION);
	}
	
	public StubOAuth2ConnectionFactory(String clientId, String clientSecret, StubOAuthTemplateBehavior behavior) {
		super("oauth2Provider", new StubOAuth2ServiceProvider(clientId, clientSecret, behavior), null);
	}
	
	@Override
	public String generateState() {
		return "STATE";
	}
}
