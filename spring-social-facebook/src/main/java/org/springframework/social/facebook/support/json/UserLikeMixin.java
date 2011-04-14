package org.springframework.social.facebook.support.json;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class UserLikeMixin {
	
	@JsonCreator
	public UserLikeMixin(
			@JsonProperty("id") String id, 
			@JsonProperty("name") String name, 
			@JsonProperty("category") String category,
			@JsonProperty("created_time") Date createdTime) {}

}
