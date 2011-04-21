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
package org.springframework.social.facebook.api.impl.json;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.springframework.social.facebook.api.Photo.Image;
import org.springframework.social.facebook.api.Reference;
import org.springframework.social.facebook.api.Tag;

/**
 * Annotated mixin to add Jackson annotations to Photo. 
 * @author Craig Walls
 */
@JsonIgnoreProperties({"picture", "source", "height", "width", "comments"}) // TODO: Use comments
abstract class PhotoMixin {

	@JsonCreator
	PhotoMixin(
			@JsonProperty("id") String id, 
			@JsonProperty("from") Reference from, 
			@JsonProperty("link") String link, 
			@JsonProperty("icon") String icon, 
			@JsonProperty("created_time") Date createdTime,
			@JsonProperty("images") List<Image> images) {}
	
	@JsonProperty("name")
	String name;
	
	@JsonProperty("position")
	Integer position;
	
	@JsonProperty("updated_time")
	Date updatedTime;
	
	@JsonProperty("tags")
	@JsonDeserialize(using=TagListDeserializer.class)
	List<Tag> tags;

	public static abstract class ImageMixin {
		@JsonCreator
		public ImageMixin(
				@JsonProperty("source") String source,
				@JsonProperty("width") int width,
				@JsonProperty("height") int height
				) {}
	}
}
