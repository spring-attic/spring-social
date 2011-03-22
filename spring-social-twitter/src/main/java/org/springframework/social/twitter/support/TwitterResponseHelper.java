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
package org.springframework.social.twitter.support;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.social.twitter.Tweet;
import org.springframework.social.twitter.TwitterProfile;
import org.springframework.util.ObjectUtils;

class TwitterResponseHelper {
	
	public static Tweet populateTweetFromTimelineItem(Map<String, Object> item) {
		Tweet tweet = new Tweet();
		tweet.setId(Long.valueOf(String.valueOf(item.get("id"))));
		tweet.setText(String.valueOf(item.get("text")));
		tweet.setFromUser(String.valueOf(((Map<String, Object>) item.get("user")).get("screen_name")));
		tweet.setFromUserId(Long.valueOf(String.valueOf(((Map<String, Object>) item.get("user")).get("id"))));
		tweet.setProfileImageUrl(String.valueOf(((Map<String, Object>) item.get("user")).get("profile_image_url")));
		tweet.setSource(String.valueOf(item.get("source")));
		Object toUserId = item.get("in_reply_to_user_id");
		tweet.setToUserId(toUserId != null ? Long.valueOf(String.valueOf(toUserId)) : null);
		tweet.setCreatedAt(toDate(ObjectUtils.nullSafeToString(item.get("created_at")), timelineDateFormat));
		return tweet;
	}

	public static List<Tweet> extractTimelineTweetsFromResponse(List response) {
		List<Map<String, Object>> results = (List<Map<String, Object>>) response;
		List<Tweet> tweets = new ArrayList<Tweet>();
		for (Map<String, Object> item : results) {
			tweets.add(populateTweetFromTimelineItem(item));
		}
		return tweets;
	}

	public static TwitterProfile getProfileFromResponseMap(Map<?, ?> response) {
		TwitterProfile profile = new TwitterProfile();
		profile.setId(Long.valueOf(String.valueOf(response.get("id"))).longValue());
		profile.setScreenName(String.valueOf(response.get("screen_name")));
		profile.setName(String.valueOf(response.get("name")));
		profile.setDescription(String.valueOf(response.get("description")));
		profile.setLocation(String.valueOf(response.get("location")));
		profile.setUrl(String.valueOf(response.get("url")));
		profile.setProfileImageUrl(String.valueOf(response.get("profile_image_url")));
		profile.setCreatedDate(toDate(String.valueOf(response.get("created_at")), timelineDateFormat));
		return profile;
	}

	private static final DateFormat timelineDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH);

	private static Date toDate(String dateString, DateFormat dateFormat) {
		try {
			return dateFormat.parse(dateString);
		} catch (ParseException e) {
			return null;
		}
	}

}
