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
import static org.springframework.social.test.client.RequestMatchers.*;
import static org.springframework.social.test.client.ResponseCreators.*;

import java.util.List;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.social.twitter.api.TwitterProfile;


/**
 * @author Craig Walls
 */
public class FriendTemplateTest extends AbstractTwitterApiTest {

	@Test
	public void getFriends_byUserId() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/friends.json?user_id=98765"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("friends-or-followers.json", getClass()), responseHeaders));

		List<TwitterProfile> friends = twitter.friendOperations().getFriends(98765L);
		assertEquals(2, friends.size());
		assertEquals("royclarkson", friends.get(0).getScreenName());
		assertEquals("kdonald", friends.get(1).getScreenName());
	}

	@Test
	public void getFriends_byScreenName() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/friends.json?screen_name=habuma"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("friends-or-followers.json", getClass()), responseHeaders));

		List<TwitterProfile> friends = twitter.friendOperations().getFriends("habuma");
		assertEquals(2, friends.size());
		assertEquals("royclarkson", friends.get(0).getScreenName());
		assertEquals("kdonald", friends.get(1).getScreenName());
	}

	@Test
	public void getFriendIds_byUserId() {
		mockServer.expect(requestTo("https://api.twitter.com/1/friends/ids.json?user_id=98765"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("friend-or-follower-ids.json", getClass()), responseHeaders));
		
		List<Long> followerIds = twitter.friendOperations().getFriendIds(98765L);
		assertEquals(3, followerIds.size());
		assertEquals(12345L, (long) followerIds.get(0));
		assertEquals(9223372036854775807L, (long) followerIds.get(1));		
		assertEquals(34567L, (long) followerIds.get(2));
	}

	@Test
	public void getFriendIds_byScreenName() {
		mockServer.expect(requestTo("https://api.twitter.com/1/friends/ids.json?screen_name=habuma"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("friend-or-follower-ids.json", getClass()), responseHeaders));
		
		List<Long> followerIds = twitter.friendOperations().getFriendIds("habuma");
		assertEquals(3, followerIds.size());
		assertEquals(12345L, (long) followerIds.get(0));
		assertEquals(9223372036854775807L, (long) followerIds.get(1));		
		assertEquals(34567L, (long) followerIds.get(2));
	}
	
	@Test 
	public void getFollowers_byUserId() {
	    mockServer.expect(requestTo("https://api.twitter.com/1/statuses/followers.json?user_id=98765"))
	        .andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("friends-or-followers.json", getClass()), responseHeaders));
	    
		List<TwitterProfile> followers = twitter.friendOperations().getFollowers(98765L);
		assertEquals(2, followers.size());
		assertEquals("royclarkson", followers.get(0).getScreenName());
		assertEquals("kdonald", followers.get(1).getScreenName());
	}
	
	@Test 
	public void getFollowers_byScreenName() {
	    mockServer.expect(requestTo("https://api.twitter.com/1/statuses/followers.json?screen_name=oizik"))
	        .andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("friends-or-followers.json", getClass()), responseHeaders));
	    
		List<TwitterProfile> followers = twitter.friendOperations().getFollowers("oizik");
		assertEquals(2, followers.size());
		assertEquals("royclarkson", followers.get(0).getScreenName());
		assertEquals("kdonald", followers.get(1).getScreenName());
	}
	
	@Test
	public void getFollowerIds_byUserId() {
		mockServer.expect(requestTo("https://api.twitter.com/1/followers/ids.json?user_id=98765"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("friend-or-follower-ids.json", getClass()), responseHeaders));
		
		List<Long> followerIds = twitter.friendOperations().getFollowerIds(98765L);
		assertEquals(3, followerIds.size());
		assertEquals(12345L, (long) followerIds.get(0));
		assertEquals(9223372036854775807L, (long) followerIds.get(1));		
		assertEquals(34567L, (long) followerIds.get(2));
	}

	@Test
	public void getFollowerIds_byScreenName() {
		mockServer.expect(requestTo("https://api.twitter.com/1/followers/ids.json?screen_name=habuma"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("friend-or-follower-ids.json", getClass()), responseHeaders));
		
		List<Long> followerIds = twitter.friendOperations().getFollowerIds("habuma");
		assertEquals(3, followerIds.size());
		assertEquals(12345L, (long) followerIds.get(0));
		assertEquals(9223372036854775807L, (long) followerIds.get(1));		
		assertEquals(34567L, (long) followerIds.get(2));
	}
	
	@Test
	public void follow_byUserId() {
	    mockServer.expect(requestTo("https://api.twitter.com/1/friendships/create.json?user_id=98765"))
	        .andExpect(method(POST))
	        .andRespond(withResponse(new ClassPathResource("follow.json", getClass()), responseHeaders));
	    
		String followedScreenName = twitter.friendOperations().follow(98765);
	    assertEquals("oizik2", followedScreenName);
	    
	    mockServer.verify();
	}
	@Test
	public void follow_byScreenName() {
	    mockServer.expect(requestTo("https://api.twitter.com/1/friendships/create.json?screen_name=oizik2"))
	        .andExpect(method(POST))
	        .andRespond(withResponse(new ClassPathResource("follow.json", getClass()), responseHeaders));
	    
		String followedScreenName = twitter.friendOperations().follow("oizik2");
	    assertEquals("oizik2", followedScreenName);
	    
	    mockServer.verify();
	}
	
	@Test
	public void unfollow_byUserId() {
        mockServer.expect(requestTo("https://api.twitter.com/1/friendships/destroy.json?user_id=98765"))
            .andExpect(method(POST))
            .andRespond(withResponse(new ClassPathResource("unfollow.json", getClass()), responseHeaders));
        
		String unFollowedScreenName = twitter.friendOperations().unfollow(98765);
        assertEquals("oizik2", unFollowedScreenName);
        
        mockServer.verify();
    }
	
	@Test
	public void unfollow_byScreenName() {
        mockServer.expect(requestTo("https://api.twitter.com/1/friendships/destroy.json?screen_name=oizik2"))
            .andExpect(method(POST))
            .andRespond(withResponse(new ClassPathResource("unfollow.json", getClass()), responseHeaders));
        
		String unFollowedScreenName = twitter.friendOperations().unfollow("oizik2");
        assertEquals("oizik2", unFollowedScreenName);
        
        mockServer.verify();
    }
	
	@Test
	public void exists() {
		mockServer.expect(requestTo("https://api.twitter.com/1/friendships/exists.json?user_a=kdonald&user_b=tinyrod"))
			.andExpect(method(GET))
			.andRespond(withResponse("true", responseHeaders));
		mockServer.expect(requestTo("https://api.twitter.com/1/friendships/exists.json?user_a=royclarkson&user_b=charliesheen"))
			.andExpect(method(GET))
			.andRespond(withResponse("false", responseHeaders));
		
		assertTrue(twitter.friendOperations().friendshipExists("kdonald", "tinyrod"));
		assertFalse(twitter.friendOperations().friendshipExists("royclarkson", "charliesheen"));
	}
	
	@Test
	public void getIncomingFriendships() {
		mockServer.expect(requestTo("https://api.twitter.com/1/friendships/incoming.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("incoming-or-outgoing-friendships.json", getClass()), responseHeaders));

		List<Long> friendships = twitter.friendOperations().getIncomingFriendships();
		assertEquals(3, friendships.size());
		assertEquals(12345, (long) friendships.get(0));
		assertEquals(23456, (long) friendships.get(1));
		assertEquals(34567, (long) friendships.get(2));
	}
	
	@Test
	public void getOutgoingFriendships() {
		mockServer.expect(requestTo("https://api.twitter.com/1/friendships/outgoing.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("incoming-or-outgoing-friendships.json", getClass()), responseHeaders));

		List<Long> friendships = twitter.friendOperations().getOutgoingFriendships();
		assertEquals(3, friendships.size());
		assertEquals(12345, (long) friendships.get(0));
		assertEquals(23456, (long) friendships.get(1));
		assertEquals(34567, (long) friendships.get(2));
	}
}
