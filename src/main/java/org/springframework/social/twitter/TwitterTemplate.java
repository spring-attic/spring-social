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
import org.springframework.social.core.AccountNotConnectedException;
import org.springframework.social.core.ResponseStatusCodeTranslator;
import org.springframework.social.core.SocialException;
import org.springframework.social.oauth.OAuthSigningClientHttpRequestFactory;
import org.springframework.social.oauth1.ScribeOAuth1RequestSigner;
import org.springframework.util.NumberUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

/**
 * This is the central class for interacting with Twitter.
 * 
 * @author Craig Walls
 */
public class TwitterTemplate implements TwitterOperations {

	private final RestOperations restOperations;
	private ResponseStatusCodeTranslator statusCodeTranslator;

	/**
	 * Create a new instance of TwitterTemplate.
	 * 
	 * Because many Twitter operations require an OAuth access token,
	 * TwitterTemplate must be constructed with an {@link RestOperations} that
	 * is able to sign requests with OAuth authorization details. If it is given
	 * a {@link RestTemplate} or some other implementation of RestOperations
	 * that is not OAuth-enabled, then some operations (such as search) may
	 * work. Those that require authentication, however, will result in a
	 * {@link AccountNotConnectedException} being thrown.
	 * 
	 * @param restOperations
	 *            An {@link RestOperations} that will perform the calls against
	 *            Twitter's REST APIs.
	 */
	public TwitterTemplate(RestOperations restOperations) {
		this.restOperations = restOperations;
		this.statusCodeTranslator = new TwitterResponseStatusCodeTranslator();
	}

	/**
	 * Create a new instance of TwitterTemplate.
	 * 
	 * This constructor creates a new TwitterTemplate given the minimal amount
	 * of information required to sign a request and builds up a
	 * {@link RestOperations} internally using this information.
	 * 
	 * @param apiKey
	 *            the application's API key.
	 * @param apiSecret
	 *            the application's API secret.
	 * @param accessToken
	 *            the user's access token, given after successful OAuth
	 *            authentication.
	 * @param accessTokenSecret
	 *            the access token secret, given along with the access token
	 *            after successful OAuth authentication.
	 */
	public TwitterTemplate(String apiKey, String apiSecret, String accessToken, String accessTokenSecret) {
		this.restOperations = new RestTemplate(new OAuthSigningClientHttpRequestFactory(new ScribeOAuth1RequestSigner(
				apiKey, apiSecret, accessToken, accessTokenSecret)));
	}

	public String getScreenName() {
		Map<?, ?> response = restOperations.getForObject(VERIFY_CREDENTIALS_URL, Map.class);
		return (String) response.get("screen_name");
	}

	public List<String> getFollowed(String screenName) {
		@SuppressWarnings("unchecked")
		List<Map<String, String>> response = restOperations.getForObject(FRIENDS_STATUSES_URL, List.class,
				Collections.singletonMap("screen_name", screenName));
		List<String> friends = new ArrayList<String>(response.size());
		for (Map<String, String> item : response) {
			friends.add(item.get("screen_name"));
		}
		return friends;
	}

	public void tweet(String message) {
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> response = restOperations.postForEntity(TWEET_URL, null, Map.class,
				Collections.singletonMap("status", message));
		handleResponseErrors(response);
	}

	public void retweet(long tweetId) {
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> response = restOperations.postForEntity(RETWEET_URL, Collections.emptyMap(), Map.class,
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

		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> response = restOperations.getForEntity(searchUrl, Map.class, parameters);
		// handleResponseErrors(response);

		@SuppressWarnings("unchecked")
		Map<String, Object> resultsMap = response.getBody();

		@SuppressWarnings("unchecked")
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

	@SuppressWarnings("rawtypes")
	private void handleResponseErrors(ResponseEntity<Map> response) {
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
