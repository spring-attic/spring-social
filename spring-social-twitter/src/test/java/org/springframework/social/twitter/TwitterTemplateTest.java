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
package org.springframework.social.twitter;

import static org.junit.Assert.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.client.test.RequestMatchers.*;
import static org.springframework.web.client.test.ResponseCreators.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.social.AccountNotConnectedException;
import org.springframework.social.OperationNotPermittedException;
import org.springframework.web.client.test.MockRestServiceServer;

/**
 * @author Craig Walls
 */
public class TwitterTemplateTest {

	private TwitterTemplate twitter;
	private MockRestServiceServer mockServer;
	private HttpHeaders responseHeaders;

	@Before
	public void setup() {
		twitter = new TwitterTemplate();
		mockServer = MockRestServiceServer.createServer(twitter.getRestTemplate());
		responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
	}

	@Test
	public void getProfileId() {
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		mockServer.expect(requestTo("https://api.twitter.com/1/account/verify_credentials.json"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("verify-credentials.json", getClass()), responseHeaders));

		assertEquals("habuma", twitter.getProfileId());
	}

	@Test
	public void getUserProfile() throws Exception {
		mockServer.expect(requestTo("https://api.twitter.com/1/account/verify_credentials.json"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("verify-credentials.json", getClass()), responseHeaders));
		mockServer.expect(requestTo("https://api.twitter.com/1/users/show.json?screen_name=habuma"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("twitter-profile.json", getClass()), responseHeaders));

		TwitterProfile profile = twitter.getUserProfile();
		assertEquals(12345, profile.getId());
		assertEquals("habuma", profile.getScreenName());
		assertEquals("Craig Walls", profile.getName());
		assertEquals("Spring Guy", profile.getDescription());
		assertEquals("Plano, TX", profile.getLocation());
		assertEquals("http://www.springsource.org", profile.getUrl());
		assertEquals("http://a3.twimg.com/profile_images/1205746571/me2_300.jpg", profile.getProfileImageUrl());
	}
	
	@Test
	public void getUserProfile_userId() throws Exception {
		mockServer.expect(requestTo("https://api.twitter.com/1/users/show.json?user_id=12345"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("twitter-profile.json", getClass()), responseHeaders));

		TwitterProfile profile = twitter.getUserProfile(12345);
		assertEquals(12345, profile.getId());
		assertEquals("habuma", profile.getScreenName());
		assertEquals("Craig Walls", profile.getName());
		assertEquals("Spring Guy", profile.getDescription());
		assertEquals("Plano, TX", profile.getLocation());
		assertEquals("http://www.springsource.org", profile.getUrl());
		assertEquals("http://a3.twimg.com/profile_images/1205746571/me2_300.jpg", profile.getProfileImageUrl());
	}

	@Test
	public void getFriends() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/friends.json?screen_name=habuma"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("friends.json", getClass()), responseHeaders));

