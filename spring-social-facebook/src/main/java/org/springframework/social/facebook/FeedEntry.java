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

import java.util.Collections;
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

	private List<Reference> likes;

	private List<Comment> comments;

	private FeedEntry(String id, Reference from, String message, Date createdTime, Date updatedTime) {
		this.id = id;
		this.from = from;
		this.message = message;
		this.createdTime = createdTime;
		this.updatedTime = updatedTime;
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

	public static class Builder {
		private String id;

		private Reference from;

		private String message;

		private Date createdTime;

		private Date updatedTime;

		private List<Reference> likes = Collections.emptyList();

		private List<Comment> comments = Collections.emptyList();

		public Builder(String id, Reference from, String message, Date createdTime, Date updatedTime) {
			this.id = id;
			this.from = from;
			this.message = message;
			this.createdTime = createdTime;
			this.updatedTime = updatedTime;
		}

		public Builder likes(List<Reference> likes) {
			this.likes = likes;
			return this;
		}

		public Builder comments(List<Comment> comments) {
			this.comments = comments;
			return this;
		}

		public FeedEntry build() {
			FeedEntry feedEntry = new FeedEntry(id, from, message, createdTime, updatedTime);
			feedEntry.likes = likes;
			feedEntry.comments = comments;
			return feedEntry;
		}
	}
}
