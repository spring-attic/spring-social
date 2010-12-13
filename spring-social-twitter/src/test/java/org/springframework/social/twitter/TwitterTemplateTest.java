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
package org.springframework.social.twitter;

import static java.util.Collections.*;
import static org.junit.Assert.*;
import static org.junit.internal.matchers.IsCollectionContaining.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.social.twitter.TwitterResponseStatusCodeTranslator.*;
import static org.springframework.social.twitter.TwitterTemplate.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.social.core.AccountNotConnectedException;
import org.springframework.social.core.OperationNotPermittedException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;

/**
 * @author Craig Walls
 */
public class TwitterTemplateTest {
	private RestOperations restOperations;
	private TwitterTemplate twitter;

	@Before
	public void setup() {
		restOperations = mock(RestOperations.class);
		twitter = new TwitterTemplate();
		twitter.restOperations = restOperations;
	}

	@Test
	public void getProfileId() {
		when(restOperations.getForObject(eq(VERIFY_CREDENTIALS_URL), eq(Map.class))).thenReturn(
				singletonMap("screen_name", "habuma"));
		assertEquals("habuma", twitter.getProfileId());
	}

	@Test
	public void getFollowed() {
		List<Map<String, String>> friendsList = new ArrayList<Map<String,String>>();
		friendsList.add(singletonMap("screen_name", "kdonald"));
		friendsList.add(singletonMap("screen_name", "royclarkson"));
		friendsList.add(singletonMap("screen_name", "springrod"));
		when(restOperations.getForObject(eq(FRIENDS_STATUSES_URL), eq(List.class), 
				eq(singletonMap("screen_name", "habuma")))).thenReturn(friendsList);

		List<String> followed = twitter.getFriends("habuma");
		assertEquals(3, followed.size());
		assertThat(followed, hasItem("kdonald"));
		assertThat(followed, hasItem("royclarkson"));
		assertThat(followed, hasItem("springrod"));
	}

	@Test
	public void updateStatus() {
		testTweet(new ResponseEntity<Map>(emptyMap(), OK));
	}

	@Test
	public void updateStatus_withLocation() {
		testTweetWithLocation(new ResponseEntity<Map>(emptyMap(), OK));
	}

	@Test(expected = DuplicateTweetException.class)
	public void updateStatus_duplicateTweet() {
		testTweet(new ResponseEntity<Map>(singletonMap("error", DUPLICATE_STATUS_TEXT), FORBIDDEN));
	}

	@Test(expected = OperationNotPermittedException.class)
	public void updateStatus_forbidden() {
		testTweet(new ResponseEntity<Map>(singletonMap("error", "You can't do that!"), FORBIDDEN));
	}

	@Test(expected = AccountNotConnectedException.class)
	public void updateStatus_unauthorized() {
		testTweet(new ResponseEntity<Map>(singletonMap("error", "who are you?"), UNAUTHORIZED));
	}

	@Test
	public void retweet() {
		testRetweet(new ResponseEntity<Map>(emptyMap(), OK));
	}

	@Test(expected = DuplicateTweetException.class)
	public void retweet_duplicateTweet() {
		testRetweet(new ResponseEntity<Map>(singletonMap("error", DUPLICATE_STATUS_TEXT), FORBIDDEN));
	}

	@Test(expected = OperationNotPermittedException.class)
	public void retweet_forbidden() {
		testRetweet(new ResponseEntity<Map>(singletonMap("error", "You can't do that!"), FORBIDDEN));
	}

	@Test(expected = AccountNotConnectedException.class)
	public void retweet_unauthorized() {
		testRetweet(new ResponseEntity<Map>(singletonMap("error", "who are you?"), UNAUTHORIZED));
	}

	@Test
	public void search_queryOnly() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("query", "Spring");
		parameters.put("rpp", String.valueOf(DEFAULT_RESULTS_PER_PAGE));
		parameters.put("page", String.valueOf(1));
		ResponseEntity<Map> response = buildSearchResponse();
		when(restOperations.getForEntity(eq(SEARCH_URL), eq(Map.class), eq(parameters))).thenReturn(response);
		SearchResults search = twitter.search("Spring");

		assertEquals(2, search.getTweets().size());

