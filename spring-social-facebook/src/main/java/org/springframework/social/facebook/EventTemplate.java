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

import java.util.List;

import org.springframework.social.facebook.support.extractors.EventResponseExtractor;
import org.springframework.social.facebook.support.extractors.InviteeResponseExtractor;
import org.springframework.social.facebook.support.extractors.UserEventResponseExtractor;
import org.springframework.social.facebook.types.Event;
import org.springframework.social.facebook.types.EventInvitee;
import org.springframework.social.facebook.types.UserEvent;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

class EventTemplate implements EventOperations {

	private EventResponseExtractor eventExtractor;
	
	private UserEventResponseExtractor userEventExtractor;
	
	private InviteeResponseExtractor inviteeExtractor;
	
	private final GraphApi graphApi;

	public EventTemplate(GraphApi graphApi) {
		this.graphApi = graphApi;
		eventExtractor = new EventResponseExtractor();
		userEventExtractor = new UserEventResponseExtractor();
		inviteeExtractor = new InviteeResponseExtractor();
	}

	public List<UserEvent> getEvents() {
		return getEvents("me");
	}

	public List<UserEvent> getEvents(String userId) {
		return graphApi.fetchConnections(userId, "events", userEventExtractor);
	}
	
	public Event getEvent(String eventId) {
		return graphApi.fetchObject(eventId, eventExtractor);
	}
	
	public String createEvent(String name, String startTime, String endTime) {
		MultiValueMap<String, String> data = new LinkedMultiValueMap<String, String>();
		data.set("name", name);
		data.set("start_time", startTime);
		data.set("end_time", endTime);
		return graphApi.publish("me", "events", data);
	}
	
	public void deleteEvent(String eventId) {
		graphApi.delete(eventId);
	}

	public List<EventInvitee> getInvited(String eventId) {
		return graphApi.fetchConnections(eventId, "invited", inviteeExtractor);
	}

	public List<EventInvitee> getAttending(String eventId) {
		return graphApi.fetchConnections(eventId, "attending", inviteeExtractor);
	}
	
	public List<EventInvitee> getMaybeAttending(String eventId) {
		return graphApi.fetchConnections(eventId, "maybe", inviteeExtractor);
	}
	
	public List<EventInvitee> getNoReplies(String eventId) {
		return graphApi.fetchConnections(eventId, "noreply", inviteeExtractor);
	}

	public List<EventInvitee> getDeclined(String eventId) {
		return graphApi.fetchConnections(eventId, "declined", inviteeExtractor);
	}
	
	public void acceptInvitation(String eventId) {
		graphApi.post(eventId, "attending", new LinkedMultiValueMap<String, String>());
	}

	public void maybeInvitation(String eventId) {
		graphApi.post(eventId, "maybe", new LinkedMultiValueMap<String, String>());
	}

	public void declineInvitation(String eventId) {
		graphApi.post(eventId, "declined", new LinkedMultiValueMap<String, String>());
	}
}
