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
package org.springframework.social.facebook.types;

import java.util.Date;
import java.util.List;

import org.springframework.social.facebook.support.json.CommentList;
import org.springframework.social.facebook.support.json.ReferenceList;

public class Checkin {
	private String id;  // in Post

	private Place place;

	private Reference from;  // in Post

	private Reference application;   // in Post ??? (should probably be)

	private Date createdTime;  // in Post

	private String message;  // in Post

	private CommentList comments;

	private ReferenceList likes;

	private ReferenceList tags;

	private Checkin(String id, Place place, Reference from, Reference application, Date createdTime) {
		this.id = id;
		this.place = place;
		this.from = from;
		this.application = application;
		this.createdTime = createdTime;
	}

	public String getId() {
		return id;
	}

	public Place getPlace() {
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
		return comments.getList();
	}

	public List<Reference> getLikes() {
		return likes.getList();
	}

	public List<Reference> getTags() {
		return tags.getList();
	}

}
