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

public class MediaApiImplTest extends AbstractFacebookApiTest {
	@Test
	public void getAlbums() {
		mockServer.expect(requestTo("https://graph.facebook.com/me/albums"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("user-albums.json", getClass()), responseHeaders));
		List<Album> albums = facebook.mediaApi().getAlbums();
		assertAlbums(albums);
	}
	
	@Test
	public void getAlbums_forSpecificUser() {
		mockServer.expect(requestTo("https://graph.facebook.com/192837465/albums"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("user-albums.json", getClass()), responseHeaders));
		List<Album> albums = facebook.mediaApi().getAlbums("192837465");
		assertAlbums(albums);
	}
	

	private void assertAlbums(List<Album> albums) {
		assertEquals(3, albums.size());
		assertEquals("10151447271460580", albums.get(0).getId());
		assertEquals("738140579", albums.get(0).getFrom().getId());
		assertEquals("Craig Walls", albums.get(0).getFrom().getName());
		assertEquals("http://www.facebook.com/album.php?aid=620722&id=738140579", albums.get(0).getLink());
		assertEquals("Early Broncos", albums.get(0).getName());
		assertNull(albums.get(0).getDescription());
		assertEquals("Somewhere", albums.get(0).getLocation());
		assertEquals(Album.Privacy.CUSTOM, albums.get(0).getPrivacy());
		assertEquals(Album.Type.NORMAL, albums.get(0).getType());
		assertEquals(1, albums.get(0).getCount());
		assertEquals(toDate("2011-03-24T21:36:04+0000"), albums.get(0).getCreatedTime());
		assertEquals(toDate("2011-03-24T22:00:12+0000"), albums.get(0).getUpdatedTime());

		assertEquals("10150694228040580", albums.get(1).getId());
		assertEquals("738140579", albums.get(1).getFrom().getId());
		assertEquals("Craig Walls", albums.get(1).getFrom().getName());
		assertEquals("http://www.facebook.com/album.php?aid=526031&id=738140579", albums.get(1).getLink());
		assertEquals("Profile Pictures", albums.get(1).getName());
		assertNull(albums.get(1).getDescription());
		assertNull(albums.get(1).getLocation());
		assertEquals(Album.Privacy.FRIENDS_OF_FRIENDS, albums.get(1).getPrivacy());
		assertEquals(Album.Type.PROFILE, albums.get(1).getType());
		assertEquals(5, albums.get(1).getCount());
		assertEquals(toDate("2010-10-22T20:22:51+0000"), albums.get(1).getCreatedTime());
		assertNull(albums.get(1).getUpdatedTime());

		assertEquals("247501695549", albums.get(2).getId());
		assertEquals("738140579", albums.get(2).getFrom().getId());
		assertEquals("Craig Walls", albums.get(2).getFrom().getName());
		assertEquals("http://www.facebook.com/album.php?aid=290408&id=738140579", albums.get(2).getLink());
		assertEquals("Photos on the go", albums.get(2).getName());
		assertNull(albums.get(2).getDescription());
		assertNull(albums.get(2).getLocation());
		assertEquals(Album.Privacy.EVERYONE, albums.get(2).getPrivacy());
		assertEquals(Album.Type.MOBILE, albums.get(2).getType());
		assertEquals(3, albums.get(2).getCount());
		assertEquals(toDate("2009-08-08T19:28:46+0000"), albums.get(2).getCreatedTime());
		assertEquals(toDate("2010-08-25T02:03:43+0000"), albums.get(2).getUpdatedTime());
	}

}
