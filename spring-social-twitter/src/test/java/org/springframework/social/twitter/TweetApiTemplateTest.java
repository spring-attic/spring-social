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
import static org.springframework.social.test.client.RequestMatchers.*;
import static org.springframework.social.test.client.ResponseCreators.*;

import java.util.List;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.social.AccountNotConnectedException;
import org.springframework.social.OperationNotPermittedException;


/**
 * @author Craig Walls
 */
public class TweetApiTemplateTest extends AbstractTwitterApiTest {

	@Test
	public void updateStatus() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/update.json"))
				.andExpect(method(POST))
				.andExpect(body("status=Test+Message"))
				.andRespond(withResponse("{}", responseHeaders));

		twitter.tweetApi().updateStatus("Test Message");

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
		twitter.tweetApi().updateStatus("Test Message", details);

		mockServer.verify();
	}

	@Test(expected = DuplicateTweetException.class)
	public void updateStatus_duplicateTweet() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/update.json"))
				.andExpect(method(POST))
				.andExpect(body("status=Test+Message"))
				.andRespond(withResponse("{\"error\":\"You already said that\"}", responseHeaders, FORBIDDEN, ""));

		twitter.tweetApi().updateStatus("Test Message");
	}

	@Test(expected = OperationNotPermittedException.class)
	public void updateStatus_forbidden() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/update.json"))
				.andExpect(method(POST))
				.andExpect(body("status=Test+Message"))
				.andRespond(withResponse("{\"error\":\"Forbidden\"}", responseHeaders, FORBIDDEN, ""));

		twitter.tweetApi().updateStatus("Test Message");
	}

	@Test(expected = AccountNotConnectedException.class)
	public void updateStatus_unauthorized() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/update.json"))
				.andExpect(method(POST))
				.andExpect(body("status=Test+Message"))
				.andRespond(withResponse("{\"error\":\"Not authenticated\"}", responseHeaders, UNAUTHORIZED, ""));

		twitter.tweetApi().updateStatus("Test Message");
	}

	@Test
	public void deleteStatus() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/destroy/12345.json"))
			.andExpect(method(DELETE))
			.andRespond(withResponse("{}", responseHeaders));		
		twitter.tweetApi().deleteStatus(12345L);
		mockServer.verify();
	}
	
	@Test
	public void retweet() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/retweet/12345.json"))
				.andExpect(method(POST))
				.andRespond(withResponse("{}", responseHeaders));

		twitter.tweetApi().retweet(12345);

		mockServer.verify();
	}

	@Test(expected=DuplicateTweetException.class)
	public void retweet_duplicateTweet() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/retweet/12345.json"))
				.andExpect(method(POST))
				.andRespond(withResponse("{\"error\":\"You already said that\"}", responseHeaders, FORBIDDEN, ""));

		twitter.tweetApi().retweet(12345);
	}

	@Test(expected = OperationNotPermittedException.class)
	public void retweet_forbidden() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/retweet/12345.json"))
				.andExpect(method(POST))
				.andRespond(withResponse("{\"error\":\"Forbidden\"}", responseHeaders, FORBIDDEN, ""));

		twitter.tweetApi().retweet(12345);
	}

	@Test(expected = AccountNotConnectedException.class)
	public void retweet_unauthorized() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/retweet/12345.json"))
				.andExpect(method(POST))
				.andRespond(withResponse("{\"error\":\"Not authenticated\"}", responseHeaders, UNAUTHORIZED, ""));

		twitter.tweetApi().retweet(12345);
	}

	@Test
	public void getMentions() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/mentions.json"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("timeline.json", getClass()), responseHeaders));
		List<Tweet> mentions = twitter.tweetApi().getMentions();
		assertTimelineTweets(mentions);
	}

	@Test
	public void getPublicTimeline() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/public_timeline.json"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("timeline.json", getClass()), responseHeaders));
		List<Tweet> timeline = twitter.tweetApi().getPublicTimeline();
		assertTimelineTweets(timeline);
	}

	@Test
	public void getHomeTimeline() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/home_timeline.json"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("timeline.json", getClass()), responseHeaders));
		List<Tweet> timeline = twitter.tweetApi().getHomeTimeline();
		assertTimelineTweets(timeline);
	}

	@Test
	public void getFriendsTimeline() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/friends_timeline.json"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("timeline.json", getClass()), responseHeaders));
		List<Tweet> timeline = twitter.tweetApi().getFriendsTimeline();
		assertTimelineTweets(timeline);
	}

	@Test
	public void getUserTimeline() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/user_timeline.json"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("timeline.json", getClass()), responseHeaders));
		List<Tweet> timeline = twitter.tweetApi().getUserTimeline();
		assertTimelineTweets(timeline);
	}

	@Test
	public void getUserTimeline_forScreenName() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/user_timeline.json?screen_name=habuma"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("timeline.json", getClass()), responseHeaders));
		List<Tweet> timeline = twitter.tweetApi().getUserTimeline("habuma");
		assertTimelineTweets(timeline);
	}

	@Test
	public void getUserTimeline_forUserId() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/user_timeline.json?user_id=12345"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("timeline.json", getClass()), responseHeaders));
		List<Tweet> timeline = twitter.tweetApi().getUserTimeline(12345);
		assertTimelineTweets(timeline);
	}
	
	@Test
	public void getFavorites() {
		mockServer.expect(requestTo("https://api.twitter.com/1/favorites.json"))
				.andExpect(method(GET))
				.andRespond(withResponse(new ClassPathResource("favorite.json", getClass()), responseHeaders));
		List<Tweet> timeline = twitter.tweetApi().getFavorites();
		assertTimelineTweets(timeline);
	}

	@Test
	public void addToFavorites() {
		mockServer.expect(requestTo("https://api.twitter.com/1/favorites/create/42"))
			.andExpect(method(POST))
			.andRespond(withResponse("{}", responseHeaders));
		twitter.tweetApi().addToFavorites(42L);
		mockServer.verify();
	}
	
	@Test
	public void getRetweetedBy() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/42/retweeted_by.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("list-of-profiles.json", getClass()), responseHeaders));
		List<TwitterProfile> retweetedBy = twitter.tweetApi().getRetweetedBy(42L);
		assertEquals(2, retweetedBy.size());
		assertEquals("royclarkson", retweetedBy.get(0).getScreenName());
		assertEquals("kdonald", retweetedBy.get(1).getScreenName());
		
		mockServer.verify();
	}
	
	@Test
	public void getRetweetedByIds() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/42/retweeted_by/ids.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("list-of-profile-ids.json", getClass()), responseHeaders));
		List<Long> retweetedByIds = twitter.tweetApi().getRetweetedByIds(42L);
		assertEquals(3, retweetedByIds.size());
		assertTrue(retweetedByIds.contains(12345));
		assertTrue(retweetedByIds.contains(23456));
		assertTrue(retweetedByIds.contains(34567));
	}
	
	@Test
	public void getRetweetedByMe() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/retweeted_by_me.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("timeline.json", getClass()), responseHeaders));
		List<Tweet> timeline = twitter.tweetApi().getRetweetedByMe();
		assertTimelineTweets(timeline);		
	}
	
	@Test
	public void getRetweetedToMe() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/retweeted_to_me.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("timeline.json", getClass()), responseHeaders));
		List<Tweet> timeline = twitter.tweetApi().getRetweetedToMe();
		assertTimelineTweets(timeline);				
	}
	
	@Test
	public void getRetweets() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/retweets/42.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("timeline.json", getClass()), responseHeaders));
		List<Tweet> timeline = twitter.tweetApi().getRetweets(42L);
		assertTimelineTweets(timeline);						
	}
	
	@Test
	public void getRetweetsOfMe() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/retweets_of_me.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("timeline.json", getClass()), responseHeaders));
		List<Tweet> timeline = twitter.tweetApi().getRetweetsOfMe();
		assertTimelineTweets(timeline);				
	}
	
	@Test
	public void removeFromFavorites() {
		mockServer.expect(requestTo("https://api.twitter.com/1/favorites/destroy/71"))
			.andExpect(method(POST))
			.andRespond(withResponse("{}", responseHeaders));
		twitter.tweetApi().removeFromFavorites(71L);
		mockServer.verify();
	}
	
}
