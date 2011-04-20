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

import java.io.IOException;
import java.util.Date;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.springframework.social.facebook.media.Album.Privacy;
import org.springframework.social.facebook.media.Album.Type;
import org.springframework.social.facebook.shared.Reference;

/**
 * Annotated mixin to add Jackson annotations to Album. 
 * @author Craig Walls
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AlbumMixin {

	@JsonCreator
	AlbumMixin(
			@JsonProperty("id") String id, 
			@JsonProperty("from") Reference from, 
			@JsonProperty("name") String name, 
			@JsonProperty("type") @JsonDeserialize(using=TypeDeserializer.class) Type type, 
			@JsonProperty("link") String link, 
			@JsonProperty("count") int count, 
			@JsonProperty("privacy") @JsonDeserialize(using=PrivacyDeserializer.class) Privacy privacy, 
			@JsonProperty("created_time") Date createdTime) {}
	
	@JsonProperty("location")
	String location;
	
	@JsonProperty("description")
	String description;
	
	@JsonProperty("updated_time")
	Date updatedTime;
		
	private static class TypeDeserializer extends JsonDeserializer<Type> {
		@Override
		public Type deserialize(JsonParser jp, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			return Type.valueOf(jp.getText().toUpperCase());
		}		
	}
			
	private static class PrivacyDeserializer extends JsonDeserializer<Privacy> {
		@Override
		public Privacy deserialize(JsonParser jp, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			return Privacy.valueOf(jp.getText().replace("-", "_").toUpperCase());
		}
	}
}
