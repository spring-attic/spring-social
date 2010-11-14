/*
 * Copyright 2010 the original author or authors.
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
package org.springframework.social.greenhouse;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * Interface specifying a basic set of operations for interacting with
 * Greenhouse. Implemented by {@link GreenhouseTemplate}. Not often used
 * directly, but a useful option to enhance testability, as it can easily be
 * mocked or stubbed.
 * </p>
 * 
 * <p>
 * The methods contained in this interface require OAuth authentication with
 * Greenhouse. When a method's description speaks of the "current user", it is
 * referring to the user for whom access has been granted.
 * </p>
 * 
 * @author Craig Walls
 * 
 */
public interface GreenhouseOperations {

	/**
	 * Retrieve the current user's Greenhouse profile information.
	 * 
	 * @return the user's profile information.
	 */
	GreenhouseProfile getUserProfile();

	/**
	 * Retrieve a list of upcoming events.
	 * 
	 * @return A list of events that take place after the current time.
	 */
	List<Event> getUpcomingEvents();

	/**
	 * Retrieve a list of events that take place after a given time.
	 * 
	 * @param date
	 *            the starting point used to filter the list of events.
	 * @return a list of events that take place after the given time.
	 */
	List<Event> getEventsAfter(Date date);
	
	/**
	 * Retrieves a list of sessions for an event that take place on a given day.
	 * 
	 * @param eventId
	 *            the ID of the event
	 * @param date
	 *            the day to retrieve events for
	 * @return a list of sessions for the given event and day
	 */
	List<EventSession> getSessionsOnDay(long eventId, Date date);

	/**
	 * Retrieves a list of sessions marked as favorites by the user
	 * 
	 * @param eventId
	 *            the ID of the event
	 * @return a list of sessions marked as favorites
	 */
	List<EventSession> getFavoriteSessions(long eventId);
	
	/**
	 * Retrieves a list of sessions marked as favorites by all users.
	 * 
	 * @param eventId
	 *            the ID of the event
	 * @return a list of sessions marked as favorites
	 */
	List<EventSession> getConferenceFavoriteSessions(long eventId);
		
	/**
	 * Updates the favorite status for a session. If the session is not a favorite 
	 * it is marked as a favorite.
	 * 
	 * @param eventId
	 *            the ID of the event
	 * @param sessionId
	 *            the ID of the session
	 * @return true if the session is a favorite, and false if it is not
	 */
	boolean updateFavoriteSession(long eventId, long sessionId);
}
