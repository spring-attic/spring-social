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
