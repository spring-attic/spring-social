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
package org.springframework.social.connect.oauth1;

import org.springframework.social.connect.FakeApi;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1ServiceProvider;
import org.springframework.social.oauth1.OAuth1Template;

class FakeServiceProvider implements OAuth1ServiceProvider<FakeApi> {

	private OAuth1Template oAuth1Template;

	public FakeServiceProvider(String clientId, String clientSecret) {
		oAuth1Template = new OAuth1Template(clientId, clientSecret, "http://fake/request", "http://fake/auth", "http://fake/token");
	}
	
	public OAuth1Operations getOAuthOperations() {
		return oAuth1Template;
	}

	public FakeApi getApi(String accessToken, String secret) {
		return new FakeApi() {};
	}

}
