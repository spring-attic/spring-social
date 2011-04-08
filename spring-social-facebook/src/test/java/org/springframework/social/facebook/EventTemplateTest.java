package org.springframework.social.facebook;

import static org.junit.Assert.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.social.test.client.RequestMatchers.*;
import static org.springframework.social.test.client.ResponseCreators.*;

import java.util.List;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.social.facebook.types.Event;
import org.springframework.social.facebook.types.EventInvitee;
import org.springframework.social.facebook.types.RsvpStatus;
import org.springframework.social.facebook.types.UserEvent;

public class EventTemplateTest extends AbstractFacebookApiTest {

	@Test
	public void getEvents() {
		mockServer.expect(requestTo("https://graph.facebook.com/me/events"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("testdata/user-events.json", getClass()), responseHeaders));
		List<UserEvent> events = facebook.eventOperations().getEvents();
		assertEvents(events);
	}
	
	@Test
	public void getEvents_forSpecificUser() {
		mockServer.expect(requestTo("https://graph.facebook.com/123456789/events"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("testdata/user-events.json", getClass()), responseHeaders));
		List<UserEvent> events = facebook.eventOperations().getEvents("123456789");
		assertEvents(events);
	}
	
	@Test
	public void getEvent() {
		mockServer.expect(requestTo("https://graph.facebook.com/193482154020832"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("testdata/simple-event.json", getClass()), responseHeaders));
		Event event = facebook.eventOperations().getEvent("193482154020832");
		assertEquals("193482154020832", event.getId());
		assertEquals("100001387295207", event.getOwner().getId());
		assertEquals("Art Names", event.getOwner().getName());
		assertEquals("Breakdancing Class", event.getName());
		assertEquals(Event.Privacy.OPEN, event.getPrivacy());
		assertEquals(toDate("2011-03-30T14:30:00"), event.getStartTime());
		assertEquals(toDate("2011-03-30T17:30:00"), event.getEndTime());
		assertEquals(toDate("2011-03-30T14:30:28+0000"), event.getUpdatedTime());
		assertNull(event.getDescription());
		assertNull(event.getLocation());
	}
	
	@Test
	public void getEvent_withLocationAndDescription() {
		mockServer.expect(requestTo("https://graph.facebook.com/193482154020832"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("testdata/full-event.json", getClass()), responseHeaders));
		Event event = facebook.eventOperations().getEvent("193482154020832");
		assertEquals("193482154020832", event.getId());
		assertEquals("100001387295207", event.getOwner().getId());
		assertEquals("Art Names", event.getOwner().getName());
		assertEquals("Breakdancing Class", event.getName());
		assertEquals(Event.Privacy.SECRET, event.getPrivacy());
		assertEquals(toDate("2011-03-30T14:30:00"), event.getStartTime());
		assertEquals(toDate("2011-03-30T17:30:00"), event.getEndTime());
		assertEquals(toDate("2011-03-30T14:38:40+0000"), event.getUpdatedTime());
		assertEquals("Bring your best parachute pants!", event.getDescription());
		assertEquals("2400 Dunlavy Dr, Denton, TX", event.getLocation());
	}
	
