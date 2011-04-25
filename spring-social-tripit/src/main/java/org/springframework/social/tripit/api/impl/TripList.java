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
package org.springframework.social.tripit.api.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.springframework.social.tripit.api.Trip;

/**
 * Holder class for Trips. 
 * Relies on custom deserializer to handle cases where the upcoming list of trips is either an actual list or just a single object.
 * @author Craig Walls
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class TripList {

	private final List<Trip> trips;
	
	@JsonCreator
	public TripList(
			@JsonProperty("Trip") @JsonDeserialize(using = TripListDeserializer.class) List<Trip> trips) {
		this.trips = trips != null ? trips : Collections.<Trip>emptyList();
	}

	public List<Trip> getList() {
		return trips;
	}
	
	private static class TripListDeserializer extends JsonDeserializer<List<Trip>> {
		@Override
		public List<Trip> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setDeserializationConfig(ctxt.getConfig());
			
			JsonNode tree = jp.readValueAsTree();
			if(tree.asToken() == JsonToken.START_OBJECT) {
				return Collections.singletonList(objectMapper.readValue(tree, Trip.class));
			} else if(tree.asToken() == JsonToken.START_ARRAY) {
				List<Trip> trips = new ArrayList<Trip>(tree.size());
				for(Iterator<JsonNode> iterator = tree.getElements(); iterator.hasNext(); ) {
					trips.add(objectMapper.readValue(iterator.next(), Trip.class));
				}
				return trips;
			}
			
			return Collections.emptyList();
		}
	}
}
