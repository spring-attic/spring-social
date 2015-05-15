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

import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;
import org.springframework.social.oauth2.OAuth2Template;

public class FakeConnectionFactory extends OAuth2ConnectionFactory<Fake> {

	private String appId;
	
	private String appSecret;

	public FakeConnectionFactory(String appId, String appSecret) {
		super("fake", new FakeServiceProvider(appId, appSecret), null);
		this.appId = appId;
		this.appSecret = appSecret;
	}
	
	public String getAppId() {
		return appId;
	}
	
	public String getAppSecret() {
		return appSecret;
	}
	
	public static final class FakeServiceProvider extends AbstractOAuth2ServiceProvider<Fake> {
		public FakeServiceProvider(String appId, String appSecret) {
			super(new OAuth2Template(appId, appSecret, "http://fake.com/auth", "http://fake.com/token"));
		}
		
		@Override
		public Fake getApi(String accessToken) {
			return new FakeTemplate(accessToken);
		}
	}
}
