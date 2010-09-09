package org.springframework.social.twitter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.social.core.ResponseStatusCodeTranslator;
import org.springframework.social.core.SocialException;
import org.springframework.social.oauth.OAuthEnabledRestTemplate;
import org.springframework.util.NumberUtils;
import org.springframework.util.ObjectUtils;

public class TwitterTemplate implements TwitterOperations {

	private final OAuthEnabledRestTemplate restTemplate;
	private ResponseStatusCodeTranslator statusCodeTranslator;

	public TwitterTemplate(OAuthEnabledRestTemplate restTemplate) {
		this.restTemplate = restTemplate;
		// TODO: May want to make the error handler configurable or part of the
		// factory and not do it here.
		this.restTemplate.setErrorHandler(new TwitterErrorHandler());
		this.statusCodeTranslator = new TwitterResponseStatusCodeTranslator();
	}

	public String getScreenName() {
		Map<?, ?> response = restTemplate.getForObject(VERIFY_CREDENTIALS_URL, Map.class);
		return (String) response.get("screen_name");
	}

	public List<String> getFollowed(String screenName) {
		List<Map<String, String>> response = restTemplate.getForObject(FRIENDS_STATUSES_URL, List.class,
				Collections.singletonMap("screen_name", screenName));
		List<String> friends = new ArrayList<String>(response.size());
		for (Map<String, String> item : response) {
			friends.add(item.get("screen_name"));
		}
		return friends;
	}

	public void tweet(String message) throws SocialException {
		ResponseEntity<Map> response = restTemplate.postForEntity(TWEET_URL, null, Map.class,
				Collections.singletonMap("status", message));
		handleResponseErrors(response);
	}

	public void retweet(long tweetId) throws SocialException {
		ResponseEntity<Map> response = restTemplate.postForEntity(RETWEET_URL, Collections.emptyMap(), Map.class,
				Collections.singletonMap("tweet_id", Long.toString(tweetId)));
		handleResponseErrors(response);
	}

	public SearchResults search(String query) {
		return search(query, 1, DEFAULT_RESULTS_PER_PAGE, 0, 0);
	}

	public SearchResults search(String query, int page, int resultsPerPage) {
		return search(query, page, resultsPerPage, 0, 0);
	}

	public SearchResults search(String query, int page, int resultsPerPage, int sinceId, int maxId) {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("query", query);
		parameters.put("rpp", String.valueOf(resultsPerPage));
		parameters.put("page", String.valueOf(page));

		String searchUrl = SEARCH_URL;
		if (sinceId > 0) {
			searchUrl += "&since_id={since}";
			parameters.put("since", String.valueOf(sinceId));
		}
		if (maxId > 0) {
			searchUrl += "&max_id={max}";
			parameters.put("max", String.valueOf(maxId));
		}

		ResponseEntity<Map> response = restTemplate.getForEntity(searchUrl, Map.class, parameters);
		// handleResponseErrors(response);
		Map<String, Object> resultsMap = response.getBody();
		List<Map<String, Object>> items = (List<Map<String, Object>>) resultsMap.get("results");
		List<Tweet> tweets = new ArrayList<Tweet>(resultsMap.size());
		for (Map<String, Object> item : items) {
			tweets.add(populateTweet(item));
		}

		return buildSearchResults(resultsMap, tweets);
	}

	SearchResults buildSearchResults(Map<String, Object> response, List<Tweet> tweets) {
		Number maxId = response.containsKey("max_id") ? (Number) response.get("max_id") : 0;
		Number sinceId = response.containsKey("since_id") ? (Number) response.get("since_id") : 0;
		return new SearchResults(tweets, maxId.longValue(), sinceId.longValue(), response.get("next_page") == null);
	}

	private Tweet populateTweet(Map<String, Object> item) {
		Tweet tweet = new Tweet();
		tweet.setId(NumberUtils.parseNumber(ObjectUtils.nullSafeToString(item.get("id")), Long.class));
		tweet.setFromUser(ObjectUtils.nullSafeToString(item.get("from_user")));
		tweet.setText(ObjectUtils.nullSafeToString(item.get("text")));
		tweet.setCreatedAt(toDate(ObjectUtils.nullSafeToString(item.get("created_at"))));
		tweet.setFromUserId(NumberUtils.parseNumber(ObjectUtils.nullSafeToString(item.get("from_user_id")), Long.class));
		Object toUserId = item.get("to_user_id");
		if (toUserId != null) {
			tweet.setToUserId(NumberUtils.parseNumber(ObjectUtils.nullSafeToString(toUserId), Long.class));
		}
		tweet.setLanguageCode(ObjectUtils.nullSafeToString(item.get("iso_language_code")));
		tweet.setProfileImageUrl(ObjectUtils.nullSafeToString(item.get("profile_image_url")));
		tweet.setSource(ObjectUtils.nullSafeToString(item.get("source")));
		return tweet;
	}

	private DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

	private Date toDate(String dateString) {
		try {
			return dateFormat.parse(dateString);
		} catch (ParseException e) {
			return null;
		}
	}

	private void handleResponseErrors(ResponseEntity<Map> response) throws SocialException {
		SocialException exception = statusCodeTranslator.translate(response);
		if (exception != null) {
			throw exception;
		}
	}

	private static final int DEFAULT_RESULTS_PER_PAGE = 50;

	static final String VERIFY_CREDENTIALS_URL = "http://api.twitter.com/1/account/verify_credentials.json";
	static final String FRIENDS_STATUSES_URL = "http://api.twitter.com/1/statuses/friends.json?screen_name={screen_name}";
	static final String SEARCH_URL = "http://search.twitter.com/search.json?q={query}&rpp={rpp}&page={page}";
	static final String TWEET_URL = "http://api.twitter.com/1/statuses/update.json?status={status}";
	static final String RETWEET_URL = "http://api.twitter.com/1/statuses/retweet/{tweet_id}.json";
}
