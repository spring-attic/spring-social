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
package org.springframework.social.connect;

import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UserProfileBuilder;

public class FakeApiAdapter implements ApiAdapter<FakeApi> {

	public boolean test(FakeApi api) {
		return true;
	}

	public void setConnectionValues(FakeApi api, ConnectionValues values) {
	}

	public UserProfile fetchUserProfile(FakeApi api) {
		return new UserProfileBuilder().build();
	}

	public void updateStatus(FakeApi api, String message) {
	}

}
