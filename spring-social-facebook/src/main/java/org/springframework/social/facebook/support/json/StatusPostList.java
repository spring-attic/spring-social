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
package org.springframework.social.facebook.support.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.social.facebook.types.StatusPost;

/**
 * Holder class to hold a typed list of status Posts, pulled from the "data" field of the JSON object structure.
 * This helps Jackson know what type to deserialize list data into. 
 * @author Craig Walls
 */
@JsonIgnoreProperties("paging")
public class StatusPostList {

	private final List<StatusPost> list;
	
	@JsonCreator
	public StatusPostList(@JsonProperty("data") @JsonDeserialize(using=StatusPostDeserializer.class) List<StatusPost> list) {
		this.list = list;
	}

	public List<StatusPost> getList() {
		return list;
	}
	
	private static class StatusPostDeserializer extends JsonDeserializer<List<StatusPost>> {
		@Override
		public List<StatusPost> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setDeserializationConfig(ctxt.getConfig());
			List<StatusPost> posts = new ArrayList<StatusPost>();
			JsonNode tree = jp.readValueAsTree();
			for(Iterator<JsonNode> iterator = tree.iterator(); iterator.hasNext();) {
				ObjectNode node = (ObjectNode) iterator.next();
				node.put("type", "status");	
				StatusPost post = objectMapper.readValue(node, StatusPost.class);
				posts.add(post);
			}
			return posts;
		}
	}

}
