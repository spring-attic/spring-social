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
package org.springframework.social.facebook.feed;

import java.util.Date;
import java.util.List;

import org.springframework.social.facebook.Reference;
import org.springframework.social.facebook.Tag;
import org.springframework.social.facebook.json.TagList;

/**
 * Model class representing a Post to a feed announcing a Photo. Note that this is not the Photo itself.
 * To get the Photo object, get the Photo's ID by calling getPhotoId(), then calling getPhoto(photoId) on MediaOperations.
 * @author Craig Walls
 */
public class PhotoPost extends Post {

	private String photoId;
	
	private TagList tags;
	
	public PhotoPost(String id, Reference from, Date createdTime, Date updatedTime) {
		super(id, from, createdTime, updatedTime);
	}
	
	public String getPhotoId() {
		return photoId;
	}
	
	public List<Tag> getTags() {
		return tags.getList();
	}

}
