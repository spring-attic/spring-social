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
package org.springframework.social.facebook;

import static org.junit.Assert.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.social.test.client.RequestMatchers.*;
import static org.springframework.social.test.client.ResponseCreators.*;

import java.util.List;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

/**
 * @author Craig Walls
 */
public class FeedApiImplTest extends AbstractFacebookApiTest {

	@Test
	public void getFeed() {
		mockServer.expect(requestTo("https://graph.facebook.com/me/feed"))
				.andExpect(method(GET))
				.andExpect(header("Authorization", "OAuth someAccessToken"))
				.andRespond(withResponse(new ClassPathResource("feed.json", getClass()), responseHeaders));
		List<FeedEntry> feed = facebook.feedApi().getFeed();
		assertEquals(3, feed.size());
		assertFeedEntries(feed);
	}
	
	@Test
	public void getFeed_forOwnerId() {
		mockServer.expect(requestTo("https://graph.facebook.com/12345678/feed"))
				.andExpect(method(GET))
				.andExpect(header("Authorization", "OAuth someAccessToken"))
				.andRespond(withResponse(new ClassPathResource("feed.json", getClass()), responseHeaders));
		List<FeedEntry> feed = facebook.feedApi().getFeed("12345678");
		assertEquals(3, feed.size());
		assertFeedEntries(feed);
	}	
	
	@Test
	public void getHomeFeed() {
		mockServer.expect(requestTo("https://graph.facebook.com/me/home"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("feed.json", getClass()), responseHeaders));
		List<FeedEntry> homeFeed = facebook.feedApi().getHomeFeed();
		assertEquals(3, homeFeed.size());
		assertFeedEntries(homeFeed);
	}
	
	@Test
	public void getHomeFeed_forSpecificUser() {
		mockServer.expect(requestTo("https://graph.facebook.com/223311/home"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("feed.json", getClass()), responseHeaders));
		List<FeedEntry> homeFeed = facebook.feedApi().getHomeFeed("223311");
		assertEquals(3, homeFeed.size());
		assertFeedEntries(homeFeed);
	}
	
	@Test 
	public void getFeedEntry() {
		mockServer.expect(requestTo("https://graph.facebook.com/100001387295207_123939024341978"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("feedEntry.json", getClass()), responseHeaders));
		FeedEntry feedEntry = facebook.feedApi().getFeedEntry("100001387295207_123939024341978");
		assertEquals("100001387295207_123939024341978", feedEntry.getId());
		assertEquals("Hello world!", feedEntry.getMessage());
		assertEquals("100001387295207", feedEntry.getFrom().getId());
		assertEquals("Art Names", feedEntry.getFrom().getName());
		assertEquals(1, feedEntry.getLikes().size());
		assertEquals(2, feedEntry.getComments().size());
	}

	@Test
	public void updateStatus() throws Exception {
		String requestBody = "message=Hello+Facebook+World";
		mockServer.expect(requestTo("https://graph.facebook.com/me/feed"))
				.andExpect(method(POST))
				.andExpect(header("Authorization", "OAuth someAccessToken"))
				.andExpect(body(requestBody))
				.andRespond(withResponse("{\"id\":\"123456_78901234\"}", responseHeaders));
		assertEquals("123456_78901234", facebook.feedApi().updateStatus("Hello Facebook World"));
		mockServer.verify();
	}

	@Test
	public void updateStatus_withLink() throws Exception {
		String requestBody = "link=someLink&name=some+name&caption=some+caption&description=some+description&message=Hello+Facebook+World";
		mockServer.expect(requestTo("https://graph.facebook.com/me/feed")).andExpect(method(POST))
				.andExpect(header("Authorization", "OAuth someAccessToken"))
				.andExpect(body(requestBody))
				.andRespond(withResponse("{\"id\":\"123456_78901234\"}", responseHeaders));
		FacebookLink link = new FacebookLink("someLink", "some name", "some caption", "some description");
		assertEquals("123456_78901234", facebook.feedApi().postLink("Hello Facebook World", link));
		mockServer.verify();
	}

	@Test
	public void deleteFeedEntry() {
		String requestBody = "method=delete";
		mockServer.expect(requestTo("https://graph.facebook.com/123456_78901234"))
				.andExpect(method(POST))
				.andExpect(header("Authorization", "OAuth someAccessToken")).andExpect(body(requestBody))
				.andRespond(withResponse("{}", responseHeaders));
		facebook.feedApi().deleteFeedEntry("123456_78901234");
		mockServer.verify();
	}
	
	private void assertFeedEntries(List<FeedEntry> feed) {
		FeedEntry entry1 = feed.get(0);
		assertEquals("100001387295207_160065090716400", entry1.getId());
		assertEquals("Just trying something", entry1.getMessage());
		assertEquals("100001387295207", entry1.getFrom().getId());
		assertEquals("Art Names", entry1.getFrom().getName());
		FeedEntry entry2 = feed.get(1);
		assertEquals("100001387295207_160064384049804", entry2.getId());
		assertEquals("Check out my ride", entry2.getMessage());
		assertEquals("100001387295207", entry2.getFrom().getId());
		assertEquals("Art Names", entry2.getFrom().getName());
		FeedEntry entry3 = feed.get(2);
		assertEquals("100001387295207_153453231377586", entry3.getId());
		assertEquals("Hello Facebook!", entry3.getMessage());
		assertEquals("100001387295207", entry3.getFrom().getId());
		assertEquals("Art Names", entry3.getFrom().getName());
	}

}
