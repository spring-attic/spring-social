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
package org.springframework.social.facebook.api.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

/**
 * Holder class to hold a typed list of FriendIds, pulled from the "data" field of the JSON object structure.
 * This helps Jackson know what type to deserialize list data into. 
 * @author Craig Walls
 */
public class FriendIdList {
	
	private final List<String> list;

	@JsonCreator
	public FriendIdList(@JsonProperty("data") @JsonDeserialize(using=IdDeserializer.class) List<String> list) {
		this.list = list;
	}

	public List<String> getList() {
		return list;
	}
	
	private static class IdDeserializer extends JsonDeserializer<List<String>> {
		public List<String> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			JsonNode tree = jp.readValueAsTree();
			List<String> idList = new ArrayList<String>(tree.size());
			for(Iterator<JsonNode> iterator = tree.iterator(); iterator.hasNext(); ) {
				JsonNode entryNode = iterator.next();
				if(entryNode.has("id")) {
					idList.add(entryNode.get("id").getValueAsText());
				}
			}
			return idList;
		}
	}
}
