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
import org.springframework.social.facebook.types.FacebookLink;
import org.springframework.social.facebook.types.FeedEntry;

/**
 * @author Craig Walls
 */
public class FeedApiImplTest extends AbstractFacebookApiTest {

	@Test
	public void getFeed() {
		mockServer.expect(requestTo("https://graph.facebook.com/me/feed"))
				.andExpect(method(GET))
				.andExpect(header("Authorization", "OAuth someAccessToken"))
				.andRespond(withResponse(new ClassPathResource("testdata/feed.json", getClass()), responseHeaders));
		List<FeedEntry> feed = facebook.feedApi().getFeed();
		assertEquals(3, feed.size());
		assertFeedEntries(feed);
	}
	
	@Test
	public void getFeed_forOwnerId() {
		mockServer.expect(requestTo("https://graph.facebook.com/12345678/feed"))
				.andExpect(method(GET))
				.andExpect(header("Authorization", "OAuth someAccessToken"))
				.andRespond(withResponse(new ClassPathResource("testdata/feed.json", getClass()), responseHeaders));
		List<FeedEntry> feed = facebook.feedApi().getFeed("12345678");
		assertEquals(3, feed.size());
		assertFeedEntries(feed);
	}	
	
	@Test
	public void getHomeFeed() {
		mockServer.expect(requestTo("https://graph.facebook.com/me/home"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("testdata/feed.json", getClass()), responseHeaders));
		List<FeedEntry> homeFeed = facebook.feedApi().getHomeFeed();
		assertEquals(3, homeFeed.size());
		assertFeedEntries(homeFeed);
	}
	
	@Test
	public void getHomeFeed_forSpecificUser() {
		mockServer.expect(requestTo("https://graph.facebook.com/223311/home"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("testdata/feed.json", getClass()), responseHeaders));
		List<FeedEntry> homeFeed = facebook.feedApi().getHomeFeed("223311");
		assertEquals(3, homeFeed.size());
		assertFeedEntries(homeFeed);
	}

	@Test
	public void getStatuses() {
		mockServer.expect(requestTo("https://graph.facebook.com/me/statuses"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("testdata/user-statuses.json", getClass()), responseHeaders));		
		assertStatuses(facebook.feedApi().getStatuses());
	}

	@Test
	public void getStatuses_forSpecificUser() {
		mockServer.expect(requestTo("https://graph.facebook.com/24680/statuses"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("testdata/user-statuses.json", getClass()), responseHeaders));		
		assertStatuses(facebook.feedApi().getStatuses("24680"));
	}

	private void assertStatuses(List<FeedEntry> statuses) {
		assertEquals(3, statuses.size());
		assertEquals("161195833936659", statuses.get(0).getId());
		assertEquals("100001387295207", statuses.get(0).getFrom().getId());
		assertEquals("Art Names", statuses.get(0).getFrom().getName());
		assertEquals("One more...just for fun", statuses.get(0).getMessage());
		assertEquals(toDate("2011-03-28T14:54:07+0000"), statuses.get(0).getUpdatedTime());
		assertEquals("161195783936664", statuses.get(1).getId());
		assertEquals("100001387295207", statuses.get(1).getFrom().getId());
		assertEquals("Art Names", statuses.get(1).getFrom().getName());
		assertEquals("Just another status.", statuses.get(1).getMessage());
		assertEquals(toDate("2011-03-28T14:53:57+0000"), statuses.get(1).getUpdatedTime());
		assertEquals("161195107270065", statuses.get(2).getId());
		assertEquals("100001387295207", statuses.get(2).getFrom().getId());
		assertEquals("Art Names", statuses.get(2).getFrom().getName());
		assertEquals("Good morning Monday!", statuses.get(2).getMessage());
		assertEquals(toDate("2011-03-28T14:50:27+0000"), statuses.get(2).getUpdatedTime());
	}

	@Test
	public void getLinks() {
		mockServer.expect(requestTo("https://graph.facebook.com/me/links"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("testdata/links.json", getClass()), responseHeaders));		
		assertLinks(facebook.feedApi().getLinks());
	}
	
	@Test
	public void getLinks_forSpecificUser() {
		mockServer.expect(requestTo("https://graph.facebook.com/13579/links"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("testdata/links.json", getClass()), responseHeaders));		
		assertLinks(facebook.feedApi().getLinks("13579"));
	}
	
	@Test
	public void getNotes() {
		mockServer.expect(requestTo("https://graph.facebook.com/me/notes"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("testdata/user-notes.json", getClass()), responseHeaders));		
		List<FeedEntry> notes = facebook.feedApi().getNotes();
		assertNotes(notes);
	}

