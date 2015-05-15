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

import static java.util.Collections.*;

import java.util.List;
import java.util.Set;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;

public class StubUsersConnectionRepository implements UsersConnectionRepository {
	
	private final List<String> userIdsToReturn;
	
	public StubUsersConnectionRepository() {
		userIdsToReturn = emptyList();
	}
	
	public StubUsersConnectionRepository(List<String> userIdsToReturn) {
		this.userIdsToReturn = userIdsToReturn;
	}
	
	public List<String> findUserIdsWithConnection(Connection<?> connection) {
		return userIdsToReturn;
	}

	public Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds) {
		return null;
	}

	public ConnectionRepository createConnectionRepository(String userId) {
		return new StubConnectionRepository();
	}

}
