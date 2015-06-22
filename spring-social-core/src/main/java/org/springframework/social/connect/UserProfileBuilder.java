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
 * Builder for constructing a {@link UserProfile} instance returned by {@link ApiAdapter#fetchUserProfile(Object)}
 * Makes it easy to construct a profile instance where some of the individual fields may be null.
 * Also allows new profile fields to be introduced in the future without breaking compatibility with existing {@link ApiAdapter} implementations.
 * @author Keith Donald
 * @see UserProfile
 */
public class UserProfileBuilder {

	private String id;

	private String name;

	private String firstName;

	private String lastName;

	private String email;

	private String username;

	/**
	 * Sets the profile id field.
	 * @param id the user's id in the provider
	 * @return this {@link UserProfileBuilder} for setting more properties
	 */
	public UserProfileBuilder setId(String id) {
		this.id = id;
		return this;
	}

	/**
	 * Sets the profile name field.
	 * Note: parses the name string and sets the individual firstName and lastName fields as well.
	 * @param name the user's name
	 * @return this {@link UserProfileBuilder} for setting more properties
	 */
	public UserProfileBuilder setName(String name) {
		this.name = name;
		String[] firstAndLastName = firstAndLastName(this.name);
		setFirstName(firstAndLastName[0]);
		setLastName(firstAndLastName[1]);
		return this;
	}

	/**
	 * Sets the profile firstName field.
	 * @param firstName the user's first name
	 * @return this {@link UserProfileBuilder} for setting more properties
	 */
	public UserProfileBuilder setFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}

	/**
	 * Sets the profile lastName field.
	 * @param lastName the user's last name
	 * @return this {@link UserProfileBuilder} for setting more properties
	 */
	public UserProfileBuilder setLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	/**
	 * Sets the profile email field.
	 * @param email the user's email address
	 * @return this {@link UserProfileBuilder} for setting more properties
	 */
	public UserProfileBuilder setEmail(String email) {
		this.email = email;
		return this;
	}

	/**
	 * Sets the profile username field.
	 * @param username the user's username
	 * @return this {@link UserProfileBuilder} for setting more properties
	 */
	public UserProfileBuilder setUsername(String username) {
		this.username = username;
		return this;
	}

	/**
	 * Builds the user profile.
	 * Call this method after setting all profile field values.
	 * @return the {@link UserProfile}
	 */
	public UserProfile build() {
		return new UserProfile(id, name, firstName, lastName, email, username);
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