	private void assertNotes(List<FeedEntry> notes) {
		assertEquals(2, notes.size());
		assertEquals("161200187269557", notes.get(0).getId());
		assertEquals("100001387295207", notes.get(0).getFrom().getId());
		assertEquals("Art Names", notes.get(0).getFrom().getName());
		assertEquals("Just a note", notes.get(0).getSubject());
		assertEquals("<p>This is just a test note. Nothing special to see here.</p>", notes.get(0).getMessage());
		assertEquals("http://static.ak.fbcdn.net/rsrc.php/v1/yY/r/1gBp2bDGEuh.gif", notes.get(0).getIcon());
		assertEquals(toDate("2011-03-28T15:17:41+0000"), notes.get(0).getCreatedTime());
		assertEquals(toDate("2011-03-28T15:17:41+0000"), notes.get(0).getUpdatedTime());
		assertEquals("160546394001603", notes.get(1).getId());
		assertEquals("100001387295207", notes.get(1).getFrom().getId());
		assertEquals("Art Names", notes.get(1).getFrom().getName());
		assertEquals("Test Note", notes.get(1).getSubject());
		assertEquals("<p>Just a <strong>test</strong> note...nothing to see here.</p>", notes.get(1).getMessage());
		assertEquals("http://static.ak.fbcdn.net/rsrc.php/v1/yY/r/1gBp2bDGEuh.gif", notes.get(1).getIcon());
		assertEquals(toDate("2011-03-25T18:25:01+0000"), notes.get(1).getCreatedTime());
		assertEquals(toDate("2011-03-25T20:08:27+0000"), notes.get(1).getUpdatedTime());
	}
	
	@Test
	public void getPosts() {
		mockServer.expect(requestTo("https://graph.facebook.com/me/posts"))
				.andExpect(method(GET))
				.andExpect(header("Authorization", "OAuth someAccessToken"))
				.andRespond(withResponse(new ClassPathResource("testdata/feed.json", getClass()), responseHeaders));
		List<FeedEntry> feed = facebook.feedApi().getPosts();
		assertEquals(3, feed.size());
		assertFeedEntries(feed);
	}
	
	@Test
	public void getPosts_forOwnerId() {
		mockServer.expect(requestTo("https://graph.facebook.com/12345678/posts"))
				.andExpect(method(GET))
				.andExpect(header("Authorization", "OAuth someAccessToken"))
				.andRespond(withResponse(new ClassPathResource("testdata/feed.json", getClass()), responseHeaders));
		List<FeedEntry> feed = facebook.feedApi().getPosts("12345678");
		assertEquals(3, feed.size());
		assertFeedEntries(feed);
	}	

	
	@Test 
	public void getFeedEntry() {
		mockServer.expect(requestTo("https://graph.facebook.com/100001387295207_123939024341978"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("testdata/feedEntry.json", getClass()), responseHeaders));
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
		assertEquals("100001387295207_160065090716400", feed.get(0).getId());
		assertEquals("Just trying something", feed.get(0).getMessage());
		assertEquals("100001387295207", feed.get(0).getFrom().getId());
		assertEquals("Art Names", feed.get(0).getFrom().getName());
		assertEquals("100001387295207_160064384049804", feed.get(1).getId());
		assertEquals("Check out my ride", feed.get(1).getMessage());
		assertEquals("100001387295207", feed.get(1).getFrom().getId());
		assertEquals("Art Names", feed.get(1).getFrom().getName());
		assertEquals("100001387295207_153453231377586", feed.get(2).getId());
		assertEquals("Hello Facebook!", feed.get(2).getMessage());
		assertEquals("100001387295207", feed.get(2).getFrom().getId());
		assertEquals("Art Names", feed.get(2).getFrom().getName());
	}
	
	private void assertLinks(List<FeedEntry> feed) {
		assertEquals(2, feed.size());
		assertEquals("125736073702566", feed.get(0).getId());
		assertEquals("Warning about Facebook Phishing: See http://www.facebook.com/group.php?gid=9874388706", feed.get(0).getMessage());
		assertEquals("738140579", feed.get(0).getFrom().getId());
		assertEquals("Craig Walls", feed.get(0).getFrom().getName());
		assertEquals("http://profile.ak.fbcdn.net/hprofile-ak-snc4/50255_9874388706_6623_n.jpg", feed.get(0).getPicture());
		assertEquals("http://static.ak.fbcdn.net/rsrc.php/v1/yD/r/aS8ecmYRys0.gif", feed.get(0).getIcon());
		assertEquals("Facebook Phishing Scam Awareness", feed.get(0).getName());
		assertNull(feed.get(0).getDescription());
		assertNull(feed.get(0).getLink()); // sometimes links are in the message
		assertEquals("147264864601", feed.get(1).getId());
		assertEquals("Hey, let's go buy some furniture from the guy who's off his meds.", feed.get(1).getMessage());
		assertEquals("738140579", feed.get(1).getFrom().getId());
		assertEquals("Craig Walls", feed.get(1).getFrom().getName());
		assertEquals("http://i.ytimg.com/vi/QSLT2N-Ome8/2.jpg", feed.get(1).getPicture());
		assertEquals("http://static.ak.fbcdn.net/rsrc.php/v1/yD/r/aS8ecmYRys0.gif", feed.get(1).getIcon());
		assertEquals("Competition Beatdown Fail", feed.get(1).getName());
		assertEquals("What was this guy thinking?", feed.get(1).getDescription());
		assertEquals("http://www.youtube.com/watch?v=QSLT2N-Ome8", feed.get(1).getLink());
	}

}
