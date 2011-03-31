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

public interface EventsApi {

	/**
	 * Retrieves a list of events that the authenticated user has been invited to.
	 * Requires "user_events" or "friends_events" permission.
	 * @return a list {@link UserEvent}s for the user, or an empty list if not available.
	 */
	List<UserEvent> getEvents();

	/**
	 * Retrieves a list of events that the specified user has been invited to.
	 * Requires "user_events" or "friends_events" permission.
	 * @param userId the user's ID
	 * @return a list {@link UserEvent}s for the user, or an empty list if not available.
	 */
	List<UserEvent> getEvents(String userId);
	
	/**
	 * Retrieves event data for a specified event.
	 * @param eventId the event ID
	 * @return an {@link Event} object
	 */
	Event getEvent(String eventId);
	
	/**
	 * Creates an event.
	 * The String passed in for start time and end time is flexible in regard to format. Some valid examples are:
	 * <ul>
	 * <li>2011-04-01T15:30:00 (3:30PM on April 1, 2011)</li>
	 * <li>2011-04-01 (midnight on April 1, 2011)</li>
	 * <li>April 1, 2011 (midnight on April 1, 2011)</li>
	 * <li>17:00:00 (5:00PM today)</li>
	 * <li>10-11-2011 (November 10, 2012)</li>
	 * <li>10/11/2012 (October 11, 2012)</li>
	 * <li>10.11.2012 (November 10, 2012)</li>
	 * <li>Tomorrow 2PM</li>
	 * </ul>
	 * @param name the name of the event
	 * @param startTime the start time of the event.
	 * @param endTime the end time of the event.
	 * @return the newly created event's ID
	 */
	String createEvent(String name, String startTime, String endTime);
	
	void deleteEvent(String eventId);
	
	/**
	 * Retrieves the list of an event's invitees.
	 * @param eventId the event ID.
	 * @return a list of {@link EventInvitee}s for the event.
	 */
	List<EventInvitee> getInvited(String eventId);
	
	/**
	 * Retrieves the list of an event's invitees who have accepted the invitation.
	 * @param eventId the event ID.
	 * @return a list of {@link EventInvitee}s for the event.
	 */
	List<EventInvitee> getAttending(String eventId);
	
	/**
	 * Retrieves the list of an event's invitees who have indicated that they may attend the event.
	 * @param eventId the event ID.
	 * @return a list of {@link EventInvitee}s for the event.
	 */
	List<EventInvitee> getMaybeAttending(String eventId);
	
	/**
	 * Retrieves the list of an event's invitees who have not yet RSVP'd.
	 * @param eventId the event ID.
	 * @return a list of {@link EventInvitee}s for the event.
	 */
	List<EventInvitee> getNoReplies(String eventId);
	
	/**
	 * Retrieves the list of an event's invitees who have declined the invitation.
	 * @param eventId the event ID.
	 * @return a list of {@link EventInvitee}s for the event.
	 */
	List<EventInvitee> getDeclined(String eventId);

	/**
	 * Accepts an invitation to an event.
	 * Requires "rsvp_event" permission.
	 * @param eventId the event ID
	 */
	void acceptInvitation(String eventId);
	
	/**
	 * RSVPs to an event with a maybe.
	 * Requires "rsvp_event" permission.
	 * @param eventId the event ID
	 */
	void maybeInvitation(String eventId);
	
	/**
	 * Declines an invitation to an event.
	 * Requires "rsvp_event" permission.
	 * @param eventId the event ID
	 */
	void declineInvitation(String eventId);

}
