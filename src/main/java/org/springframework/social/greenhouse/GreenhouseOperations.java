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
}
