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
package org.springframework.social.facebook.api.impl;

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
import org.springframework.social.facebook.api.LinkPost;

/**
 * Holder class to hold a typed list of link Posts, pulled from the "data" field of the JSON object structure.
 * This helps Jackson know what type to deserialize list data into. 
 * @author Craig Walls
 */
@JsonIgnoreProperties("paging")
class LinkPostList {

	private final List<LinkPost> list;

	@JsonCreator
	public LinkPostList(@JsonProperty("data") @JsonDeserialize(using=LinkPostDeserializer.class) List<LinkPost> list) {
		this.list = list;
	}

	public List<LinkPost> getList() {
		return list;
	}
	
	private static class LinkPostDeserializer extends JsonDeserializer<List<LinkPost>> {
		@Override
		public List<LinkPost> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setDeserializationConfig(ctxt.getConfig());
			List<LinkPost> posts = new ArrayList<LinkPost>();
			JsonNode tree = jp.readValueAsTree();
			for(Iterator<JsonNode> iterator = tree.iterator(); iterator.hasNext();) {
				ObjectNode node = (ObjectNode) iterator.next();
				node.put("type", "link");	
				LinkPost post = objectMapper.readValue(node, LinkPost.class);
				posts.add(post);
			}
			return posts;
		}
	}
	
}
