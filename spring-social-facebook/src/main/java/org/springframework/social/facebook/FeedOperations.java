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

import org.springframework.social.facebook.types.FacebookLink;
import org.springframework.social.facebook.types.LinkPost;
import org.springframework.social.facebook.types.NotePost;
import org.springframework.social.facebook.types.Post;
import org.springframework.social.facebook.types.StatusPost;

/**
 * Interface defining operations that can be performed on a Facebook feed.
 * @author Craig Walls
 */
public interface FeedOperations {

	/**
	 * Retrieves recent feed entries for the authenticated user. 
	 * Returns up to 50 entries or 30 days worth of entries, whichever is greatest.
	 * @return a list of {@link Post}s for the authenticated user. 
	 */
	List<Post> getFeed();

	/**
	 * Retrieves recent feed entries for a given user. 
	 * Returns up to 50 entries or 30 days worth of entries, whichever is greatest.
	 * @param ownerId the Facebook ID or alias for the owner (user, group, event, page, etc) of the feed.
	 * @return a list of {@link Post}s for the specified user. 
	 */
	List<Post> getFeed(String ownerId);

	/**
	 * Retrieves the user's home feed. This includes entries from the user's friends.
	 * @return a list of {@link Post}s from the authenticated user's home feed.
	 */
	List<Post> getHomeFeed();

	/**
	 * Retrieves the user's home feed. This includes entries from the user's friends.
	 * @param userId the user ID.
	 * @return a list of {@link Post}s from the specified user's home feed.
	 */
	List<Post> getHomeFeed(String userId);

	/**
	 * Retrieves a single feed entry.
	 * @param entryId the entry ID
	 * @return the requested {@link Post}
	 */
	Post getFeedEntry(String entryId);
	
	/**
	 * Retrieves the status entries from the authenticated user's feed.
	 * @return a list of status {@link Post}s. 
	 */
	List<StatusPost> getStatuses();
	
	/**
	 * Retrieves the status entries from the specified user's feed.
	 * @param userId the user's ID
	 * @return a list of status {@link Post}s. 
	 */
	List<StatusPost> getStatuses(String userId);
	
	/**
	 * Retrieves the link entries from the authenticated user's feed.
	 * @return a list of link {@link Post}s. 
	 */
	List<LinkPost> getLinks();
	
	/**
	 * Retrieves the link entries from the specified owner's feed.
	 * @param ownerId the owner of the feed (could be a user, page, event, etc)
	 * @return a list of link {@link Post}s. 
	 */
	List<LinkPost> getLinks(String ownerId);
	
	/**
	 * Retrieves the note entries from the authenticated user's feed.
	 * @return a list of note {@link Post}s. 
	 */
	List<NotePost> getNotes();
	
	/**
	 * Retrieves the note entries from the specified owner's feed.
	 * @param ownerId the owner of the feed (could be a user, page, event, etc)
	 * @return a list of note {@link Post}s. 
	 */
	List<NotePost> getNotes(String ownerId);
	
	/**
	 * Retrieves the post entries from the authenticated user's feed.
	 * @return a list of post {@link Post}s. 
	 */
	List<Post> getPosts();
	
	/**
	 * Retrieves the post entries from the specified owner's feed.
	 * @param ownerId the owner of the feed (could be a user, page, event, etc)
	 * @return a list of post {@link Post}s. 
	 */
	List<Post> getPosts(String ownerId);
	
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
