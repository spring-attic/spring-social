/*
 * Copyright 2011 the original author or authors.
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
 * Builder for constructing a {@link ServiceProviderUserProfile} instance returned by {@link ServiceApiAdapter#fetchUserProfile(Object)}
 * Makes it easy to construct an instance where some of the individual field values may be null.
 * Also will make it easier to add new values in the future without breaking compatibility with existing api adapter implementations.
 * @author Keith Donald
 */
public class ServiceProviderUserProfileBuilder {

	private String name;
	
	private String firstName;
	
	private String lastName;
	
	private String email;
	
	private String username;
	
	/**
	 * Sets the name field.
	 */
	public ServiceProviderUserProfileBuilder setName(String name) {
		this.name = name;
		String[] firstAndLastName = firstAndLastName(this.name);
		setFirstName(firstAndLastName[0]);
		setLastName(firstAndLastName[1]);
		return this;
	}

	/**
	 * Sets the first name field.
	 */
	public ServiceProviderUserProfileBuilder setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	/**
	 * Sets the last name field.
	 */
	public ServiceProviderUserProfileBuilder setLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	/**
	 * Sets the email field.
	 */
	public ServiceProviderUserProfileBuilder setEmail(String email) {
		this.email = email;
		return this;		
	}

	/**
	 * Sets the username field.
	 */
	public ServiceProviderUserProfileBuilder setUsername(String username) {
		this.username = username;
		return this;		
	}

	/**
	 * Build the user profile.
	 */
	public ServiceProviderUserProfile build() {
		return new ServiceProviderUserProfile(name, firstName, lastName, email, username);
	}
	
	// internal helpers
	
	private String[] firstAndLastName(String name) {
		if (name == null) {
			return EMPTY_FIRST_AND_LAST_NAME_ARRAY;
		}
		String[] nameParts = name.split("\\s+");
		if (nameParts.length == 1) {
			return new String[] { nameParts[0], null };
		} else {
			return new String[] { nameParts[0], nameParts[nameParts.length - 1] };
		}
	}
	
	private String[] EMPTY_FIRST_AND_LAST_NAME_ARRAY = new String[] { null, null };

}