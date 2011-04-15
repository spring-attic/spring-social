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


/**
 * Represents a single entry in a feed.
 * @author Craig Walls
 */
public class FeedEntry {
	// TODO: This covers several kinds of feed entry items...wondering if it'd be better to somehow extract separate types for each kind, even though they're all quite similar.
	
	private final String id;

	private final Reference from;

	private final String message;
	
	private String picture;
	
	private String link;
	
	private String subject;
	
	private String name;
	
	private String description;
	
	private String icon;

	private final Date createdTime;

	private final Date updatedTime;

	private ReferenceList likes;

	private CommentList comments;

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

	public String getPicture() {
		return picture;
	}

	public String getLink() {
		return link;
	}

	public String getSubject() {
		return subject;
	}
	
	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getIcon() {
		return icon;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public List<Reference> getLikes() {
		return likes.getList();
	}

	public List<Comment> getComments() {
		return comments.getList();
	}
}
