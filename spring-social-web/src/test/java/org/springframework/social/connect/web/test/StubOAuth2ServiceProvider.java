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

import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;

public class StubOAuth2ServiceProvider extends AbstractOAuth2ServiceProvider<TestApi2> {

	public StubOAuth2ServiceProvider(String clientId, String clientSecret) {
		this(clientId, clientSecret, NO_EXCEPTION);
	}
	
	public StubOAuth2ServiceProvider(String clientId, String clientSecret, StubOAuthTemplateBehavior behavior) {
		super(new StubOAuth2Template(clientId, clientSecret, "https://someprovider.com/oauth/authorize", "https://someprovider.com/oauth/token", behavior));
	}

	public TestApi2 getApi(String accessToken) {
		return new TestApi2() {};
	}

}
