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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

/**
 * @author Craig Walls
 */
public class UserApiImplTest extends AbstractFacebookApiTest {
	
	@Test
	public void getUserProfile_authenticatedUser() {
		mockServer.expect(requestTo("https://graph.facebook.com/me"))
				.andExpect(method(GET))
				.andExpect(header("Authorization", "OAuth someAccessToken"))
				.andRespond(withResponse(new ClassPathResource("full-profile.json", getClass()), responseHeaders));

		FacebookProfile profile = facebook.userApi().getUserProfile();
		assertBasicProfileData(profile);
		assertEquals("cwalls@vmware.com", profile.getEmail());
		assertEquals("http://www.facebook.com/habuma", profile.getLink());
		assertEquals("xyz123abc987", profile.getThirdPartyId());
		assertEquals(Integer.valueOf(-6), profile.getTimezone());
		assertEquals(toDate("2010-08-22T00:01:59+0000"), profile.getUpdatedTime());
		assertTrue(profile.isVerified());
		assertEquals("Just some dude", profile.getAbout());
		assertEquals("I was born at a very early age.", profile.getBio());
		assertEquals("06/09/1971", profile.getBirthday());
		assertEquals("111762725508574", profile.getLocation().getId());
		assertEquals("Dallas, Texas", profile.getLocation().getName());
		assertEquals("107925612568471", profile.getHometown().getId());
		assertEquals("Plano, Texas", profile.getHometown().getName());
		assertEquals(1, profile.getInterestedIn().size());
		assertEquals("female", profile.getInterestedIn().get(0));
		assertEquals("Jedi", profile.getReligion());
		assertEquals("Galactic Republic", profile.getPolitical());
		assertEquals("\"May the force be with you.\" - Common Jedi greeting", profile.getQuotes());
		assertEquals("Married", profile.getRelationshipStatus());
		assertEquals("533477039", profile.getSignificantOther().getId());
		assertEquals("Raymie Walls", profile.getSignificantOther().getName());
		assertEquals("http://www.habuma.com", profile.getWebsite());
		assertWorkHistory(profile.getWork());
		assertEducationHistory(profile.getEducation());
	}

	@Test
	public void getUserProfile_specificUserByUserId() {
		mockServer.expect(requestTo("https://graph.facebook.com/123456789"))
				.andExpect(method(GET))
				.andExpect(header("Authorization", "OAuth someAccessToken"))
				.andRespond(withResponse(new ClassPathResource("minimal-profile.json", getClass()), responseHeaders));

		FacebookProfile profile = facebook.userApi().getUserProfile("123456789");
		assertBasicProfileData(profile);
	}
	
	@Test
	public void getCheckins() {
		mockServer.expect(requestTo("https://graph.facebook.com/me/checkins"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("checkins.json", getClass()), responseHeaders));
		List<Checkin> checkins = facebook.userApi().getCheckins();
		assertCheckins(checkins);
	}

	@Test
	public void getCheckins_forSpecificUser() {
		mockServer.expect(requestTo("https://graph.facebook.com/987654321/checkins"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("checkins.json", getClass()), responseHeaders));
		List<Checkin> checkins = facebook.userApi().getCheckins("987654321");
		assertCheckins(checkins);
	}
	
