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
package org.springframework.social;

/**
 * Base interface that may be implemented by API bindings.
 * Provides a simple boolean tester that can be used to determine if the API binding has been authorized for a specific user.
 * @author Keith Donald
 */
public interface ApiBinding {
	
	/**
	 * Returns true if this API binding has been authorized on behalf of a specific user.
	 * If so, calls to the API are signed with the user's authorization credentials, indicating an application is invoking the API on a user's behalf.
	 * If not, API calls do not contain any user authorization information.
	 * Callers can use this status flag to determine if API operations requiring authorization can be invoked.
	 * @see MissingAuthorizationException
	 * @return true if this API binding has been authorized on behalf of a specific user.
	 */
	public boolean isAuthorized();
	
}
