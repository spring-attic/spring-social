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

public class Checkin {
	private String id;

	private Location place;

	private Reference from;

	private Reference application;

	private Date createdTime;

	private String message;

	private List<Comment> comments;

	private List<Reference> likes;

	private List<Reference> tags;

	private Checkin(String id, Location place, Reference from, Reference application, Date createdTime) {
		this.id = id;
		this.place = place;
		this.from = from;
		this.application = application;
		this.createdTime = createdTime;
	}

	public String getId() {
		return id;
	}

	public Location getPlace() {
		return place;
	}

	public Reference getFrom() {
		return from;
	}

	public Reference getApplication() {
		return application;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public String getMessage() {
		return message;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public List<Reference> getLikes() {
		return likes;
	}

	public List<Reference> getTags() {
		return tags;
	}

	public static class Builder {
		private String id;

		private Location place;

		private Reference from;

		private Reference application;

		private Date createdTime;

		private String message;

		private List<Comment> comments;

		private List<Reference> likes;

		private List<Reference> tags;

		public Builder(String id, Location place, Reference from, Reference application, Date createdTime) {
			this.id = id;
			this.place = place;
			this.from = from;
			this.application = application;
			this.createdTime = createdTime;
		}

		public Builder message(String message) {
			this.message = message;
			return this;
		}

		public Builder comments(List<Comment> comments) {
			this.comments = comments;
			return this;
		}

		public Builder likes(List<Reference> likes) {
			this.likes = likes;
			return this;
		}

		public Builder tags(List<Reference> tags) {
			this.tags = tags;
			return this;
		}

		public Checkin build() {
			Checkin checkin = new Checkin(id, place, from, application, createdTime);
			checkin.message = message;
			checkin.comments = comments;
			checkin.likes = likes;
			checkin.tags = tags;
			return checkin;
		}
	}
}
