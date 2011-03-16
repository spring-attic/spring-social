package org.springframework.social.twitter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.social.ResponseStatusCodeTranslator;
import org.springframework.social.SocialException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

class TweetApiTemplate implements TweetApi {

	private final RestTemplate restTemplate;

	private final ResponseStatusCodeTranslator statusCodeTranslator;

	public TweetApiTemplate(RestTemplate restTemplate, ResponseStatusCodeTranslator statusCodeTranslator) {
		this.restTemplate = restTemplate;
		this.statusCodeTranslator = statusCodeTranslator;
	}

	public List<Tweet> getPublicTimeline() {
		return retrieveTimelineTweets(PUBLIC_TIMELINE_URL);
	}

	public List<Tweet> getHomeTimeline() {
		return retrieveTimelineTweets(HOME_TIMELINE_URL);
	}

	public List<Tweet> getFriendsTimeline() {
		return retrieveTimelineTweets(FRIENDS_TIMELINE_URL);
	}

	public List<Tweet> getUserTimeline() {
		return retrieveTimelineTweets(USER_TIMELINE_URL);
	}

	public List<Tweet> getUserTimeline(String screenName) {
		return retrieveTimelineTweets(USER_TIMELINE_URL + "?screen_name={screenName}", screenName);
	}

	public List<Tweet> getUserTimeline(long userId) {
		return retrieveTimelineTweets(USER_TIMELINE_URL + "?user_id={userId}", userId);
	}

	public List<Tweet> getMentions() {
		List response = restTemplate.getForObject(MENTIONS_URL, List.class);
		List<Map<String, Object>> results = (List<Map<String, Object>>) response;
		List<Tweet> tweets = new ArrayList<Tweet>();
		for (Map<String, Object> item : results) {
			tweets.add(populateTweetFromTimelineItem(item));
		}
		return tweets;
	}

	public List<Tweet> getRetweetedByMe() {
		return retrieveTimelineTweets(RETWEETED_BY_ME_URL);
	}

	public List<Tweet> getRetweetedToMe() {
		return retrieveTimelineTweets(RETWEETED_TO_ME_URL);
	}

	public List<Tweet> getRetweetsOfMe() {
		return retrieveTimelineTweets(RETWEETS_OF_ME_URL);
	}

	public Tweet getStatus(long tweetId) {
		Map<String, Object> tweetMap = restTemplate.getForObject(SHOW_TWEET_URL, Map.class, tweetId);
		return populateTweetFromTimelineItem(tweetMap);
	}

	public void updateStatus(String message) {
		updateStatus(message, new StatusDetails());
	}

	public void updateStatus(String message, StatusDetails details) {
		MultiValueMap<String, Object> tweetParams = new LinkedMultiValueMap<String, Object>();
		tweetParams.add("status", message);
		tweetParams.setAll(details.toParameterMap());
		ResponseEntity<Map> response = restTemplate.postForEntity(TWEET_URL, tweetParams, Map.class);
		handleResponseErrors(response);
	}

	public void deleteStatus(long tweetId) {
		restTemplate.delete(DESTROY_TWEET_URL, tweetId);
	}

	public void retweet(long tweetId) {
		ResponseEntity<Map> response = restTemplate.postForEntity(RETWEET_URL, "", Map.class,
				Collections.singletonMap("tweet_id", Long.toString(tweetId)));
		handleResponseErrors(response);
	}

	public List<Tweet> getRetweets(long tweetId) {
		return retrieveTimelineTweets(RETWEETS_URL, tweetId);
	}

	public List<TwitterProfile> getRetweetedBy(long tweetId) {
		List<Map<String, Object>> response = restTemplate.getForObject(RETWEETED_BY_URL, List.class, tweetId);
		List<TwitterProfile> profiles = new ArrayList<TwitterProfile>();
		for (Map<String, Object> profileEntry : response) {
			profiles.add(TwitterResponseHelper.getProfileFromResponseMap(profileEntry));
		}
		return profiles;
	}

	public List<Integer> getRetweetedByIds(long tweetId) {
		return restTemplate.getForObject(RETWEETED_BY_IDS_URL, List.class, tweetId);
	}

