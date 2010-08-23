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
import org.springframework.social.oauth.AccessTokenProvider;
import org.springframework.social.oauth.OAuthHelper;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.NumberUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

public class TwitterTemplate implements TwitterOperations {

	private static final int DEFAULT_RESULTS_PER_PAGE = 50;

	private RestTemplate restTemplate;

	private DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

	private final OAuthHelper oauthHelper;

	public TwitterTemplate(OAuthHelper oauthHelper) {
		this.oauthHelper = oauthHelper;
		this.restTemplate = new RestTemplate();
	}

	public String getScreenName(AccessTokenProvider<?> tokenProvider) {
		Map<String, String> parameters = Collections.emptyMap();
		Map<String, String> response = exchangeForMap(HttpMethod.GET, VERIFY_CREDENTIALS_URL, parameters, tokenProvider);
		return response.get("screen_name");
	}

	public List<String> getFollowed(String screenName) {
		Map<String, String> parameters = Collections.singletonMap("screen_name", screenName);
		List<Map<String, String>> response = exchangeForList(HttpMethod.GET, FRIENDS_STATUSES_URL, parameters, null);
		List<String> friends = new ArrayList<String>(response.size());
		for (Map<String, String> item : response) {
			friends.add(item.get("screen_name"));
		}
		return friends;
	}

	public void tweet(String message, AccessTokenProvider<?> tokenProvider) {
		Map<String, String> parameters = Collections.singletonMap("status", message);
		exchangeForMap(HttpMethod.POST, TWEET_URL, parameters, tokenProvider);
	}

	public void retweet(long tweetId, AccessTokenProvider<?> tokenProvider) {
		Map<String, String> urlVariable = Collections.singletonMap("tweet_id", Long.toString(tweetId));
		Map<String, String> queryParameters = Collections.emptyMap();
		exchangeForMap(HttpMethod.POST, RETWEET_URL, urlVariable, queryParameters, tokenProvider);
	}

	public SearchResults search(String query, AccessTokenProvider<?> tokenProvider) {
		return search(query, 1, DEFAULT_RESULTS_PER_PAGE, 0, 0, tokenProvider);
	}

	public SearchResults search(String query, int page, int resultsPerPage) {
		return search(query, page, resultsPerPage, 0, 0, null);
	}

	public SearchResults search(String query, int page, int resultsPerPage, AccessTokenProvider<?> tokenProvider) {
		return search(query, page, resultsPerPage, 0, 0, tokenProvider);
	}

	public SearchResults search(String query, int page, int resultsPerPage, int sinceId, int maxId,
			AccessTokenProvider<?> tokenProvider) {
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

		Map<String, Object> response = exchangeForMap(HttpMethod.GET, searchUrl, parameters, tokenProvider);
		List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("results");
		List<Tweet> tweets = new ArrayList<Tweet>(response.size());
		for (Map<String, Object> item : items) {
			tweets.add(populateTweet(item));
		}

		SearchResults results = new SearchResults();
		results.setMaxId(NumberUtils.parseNumber(ObjectUtils.nullSafeToString(response.get("max_id")), Long.class));
		results.setSinceId(NumberUtils.parseNumber(ObjectUtils.nullSafeToString(response.get("since_id")), Long.class));
		results.setLastPage(response.get("next_page") == null);
		results.setTweets(tweets);

		return results;
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

	private Map exchangeForMap(HttpMethod method, String url, Map<String, String> queryParameters,
			AccessTokenProvider<?> tokenProvider) {
		Map<String, String> urlVariable = Collections.emptyMap();
		return exchangeForMap(method, url, urlVariable, queryParameters, tokenProvider);
	}

	private Map exchangeForMap(HttpMethod method, String url, Map<String, String> urlVariable,
			Map<String, String> queryParameters, AccessTokenProvider<?> tokenProvider) {
		return exchange(method, url, urlVariable, queryParameters, Map.class, tokenProvider);
	}

	private List exchangeForList(HttpMethod method, String url, Map<String, String> queryParameters,
			AccessTokenProvider<?> tokenProvider) {
		Map<String, String> urlVariable = Collections.emptyMap();
		return exchange(method, url, urlVariable, queryParameters, List.class, tokenProvider);
	}

	private <T> T exchange(HttpMethod method, String twitterUrl, Map<String, String> urlVariable,
			Map<String, String> queryParameters, Class<T> responseType, AccessTokenProvider<?> tokenProvider) {

		HttpHeaders headers = buildRequestHeaders(method, twitterUrl, urlVariable, queryParameters, tokenProvider);
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

	private HttpHeaders buildRequestHeaders(HttpMethod method, String twitterUrl, Map<String, String> urlVariable,
			Map<String, String> queryParameters,
			AccessTokenProvider<?> tokenProvider) {
		try {
			UriTemplate uriTemplate = new UriTemplate(twitterUrl);
			Map<String, String> combinedParamaters = new HashMap<String, String>(urlVariable);
			combinedParamaters.putAll(queryParameters);
			String expandedUrl = uriTemplate.expand(combinedParamaters).toString();

			HttpHeaders headers = new HttpHeaders();
			if (tokenProvider != null) {
				String authorizationHeader = oauthHelper.buildAuthorizationHeader(tokenProvider, method, expandedUrl,
						"Twitter", queryParameters);
				headers.add("Authorization", authorizationHeader);
			}
			headers.add("Content-Type", "application/x-www-form-urlencoded");
			return headers;
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