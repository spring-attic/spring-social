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
package org.springframework.social.facebook.api;

import static org.junit.Assert.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.social.test.client.RequestMatchers.*;
import static org.springframework.social.test.client.ResponseCreators.*;

import java.util.List;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;


public class GroupTemplateTest extends AbstractFacebookApiTest {

	@Test
	public void getGroup() {
		mockServer.expect(requestTo("https://graph.facebook.com/213106022036379"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("testdata/group.json", getClass()), responseHeaders));
		
		Group group = facebook.groupOperations().getGroup("213106022036379");
		assertEquals("213106022036379", group.getId());
		assertEquals("Test Group", group.getName());
		assertEquals("Just a test group", group.getDescription());
		assertEquals("738140579", group.getOwner().getId());
		assertEquals("Craig Walls", group.getOwner().getName());
		assertEquals(Group.Privacy.SECRET, group.getPrivacy());
		assertEquals("http://static.ak.fbcdn.net/rsrc.php/v1/yN/r/IPw3LB5BsPK.png", group.getIcon());
		assertEquals(toDate("2011-03-30T19:24:59+0000"), group.getUpdatedTime());
		assertEquals("213106022036379@groups.facebook.com", group.getEmail());
	}
	
	@Test
	public void getMembers() {
		mockServer.expect(requestTo("https://graph.facebook.com/213106022036379/members"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("testdata/group-members.json", getClass()), responseHeaders));
		List<GroupMemberReference> members = facebook.groupOperations().getMembers("213106022036379");
		assertEquals(3, members.size());
		assertEquals("100001387295207", members.get(0).getId());
		assertEquals("Art Names", members.get(0).getName());
		assertFalse(members.get(0).isAdministrator());
		assertEquals("738140579", members.get(1).getId());
		assertEquals("Craig Walls", members.get(1).getName());
		assertTrue(members.get(1).isAdministrator());
		assertEquals("627039468", members.get(2).getId());
		assertEquals("Chuck Wagon", members.get(2).getName());
		assertTrue(members.get(2).isAdministrator());
	}
	
	@Test
	public void search() {
		mockServer.expect(requestTo("https://graph.facebook.com/search?q=Spring+User+Group&type=group&fields=owner%2Cname%2Cdescription%2Cprivacy%2Cicon%2Cupdated_time%2Cemail%2Cversion"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("testdata/group-list.json", getClass()), responseHeaders));
		List<Group> results = facebook.groupOperations().search("Spring User Group");
		assertEquals(3, results.size());
		assertEquals("108286519250791", results.get(0).getId());
		assertEquals("Spring User Group - Mauritius", results.get(0).getName());
		assertEquals("Spring User Group - Mauritius has for purpose to propagate the use Spring Framework within Mauritius.", results.get(0).getDescription());
		assertEquals("108286519250791@groups.facebook.com", results.get(0).getEmail());
		assertEquals("680947045", results.get(0).getOwner().getId());
		assertEquals("Javed Mandary", results.get(0).getOwner().getName());
		assertEquals("http://b.static.ak.fbcdn.net/rsrc.php/v1/y_/r/CbwcMZjMUbR.png", results.get(0).getIcon());
		assertEquals(Group.Privacy.OPEN, results.get(0).getPrivacy());
		assertEquals(toDate("2011-03-05T10:01:31+0000"), results.get(0).getUpdatedTime());
		assertEquals("120726277961844", results.get(1).getId());
		assertEquals("Atlanta Spring User Group", results.get(1).getName());
		assertEquals("ASUG is the first user group created to support the growing Spring community in the Atlanta area.", results.get(1).getDescription());
		assertNull(results.get(1).getEmail());
		assertEquals("25500170", results.get(1).getOwner().getId());
		assertEquals("Kate Clark", results.get(1).getOwner().getName());
		assertEquals("http://b.static.ak.fbcdn.net/rsrc.php/v1/y_/r/CbwcMZjMUbR.png", results.get(1).getIcon());
		assertEquals(Group.Privacy.OPEN, results.get(1).getPrivacy());
		assertEquals(toDate("2010-05-20T21:46:07+0000"), results.get(1).getUpdatedTime());
		assertEquals("114934361850206", results.get(2).getId());
		assertEquals("Martimes Java User Group", results.get(2).getName());
		assertEquals("The Maritime Area Java Users\u2019 Group was founded in December of 2009 by Ron Smith and Senan Almosawie of Mariner.", results.get(2).getDescription());
		assertNull(results.get(2).getEmail());
		assertEquals("709242026", results.get(2).getOwner().getId());
		assertEquals("Jay Logelin", results.get(2).getOwner().getName());
		assertEquals("http://b.static.ak.fbcdn.net/rsrc.php/v1/y_/r/CbwcMZjMUbR.png", results.get(2).getIcon());
		assertEquals(Group.Privacy.OPEN, results.get(2).getPrivacy());
		assertEquals(toDate("2010-04-01T01:16:44+0000"), results.get(2).getUpdatedTime());
	}	
}
