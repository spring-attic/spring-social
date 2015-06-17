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

import java.io.Serializable;

/**
 * A normalized model representing a service provider user profile.
 * The structure of a "UserProfile" varies across providers (see the difference between Facebook and Twitter, for example).
 * That said, there are generally a common set of profile fields that apply across providers.
 * This model provides access to those common fields in an uniform way.
 * This is particularly useful for pre-populating a local application registration form with provider profile data during a provider sign-in attempt. 
 * @author Keith Donald
 */
@SuppressWarnings("serial")
public class UserProfile implements Serializable {

	/**
	 * Shared, empty profile that when used indicates no profile data is available (all property values are null).
	 */
	public static final UserProfile EMPTY = new UserProfile(null, null, null, null, null);

	private final String name;
	
	private final String firstName;
	
	private final String lastName;
	
	private final String email;
	
	private final String username;

	/**
	 * The user's registered full name e.g. Keith Donald.
	 * May be null if not exposed/supported by the provider. 
	 * @return the user's registered full name
	 */
	public String getName() {
		return name;
	}

	/**
	 * The user's registered first name e.g. Keith.
	 * May be null if not exposed/supported by the provider.
	 * @return The user's registered first name
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * The user's registered last name e.g. Donald.
	 * May be null if not exposed/supported by the provider.
	 * @return The user's registered last name
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * The user's registered email address.
	 * May be null if not exposed/supported by the provider.
	 * @return The user's registered email address.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * The user's registered username e.g. kdonald.
	 * May be null if not exposed/supported by the provider.
	 * @return The user's registered username
	 */
	public String getUsername() {
		return username;
	}
	
	public UserProfile(String name, String firstName, String lastName, String email, String username) {
		this.name = name;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.username = username;
	}

}
