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
package org.springframework.social.facebook.comment;

import java.util.Date;
import java.util.List;

import org.springframework.social.facebook.shared.Reference;

/**
 * Model class representing a comment.
 * @author Craig Walls
 */
public class Comment {
	private final String id;
	
	private final String message;
	
	private final Date createdTime;
	
	private final Reference from;
	
	private List<Reference> likes;
	
	private int likesCount;

	/**
	 * Constructs a Comment object.
	 * @param id the comment's Graph API ID
	 * @param from the author of the comment
	 * @param message the comment text
	 * @param createdTime the creation time of the comment
	 */
	public Comment(String id, Reference from, String message, Date createdTime) {
		this.id = id;
		this.from = from;
		this.message = message;
		this.createdTime = createdTime;
	}

	/**
	 * The comment's Graph API object ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * The text of the comment
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * The time the comment was created.
	 */
	public Date getCreatedTime() {
		return createdTime;
	}

	/**
	 * A reference to the user who posted the comment.
	 */
	public Reference getFrom() {
		return from;
	}

	/**
	 * A list of references to users who liked this comment.
	 * May be null, as Facebook often sends only a count of likes.
	 * In some cases (such as a comment on a checkin) the likes will be a list of references.
	 */
	public List<Reference> getLikes() {
		return likes;
	}

	/**
	 * The number of users who like this comment.
	 */
	public int getLikesCount() {
		return likesCount;
	}
}
