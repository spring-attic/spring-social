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
package org.springframework.social.facebook;

import java.util.Date;
import java.util.List;

/**
 * Represents a single entry in a feed.
 * @author Craig Walls
 */
public class FeedEntry {

	private final String id;

	private final Reference from;

	private final String message;

	private final Date createdTime;

	private final Date updatedTime;

	private final List<Reference> likes;

	private final List<Comment> comments;

	public FeedEntry(String id, Reference from, String message, Date createdTime, Date updatedTime, List<Reference> likes, List<Comment> comments) {
		this.id = id;
		this.from = from;
		this.message = message;
		this.createdTime = createdTime;
		this.updatedTime = updatedTime;
		this.likes = likes;
		this.comments = comments;
	}

	public String getId() {
		return id;
	}

	public Reference getFrom() {
		return from;
	}

	public String getMessage() {
		return message;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public List<Reference> getLikes() {
		return likes;
	}

	public List<Comment> getComments() {
		return comments;
	}
}