	@Test
	public void createEvent() {
		mockServer.expect(requestTo("https://graph.facebook.com/me/events"))
			.andExpect(method(POST))
			.andExpect(body("name=Test+Event&start_time=2011-04-01T15%3A30%3A00&end_time=2011-04-01T18%3A30%3A00"))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse("{\"id\":\"193482145020832\"}", responseHeaders));
		String eventId = facebook.eventOperations().createEvent("Test Event", "2011-04-01T15:30:00", "2011-04-01T18:30:00");
		assertEquals("193482145020832", eventId);
	}
	
	@Test
	public void getInvited() {
		mockServer.expect(requestTo("https://graph.facebook.com/193482154020832/invited"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("testdata/invited.json", getClass()), responseHeaders));
		List<EventInvitee> invited = facebook.eventOperations().getInvited("193482154020832");
		assertEquals(3, invited.size());
		assertInvitee(invited.get(0), "100001387295207", "Art Names", RsvpStatus.ATTENDING);
		assertInvitee(invited.get(1), "738140579", "Craig Walls", RsvpStatus.UNSURE);
		assertInvitee(invited.get(2), "975041837", "Chuck Wagon", RsvpStatus.NOT_REPLIED);
	}
	
	@Test
	public void getAttending() {
		mockServer.expect(requestTo("https://graph.facebook.com/193482154020832/attending"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("testdata/attending.json", getClass()), responseHeaders));
		List<EventInvitee> invited = facebook.eventOperations().getAttending("193482154020832");
		assertEquals(3, invited.size());
		assertInvitee(invited.get(0), "100001387295207", "Art Names", RsvpStatus.ATTENDING);
		assertInvitee(invited.get(1), "738140579", "Craig Walls", RsvpStatus.ATTENDING);
		assertInvitee(invited.get(2), "975041837", "Chuck Wagon", RsvpStatus.ATTENDING);
	}
	
	@Test
	public void getMaybeAttending() {
		mockServer.expect(requestTo("https://graph.facebook.com/193482154020832/maybe"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("testdata/maybe-attending.json", getClass()), responseHeaders));
		List<EventInvitee> invited = facebook.eventOperations().getMaybeAttending("193482154020832");
		assertEquals(3, invited.size());
		assertInvitee(invited.get(0), "100001387295207", "Art Names", RsvpStatus.UNSURE);
		assertInvitee(invited.get(1), "738140579", "Craig Walls", RsvpStatus.UNSURE);
		assertInvitee(invited.get(2), "975041837", "Chuck Wagon", RsvpStatus.UNSURE);
	}
	
	@Test
	public void getNoReplies() {
		mockServer.expect(requestTo("https://graph.facebook.com/193482154020832/noreply"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("testdata/no-replies.json", getClass()), responseHeaders));
		List<EventInvitee> invited = facebook.eventOperations().getNoReplies("193482154020832");
		assertEquals(3, invited.size());
		assertInvitee(invited.get(0), "100001387295207", "Art Names", RsvpStatus.NOT_REPLIED);
		assertInvitee(invited.get(1), "738140579", "Craig Walls", RsvpStatus.NOT_REPLIED);
		assertInvitee(invited.get(2), "975041837", "Chuck Wagon", RsvpStatus.NOT_REPLIED);
	}
	
	@Test
	public void getDeclined() {
		mockServer.expect(requestTo("https://graph.facebook.com/193482154020832/declined"))
			.andExpect(method(GET))
			.andExpect(header("Authorization", "OAuth someAccessToken"))
			.andRespond(withResponse(new ClassPathResource("testdata/declined.json", getClass()), responseHeaders));
		List<EventInvitee> invited = facebook.eventOperations().getDeclined("193482154020832");
		assertEquals(3, invited.size());
		assertInvitee(invited.get(0), "100001387295207", "Art Names", RsvpStatus.DECLINED);
		assertInvitee(invited.get(1), "738140579", "Craig Walls", RsvpStatus.DECLINED);
		assertInvitee(invited.get(2), "975041837", "Chuck Wagon", RsvpStatus.DECLINED);
	}

	private void assertInvitee(EventInvitee invitee, String id, String name, RsvpStatus rsvpStatus) {
		assertEquals(id, invitee.getId());
		assertEquals(name, invitee.getName());
		assertEquals(rsvpStatus, invitee.getRsvpStatus());
	}
	
	private void assertEvents(List<UserEvent> events) {
		assertEquals(2, events.size());
		assertEquals("188420717869087", events.get(0).getId());
		assertEquals("Afternoon naptime", events.get(0).getName());
		assertEquals("On the couch", events.get(0).getLocation());
		assertEquals(toDate("2011-03-26T14:00:00"), events.get(0).getStartTime());
		assertEquals(toDate("2011-03-26T15:00:00"), events.get(0).getEndTime());
		assertEquals(RsvpStatus.ATTENDING, events.get(0).getRsvpStatus());
		assertEquals("188420717869780", events.get(1).getId());
		assertEquals("Mow the lawn", events.get(1).getName());
		assertNull(events.get(1).getLocation());
		assertEquals(toDate("2011-03-26T15:00:00"), events.get(1).getStartTime());
		assertEquals(toDate("2011-03-26T16:00:00"), events.get(1).getEndTime());
		assertEquals(RsvpStatus.NOT_REPLIED, events.get(1).getRsvpStatus());
	}
	
}
