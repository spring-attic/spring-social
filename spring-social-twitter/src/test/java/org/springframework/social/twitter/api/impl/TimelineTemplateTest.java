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
package org.springframework.social.twitter.api.impl;

import static org.junit.Assert.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.social.test.client.RequestMatchers.*;
import static org.springframework.social.test.client.ResponseCreators.*;

import java.util.List;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.social.OperationNotPermittedException;
import org.springframework.social.twitter.api.DuplicateTweetException;
import org.springframework.social.twitter.api.StatusDetails;
import org.springframework.social.twitter.api.StatusLengthException;
import org.springframework.social.twitter.api.Tweet;
import org.springframework.social.twitter.api.TwitterProfile;


/**
 * @author Craig Walls
 */
public class TimelineTemplateTest extends AbstractTwitterApiTest {

	@Test
	public void updateStatus() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/update.json"))
				.andExpect(method(POST))
				.andExpect(body("status=Test+Message"))
				.andRespond(withResponse("{}", responseHeaders));

		twitter.timelineOperations().updateStatus("Test Message");

		mockServer.verify();
	}

	@Test
	public void updateStatus_withLocation() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/update.json"))
				.andExpect(method(POST))
				.andExpect(body("status=Test+Message&lat=123.1&long=-111.2"))
				.andRespond(withResponse("{}", responseHeaders));

		StatusDetails details = new StatusDetails();
		details.setLocation(123.1f, -111.2f);
		twitter.timelineOperations().updateStatus("Test Message", details);

		mockServer.verify();
	}

	@Test(expected = DuplicateTweetException.class)
	public void updateStatus_duplicateTweet() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/update.json"))
				.andExpect(method(POST))
				.andExpect(body("status=Test+Message"))
				.andRespond(withResponse("{\"error\":\"You already said that\"}", responseHeaders, FORBIDDEN, ""));

		twitter.timelineOperations().updateStatus("Test Message");
	}
	
	@Test(expected=StatusLengthException.class)
	public void updateStatus_tweetTooLong() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/update.json"))
			.andExpect(method(POST))
			.andExpect(body("status=Really+long+message"))
			.andRespond(withResponse("{\"error\":\"Status is over 140 characters.\"}", responseHeaders, HttpStatus.FORBIDDEN, ""));
		twitter.timelineOperations().updateStatus("Really long message");
	}
	
	@Test(expected = OperationNotPermittedException.class)
	public void updateStatus_forbidden() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/update.json"))
				.andExpect(method(POST))
				.andExpect(body("status=Test+Message"))
				.andRespond(withResponse("{\"error\":\"Forbidden\"}", responseHeaders, FORBIDDEN, ""));

		twitter.timelineOperations().updateStatus("Test Message");
	}

	@Test
	public void deleteStatus() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/destroy/12345.json"))
			.andExpect(method(DELETE))
			.andRespond(withResponse("{}", responseHeaders));		
		twitter.timelineOperations().deleteStatus(12345L);
		mockServer.verify();
	}
	
	@Test
	public void retweet() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/retweet/12345.json"))
				.andExpect(method(POST))
				.andRespond(withResponse("{}", responseHeaders));

		twitter.timelineOperations().retweet(12345);

		mockServer.verify();
	}

	@Test(expected=DuplicateTweetException.class)
	public void retweet_duplicateTweet() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/retweet/12345.json"))
				.andExpect(method(POST))
				.andRespond(withResponse("{\"error\":\"You already said that\"}", responseHeaders, FORBIDDEN, ""));

		twitter.timelineOperations().retweet(12345);
	}

	@Test(expected = OperationNotPermittedException.class)
	public void retweet_forbidden() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/retweet/12345.json"))
				.andExpect(method(POST))
				.andRespond(withResponse("{\"error\":\"Forbidden\"}", responseHeaders, FORBIDDEN, ""));

		twitter.timelineOperations().retweet(12345);
	}

	@Test
	public void getMentions() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/mentions.json"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("timeline.json", getClass()), responseHeaders));
		List<Tweet> mentions = twitter.timelineOperations().getMentions();
		assertTimelineTweets(mentions);
	}

	@Test
	public void getPublicTimeline() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/public_timeline.json"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("timeline.json", getClass()), responseHeaders));
		List<Tweet> timeline = twitter.timelineOperations().getPublicTimeline();
		assertTimelineTweets(timeline);
	}

	@Test
	public void getHomeTimeline() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/home_timeline.json"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("timeline.json", getClass()), responseHeaders));
		List<Tweet> timeline = twitter.timelineOperations().getHomeTimeline();
		assertTimelineTweets(timeline);
	}

	@Test
	public void getFriendsTimeline() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/friends_timeline.json"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("timeline.json", getClass()), responseHeaders));
		List<Tweet> timeline = twitter.timelineOperations().getFriendsTimeline();
		assertTimelineTweets(timeline);
	}

	@Test
	public void getUserTimeline() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/user_timeline.json"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("timeline.json", getClass()), responseHeaders));
		List<Tweet> timeline = twitter.timelineOperations().getUserTimeline();
		assertTimelineTweets(timeline);
	}

	@Test
	public void getUserTimeline_forScreenName() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/user_timeline.json?screen_name=habuma"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("timeline.json", getClass()), responseHeaders));
		List<Tweet> timeline = twitter.timelineOperations().getUserTimeline("habuma");
		assertTimelineTweets(timeline);
	}

	@Test
	public void getUserTimeline_forUserId() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/user_timeline.json?user_id=12345"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("timeline.json", getClass()), responseHeaders));
		List<Tweet> timeline = twitter.timelineOperations().getUserTimeline(12345);
		assertTimelineTweets(timeline);
	}
	
	@Test
	public void getFavorites() {
		mockServer.expect(requestTo("https://api.twitter.com/1/favorites.json"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("favorite.json", getClass()), responseHeaders));
		List<Tweet> timeline = twitter.timelineOperations().getFavorites();
		assertTimelineTweets(timeline);
	}

	@Test
	public void addToFavorites() {
		mockServer.expect(requestTo("https://api.twitter.com/1/favorites/create/42.json"))
			.andExpect(method(POST))
			.andRespond(withResponse("{}", responseHeaders));
		twitter.timelineOperations().addToFavorites(42L);
		mockServer.verify();
	}
	
	@Test
	public void getRetweetedBy() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/42/retweeted_by.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("retweeted-by.json", getClass()), responseHeaders));
		List<TwitterProfile> retweetedBy = twitter.timelineOperations().getRetweetedBy(42L);
		assertEquals(2, retweetedBy.size());
		assertEquals("royclarkson", retweetedBy.get(0).getScreenName());
		assertEquals("kdonald", retweetedBy.get(1).getScreenName());
		
		mockServer.verify();
	}
	
	@Test
	public void getRetweetedByIds() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/42/retweeted_by/ids.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("retweeted-by-ids.json", getClass()), responseHeaders));
		List<Long> retweetedByIds = twitter.timelineOperations().getRetweetedByIds(42L);
		assertEquals(3, retweetedByIds.size());
		assertEquals(12345, (long) retweetedByIds.get(0));
		assertEquals(9223372036854775807L, (long) retweetedByIds.get(1));
		assertEquals(34567, (long) retweetedByIds.get(2));
	}
	
	@Test
	public void getRetweetedByMe() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/retweeted_by_me.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("timeline.json", getClass()), responseHeaders));
		List<Tweet> timeline = twitter.timelineOperations().getRetweetedByMe();
		assertTimelineTweets(timeline);		
	}
	
	@Test
	public void getRetweetedToMe() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/retweeted_to_me.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("timeline.json", getClass()), responseHeaders));
		List<Tweet> timeline = twitter.timelineOperations().getRetweetedToMe();
		assertTimelineTweets(timeline);				
	}
	
	@Test
	public void getRetweets() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/retweets/42.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("timeline.json", getClass()), responseHeaders));
		List<Tweet> timeline = twitter.timelineOperations().getRetweets(42L);
		assertTimelineTweets(timeline);						
	}
	
	@Test
	public void getRetweetsOfMe() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/retweets_of_me.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("timeline.json", getClass()), responseHeaders));
		List<Tweet> timeline = twitter.timelineOperations().getRetweetsOfMe();
		assertTimelineTweets(timeline);				
	}
	
	@Test
	public void removeFromFavorites() {
		mockServer.expect(requestTo("https://api.twitter.com/1/favorites/destroy/71.json"))
			.andExpect(method(POST))
			.andRespond(withResponse("{}", responseHeaders));
		twitter.timelineOperations().removeFromFavorites(71L);
		mockServer.verify();
	}
	
}
