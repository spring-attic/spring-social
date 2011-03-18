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


/**
 * @author Craig Walls
 */
public class FriendsApiTemplateTest extends AbstractTwitterApiTest {

	@Test
	public void getFriends_byUserId() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/friends.json?user_id=98765"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("list-of-profiles.json", getClass()), responseHeaders));

		List<TwitterProfile> friends = twitter.friendsApi().getFriends(98765L);
		assertEquals(2, friends.size());
		assertEquals("royclarkson", friends.get(0).getScreenName());
		assertEquals("kdonald", friends.get(1).getScreenName());
	}

	@Test
	public void getFriends_byScreenName() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/friends.json?screen_name=habuma"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("list-of-profiles.json", getClass()), responseHeaders));

		List<TwitterProfile> friends = twitter.friendsApi().getFriends("habuma");
		assertEquals(2, friends.size());
		assertEquals("royclarkson", friends.get(0).getScreenName());
		assertEquals("kdonald", friends.get(1).getScreenName());
	}

	@Test
	public void getFriendIds_byUserId() {
		mockServer.expect(requestTo("https://api.twitter.com/1/friends/ids.json?user_id=98765"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("list-of-profile-ids.json", getClass()), responseHeaders));
		
		List<Long> followerIds = twitter.friendsApi().getFriendIds(98765L);
		assertEquals(3, followerIds.size());
	}

	@Test
	public void getFriendIds_byScreenName() {
		mockServer.expect(requestTo("https://api.twitter.com/1/friends/ids.json?screen_name=habuma"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("list-of-profile-ids.json", getClass()), responseHeaders));
		
		List<Long> followerIds = twitter.friendsApi().getFriendIds("habuma");
		assertEquals(3, followerIds.size());
	}
	
	@Test 
	public void getFollowers_byUserId() {
	    mockServer.expect(requestTo("https://api.twitter.com/1/statuses/followers.json?user_id=98765"))
	        .andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("list-of-profiles.json", getClass()), responseHeaders));
	    
		List<TwitterProfile> followers = twitter.friendsApi().getFollowers(98765L);
		assertEquals(2, followers.size());
		assertEquals("royclarkson", followers.get(0).getScreenName());
		assertEquals("kdonald", followers.get(1).getScreenName());
	}
	
	@Test 
	public void getFollowers_byScreenName() {
	    mockServer.expect(requestTo("https://api.twitter.com/1/statuses/followers.json?screen_name=oizik"))
	        .andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("list-of-profiles.json", getClass()), responseHeaders));
	    
		List<TwitterProfile> followers = twitter.friendsApi().getFollowers("oizik");
		assertEquals(2, followers.size());
		assertEquals("royclarkson", followers.get(0).getScreenName());
		assertEquals("kdonald", followers.get(1).getScreenName());
	}
	
	@Test
	public void getFollowerIds_byUserId() {
		mockServer.expect(requestTo("https://api.twitter.com/1/followers/ids.json?user_id=98765"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("list-of-profile-ids.json", getClass()), responseHeaders));
		
		List<Long> followerIds = twitter.friendsApi().getFollowerIds(98765L);
		assertEquals(3, followerIds.size());
	}

	@Test
	public void getFollowerIds_byScreenName() {
		mockServer.expect(requestTo("https://api.twitter.com/1/followers/ids.json?screen_name=habuma"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("list-of-profile-ids.json", getClass()), responseHeaders));
		
		List<Long> followerIds = twitter.friendsApi().getFollowerIds("habuma");
		assertEquals(3, followerIds.size());
	}
	
	@Test
	public void follow_byUserId() {
	    mockServer.expect(requestTo("https://api.twitter.com/1/friendships/create.json?user_id=98765"))
	        .andExpect(method(POST))
	        .andRespond(withResponse(new ClassPathResource("follow.json", getClass()), responseHeaders));
	    
		String followedScreenName = twitter.friendsApi().follow(98765);
	    assertEquals("oizik2", followedScreenName);
	    
	    mockServer.verify();
	}
	@Test
	public void follow_byScreenName() {
	    mockServer.expect(requestTo("https://api.twitter.com/1/friendships/create.json?screen_name=oizik2"))
	        .andExpect(method(POST))
	        .andRespond(withResponse(new ClassPathResource("follow.json", getClass()), responseHeaders));
	    
		String followedScreenName = twitter.friendsApi().follow("oizik2");
	    assertEquals("oizik2", followedScreenName);
	    
	    mockServer.verify();
	}
	
	@Test(expected = FriendshipFailureException.class)
	public void follow_alreadyFollowing() {
	    mockServer.expect(requestTo("https://api.twitter.com/1/friendships/create.json?screen_name=oizik2"))
            .andExpect(method(POST))
            .andRespond(withResponse("{\"error\" : \"Could not follow user: oizik2 is already on your list.\"}",
                    responseHeaders, FORBIDDEN, ""));
	    
		twitter.friendsApi().follow("oizik2");
	}

	@Test
	public void unfollow_byUserId() {
        mockServer.expect(requestTo("https://api.twitter.com/1/friendships/destroy.json?user_id=98765"))
            .andExpect(method(POST))
            .andRespond(withResponse(new ClassPathResource("unfollow.json", getClass()), responseHeaders));
        
		String unFollowedScreenName = twitter.friendsApi().unfollow(98765);
        assertEquals("oizik2", unFollowedScreenName);
        
        mockServer.verify();
    }
	
	@Test
	public void unfollow_byScreenName() {
        mockServer.expect(requestTo("https://api.twitter.com/1/friendships/destroy.json?screen_name=oizik2"))
            .andExpect(method(POST))
            .andRespond(withResponse(new ClassPathResource("unfollow.json", getClass()), responseHeaders));
        
		String unFollowedScreenName = twitter.friendsApi().unfollow("oizik2");
        assertEquals("oizik2", unFollowedScreenName);
        
        mockServer.verify();
    }
	
	@Test(expected = FriendshipFailureException.class)
    public void unfollow_notFollowing() {
        mockServer.expect(requestTo("https://api.twitter.com/1/friendships/destroy.json?screen_name=oizik2"))
            .andExpect(method(POST))
            .andRespond(withResponse("{\"error\" : \"You are not friends with the specified user.\"}", responseHeaders, FORBIDDEN, ""));
        
		twitter.friendsApi().unfollow("oizik2");
    }

	@Test
	public void exists() {
		mockServer.expect(requestTo("https://api.twitter.com/1/friendships/exists.json?user_a=kdonald&user_b=tinyrod"))
			.andExpect(method(GET))
			.andRespond(withResponse("true", responseHeaders));
		mockServer.expect(requestTo("https://api.twitter.com/1/friendships/exists.json?user_a=royclarkson&user_b=charliesheen"))
			.andExpect(method(GET))
			.andRespond(withResponse("false", responseHeaders));
		
		assertTrue(twitter.friendsApi().friendshipExists("kdonald", "tinyrod"));
		assertFalse(twitter.friendsApi().friendshipExists("royclarkson", "charliesheen"));
	}
	
	@Test
	public void getIncomingFriendships() {
		mockServer.expect(requestTo("https://api.twitter.com/1/friendships/incoming.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("incoming-or-outgoing-friendships.json", getClass()), responseHeaders));

		List<Long> friendships = twitter.friendsApi().getIncomingFriendships();
		assertEquals(3, friendships.size());
		assertTrue(friendships.contains(12345));
		assertTrue(friendships.contains(23456));
		assertTrue(friendships.contains(34567));
	}
	
	@Test
	public void getOutgoingFriendships() {
		mockServer.expect(requestTo("https://api.twitter.com/1/friendships/outgoing.json"))
			.andExpect(method(GET))
			.andRespond(withResponse(new ClassPathResource("incoming-or-outgoing-friendships.json", getClass()), responseHeaders));

		List<Long> friendships = twitter.friendsApi().getOutgoingFriendships();
		assertEquals(3, friendships.size());
		assertTrue(friendships.contains(12345));
		assertTrue(friendships.contains(23456));
		assertTrue(friendships.contains(34567));
	}
}
