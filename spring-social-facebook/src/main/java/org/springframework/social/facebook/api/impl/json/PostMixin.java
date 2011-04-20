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

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.springframework.social.facebook.api.CheckinPost;
import org.springframework.social.facebook.api.Comment;
import org.springframework.social.facebook.api.LinkPost;
import org.springframework.social.facebook.api.NotePost;
import org.springframework.social.facebook.api.PhotoPost;
import org.springframework.social.facebook.api.Post.PostType;
import org.springframework.social.facebook.api.Reference;
import org.springframework.social.facebook.api.StatusPost;
import org.springframework.social.facebook.api.VideoPost;

/**
 * Annotated mixin to add Jackson annotations to Post.
 * Also defines Post subtypes to deserialize into based on the "type" attribute. 
 * @author Craig Walls
 */
@JsonTypeInfo(use=Id.NAME, include=As.PROPERTY, property="type")
@JsonSubTypes({
				@Type(name="checkin", value=CheckinPost.class),
				@Type(name="link", value=LinkPost.class),
				@Type(name="note", value=NotePost.class),
				@Type(name="photo", value=PhotoPost.class),
				@Type(name="status", value=StatusPost.class),
				@Type(name="video", value=VideoPost.class)
				})
abstract class PostMixin {
	
	@JsonCreator
	PostMixin(
			@JsonProperty("id") String id, 
			@JsonProperty("from") Reference from, 
			@JsonProperty("created_time") Date createdTime, 
			@JsonProperty("updated_time") Date updatedTime) {}

	@JsonProperty("to")
	@JsonDeserialize(using = ReferenceListDeserializer.class)
	List<Reference> to;
	
	@JsonProperty("message")
	String message;

	@JsonProperty("caption")
	String caption;
	
	@JsonProperty("picture")
	String picture;
	
	@JsonProperty("link")
	String link;
	
	@JsonProperty("subject")
	String subject;
	
	@JsonProperty("name")
	String name;
	
	@JsonProperty("description")
	String description;
	
	@JsonProperty("icon")
	String icon;
	
	@JsonProperty("application")
	Reference application;
	
	@JsonProperty("type")
	@JsonDeserialize(using = TypeDeserializer.class)
	PostType type;

	@JsonProperty("likes")
	@JsonDeserialize(using = ReferenceListDeserializer.class)
	List<Reference> likes;

	@JsonProperty("comments")
	@JsonDeserialize(using = CommentListDeserializer.class)
	List<Comment> comments;

	private static class TypeDeserializer extends JsonDeserializer<PostType> {
		@Override
		public PostType deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			return PostType.valueOf(jp.getText().toUpperCase());
		}
	}
}
