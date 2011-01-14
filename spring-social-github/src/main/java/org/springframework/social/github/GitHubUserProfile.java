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
package org.springframework.social.github;

import java.io.Serializable;

public class GitHubUserProfile implements Serializable {
	private static final long serialVersionUID = 1L;

	private final long id;
	private final String name;
	private final String username;
	private final String company;
	private final String blog;
	private final String email;

	public GitHubUserProfile(long id, String username, String name, String company, String blog, String email) {
		this.id = id;
		this.username = username;
		this.name = name;
		this.company = company;
		this.blog = blog;
		this.email = email;
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

	public String getCompany() {
		return company;
	}

	public String getBlog() {
		return blog;
	}

	public String getEmail() {
		return email;
	}
	
}
/*
{
	user: {
	id: 56928
	followers_count: 9
	created_at: "2009/02/22 14:40:13 -0800"
	following_count: 9
	public_repo_count: 14
	company: ""
	type: "User"
	permission: null
	public_gist_count: 0
	location: null
	login: "benrady"
	blog: "benrady.com"
	email: "benrady@gmail.com"
	gravatar_id: "0c4ce84ae6a6c7b48c00451dc0349b86"
	name: "Ben Rady"
	}
	}
*/