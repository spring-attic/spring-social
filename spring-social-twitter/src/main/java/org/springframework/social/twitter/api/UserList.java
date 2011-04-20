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
package org.springframework.social.twitter.api;

/**
 * Represents a user-defined list.
 * @author Craig Walls
 */
public class UserList {
	private final long id;
	private final String name;
	private final String fullName;
	private final String uriPath;
	private final String description;
	private final String slug;
	private final boolean isPublic;
	private final boolean isFollowing;
	private final int memberCount;
	private final int subscriberCount;

	public UserList(long id, String name, String fullName, String uriPath, String description, String slug, 
			boolean isPublic, boolean isFollowing, int memberCount, int subscriberCount) {
		this.id = id;
		this.name = name;
		this.fullName = fullName;
		this.uriPath = uriPath;
		this.description = description;
		this.slug = slug;
		this.isPublic = isPublic;
		this.isFollowing = isFollowing;
		this.memberCount = memberCount;
		this.subscriberCount = subscriberCount;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getFullName() {
		return fullName;
	}

	public String getUriPath() {
		return uriPath;
	}

	public String getDescription() {
		return description;
	}

	public String getSlug() {
		return slug;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public boolean isFollowing() {
		return isFollowing;
	}

	public int getMemberCount() {
		return memberCount;
	}

	public int getSubscriberCount() {
		return subscriberCount;
	}

}


