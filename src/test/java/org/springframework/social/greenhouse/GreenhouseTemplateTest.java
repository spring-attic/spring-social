package org.springframework.social.greenhouse;

import static org.junit.Assert.*;
import static org.junit.internal.matchers.IsCollectionContaining.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.social.greenhouse.GreenhouseTemplate.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;

public class GreenhouseTemplateTest {
	private GreenhouseTemplate greenhouse;
	private RestOperations restOperations;
	private MultiValueMap<String, String> jsonAcceptingHeaders;

	@Before
	public void setup() {
		greenhouse = new GreenhouseTemplate("API_KEY", "API_SECRET", "ACCESS_TOKEN",
				"ACCESS_TOKEN_SECRET");
		restOperations = mock(RestOperations.class);
		greenhouse.restOperations = restOperations;
		jsonAcceptingHeaders = new LinkedMultiValueMap<String, String>();
		jsonAcceptingHeaders.add("Accept", "application/json");

	}

	@Test
	public void getUserProfile() {
		GreenhouseProfile profile = new GreenhouseProfile();
		profile.accountId = 1L;
		profile.displayName = "Craig Walls";
		profile.pictureUrl = "https://greenhouse.springsource.org/images/1";
		ResponseEntity<GreenhouseProfile> response = new ResponseEntity<GreenhouseProfile>(profile, HttpStatus.OK);
		when(restOperations.exchange(eq(DEFAULT_BASE_URL + PROFILE_PATH), eq(GET), any(HttpEntity.class),
						eq(GreenhouseProfile.class), eq("@self"))).thenReturn(response);

		GreenhouseProfile actual = greenhouse.getUserProfile();
		assertEquals(1L, actual.getAccountId());
		assertEquals("Craig Walls", actual.getDisplayName());
		assertEquals("https://greenhouse.springsource.org/images/1", actual.getPictureUrl());
	}

	@Test
	public void getUpcomingEvents() {
		Event event1 = new Event();
		Event event2 = new Event();
		Event event3 = new Event();
		Event[] body = new Event[] { event1, event2, event3 };
		ResponseEntity<Event[]> response = new ResponseEntity<Event[]>(body, OK);
		when(restOperations.exchange(eq(DEFAULT_BASE_URL + EVENTS_PATH), eq(GET), any(HttpEntity.class), eq(Event[].class)))
				.thenReturn(response);

		List<Event> events = greenhouse.getUpcomingEvents();
		assertThat(events, hasItem(event1));
		assertThat(events, hasItem(event2));
		assertThat(events, hasItem(event3));
	}

	@Test
	public void getEventsAfter() {
		Event event1 = new Event();
		Event event2 = new Event();
		Event event3 = new Event();
		Event[] events = new Event[] { event1, event2, event3 };
		ResponseEntity<Event[]> response = new ResponseEntity<Event[]>(events, OK);
		Date now = new Date();
		String isoDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000-00:00").format(now);
		when(restOperations.exchange(eq(DEFAULT_BASE_URL + EVENTS_PATH + "?after={dateTime}"), eq(GET),
						any(HttpEntity.class), eq(Event[].class), eq(isoDate))).thenReturn(response);

		List<Event> actual = greenhouse.getEventsAfter(now);
		assertThat(actual, hasItem(event1));
		assertThat(actual, hasItem(event2));
		assertThat(actual, hasItem(event3));
	}

	@Test
	public void getSessionsOnDay() {
		Date now = new Date();
		String isoDate = new SimpleDateFormat("yyyy-MM-dd").format(now);

		EventSession session1 = new EventSession();
		EventSession session2 = new EventSession();
		EventSession session3 = new EventSession();
		EventSession[] sessions = new EventSession[] { session1, session2, session3 };
		ResponseEntity<EventSession[]> response = new ResponseEntity<EventSession[]>(sessions, OK);
		when(restOperations.exchange(eq(DEFAULT_BASE_URL + SESSIONS_FOR_DAY_PATH), eq(GET), any(HttpEntity.class),
						eq(EventSession[].class), eq(123L), eq(isoDate))).thenReturn(response);

		List<EventSession> actual = greenhouse.getSessionsOnDay(123L, now);
		assertThat(actual, hasItem(session1));
		assertThat(actual, hasItem(session2));
		assertThat(actual, hasItem(session3));
	}
}
