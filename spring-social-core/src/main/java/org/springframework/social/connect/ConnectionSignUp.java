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

/**
 * A command that signs up a new user in the event no user id could be mapped from a {@link Connection}.
 * Allows for implicitly creating a local user profile from connection data during a provider sign-in attempt.
 * @see UsersConnectionRepository#findUserIdsWithConnection(Connection)
 * @author Keith Donald
 */
public interface ConnectionSignUp {

	/**
	 * Sign up a new user of the application from the connection.
	 * @param connection the connection
	 * @return the new user id. May be null to indicate that an implicit local user profile could not be created.
	 */
	String execute(Connection<?> connection);

}
