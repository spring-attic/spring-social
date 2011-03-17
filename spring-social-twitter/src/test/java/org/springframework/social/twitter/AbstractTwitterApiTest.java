package org.springframework.social.twitter;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.social.test.client.MockRestServiceServer;

public abstract class AbstractTwitterApiTest {

	protected TwitterTemplate twitter;

	protected MockRestServiceServer mockServer;

	protected HttpHeaders responseHeaders;

	@Before
	public void setup() {
		twitter = new TwitterTemplate();
		mockServer = MockRestServiceServer.createServer(twitter.getRestTemplate());
		responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
	}

	protected void assertTimelineTweets(List<Tweet> tweets) {
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
}
