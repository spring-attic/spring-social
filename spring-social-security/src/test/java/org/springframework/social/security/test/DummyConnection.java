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
 package org.springframework.social.security.test;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UserProfileBuilder;

@SuppressWarnings("serial")
public class DummyConnection<T> implements Connection<T> {

	private final ConnectionKey _key;
	private final T _api;

	public static DummyConnection<Object> dummy(String provider, String user) {
		return new DummyConnection<Object>(provider, user, new Object());
	}
	
	public DummyConnection(String provider, String user, T api) {
		_key = new ConnectionKey(provider, user);
		_api = api;
	}

	public ConnectionKey getKey() {
		return _key;
	}

	public String getDisplayName() {
		return _key.getProviderUserId();
	}

	public String getProfileUrl() {
		return "http://www.example.com/" + _key.getProviderUserId();
	}

	public String getImageUrl() {
		return "http://www.example.com/img/" + _key.getProviderUserId() + ".jpg";
	}

	public void sync() {
	}

	public boolean test() {
		return true;
	}

	public boolean hasExpired() {
		return true;
	}

	public void refresh() {
	}

	public UserProfile fetchUserProfile() {
		return new UserProfileBuilder().setEmail(_key.getProviderUserId() + "@example.com").build();
	}

	public void updateStatus(String message) {
	}

	public T getApi() {
		return _api;
	}

	public ConnectionData createData() {
		return new ConnectionData(_key.getProviderId(), _key.getProviderUserId(), getDisplayName(),
				getProfileUrl(), getImageUrl(), "access_token", "secret", "refresh_token", System.currentTimeMillis() + 10000);
	}

	public static Answer<DummyConnection<Object>> answer() {
		return new Answer<DummyConnection<Object>>() {

			public DummyConnection<Object> answer(InvocationOnMock invocation) throws Throwable {
				ConnectionData data = (ConnectionData) invocation.getArguments()[0];
				return dummy(data.getProviderId(), data.getProviderUserId());
			}
		};
	}

}
