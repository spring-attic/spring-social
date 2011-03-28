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

/**
 * Interface defining operations that can be performed on a Facebook feed.
 * @author Craig Walls
 */
public interface FeedApi {

	/**
	 * Retrieves recent feed entries for the authenticated user. 
	 * Returns up to 50 entries or 30 days worth of entries, whichever is greatest.
	 * @return a list of {@link FeedEntry}s for the authenticated user. 
	 */
	List<FeedEntry> getFeed();

	/**
	 * Retrieves recent feed entries for a given user. 
	 * Returns up to 50 entries or 30 days worth of entries, whichever is greatest.
	 * @param ownerId the Facebook ID or alias for the owner (user, group, event, page, etc) of the feed.
	 * @return a list of {@link FeedEntry}s for the specified user. 
	 */
	List<FeedEntry> getFeed(String ownerId);

	List<FeedEntry> getHomeFeed();

	List<FeedEntry> getHomeFeed(String userId);

	/**
	 * Retrieves a single feed entry.
	 * @param entryId the entry ID
	 * @return the requested {@link FeedEntry}
	 */
	FeedEntry getFeedEntry(String entryId);
	
	List<FeedEntry> getStatuses();
	
	List<FeedEntry> getStatuses(String userId);
	
	List<FeedEntry> getLinks();
	
	List<FeedEntry> getLinks(String ownerId);
	
	List<FeedEntry> getNotes();
	
	List<FeedEntry> getNotes(String ownerId);
	
	List<FeedEntry> getPosts();
	
	List<FeedEntry> getPosts(String ownerId);
	
	/**
	 * Posts a status update to the authenticated user's feed.
	 * @param message the message to post.
	 * @return the ID of the new feed entry.
	 */
	String updateStatus(String message);

	/**
	 * Posts a link to the authenticated user's feed.
	 * @param message a message to send with the link.
	 * @return the ID of the new feed entry.
	 */
	String postLink(String message, FacebookLink link);

	/**
	 * Deletes a feed entry
	 * @param id the feed entry ID
	 */
	void deleteFeedEntry(String id);

}