	public List<Tweet> getFavorites() {
		return retrieveTimelineTweets(FAVORITE_TIMELINE_URL);
	}

	public void addToFavorites(long tweetId) {
		ResponseEntity<Map> response = restTemplate.postForEntity(CREATE_FAVORITE_URL, "", Map.class,
				Collections.singletonMap("tweet_id", Long.toString(tweetId)));
		handleResponseErrors(response);
	}

	public void removeFromFavorites(long tweetId) {
		ResponseEntity<Map> response = restTemplate.postForEntity(DESTROY_FAVORITE_URL, "", Map.class,
				Collections.singletonMap("tweet_id", Long.toString(tweetId)));
		handleResponseErrors(response);
	}

	private List<Tweet> retrieveTimelineTweets(String url, Object... urlArgs) {
		List response = restTemplate.getForObject(url, List.class, urlArgs);
		return extractTimelineTweetsFromResponse(response);
	}

	private static final DateFormat timelineDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy",
			Locale.ENGLISH);

	private Date toDate(String dateString, DateFormat dateFormat) {
		try {
			return dateFormat.parse(dateString);
		} catch (ParseException e) {
			return null;
		}
	}

	private Tweet populateTweetFromTimelineItem(Map<String, Object> item) {
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

	private List<Tweet> extractTimelineTweetsFromResponse(List response) {
		List<Map<String, Object>> results = (List<Map<String, Object>>) response;
		List<Tweet> tweets = new ArrayList<Tweet>();
		for (Map<String, Object> item : results) {
			tweets.add(populateTweetFromTimelineItem(item));
		}
		return tweets;
	}

	private void handleResponseErrors(ResponseEntity<Map> response) {
		SocialException exception = statusCodeTranslator.translate(response);
		if (exception != null) {
			throw exception;
		}
	}

	static final String TWEET_URL = TwitterTemplate.API_URL_BASE + "statuses/update.json";
	static final String RETWEET_URL = TwitterTemplate.API_URL_BASE + "statuses/retweet/{tweet_id}.json";
	static final String MENTIONS_URL = TwitterTemplate.API_URL_BASE + "statuses/mentions.json";
	static final String PUBLIC_TIMELINE_URL = TwitterTemplate.API_URL_BASE + "statuses/public_timeline.json";
	static final String HOME_TIMELINE_URL = TwitterTemplate.API_URL_BASE + "statuses/home_timeline.json";
	static final String FRIENDS_TIMELINE_URL = TwitterTemplate.API_URL_BASE + "statuses/friends_timeline.json";
	static final String USER_TIMELINE_URL = TwitterTemplate.API_URL_BASE + "statuses/user_timeline.json";
	static final String RETWEETED_BY_ME_URL = TwitterTemplate.API_URL_BASE + "statuses/retweeted_by_me.json";
	static final String RETWEETED_TO_ME_URL = TwitterTemplate.API_URL_BASE + "statuses/retweeted_to_me.json";
	static final String RETWEETS_OF_ME_URL = TwitterTemplate.API_URL_BASE + "statuses/retweets_of_me.json";
	static final String SHOW_TWEET_URL = TwitterTemplate.API_URL_BASE + "statuses/show/{tweet_id}.json";
	static final String DESTROY_TWEET_URL = TwitterTemplate.API_URL_BASE + "statuses/destroy/{tweet_id}.json";
	static final String RETWEETS_URL = TwitterTemplate.API_URL_BASE + "statuses/retweets/{tweet_id}.json";
	static final String RETWEETED_BY_URL = TwitterTemplate.API_URL_BASE + "statuses/{tweet_id}/retweeted_by.json";
	static final String RETWEETED_BY_IDS_URL = TwitterTemplate.API_URL_BASE + "statuses/{tweet_id}/retweeted_by/ids.json";
	static final String FAVORITE_TIMELINE_URL = TwitterTemplate.API_URL_BASE + "favorites.json";
	static final String CREATE_FAVORITE_URL = TwitterTemplate.API_URL_BASE + "favorites/create/{tweet_id}";
	static final String DESTROY_FAVORITE_URL = TwitterTemplate.API_URL_BASE + "favorites/destroy/{tweet_id}";
}