		List<String> friends = twitter.getFriends("habuma");
		assertEquals(2, friends.size());
		assertTrue(friends.contains("kdonald"));
		assertTrue(friends.contains("rclarkson"));
	}

	@Test
	public void updateStatus() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/update.json"))
				.andExpect(method(POST))
				.andExpect(body("status=Test+Message"))
				.andRespond(withResponse("{}", responseHeaders));

		twitter.updateStatus("Test Message");

		mockServer.verify();
	}

	@Test
	public void updateStatus_withLocation() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/update.json"))
				.andExpect(method(POST))
				.andExpect(body("status=Test+Message&long=-111.2&lat=123.1"))
				.andRespond(withResponse("{}", responseHeaders));

		StatusDetails details = new StatusDetails();
		details.setLocation(123.1f, -111.2f);
		twitter.updateStatus("Test Message", details);

		mockServer.verify();
	}

	@Test(expected = DuplicateTweetException.class)
	public void updateStatus_duplicateTweet() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/update.json"))
				.andExpect(method(POST))
				.andExpect(body("status=Test+Message"))
				.andRespond(withResponse("{\"error\":\"You already said that\"}", responseHeaders, FORBIDDEN, ""));

		twitter.updateStatus("Test Message");
	}

	@Test(expected = OperationNotPermittedException.class)
	public void updateStatus_forbidden() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/update.json"))
				.andExpect(method(POST))
				.andExpect(body("status=Test+Message"))
				.andRespond(withResponse("{\"error\":\"Forbidden\"}", responseHeaders, FORBIDDEN, ""));

		twitter.updateStatus("Test Message");
	}

	@Test(expected = AccountNotConnectedException.class)
	public void updateStatus_unauthorized() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/update.json"))
				.andExpect(method(POST))
				.andExpect(body("status=Test+Message"))
				.andRespond(withResponse("{\"error\":\"Not authenticated\"}", responseHeaders, UNAUTHORIZED, ""));

		twitter.updateStatus("Test Message");
	}

	@Test
	public void retweet() {
		mockServer.expect(requestTo("https://api.twitter.com/1//statuses/retweet/12345.json"))
				.andExpect(method(POST))
				.andRespond(withResponse("{}", responseHeaders));

		twitter.retweet(12345);

		mockServer.verify();
	}

	@Test(expected=DuplicateTweetException.class)
	public void retweet_duplicateTweet() {
		mockServer.expect(requestTo("https://api.twitter.com/1//statuses/retweet/12345.json"))
				.andExpect(method(POST))
				.andRespond(withResponse("{\"error\":\"You already said that\"}", responseHeaders, FORBIDDEN, ""));

		twitter.retweet(12345);
	}

	@Test(expected = OperationNotPermittedException.class)
	public void retweet_forbidden() {
		mockServer.expect(requestTo("https://api.twitter.com/1//statuses/retweet/12345.json"))
				.andExpect(method(POST))
				.andRespond(withResponse("{\"error\":\"Forbidden\"}", responseHeaders, FORBIDDEN, ""));

		twitter.retweet(12345);
	}

	@Test(expected = AccountNotConnectedException.class)
	public void retweet_unauthorized() {
		mockServer.expect(requestTo("https://api.twitter.com/1//statuses/retweet/12345.json"))
				.andExpect(method(POST))
				.andRespond(withResponse("{\"error\":\"Not authenticated\"}", responseHeaders, UNAUTHORIZED, ""));

		twitter.retweet(12345);
	}

	@Test
	public void getDirectMessagesReceived() {
		mockServer.expect(requestTo("https://api.twitter.com/1/direct_messages.json"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("messages.json", getClass()), responseHeaders));

		List<DirectMessage> messages = twitter.getDirectMessagesReceived();
		assertEquals(2, messages.size());
		assertEquals(12345, messages.get(0).getId());
		assertEquals("Hello there", messages.get(0).getText());
		assertEquals(24680, messages.get(0).getSenderId());
		assertEquals("rclarkson", messages.get(0).getSenderScreenName());
		assertEquals(13579, messages.get(0).getRecipientId());
		assertEquals("kdonald", messages.get(0).getRecipientScreenName());
		// assertTimelineDateEquals("Tue Jul 13 17:38:21 +0000 2010", messages.get(0).getCreatedAt());
		assertEquals(23456, messages.get(1).getId());
		assertEquals("Back at ya", messages.get(1).getText());
		assertEquals(13579, messages.get(1).getSenderId());
		assertEquals("kdonald", messages.get(1).getSenderScreenName());
		assertEquals(24680, messages.get(1).getRecipientId());
		assertEquals("rclarkson", messages.get(1).getRecipientScreenName());
	}

	@Test
	public void sendDirectMessage_toScreenName() {
		mockServer.expect(requestTo("https://api.twitter.com/1/direct_messages/new.json")).andExpect(method(POST))
				.andExpect(body("screen_name=habuma&text=Hello+there%21"))
				.andRespond(withResponse("{}", responseHeaders));
		twitter.sendDirectMessage("habuma", "Hello there!");
		mockServer.verify();
	}

	@Test
	public void sendDirectMessage_toUserId() {
		mockServer.expect(requestTo("https://api.twitter.com/1/direct_messages/new.json")).andExpect(method(POST))
				.andExpect(body("user_id=11223&text=Hello+there%21")).andRespond(withResponse("{}", responseHeaders));
		twitter.sendDirectMessage(11223, "Hello there!");
		mockServer.verify();
	}

	@Test
	public void getMentions() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/mentions.json"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("timeline.json", getClass()), responseHeaders));
		List<Tweet> mentions = twitter.getMentions();
		assertTimelineTweets(mentions);
	}

	@Test
	public void getPublicTimeline() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/public_timeline.json"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("timeline.json", getClass()), responseHeaders));
		List<Tweet> timeline = twitter.getPublicTimeline();
		assertTimelineTweets(timeline);
	}

	@Test
	public void getHomeTimeline() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/home_timeline.json"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("timeline.json", getClass()), responseHeaders));
		List<Tweet> timeline = twitter.getHomeTimeline();
		assertTimelineTweets(timeline);
	}

	@Test
	public void getFriendsTimeline() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/friends_timeline.json"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("timeline.json", getClass()), responseHeaders));
		List<Tweet> timeline = twitter.getFriendsTimeline();
		assertTimelineTweets(timeline);
	}

	@Test
	public void getUserTimeline() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/user_timeline.json"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("timeline.json", getClass()), responseHeaders));
		List<Tweet> timeline = twitter.getUserTimeline();
		assertTimelineTweets(timeline);
	}

	@Test
	public void getUserTimeline_forScreenName() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/user_timeline.json?screen_name=habuma"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("timeline.json", getClass()), responseHeaders));
		List<Tweet> timeline = twitter.getUserTimeline("habuma");
		assertTimelineTweets(timeline);
	}

	@Test
	public void getUserTimeline_forUserId() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/user_timeline.json?user_id=12345"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("timeline.json", getClass()), responseHeaders));
		List<Tweet> timeline = twitter.getUserTimeline(12345);
		assertTimelineTweets(timeline);
	}

	@Test
	public void search_queryOnly() {
		mockServer.expect(requestTo("https://search.twitter.com/search.json?q=%23spring&rpp=50&page=1"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("search.json", getClass()), responseHeaders));
		SearchResults searchResults = twitter.search("#spring");
		assertEquals(10, searchResults.getSinceId());
		assertEquals(999, searchResults.getMaxId());
		List<Tweet> tweets = searchResults.getTweets();
		assertSearchTweets(tweets);
	}

	@Test
	public void search_pageAndResultsPerPage() {
		mockServer.expect(requestTo("https://search.twitter.com/search.json?q=%23spring&rpp=10&page=2"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("search.json", getClass()), responseHeaders));
		SearchResults searchResults = twitter.search("#spring", 2, 10);
		assertEquals(10, searchResults.getSinceId());
		assertEquals(999, searchResults.getMaxId());
		List<Tweet> tweets = searchResults.getTweets();
		assertSearchTweets(tweets);
	}

	@Test
	public void search_sinceAndMaxId() {
		mockServer.expect(requestTo("https://search.twitter.com/search.json?q=%23spring&rpp=10&page=2&since_id=123&max_id=54321"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("search.json", getClass()), responseHeaders));
		SearchResults searchResults = twitter.search("#spring", 2, 10, 123, 54321);
		assertEquals(10, searchResults.getSinceId());
		assertEquals(999, searchResults.getMaxId());
		List<Tweet> tweets = searchResults.getTweets();
		assertSearchTweets(tweets);
	}

	// test helpers
	private void assertTimelineTweets(List<Tweet> tweets) {
		assertEquals(2, tweets.size());
		Tweet tweet1 = tweets.get(0);
		assertEquals(12345, tweet1.getId());
		assertEquals("Tweet 1", tweet1.getText());
		assertEquals("habuma", tweet1.getFromUser());
		assertEquals(112233, tweet1.getFromUserId());
		assertEquals("http://a3.twimg.com/profile_images/1205746571/me2_300.jpg", tweet1.getProfileImageUrl());
		assertEquals("Spring Social Showcase", tweet1.getSource());
		assertEquals(1279042701000L, tweet1.getCreatedAt().getTime());
		Tweet tweet2 = tweets.get(1);
		assertEquals(54321, tweet2.getId());
		assertEquals("Tweet 2", tweet2.getText());
		assertEquals("rclarkson", tweet2.getFromUser());
		assertEquals(332211, tweet2.getFromUserId());
		assertEquals("http://a3.twimg.com/profile_images/1205746571/me2_300.jpg", tweet2.getProfileImageUrl());
		assertEquals("Twitter", tweet2.getSource());
		assertEquals(1279654701000L, tweet2.getCreatedAt().getTime());
	}

	private void assertSearchTweets(List<Tweet> tweets) {
		assertTimelineTweets(tweets);
		assertEquals("en", tweets.get(0).getLanguageCode());
		assertEquals("de", tweets.get(1).getLanguageCode());
	}

	// TODO : FIGURE OUT A BETTER WAY TO TEST DATES!!!
	private DateFormat timelineDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZ yyyy", Locale.ENGLISH);

	private void assertTimelineDateEquals(String expected, Date actual) {
		timelineDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		assertEquals(expected, timelineDateFormat.format(actual));
	}
}
