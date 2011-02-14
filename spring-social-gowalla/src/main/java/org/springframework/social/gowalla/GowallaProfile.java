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
package org.springframework.social.gowalla;

import java.io.Serializable;

@SuppressWarnings("serial")
public class GowallaProfile implements Serializable {
	private final String id;
	private final String firstName;
	private final String lastName;
	private final String hometown;
	private final int pinsCount;
	private final int stampsCount;

	public GowallaProfile(String id, String firstName, String lastName, String hometown, int pinsCount, int stampsCount) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.hometown = hometown;
		this.pinsCount = pinsCount;
		this.stampsCount = stampsCount;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getHometown() {
		return hometown;
	}

	public int getPinsCount() {
		return pinsCount;
	}

	public int getStampsCount() {
		return stampsCount;
	}

	public String getId() {
		return id;
	}
}
