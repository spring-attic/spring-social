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
package org.springframework.social.facebook.like;

import java.util.Date;

/**
 * Model class representing an object that the user likes. This could be something that they explicitly liked by clicking a "like" button.
 * Or it could be something such as a favorite movie that they identified in their profile.
 * @author Craig Walls
 */
public class UserLike {
	private final String id;
	private final String name;
	private final String category;
	private final Date createdTime;

	public UserLike(String id, String name, String category, Date createdTime) {
		this.id = id;
		this.name = name;
		this.category = category;
		this.createdTime = createdTime;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getCategory() {
		return category;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

}
