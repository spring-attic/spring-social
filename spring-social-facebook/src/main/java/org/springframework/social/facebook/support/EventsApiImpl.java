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
package org.springframework.social.facebook.support;

import java.util.List;
import java.util.Map;

import org.springframework.social.facebook.Event;
import org.springframework.social.facebook.EventInvitee;
import org.springframework.social.facebook.EventsApi;
import org.springframework.social.facebook.UserEvent;
import org.springframework.social.facebook.support.extractors.EventResponseExtractor;
import org.springframework.social.facebook.support.extractors.InviteeResponseExtractor;
import org.springframework.social.facebook.support.extractors.UserEventResponseExtractor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class EventsApiImpl extends AbstractFacebookApi implements EventsApi {

	private EventResponseExtractor eventExtractor;
	private UserEventResponseExtractor userEventExtractor;
	private InviteeResponseExtractor inviteeExtractor;

	public EventsApiImpl(RestTemplate restTemplate) {
		super(restTemplate);
		eventExtractor = new EventResponseExtractor();
		userEventExtractor = new UserEventResponseExtractor();
		inviteeExtractor = new InviteeResponseExtractor();
	}

	public List<UserEvent> getEvents() {
		return getEvents("me");
	}

	public List<UserEvent> getEvents(String userId) {
		return getObjectConnection(userId, "events", userEventExtractor);
	}
	
	public Event getEvent(String eventId) {
		return getObject(eventId, eventExtractor);
	}
	
	public String createEvent(String name, String startTime, String endTime) {
		MultiValueMap<String, String> data = new LinkedMultiValueMap<String, String>();
		data.set("name", name);
		data.set("start_time", startTime);
		data.set("end_time", endTime);
		return (String) ((Map<String, Object>) publish("me", "events", data)).get("id");
	}
	
	public void deleteEvent(String eventId) {
		delete(eventId);
	}

	public List<EventInvitee> getInvited(String eventId) {
		return getObjectConnection(eventId, "invited", inviteeExtractor);
	}

	public List<EventInvitee> getAttending(String eventId) {
		return getObjectConnection(eventId, "attending", inviteeExtractor);
	}
	
	public List<EventInvitee> getMaybeAttending(String eventId) {
		return getObjectConnection(eventId, "maybe", inviteeExtractor);
	}
	
	public List<EventInvitee> getNoReplies(String eventId) {
		return getObjectConnection(eventId, "noreply", inviteeExtractor);
	}

	public List<EventInvitee> getDeclined(String eventId) {
		return getObjectConnection(eventId, "declined", inviteeExtractor);
	}
	
	public void acceptInvitation(String eventId) {
		post(eventId, "attending", new LinkedMultiValueMap<String, String>());
	}

	public void maybeInvitation(String eventId) {
		post(eventId, "maybe", new LinkedMultiValueMap<String, String>());
	}

	public void declineInvitation(String eventId) {
		post(eventId, "declined", new LinkedMultiValueMap<String, String>());
	}
}
