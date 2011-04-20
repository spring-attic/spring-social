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
package org.springframework.social.facebook.event;

import java.util.List;

import org.springframework.social.facebook.graph.GraphApi;
import org.springframework.social.facebook.graph.ImageType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class EventTemplate implements EventOperations {
			
	private final GraphApi graphApi;

	public EventTemplate(GraphApi graphApi) {
		this.graphApi = graphApi;
	}

	public List<Invitation> getInvitations() {
		return getInvitations("me");
	}

	public List<Invitation> getInvitations(String userId) {
		return graphApi.fetchConnections(userId, "events", InvitationList.class).getList();
	}
	
	public Event getEvent(String eventId) {
		return graphApi.fetchObject(eventId, Event.class);
	}
	
	public byte[] getEventImage(String eventId) {
		return getEventImage(eventId, ImageType.NORMAL);
	}
	
	public byte[] getEventImage(String eventId, ImageType imageType) {
		return graphApi.fetchImage(eventId, "picture", imageType);
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
		return graphApi.fetchConnections(eventId, "invited", EventInviteeList.class).getList();
	}

	public List<EventInvitee> getAttending(String eventId) {
		return graphApi.fetchConnections(eventId, "attending", EventInviteeList.class).getList();
	}
	
	public List<EventInvitee> getMaybeAttending(String eventId) {
		return graphApi.fetchConnections(eventId, "maybe", EventInviteeList.class).getList();
	}
	
	public List<EventInvitee> getNoReplies(String eventId) {
		return graphApi.fetchConnections(eventId, "noreply", EventInviteeList.class).getList();
	}

	public List<EventInvitee> getDeclined(String eventId) {
		return graphApi.fetchConnections(eventId, "declined", EventInviteeList.class).getList();
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
