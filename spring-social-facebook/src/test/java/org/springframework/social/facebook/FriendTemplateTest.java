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
package org.springframework.social.facebook;

import static org.junit.Assert.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.social.test.client.RequestMatchers.*;
import static org.springframework.social.test.client.ResponseCreators.*;

import java.util.List;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.social.facebook.types.Reference;

public class FriendTemplateTest extends AbstractFacebookApiTest {

	@Test
	public void getFriendLists() {
		mockServer.expect(requestTo("https://graph.facebook.com/me/friendlists"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("testdata/friend-lists.json", getClass()), responseHeaders));
		List<Reference> friendLists = facebook.friendOperations().getFriendLists();
		assertFriendLists(friendLists);
	}

	@Test
	public void getFriendLists_forSpecificUser() {
		mockServer.expect(requestTo("https://graph.facebook.com/11223344/friendlists"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("testdata/friend-lists.json", getClass()), responseHeaders));
		List<Reference> friendLists = facebook.friendOperations().getFriendLists("11223344");
		assertFriendLists(friendLists);
	}
	
	@Test
	public void getFriendList() {
		mockServer.expect(requestTo("https://graph.facebook.com/11929590579"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("testdata/friend-list.json", getClass()), responseHeaders));
		Reference friendList = facebook.friendOperations().getFriendList("11929590579");
		assertEquals("11929590579", friendList.getId());
		assertEquals("High School Friends", friendList.getName());
	}
	
	@Test
	public void getFriendListMembers() {
		mockServer.expect(requestTo("https://graph.facebook.com/192837465/members"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("testdata/friends.json", getClass()), responseHeaders));
		List<Reference> members = facebook.friendOperations().getFriendListMembers("192837465");
		assertFriends(members);
	}
	
	@Test
	public void deleteFriendList() {
		mockServer.expect(requestTo("https://graph.facebook.com/123456"))
			.andExpect(method(POST))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse("method=delete", responseHeaders));
		facebook.friendOperations().deleteFriendList("123456");
		mockServer.verify();
	}
	
	@Test
	public void addToFriendList() {
		mockServer.expect(requestTo("https://graph.facebook.com/123456/members/7890123"))
			.andExpect(method(POST))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse("true", responseHeaders));
		facebook.friendOperations().addToFriendList("123456", "7890123");
		mockServer.verify();
	}
	
	@Test
	public void removeFromFriendList() {
		mockServer.expect(requestTo("https://graph.facebook.com/123456/members/7890123"))
			.andExpect(method(DELETE))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse("true", responseHeaders));
		facebook.friendOperations().removeFromFriendList("123456", "7890123");
		mockServer.verify();		
	}
	
	@Test
	public void getFriends() {
		mockServer.expect(requestTo("https://graph.facebook.com/me/friends"))
				.andExpect(method(GET))
				.andExpect(header("Authorization", "OAuth someAccessToken"))
				.andRespond(withResponse(new ClassPathResource("testdata/friends.json", getClass()), responseHeaders));
		List<Reference> friends = facebook.friendOperations().getFriends();
		assertFriends(friends);
	}
	
	@Test
	public void getFriends_forSpecificUser() {
		mockServer.expect(requestTo("https://graph.facebook.com/912873465/friends"))
				.andExpect(method(GET))
				.andExpect(header("Authorization", "OAuth someAccessToken"))
				.andRespond(withResponse(new ClassPathResource("testdata/friends.json", getClass()), responseHeaders));
		List<Reference> friends = facebook.friendOperations().getFriends("912873465");
		assertFriends(friends);
	}
	
	@Test
	public void getFriendIds() {
		mockServer.expect(requestTo("https://graph.facebook.com/me/friends?fields=id"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("testdata/friend-ids.json", getClass()), responseHeaders));
		List<String> friendIds = facebook.friendOperations().getFriendIds();
		assertFriendIds(friendIds);
	}

	@Test
	public void getFriendIds_forSpecificUser() {
		mockServer.expect(requestTo("https://graph.facebook.com/912873465/friends?fields=id"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("testdata/friend-ids.json", getClass()), responseHeaders));
		List<String> friendIds = facebook.friendOperations().getFriendIds("912873465");
		assertFriendIds(friendIds);
	}
	
	private void assertFriends(List<Reference> friends) {
		assertEquals(3, friends.size());
		assertEquals("12345", friends.get(0).getId());
		assertEquals("Roy Clarkson", friends.get(0).getName());
		assertEquals("67890", friends.get(1).getId());
		assertEquals("Keith Donald", friends.get(1).getName());
		assertEquals("24680", friends.get(2).getId());
		assertEquals("Rod Johnson", friends.get(2).getName());
	}

	private void assertFriendLists(List<Reference> friendLists) {
		assertEquals(3, friendLists.size());
		assertEquals("11929590579", friendLists.get(0).getId());
		assertEquals("High School Friends", friendLists.get(0).getName());
		assertEquals("7770595579", friendLists.get(1).getId());
		assertEquals("Family", friendLists.get(1).getName());
		assertEquals("7716889379", friendLists.get(2).getId());
		assertEquals("College Friends", friendLists.get(2).getName());
	}

	private void assertFriendIds(List<String> friendIds) {
		assertEquals(3, friendIds.size());
		assertEquals("7918522", friendIds.get(0));
		assertEquals("149000307", friendIds.get(1));
		assertEquals("151101314", friendIds.get(2));
	}

}
