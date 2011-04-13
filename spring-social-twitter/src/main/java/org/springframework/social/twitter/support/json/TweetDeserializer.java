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
package org.springframework.social.twitter.support.json;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.springframework.social.twitter.types.Tweet;

public class TweetDeserializer extends JsonDeserializer<Tweet> {

	@Override
	public Tweet deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		JsonNode tree = jp.readValueAsTree();
		long id = tree.get("id").getValueAsLong();
		String text = tree.get("text").getValueAsText();
		JsonNode fromUserNode = tree.get("user");
		String fromScreenName = fromUserNode.get("screen_name").getValueAsText();
		long fromId = fromUserNode.get("id").getValueAsLong();
		String fromImageUrl = fromUserNode.get("profile_image_url").getValueAsText();
		String source = tree.get("source").getValueAsText();
		Date createdAt = toDate(tree.get("created_at").getValueAsText());
		JsonNode toUserIdNode = tree.get("in_reply_to_user_id");
		Long toUserId = toUserIdNode != null ? toUserIdNode.getValueAsLong() : null;
		JsonNode languageCodeNode = tree.get("iso_language_code");
		String languageCode = languageCodeNode != null ? languageCodeNode.getTextValue() : null;
		Tweet tweet = new Tweet(id, text, createdAt, fromScreenName, fromImageUrl, toUserId, fromId, languageCode, source);
		jp.skipChildren();
		return tweet;
	}
	
    private Date toDate(String dateString) {
        if (dateString == null) {
            return null;
        }

        try {
            return TIMELINE_DATE_FORMAT.parse(dateString);
        } catch (ParseException e) {
            return null;
        }
    }


	private static final DateFormat TIMELINE_DATE_FORMAT = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH);

}