	private void assertCheckins(List<Checkin> checkins) {
		assertEquals(2, checkins.size());
		Checkin checkin1 = checkins.get(0);
		assertEquals("10150431253050580", checkin1.getId());
		assertEquals("738140579", checkin1.getFrom().getId());
		assertEquals("Craig Walls", checkin1.getFrom().getName());
		Location place1 = checkin1.getPlace();
		assertEquals("117372064948189", place1.getId());
		assertEquals("Freebirds World Burrito", place1.getName());
		assertEquals("238 W Campbell Rd", place1.getStreet());
		assertEquals("Richardson", place1.getCity());
		assertEquals("TX", place1.getState());
		assertEquals("United States", place1.getCountry());
		assertEquals("75080-3512", place1.getZip());
		assertEquals(32.975537, place1.getLatitude(), 0.0001);
		assertEquals(-96.722944, place1.getLongitude(), 0.0001);
		assertEquals("6628568379", checkin1.getApplication().getId());
		assertEquals("Facebook for iPhone", checkin1.getApplication().getName());
		assertEquals(toDate("2011-03-13T01:00:49+0000"), checkin1.getCreatedTime());
		Checkin checkin2 = checkins.get(1);
		assertEquals("10150140239512040", checkin2.getId());
		assertEquals("533477039", checkin2.getFrom().getId());
		assertEquals("Raymie Walls", checkin2.getFrom().getName());
		assertEquals(1, checkin2.getTags().size());
		assertEquals("738140579", checkin2.getTags().get(0).getId());
		assertEquals("Craig Walls", checkin2.getTags().get(0).getName());
		assertEquals("With my favorite people! ;-)", checkin2.getMessage());
		Location place2 = checkin2.getPlace();
		assertEquals("150366431753543", place2.getId());
		assertEquals("Somewhere", place2.getName());
		assertEquals(35.0231428, place2.getLatitude(), 0.0001);
		assertEquals(-98.740305416667, place2.getLongitude(), 0.0001);
		assertEquals("6628568379", checkin2.getApplication().getId());
		assertEquals("Facebook for iPhone", checkin2.getApplication().getName());
		assertEquals(toDate("2011-02-11T20:59:41+0000"), checkin2.getCreatedTime());
		assertEquals(2, checkin2.getLikes().size());
		assertEquals("1524405653", checkin2.getLikes().get(0).getId());
		assertEquals("Samuel Hugh Parsons", checkin2.getLikes().get(0).getName());
		assertEquals("1580082219", checkin2.getLikes().get(1).getId());
		assertEquals("Kris Len Nicholson", checkin2.getLikes().get(1).getName());
		assertEquals(1, checkin2.getComments().size());
		assertEquals("10150140239512040_15204657", checkin2.getComments().get(0).getId());
		assertEquals("100000094813002", checkin2.getComments().get(0).getFrom().getId());
		assertEquals("Otis Nelson", checkin2.getComments().get(0).getFrom().getName());
		assertEquals("You are not with me!!!!", checkin2.getComments().get(0).getMessage());
		assertEquals(toDate("2011-02-11T21:31:31+0000"), checkin2.getComments().get(0).getCreatedTime());
	}
	
	@Test
	public void getEvents() {
		mockServer.expect(requestTo("https://graph.facebook.com/me/events"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("user-events.json", getClass()), responseHeaders));
		List<UserEvent> events = facebook.userApi().getEvents();
		assertEvents(events);
	}
	
	@Test
	public void getEvents_forSpecificUser() {
		mockServer.expect(requestTo("https://graph.facebook.com/123456789/events"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("user-events.json", getClass()), responseHeaders));
		List<UserEvent> events = facebook.userApi().getEvents("123456789");
		assertEvents(events);
	}
	
	@Test
	public void getAlbums() {
		mockServer.expect(requestTo("https://graph.facebook.com/me/albums"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("user-albums.json", getClass()), responseHeaders));
		List<Album> albums = facebook.userApi().getAlbums();
		assertAlbums(albums);
	}
	
	@Test
	public void getAlbums_forSpecificUser() {
		mockServer.expect(requestTo("https://graph.facebook.com/192837465/albums"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("user-albums.json", getClass()), responseHeaders));
		List<Album> albums = facebook.userApi().getAlbums("192837465");
		assertAlbums(albums);
	}
	
	@Test
	public void getFriendLists() {
		mockServer.expect(requestTo("https://graph.facebook.com/me/friendlists"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("friend-lists.json", getClass()), responseHeaders));
		List<Reference> friendLists = facebook.userApi().getFriendLists();
		assertFriendLists(friendLists);
	}

	@Test
	public void getFriendLists_forSpecificUser() {
		mockServer.expect(requestTo("https://graph.facebook.com/11223344/friendlists"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("friend-lists.json", getClass()), responseHeaders));
		List<Reference> friendLists = facebook.userApi().getFriendLists("11223344");
		assertFriendLists(friendLists);
	}
	
