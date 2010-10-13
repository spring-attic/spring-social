package org.springframework.social.greenhouse;

import java.util.Date;
import java.util.List;

public interface GreenhouseOperations {
	GreenhouseProfile getUserProfile();
	
	List<Event> getUpcomingEvents();

	List<Event> getEventsAfter(Date date);

	List<EventSession> getSessionsOnDay(long eventId, Date date);
}
