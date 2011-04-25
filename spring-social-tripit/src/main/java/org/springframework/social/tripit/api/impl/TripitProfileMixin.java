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
import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.springframework.social.tripit.api.TripItProfile;
import org.springframework.social.tripit.api.impl.TripitProfileMixin.TripItProfileDeserializer;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = TripItProfileDeserializer.class)
class TripitProfileMixin {

	static class TripItProfileDeserializer extends JsonDeserializer<TripItProfile> {

		@Override
		public TripItProfile deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			JsonNode tree = jp.readValueAsTree();
			JsonNode profileNode = tree.get("Profile");
			String id = profileNode.get("@attributes").get("ref").getValueAsText();
			String screenName = profileNode.get("screen_name").getValueAsText();
			String publicDisplayName = profileNode.get("public_display_name").getValueAsText();
			String emailAddress = getEmailAddress(profileNode);
			String homeCity = profileNode.get("home_city").getValueAsText();
			String company = profileNode.get("company").getValueAsText();
			String profilePath = profileNode.get("profile_url").getValueAsText();
			JsonNode photoUrlNode = profileNode.get("photo_url");
			String profileImageUrl = photoUrlNode != null ? photoUrlNode.getValueAsText() : null;
			
			return new TripItProfile(id, screenName, publicDisplayName, emailAddress, homeCity, company, profilePath, profileImageUrl);
		}
		
		private String getEmailAddress(JsonNode profileNode) {
			JsonNode emailsNode = profileNode.get("ProfileEmailAddresses").get("ProfileEmailAddress");
			if(emailsNode.asToken() == JsonToken.START_OBJECT) {
				return emailsNode.get("address").getValueAsText();
			} else if (emailsNode.asToken() == JsonToken.START_ARRAY) {
				for(Iterator<JsonNode> iterator = emailsNode.getElements(); iterator.hasNext();) {
					JsonNode emailNode = iterator.next();
					if(emailNode.get("is_primary").getValueAsBoolean()) {
						return emailNode.get("address").getValueAsText();
					}
				}
			}
			return null;
		}
	}
}