	@Test
	public void getFriends() {
		mockServer.expect(requestTo("https://graph.facebook.com/me/friends"))
				.andExpect(method(GET))
				.andExpect(header("Authorization", "OAuth someAccessToken"))
				.andRespond(withResponse(new ClassPathResource("friends.json", getClass()), responseHeaders));
		List<Reference> friends = facebook.userApi().getFriends();
		assertFriends(friends);
	}
	
	@Test
	public void getFriends_forSpecificUser() {
		mockServer.expect(requestTo("https://graph.facebook.com/912873465/friends"))
				.andExpect(method(GET))
				.andExpect(header("Authorization", "OAuth someAccessToken"))
				.andRespond(withResponse(new ClassPathResource("friends.json", getClass()), responseHeaders));
		List<Reference> friends = facebook.userApi().getFriends("912873465");
		assertFriends(friends);
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

	private void assertEvents(List<UserEvent> events) {
		assertEquals(2, events.size());
		assertEquals("188420717869087", events.get(0).getId());
		assertEquals("Afternoon naptime", events.get(0).getName());
		assertEquals("On the couch", events.get(0).getLocation());
		assertEquals(toDate("2011-03-26T14:00:00"), events.get(0).getStartTime());
		assertEquals(toDate("2011-03-26T15:00:00"), events.get(0).getEndTime());
		assertEquals(UserEvent.RsvpStatus.ATTENDING, events.get(0).getRsvpStatus());
		assertEquals("188420717869780", events.get(1).getId());
		assertEquals("Mow the lawn", events.get(1).getName());
		assertNull(events.get(1).getLocation());
		assertEquals(toDate("2011-03-26T15:00:00"), events.get(1).getStartTime());
		assertEquals(toDate("2011-03-26T16:00:00"), events.get(1).getEndTime());
		assertEquals(UserEvent.RsvpStatus.NOT_REPLIED, events.get(1).getRsvpStatus());
	}

	private void assertBasicProfileData(FacebookProfile profile) {
		assertEquals(123456789, profile.getId());
		assertEquals("Craig", profile.getFirstName());
		assertEquals("Walls", profile.getLastName());
		assertEquals("Craig Walls", profile.getName());
		assertEquals(new Locale("en_us"), profile.getLocale());
		assertEquals("male", profile.getGender());
	}

	private void assertEducationHistory(List<EducationEntry> educationHistory) {
		assertEquals(2, educationHistory.size());
		assertEquals("College", educationHistory.get(0).getType());
		assertEquals("103768553006294", educationHistory.get(0).getSchool().getId());
		assertEquals("New Mexico", educationHistory.get(0).getSchool().getName());
		assertEquals("117348274968344", educationHistory.get(0).getYear().getId());
		assertEquals("1994", educationHistory.get(0).getYear().getName());
		assertEquals("High School", educationHistory.get(1).getType());
		assertEquals("115157218496067", educationHistory.get(1).getSchool().getId());
		assertEquals("Jal High School", educationHistory.get(1).getSchool().getName());
		assertEquals("127132740657422", educationHistory.get(1).getYear().getId());
		assertEquals("1989", educationHistory.get(1).getYear().getName());
	}

	private void assertWorkHistory(List<WorkEntry> workHistory) {
		assertEquals(2, workHistory.size());
		assertEquals("119387448093014", workHistory.get(0).getEmployer().getId());
		assertEquals("SpringSource", workHistory.get(0).getEmployer().getName());
		assertEquals("0000-00", workHistory.get(0).getStartDate());
		assertEquals("0000-00", workHistory.get(0).getEndDate());
		assertEquals("298846151879", workHistory.get(1).getEmployer().getId());
		assertEquals("Improving", workHistory.get(1).getEmployer().getName());
		assertEquals("2009-03", workHistory.get(1).getStartDate());
		assertEquals("2010-05", workHistory.get(1).getEndDate());
	}

	private static final DateFormat FB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);

	private Date toDate(String dateString) {
		try {
			return FB_DATE_FORMAT.parse(dateString);
		} catch (ParseException e) {
			return null;
		}
	}

}
