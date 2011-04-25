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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

/**
 * Mixin class for adding Jackson annotations to Trip. 
 * @author Craig Walls
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class TripMixin {

	@JsonCreator
	public TripMixin(
			@JsonProperty("id") long id, 
			@JsonProperty("display_name") String displayName, 
			@JsonProperty("primary_location") String primaryLocation, 
			@JsonProperty("start_date") @JsonDeserialize(using=TripDateDeserializer.class) Date startDate, 
			@JsonProperty("end_date") @JsonDeserialize(using=TripDateDeserializer.class) Date endDate, 
			@JsonProperty("relative_url") String tripPath) {}

	private static class TripDateDeserializer extends JsonDeserializer<Date> {
		@Override
		public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			try {
				return DATE_FORMATTER.parse(jp.getText());
			} catch (ParseException e) {
				return new Date();
			}
		}
	}
	
	private static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

}
