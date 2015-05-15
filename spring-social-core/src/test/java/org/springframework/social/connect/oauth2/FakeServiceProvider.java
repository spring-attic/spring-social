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
package org.springframework.social.connect.oauth2;

import org.springframework.social.connect.FakeApi;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2ServiceProvider;
import org.springframework.social.oauth2.OAuth2Template;

class FakeServiceProvider implements OAuth2ServiceProvider<FakeApi> {

	private OAuth2Template oAuth2Template;

	public FakeServiceProvider(String clientId, String clientSecret) {
		oAuth2Template = new OAuth2Template(clientId, clientSecret, "http://fake/auth", "http://fake/access");
		
	}
	
	public OAuth2Operations getOAuthOperations() {
		return oAuth2Template;
	}

	public FakeApi getApi(String accessToken) {
		return new FakeApi() {};
	}

}
