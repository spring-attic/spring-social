/*
 * Copyright 2010 the original author or authors.
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
package org.springframework.social.facebook;

import java.io.Serializable;

/**
 * Model class containing a Facebook user's profile information.
 * 
 * @author Craig Walls
 */
@SuppressWarnings("serial")
public class FacebookProfile implements Serializable {

	private final long id;
	private final String name;
	private final String firstName;
	private final String lastName;
	private final String email;

	public FacebookProfile(long id, String name, String firstName, String lastName, String email) {
		this.id = id;
		this.name = name;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}

	/**
	 * The user's Facebook ID
	 * 
	 * @return The user's Facebook ID
	 */
	public long getId() {
		return id;
	}

	/**
	 * The user's full name
	 * 
	 * @return The user's full name
	 */
	public String getName() {
		return name;
	}

	/**
	 * The user's first name
	 * 
	 * @return The user's first name
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * The user's last name
	 * 
	 * @return The user's last name
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * The user's email address
	 * 
	 * @return The user's email address
	 */
	public String getEmail() {
	    return email;
    }

	/**
	 * <p>
	 * The URL of the user's profile image in "normal" size.
	 * </p>
	 * 
	 * @return The URL of the user's normal-sized profile image.
	 */
	public String getProfileImageUrl() {
		return "https://graph.facebook.com/" + id + "/picture";
	}

	/**
	 * Retrieve the URL to the user's Facebook profile.
	 * 
	 * @return the URL to the user's Facebook profile.
	 */
	public String getProfileUrl() {
		return "http://www.facebook.com/profile.php?id=" + id;
	}

}
