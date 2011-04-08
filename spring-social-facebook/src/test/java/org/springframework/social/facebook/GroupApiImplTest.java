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
import org.springframework.social.facebook.types.Group;
import org.springframework.social.facebook.types.Reference;


public class GroupApiImplTest extends AbstractFacebookApiTest {

	@Test
	public void getGroup() {
		mockServer.expect(requestTo("https://graph.facebook.com/213106022036379"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("testdata/group.json", getClass()), responseHeaders));
		
		Group group = facebook.groupApi().getGroup("213106022036379");
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
		List<Reference> members = facebook.groupApi().getMembers("213106022036379");
		assertEquals(3, members.size());
		assertEquals("100001387295207", members.get(0).getId());
		assertEquals("Art Names", members.get(0).getName());
		assertEquals("738140579", members.get(1).getId());
		assertEquals("Craig Walls", members.get(1).getName());
		assertEquals("627039468", members.get(2).getId());
		assertEquals("Chuck Wagon", members.get(2).getName());
	}
}
