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
package org.springframework.social.github.api;

import java.io.Serializable;
import java.util.Date;

public class GitHubUserProfile implements Serializable {
	private static final long serialVersionUID = 1L;

	private final long id;
	private final String name;
	private final String username;
	private final String location;
	private final String company;
	private final String blog;
	private final String email;
	private final Date createdDate;
	private final String profileImageUrl;

	public GitHubUserProfile(long id, String username, String name, String location, String company, String blog,
			String email, String profileImageUrl, Date createdDate) {
		this.id = id;
		this.username = username;
		this.name = name;
		this.location = location;
		this.company = company;
		this.blog = blog;
		this.email = email;
		this.profileImageUrl = profileImageUrl;
		this.createdDate = createdDate;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getUsername() {
		return username;
	}

	public String getLocation() {
		return location;
	}

	public String getCompany() {
		return company;
	}

	public String getBlog() {
		return blog;
	}

	public String getEmail() {
		return email;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}
}
