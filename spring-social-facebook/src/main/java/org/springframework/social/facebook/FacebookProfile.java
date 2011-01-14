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

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Model class containing a Facebook user's profile information.
 * 
 * @author Craig Walls
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class FacebookProfile implements Serializable {
	private static final long serialVersionUID = 1L;

	@JsonProperty
	long id;

	@JsonProperty
	String name;

	@JsonProperty("first_name")
	String firstName;

	@JsonProperty("last_name")
	String lastName;

	@JsonProperty
	String email;

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
}
