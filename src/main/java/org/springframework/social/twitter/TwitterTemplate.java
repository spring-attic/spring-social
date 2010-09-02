package org.springframework.social.twitter;

import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.social.oauth.OAuthTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.NumberUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

/**
 * Template class that simplifies interaction with Twitter. It handles the
 * details of setting the OAuth Authorization header in the request, setting
 * request parameters, making the Twitter API call (via {@link RestTemplate}),
 * extracting results, and handling any errors that may occur in the process.
 * 
 * An implementation of OAuthTemplate must be provided at construction time to
 * help assemble the OAuth Authorization header.
 * 
 * @author Craig Walls
 * 
 * @see OAuthTemplate
 */
public class TwitterTemplate implements TwitterOperations {

	private static final int DEFAULT_RESULTS_PER_PAGE = 50;

	private RestTemplate restTemplate;

	private DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

	private final OAuthTemplate oauthHelper;

	public TwitterTemplate(OAuthTemplate oauthHelper) {
		this.oauthHelper = oauthHelper;
		this.restTemplate = new RestTemplate();
	}

	public String getScreenName() {
		Map<String, String> parameters = Collections.emptyMap();
		Map<String, String> response = exchangeForMap(HttpMethod.GET, VERIFY_CREDENTIALS_URL, parameters);
		return response.get("screen_name");
	}

	public List<String> getFollowed(String screenName) {
		Map<String, String> parameters = Collections.singletonMap("screen_name", screenName);
		List<Map<String, String>> response = exchangeForList(HttpMethod.GET, FRIENDS_STATUSES_URL, parameters);
		List<String> friends = new ArrayList<String>(response.size());
		for (Map<String, String> item : response) {
			friends.add(item.get("screen_name"));
		}
		return friends;
	}

	public void tweet(String message) {
		Map<String, String> parameters = Collections.singletonMap("status", message);
		exchangeForMap(HttpMethod.POST, TWEET_URL, parameters);
	}

	public void retweet(long tweetId) {
		Map<String, String> urlVariable = Collections.singletonMap("tweet_id", Long.toString(tweetId));
		Map<String, String> queryParameters = Collections.emptyMap();
		exchangeForMap(HttpMethod.POST, RETWEET_URL, urlVariable, queryParameters);
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

		Map<String, Object> response = exchangeForMap(HttpMethod.GET, searchUrl, parameters);
		List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("results");
		List<Tweet> tweets = new ArrayList<Tweet>(response.size());
		for (Map<String, Object> item : items) {
			tweets.add(populateTweet(item));
		}

		return buildSearchResults(response, tweets);
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

	// internal helpers

	private Map exchangeForMap(HttpMethod method, String url, Map<String, String> queryParameters) {
		Map<String, String> urlVariable = Collections.emptyMap();
		return exchangeForMap(method, url, urlVariable, queryParameters);
	}

	private Map exchangeForMap(HttpMethod method, String url, Map<String, String> urlVariable,
			Map<String, String> queryParameters) {
		return exchange(method, url, urlVariable, queryParameters, Map.class);
	}

	private List exchangeForList(HttpMethod method, String url, Map<String, String> queryParameters) {
		Map<String, String> urlVariable = Collections.emptyMap();
		return exchange(method, url, urlVariable, queryParameters, List.class);
	}

	private <T> T exchange(HttpMethod method, String twitterUrl, Map<String, String> urlVariable,
			Map<String, String> queryParameters, Class<T> responseType) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/x-www-form-urlencoded");
		addAuthorizationHeader(headers, method, twitterUrl, urlVariable, queryParameters);

		MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
		HashMap<String, Object> uriVariables = new HashMap<String, Object>();
		if (method.equals(HttpMethod.POST) || method.equals(HttpMethod.PUT)) {
			for (String key : queryParameters.keySet()) {
				form.add(key, queryParameters.get(key));
			}
		} else {
			for (String key : queryParameters.keySet()) {
				uriVariables.put(key, queryParameters.get(key));
			}
		}

		for (String key : urlVariable.keySet()) {
			uriVariables.put(key, urlVariable.get(key));
		}

		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(form,
				headers);
		ResponseEntity<T> exchange = restTemplate.exchange(twitterUrl, method, requestEntity, responseType, uriVariables);
		return exchange.getBody();
	}

	private void addAuthorizationHeader(HttpHeaders headers, HttpMethod method, String twitterUrl,
			Map<String, String> urlVariables, Map<String, String> queryParameters) {
		try {
			UriTemplate uriTemplate = new UriTemplate(twitterUrl);
			Map<String, String> combinedParamaters = new HashMap<String, String>(urlVariables);
			combinedParamaters.putAll(queryParameters);
			String expandedUrl = uriTemplate.expand(combinedParamaters).toString();

			String authorizationHeader = oauthHelper.buildAuthorizationHeader(method, expandedUrl, queryParameters);
			if (authorizationHeader != null) {
				headers.add("Authorization", authorizationHeader);
			}
		} catch (MalformedURLException e) {
			throw new RestClientException("Malformed URL: " + twitterUrl, e);
		}
	}

	private Date toDate(String dateString) {
		try {
			return dateFormat.parse(dateString);
		} catch (ParseException e) {
			return null;
		}
	}

	// to support unit testing
	void setRestTemplate(RestTemplate mock) {
		this.restTemplate = mock;
	}

	static final String VERIFY_CREDENTIALS_URL = "http://api.twitter.com/1/account/verify_credentials.json";
	static final String FRIENDS_STATUSES_URL = "http://api.twitter.com/1/statuses/friends.json?screen_name={screen_name}";
	static final String TWEET_URL = "http://api.twitter.com/1/statuses/update.json";
	static final String RETWEET_URL = "http://api.twitter.com/1/statuses/retweet/{tweet_id}.json";
	static final String SEARCH_URL = "http://search.twitter.com/search.json?q={query}&rpp={rpp}&page={page}";

}