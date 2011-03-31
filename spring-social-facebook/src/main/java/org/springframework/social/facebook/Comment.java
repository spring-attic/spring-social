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
 * Represents a comment.
 * @author Craig Walls
 */
public class Comment {
	private final String id;
	private final String message;
	private final Date createdTime;
	private final Reference from;
	private final List<Reference> likes;

	public Comment(String id, Reference from, String message, Date createdTime, List<Reference> likes) {
		this.id = id;
		this.from = from;
		this.message = message;
		this.createdTime = createdTime;
		this.likes = likes;
	}

	public String getId() {
		return id;
	}

	public String getMessage() {
		return message;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public Reference getFrom() {
		return from;
	}

	public List<Reference> getLikes() {
		return likes;
	}

}
