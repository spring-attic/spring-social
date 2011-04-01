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
import static org.springframework.social.test.client.RequestMatchers.*;
import static org.springframework.social.test.client.ResponseCreators.*;

import java.util.List;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.social.twitter.types.TwitterProfile;
import org.springframework.social.twitter.types.UserList;

/**
 * @author Craig Walls
 */
public class ListsApiImplTest extends AbstractTwitterApiTest {
	
	public void primeProfileId() {
		mockServer.expect(requestTo("https://api.twitter.com/1/account/verify_credentials.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("verify-credentials.json", getClass()), responseHeaders));
	}
	
	@Test
	public void getLists_byId() {
		mockServer.expect(requestTo("https://api.twitter.com/1/161064614/lists.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("list-of-lists.json", getClass()), responseHeaders));
		assertListOfLists(twitter.listsApi().getLists(161064614));
	}

	@Test
	public void getLists_byScreenName() {
		mockServer.expect(requestTo("https://api.twitter.com/1/habuma/lists.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("list-of-lists.json", getClass()), responseHeaders));
		assertListOfLists(twitter.listsApi().getLists("habuma"));
	}

	@Test
	public void getList_byUserIdAndListId() {
		mockServer.expect(requestTo("https://api.twitter.com/1/161064614/lists/40841803.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("single-list.json", getClass()), responseHeaders));
		assertSingleList(twitter.listsApi().getList(161064614, 40841803));
	}

	@Test
	public void getList_byScreenNameAndListSlug() {
		mockServer.expect(requestTo("https://api.twitter.com/1/habuma/lists/forfun.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("single-list.json", getClass()), responseHeaders));
		assertSingleList(twitter.listsApi().getList("habuma", "forfun"));
	}
	
	@Test
	public void createList_publicListForUserId() {
		primeProfileId();
		mockServer.expect(requestTo("https://api.twitter.com/1/161064614/lists.json"))
			.andExpect(method(POST))
			.andExpect(body("name=forfun&description=Just+for+Fun&mode=public"))
			.andRespond(withResponse(new ClassPathResource("single-list.json", getClass()), responseHeaders));
		assertSingleList(twitter.listsApi().createList("forfun", "Just for Fun", true));
	}

	@Test
	public void createList_privateListForUserId() {
		primeProfileId();
		mockServer.expect(requestTo("https://api.twitter.com/1/161064614/lists.json"))
			.andExpect(method(POST))
			.andExpect(body("name=forfun2&description=Just+for+Fun%2C+too&mode=private"))
			.andRespond(withResponse(new ClassPathResource("single-list.json", getClass()), responseHeaders));
		assertSingleList(twitter.listsApi().createList("forfun2", "Just for Fun, too", false));
	}
	
	@Test
	public void updateList_publicListForUserId() {
		primeProfileId();
		mockServer.expect(requestTo("https://api.twitter.com/1/161064614/lists/40841803.json"))
			.andExpect(method(POST))
			.andExpect(body("name=forfun&description=Just+for+Fun&mode=public"))
			.andRespond(withResponse(new ClassPathResource("single-list.json", getClass()), responseHeaders));
		assertSingleList(twitter.listsApi().updateList(40841803, "forfun", "Just for Fun", true));
	}

	@Test
	public void updateList_privateListForUserId() {
		primeProfileId();
		mockServer.expect(requestTo("https://api.twitter.com/1/161064614/lists/40841803.json"))
			.andExpect(method(POST))
			.andExpect(body("name=forfun2&description=Just+for+Fun%2C+too&mode=private"))
			.andRespond(withResponse(new ClassPathResource("single-list.json", getClass()), responseHeaders));
		assertSingleList(twitter.listsApi().updateList(40841803, "forfun2", "Just for Fun, too", false));
	}

	@Test
	public void deleteList_forUserIdByListId() {
		primeProfileId();
		mockServer.expect(requestTo("https://api.twitter.com/1/161064614/lists/40841803.json"))
			.andExpect(method(DELETE))
			.andRespond(withResponse("{}", responseHeaders));
		twitter.listsApi().deleteList(40841803);
		mockServer.verify();
	}
	
	@Test
	public void getListMembers_byUserIdAndListId() {
		mockServer.expect(requestTo("https://api.twitter.com/1/161064614/40841803/members.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("list-members.json", getClass()), responseHeaders));
		assertListMembers(twitter.listsApi().getListMembers(161064614, 40841803));
	}

	@Test
	public void getListMembers_byScreenNameAndListSlug() {
		mockServer.expect(requestTo("https://api.twitter.com/1/habuma/forfun/members.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("list-members.json", getClass()), responseHeaders));
		assertListMembers(twitter.listsApi().getListMembers("habuma", "forfun"));
	}
	
	@Test
	public void addToList_forUserIdListIdSingle() {
		primeProfileId();
		mockServer.expect(requestTo("https://api.twitter.com/1/161064614/40841803/members/create_all.json"))
			.andExpect(method(POST))
			.andExpect(body("user_id=123456"))
			.andRespond(withResponse(new ClassPathResource("single-list.json", getClass()), responseHeaders));		

		assertSingleList(twitter.listsApi().addToList(40841803, 123456));
	}

	@Test
	public void addToList_forUserIdListIdMultiple() {
		primeProfileId();
		mockServer.expect(requestTo("https://api.twitter.com/1/161064614/40841803/members/create_all.json"))
			.andExpect(method(POST))
			.andExpect(body("user_id=123456%2C234567%2C345678"))
			.andRespond(withResponse(new ClassPathResource("single-list.json", getClass()), responseHeaders));		

		assertSingleList(twitter.listsApi().addToList(40841803, 123456, 234567, 345678));
	}

	@Test
	public void removeFromList_ownerIdListIdMemberId() {
		primeProfileId();
		mockServer.expect(requestTo("https://api.twitter.com/1/161064614/40841803/members.json?id=12345"))
			.andExpect(method(DELETE))
			.andRespond(withResponse("{}", responseHeaders));
		twitter.listsApi().removeFromList(40841803, 12345);
		mockServer.verify();
	}
	
	@Test
	public void getListSubscribers_byUserIdAndListId() {
		mockServer.expect(requestTo("https://api.twitter.com/1/161064614/40841803/subscribers.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("list-members.json", getClass()), responseHeaders));
		assertListMembers(twitter.listsApi().getListSubscribers(161064614, 40841803));
	}

	@Test
	public void getListSubscribers_byScreenNameAndListSlug() {
		mockServer.expect(requestTo("https://api.twitter.com/1/habuma/forfun/subscribers.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("list-members.json", getClass()), responseHeaders));
		assertListMembers(twitter.listsApi().getListSubscribers("habuma", "forfun"));
	}

	@Test
	public void getMemberships_forUserId() {
		mockServer.expect(requestTo("https://api.twitter.com/1/161064614/lists/memberships.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("list-of-lists.json", getClass()), responseHeaders));
		assertListOfLists(twitter.listsApi().getMemberships(161064614));
	}

	@Test
	public void getMemberships_forScreenName() {
		mockServer.expect(requestTo("https://api.twitter.com/1/habuma/lists/memberships.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("list-of-lists.json", getClass()), responseHeaders));
		assertListOfLists(twitter.listsApi().getMemberships("habuma"));
	}

	@Test
	public void getSubscriptions_forUserId() {
		mockServer.expect(requestTo("https://api.twitter.com/1/161064614/lists/subscriptions.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("list-of-lists.json", getClass()), responseHeaders));
		assertListOfLists(twitter.listsApi().getSubscriptions(161064614));
	}

	@Test
	public void getSubscriptions_forScreenName() {
		mockServer.expect(requestTo("https://api.twitter.com/1/habuma/lists/subscriptions.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("list-of-lists.json", getClass()), responseHeaders));
		assertListOfLists(twitter.listsApi().getSubscriptions("habuma"));
	}
	
	@Test
	public void isMember_byUserId() {
		mockServer.expect(requestTo("https://api.twitter.com/1/161064614/40841803/members/123456.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("twitter-profile.json", getClass()), responseHeaders));
		mockServer.expect(requestTo("https://api.twitter.com/1/161064614/40841803/members/987654.json"))
			.andExpect(method(GET))
			.andRespond(withResponse("{}", responseHeaders, HttpStatus.NOT_FOUND, ""));
		assertTrue(twitter.listsApi().isMember(161064614, 40841803, 123456));
		assertFalse(twitter.listsApi().isMember(161064614, 40841803, 987654));
	}

	@Test
	public void isMember_byScreenName() {
		mockServer.expect(requestTo("https://api.twitter.com/1/habuma/forfun/members/royclarkson.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("twitter-profile.json", getClass()), responseHeaders));
		mockServer.expect(requestTo("https://api.twitter.com/1/habuma/forfun/members/kdonald.json"))
			.andExpect(method(GET))
			.andRespond(withResponse("{}", responseHeaders, HttpStatus.NOT_FOUND, ""));
		assertTrue(twitter.listsApi().isMember("habuma", "forfun", "royclarkson"));
		assertFalse(twitter.listsApi().isMember("habuma", "forfun", "kdonald"));
	}
	
	@Test
	public void isSubscriber_byUserId() {
		mockServer.expect(requestTo("https://api.twitter.com/1/161064614/40841803/subscribers/123456.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("twitter-profile.json", getClass()), responseHeaders));
		mockServer.expect(requestTo("https://api.twitter.com/1/161064614/40841803/subscribers/987654.json"))
			.andExpect(method(GET))
			.andRespond(withResponse("{}", responseHeaders, HttpStatus.NOT_FOUND, ""));
		assertTrue(twitter.listsApi().isSubscriber(161064614, 40841803, 123456));
		assertFalse(twitter.listsApi().isSubscriber(161064614, 40841803, 987654));
	}

	@Test
	public void isSubscriber_byScreenName() {
		mockServer.expect(requestTo("https://api.twitter.com/1/habuma/forfun/subscribers/royclarkson.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("twitter-profile.json", getClass()), responseHeaders));
		mockServer.expect(requestTo("https://api.twitter.com/1/habuma/forfun/subscribers/kdonald.json"))
			.andExpect(method(GET))
			.andRespond(withResponse("{}", responseHeaders, HttpStatus.NOT_FOUND, ""));
		assertTrue(twitter.listsApi().isSubscriber("habuma", "forfun", "royclarkson"));
		assertFalse(twitter.listsApi().isSubscriber("habuma", "forfun", "kdonald"));
	}
	
	@Test
	public void subscribe() {
		mockServer.expect(requestTo("https://api.twitter.com/1/12345/54321/subscribers.json"))
			.andExpect(method(POST))
			.andRespond(withResponse(new ClassPathResource("single-list.json", getClass()), responseHeaders));
		UserList list = twitter.listsApi().subscribe(12345, 54321);
		assertSingleList(list);
	}
	
	@Test
	public void subscribe_usernameAndSlug() {
		mockServer.expect(requestTo("https://api.twitter.com/1/habuma/somelist/subscribers.json"))
			.andExpect(method(POST))
			.andRespond(withResponse(new ClassPathResource("single-list.json", getClass()), responseHeaders));
		UserList list = twitter.listsApi().subscribe("habuma", "somelist");
		assertSingleList(list);
	}
	
	@Test
	public void unsubscribe() {
		mockServer.expect(requestTo("https://api.twitter.com/1/12345/54321/subscribers.json"))
			.andExpect(method(DELETE))
			.andRespond(withResponse("{}", responseHeaders));
		twitter.listsApi().unsubscribe(12345, 54321);
		mockServer.verify();
	}
	
	@Test
	public void unsubscribe_usernameAndSlug() {
		mockServer.expect(requestTo("https://api.twitter.com/1/habuma/somelist/subscribers.json"))
			.andExpect(method(DELETE))
			.andRespond(withResponse("{}", responseHeaders));
		twitter.listsApi().unsubscribe("habuma", "somelist");
		mockServer.verify();
	}

	
	// private helpers
	
	private void assertSingleList(UserList list) {
		assertEquals(40841803, list.getId());
		assertEquals("forFun", list.getName());
		assertEquals("@habuma/forfun", list.getFullName());
		assertEquals("forfun", list.getSlug());
		assertEquals("Just for fun", list.getDescription());
		assertEquals(22, list.getMemberCount());
		assertEquals(100, list.getSubscriberCount());
		assertEquals("/habuma/forfun", list.getUriPath());
	}

	private void assertListOfLists(List<UserList> lists) {
		assertEquals(2, lists.size());
		UserList list1 = lists.get(0);
		assertEquals(40842137, list1.getId());
		assertEquals("forFun2", list1.getName());
		assertEquals("@habuma/forfun2", list1.getFullName());
		assertEquals("forfun2", list1.getSlug());
		assertEquals("Just for fun, too", list1.getDescription());
		assertEquals(3, list1.getMemberCount());
		assertEquals(0, list1.getSubscriberCount());
		assertEquals("/habuma/forfun2", list1.getUriPath());
		UserList list2 = lists.get(1);
		assertEquals(40841803, list2.getId());
		assertEquals("forFun", list2.getName());
		assertEquals("@habuma/forfun", list2.getFullName());
		assertEquals("forfun", list2.getSlug());
		assertEquals("Just for fun", list2.getDescription());
		assertEquals(22, list2.getMemberCount());
		assertEquals(100, list2.getSubscriberCount());
		assertEquals("/habuma/forfun", list2.getUriPath());
	}

	private void assertListMembers(List<TwitterProfile> members) {
		assertEquals(2, members.size());
		TwitterProfile profile1 = members.get(0);
		assertEquals(14846645, profile1.getId());
		assertEquals("royclarkson", profile1.getScreenName());
		assertEquals("Roy Clarkson", profile1.getName());
		assertEquals("Follower of mobile, social, and web technology trends. I write lots of code, and work at SpringSource.", 
				profile1.getDescription());
		assertEquals("Atlanta, GA, USA", profile1.getLocation());
		TwitterProfile profile2 = members.get(1);
		assertEquals(14718006, profile2.getId());
		assertEquals("kdonald", profile2.getScreenName());
		assertEquals("Keith Donald", profile2.getName());
		assertEquals("SpringSource co-founder", profile2.getDescription());
		assertEquals("Melbourne, Fl", profile2.getLocation());
	}

}
