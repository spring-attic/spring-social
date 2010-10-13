package org.springframework.social.greenhouse;

import java.util.List;

import org.springframework.social.twitter.SearchResults;

public interface GreenhouseOperations {
	GreenhouseProfile getUserProfile();
	
	// getProfileImage() -- what to return here?

	List<RecentActivity> getRecentActivity();
	
	List<RecentActivity> getRecentActivity(String last);
	
	List<Event> getUpcomingEvents();

	List<Event> getEventsAfter(String dateTime);

	List<EventSession> getEventFavorites(long eventId);

	SearchResults getEventTweets(long eventId);

	SearchResults getEventTweets(long eventId, int page, int pageSize);

	void postEventTweet(long eventId, String status);

	void postEventRetweet(long eventId, long tweetId);

	List<EventSession> getMyEventFavorites(long eventId);

	List<EventSession> getSessionsOnDay(long eventId, String dateTime);

	void toggleSessionFavorite(long eventId, long sessionId);

	void updateSessionRating(long eventId, long sessionId, short value, String comment);

	SearchResults getSessionTweets(long eventId, long sessionId);

	SearchResults getSessionTweets(long eventId, long sessionId, int page, int pageSize);

	void postSessionTweet(long eventId, long sessionId, String status);

	void postSessionRetweet(long eventId, long sessionId, long tweetId);
}
