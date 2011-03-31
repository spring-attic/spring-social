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
package org.springframework.social.twitter.support.extractors;

import java.util.Map;

import org.springframework.social.twitter.types.Tweet;

public class TweetResponseExtractor extends AbstractResponseExtractor<Tweet> {
	public Tweet extractObject(Map<String, Object> tweetMap) {
		Tweet tweet = new Tweet();
		tweet.setId(Long.valueOf(String.valueOf(tweetMap.get("id"))));
		tweet.setText(String.valueOf(tweetMap.get("text")));
		Map<String, Object> fromUserMap = (Map<String, Object>) tweetMap.get("user");
		if(fromUserMap != null) {			
			tweet.setFromUser(String.valueOf(fromUserMap.get("screen_name")));
			tweet.setFromUserId(Long.valueOf(String.valueOf(fromUserMap.get("id"))));
			tweet.setProfileImageUrl(String.valueOf(fromUserMap.get("profile_image_url")));
		} else {
			tweet.setFromUser((String) tweetMap.get("from_user"));
		}
		tweet.setSource(String.valueOf(tweetMap.get("source")));
		Object toUserId = tweetMap.get("in_reply_to_user_id");
		tweet.setToUserId(toUserId != null ? Long.valueOf(String.valueOf(toUserId)) : null);
		tweet.setCreatedAt(toTimelineDate((String) tweetMap.get("created_at")));
		return tweet;
	}
}
