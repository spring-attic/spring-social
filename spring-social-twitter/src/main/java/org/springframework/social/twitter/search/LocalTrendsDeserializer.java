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
package org.springframework.social.twitter.search;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

/**
 * Deserializer to read local trends data into a LocalTrendsHolder object.
 * @author Craig Walls
 */
class LocalTrendsDeserializer extends JsonDeserializer<LocalTrendsHolder> {

	@Override
	public LocalTrendsHolder deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		JsonNode tree = jp.readValueAsTree();
		Iterator<JsonNode> dayIt = tree.iterator();
		if(dayIt.hasNext()) {
			JsonNode day = dayIt.next();
			Date createdAt = toDate(day.get("created_at").getValueAsText());
			JsonNode trendNodes = day.get("trends");
			List<Trend> trends = new ArrayList<Trend>();
			for(Iterator<JsonNode> trendsIt = trendNodes.iterator(); trendsIt.hasNext(); ) {
				JsonNode trendNode = trendsIt.next();
				trends.add(new Trend(trendNode.get("name").getValueAsText(), trendNode.get("query").getValueAsText()));
			}
			jp.skipChildren();
			return new LocalTrendsHolder(new Trends(createdAt, trends));
		}
		
		throw ctxt.mappingException(LocalTrendsHolder.class);
	}
	
	private static final DateFormat LOCAL_TREND_DATE_FORMAT = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss'Z'");

	private static Date toDate(String dateString) {
		try {
			return LOCAL_TREND_DATE_FORMAT.parse(dateString);
		} catch (ParseException e) {
			return null;
		}
	}
}
