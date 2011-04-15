package org.springframework.social.facebook.support.json;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.social.facebook.types.Reference;

@JsonIgnoreProperties({"type", "application", "object_id"}) // TODO: Consider using type
abstract class FeedEntryMixin {
	
	@JsonCreator
	FeedEntryMixin(
			@JsonProperty("id") String id, 
			@JsonProperty("from") Reference from, 
			@JsonProperty("message") String message, 
			@JsonProperty("created_time") Date createdTime, 
			@JsonProperty("updated_time") Date updatedTime) {}
	
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

	@JsonProperty("likes")
	ReferenceList likes;

	@JsonProperty("comments")
	CommentList comments;

}
