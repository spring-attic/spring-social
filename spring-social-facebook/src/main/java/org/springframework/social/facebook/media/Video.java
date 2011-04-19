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
package org.springframework.social.facebook.media;

import java.util.Date;
import java.util.List;

import org.springframework.social.facebook.Reference;
import org.springframework.social.facebook.Tag;
import org.springframework.social.facebook.json.TagList;

/**
 * Model class representing a video.
 * @author Craig Walls
 */
public class Video {
	private String id;
	
	private Reference from;
	
	private TagList tags;
	
	private String name;
	
	private String description;
	
	private String picture;
	
	private String embedHtml;
	
	private String icon;
	
	private String source;
	
	private Date createdTime;
	
	private Date updatedTime;
	
	private Video(String id, Reference from, String picture, String embedHtml, String icon, String source, Date createdTime, Date updatedTime) {
		this.id = id;
		this.from = from;
		this.picture = picture;
		this.embedHtml = embedHtml;
		this.icon = icon;
		this.source = source;
		this.createdTime = createdTime;
		this.updatedTime = updatedTime;
	}
	
	public String getId() {
		return id;
	}

	public Reference getFrom() {
		return from;
	}
	
	public List<Tag> getTags() {
		return tags.getList();
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getPicture() {
		return picture;
	}
	
	public String getEmbedHtml() {
		return embedHtml;
	}
	
	public String getIcon() {
		return icon;
	}
	
	public String getSource() {
		return source;
	}
	
	public Date getCreatedTime() {
		return createdTime;
	}
	
	public Date getUpdatedTime() {
		return updatedTime;
	}
}