		verify(restOperations).getForEntity(eq(SEARCH_URL), eq(Map.class), eq(parameters));
	}

	@Test
	public void search_pageAndResultsPerPage() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("query", "Spring");
		parameters.put("rpp", String.valueOf(42));
		parameters.put("page", String.valueOf(5));
		ResponseEntity<Map> response = buildSearchResponse();
		when(restOperations.getForEntity(eq(SEARCH_URL), eq(Map.class), eq(parameters))).thenReturn(response);
		SearchResults search = twitter.search("Spring", 5, 42);

		assertEquals(2, search.getTweets().size());

		verify(restOperations).getForEntity(eq(SEARCH_URL), eq(Map.class), eq(parameters));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void search_sinceAndMaxId() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("query", "Spring");
		parameters.put("rpp", String.valueOf(42));
		parameters.put("page", String.valueOf(5));
		parameters.put("since", String.valueOf(99));
		parameters.put("max", String.valueOf(199));
		ResponseEntity<Map> response = buildSearchResponse();
		response.getBody().put("since_id", 99);
		response.getBody().put("max_id", 199);
		when(restOperations.getForEntity(eq(SEARCH_URL + "&since_id={since}&max_id={max}"), eq(Map.class),
						eq(parameters))).thenReturn(response);
		SearchResults search = twitter.search("Spring", 5, 42, 99, 199);

		assertEquals(2, search.getTweets().size());
		assertEquals(99, search.getSinceId());
		assertEquals(199, search.getMaxId());

		verify(restOperations).getForEntity(eq(SEARCH_URL + "&since_id={since}&max_id={max}"), eq(Map.class),
				eq(parameters));
	}

	private ResponseEntity<Map> buildSearchResponse() {
		Map<String, Object> resultsMap = new HashMap<String, Object>();
		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
		items.add(tweetItemMap("123", "habuma", "Spring is cool", "Tue, 26 Oct 2010 14:46:57 +0000",
				"1249690", "en", "http://a3.twimg.com/profile_images/55946103/me_normal.jpg", "Twitter for iPhone"));
		items.add(tweetItemMap("456", "habuma", "Had lots of fun at #s2gx", "Mon, 25 Oct 2010 11:42:59 +0000",
				"1249690", "en", "http://a3.twimg.com/profile_images/55946103/me_normal.jpg", "Greenhouse"));
		resultsMap.put("results", items);
		ResponseEntity<Map> response = new ResponseEntity<Map>(resultsMap, OK);
		return response;
	}

	private Map<String, Object> tweetItemMap(String id, String fromUser, String text, String createdAt,
			String fromUserId, String isoLanguageCode, String profileImageUrl, String source) {
		Map<String, Object> item = new HashMap<String, Object>();
		item.put("id", id);
		item.put("from_user", fromUser);
		item.put("text", text);
		item.put("created_at", createdAt);
		item.put("from_user_id", fromUserId);
		item.put("iso_language_code", isoLanguageCode);
		item.put("profile_image_url", profileImageUrl);
		item.put("source", source);
		return item;
	}

	private void testRetweet(ResponseEntity<Map> response) {
		when(restOperations.postForEntity(eq(RETWEET_URL), eq(""), eq(Map.class), eq(singletonMap("tweet_id", "12345"))))
				.thenReturn(response);
		twitter.retweet(12345L);
		verify(restOperations).postForEntity(eq(RETWEET_URL), eq(""), eq(Map.class),
				eq(singletonMap("tweet_id", "12345")));
	}

	private void testTweet(ResponseEntity<Map> response) {
		MultiValueMap<String, Object> tweetParams = new LinkedMultiValueMap<String, Object>();
		tweetParams.add("status", "Hello Spring!");
		when(restOperations.postForEntity(eq(TWEET_URL), eq(tweetParams), eq(Map.class))).thenReturn(response);

		twitter.updateStatus("Hello Spring!");

		verify(restOperations).postForEntity(eq(TWEET_URL), eq(tweetParams), eq(Map.class));
	}

	private void testTweetWithLocation(ResponseEntity<Map> response) {
		MultiValueMap<String, Object> tweetParams = new LinkedMultiValueMap<String, Object>();
		tweetParams.add("status", "Hello Spring!");
		tweetParams.add("lat", "32.975");
		tweetParams.add("long", "-96.72");
		when(restOperations.postForEntity(eq(TWEET_URL), eq(tweetParams), eq(Map.class))).thenReturn(response);

		twitter.updateStatus("Hello Spring!", new StatusDetails().setLocation(32.975f, -96.72f));

		verify(restOperations).postForEntity(eq(TWEET_URL), eq(tweetParams), eq(Map.class));
	}
}
