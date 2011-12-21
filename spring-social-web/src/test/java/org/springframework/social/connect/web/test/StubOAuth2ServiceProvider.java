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
package org.springframework.social.connect.web.test;

import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;
import org.springframework.social.oauth2.OAuth2Template;

public class StubOAuth2ServiceProvider extends AbstractOAuth2ServiceProvider<TestApi> {

	public StubOAuth2ServiceProvider(String clientId, String clientSecret) {
		super(new OAuth2Template(clientId, clientSecret, "https://someprovider.com/oauth/authorize", "https://someprovider.com/oauth/token"));
	}

	public TestApi getApi(String accessToken) {
		return new TestApi() {};
	}

}
