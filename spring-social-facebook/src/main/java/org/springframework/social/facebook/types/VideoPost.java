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
package org.springframework.social.facebook.types;

import java.util.Date;
import java.util.List;

import org.springframework.social.facebook.support.json.TagList;

/**
 * Model class representing a Post announcing a Video to a feed. This is not the Video itself.
 * To get the Video object, get the video's ID by calling getVideoId() then pass it to MediaOperations.getVideo(videoId).
 * @author Craig Walls
 */
public class VideoPost extends Post {
	
	private String videoId;
	
	private TagList tags;
	
	public VideoPost(String id, Reference from, Date createdTime, Date updatedTime) {
		super(id, from, createdTime, updatedTime);
	}
	
	public String getVideoId() {
		return videoId;
	}
	
	public List<Tag> getTags() {
		return tags.getList();
	}
}
