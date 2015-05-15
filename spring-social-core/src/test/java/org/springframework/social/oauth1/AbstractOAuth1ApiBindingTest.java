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
package org.springframework.social.oauth1;

import org.junit.Test;

public class AbstractOAuth1ApiBindingTest {

	@Test(expected=IllegalArgumentException.class)
	public void nullConsumerKey() {
		new FakeApiBinding(null, "", "", "");
	}

	@Test(expected=IllegalArgumentException.class)
	public void nullConsumerSecret() {
		new FakeApiBinding("", null, "", "");
	}

	@Test(expected=IllegalArgumentException.class)
	public void nullAccessToken() {
		new FakeApiBinding("", "", null, "");
	}

	@Test(expected=IllegalArgumentException.class)
	public void nullAccessTokenSecret() {
		new FakeApiBinding("", "", "", null);
	}

	private static class FakeApiBinding extends AbstractOAuth1ApiBinding {
		public FakeApiBinding(String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) {
			super(consumerKey, consumerSecret, accessToken, accessTokenSecret);
		}
	}

}
