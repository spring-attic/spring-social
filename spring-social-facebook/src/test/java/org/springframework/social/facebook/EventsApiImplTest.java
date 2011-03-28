package org.springframework.social.facebook;

import static org.junit.Assert.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.social.test.client.RequestMatchers.*;
import static org.springframework.social.test.client.ResponseCreators.*;

import java.util.List;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class EventsApiImplTest extends AbstractFacebookApiTest {

	@Test
	public void getEvents() {
		mockServer.expect(requestTo("https://graph.facebook.com/me/events"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("user-events.json", getClass()), responseHeaders));
		List<UserEvent> events = facebook.eventsApi().getEvents();
		assertEvents(events);
	}
	
	@Test
	public void getEvents_forSpecificUser() {
		mockServer.expect(requestTo("https://graph.facebook.com/123456789/events"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("user-events.json", getClass()), responseHeaders));
		List<UserEvent> events = facebook.eventsApi().getEvents("123456789");
		assertEvents(events);
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
	
}
