package org.springframework.social.facebook;

import java.util.List;

import org.springframework.util.MultiValueMap;

/**
 * <p>
 * Interface specifying a basic set of operations for interacting with Facebook.
 * Implemented by {@link FacebookTemplate}. Not often used directly, but a
 * useful option to enhance testability, as it can easily be mocked or stubbed.
 * </p>
 * 
 * <p>
 * Many of the methods contained in this interface require an access token from
 * Facebook. When a method's description speaks of the "current user", it is
 * referring to the user for whom the access token has been issued.
 * </p>
 * 
 * @author Craig Walls
 */
public interface FacebookOperations {
	/**
	 * Retrieves the user's Facebook profile ID.
	 * 
	 * @return the user's Facebook profile ID.
	 */
	String getProfileId();

	/**
	 * Retrieve the current user's Facebook profile information.
	 * 
	 * @return the user's profile information.
	 */
	FacebookProfile getUserProfile();

	/**
	 * Retrieve the URL to the user's Facebook profile.
	 * 
	 * @return the URL to the user's Facebook profile.
	 */
	String getProfileUrl();

	/**
	 * Get a list of the user's friends.
	 * 
	 * @return a list of <code>String</code>s where each entry is the Facebook
	 *         ID of one of the user's friends.
	 */
	List<String> getFriendIds();

	/**
	 * Posts a message to the current user's wall.
	 * 
	 * @param message
	 *            The message to post
	 */
	void updateStatus(String status);

	/**
	 * Posts a message to the current user's wall.
	 * 
	 * @param message
	 *            The message to post
	 */
	void postToWall(String message);

	/**
	 * Posts a message to the current user's wall along with a link.
	 * 
	 * @param message
	 * @param link
	 */
	void postToWall(String message, FacebookLink link);

	/**
	 * <p>
	 * Low-level publish-to-Facebook method for publishing any type of object
	 * supported by Facebook's API.
	 * </p>
	 * 
	 * @param object
	 *            The ID of the object to publish to.
	 * @param connection
	 *            The connection to be published.
	 * @param data
	 *            The data to be published.
	 */
	void publish(String object, String connection, MultiValueMap<String, String> data);

	/**
	 * Retrieves the current user's profile picture as an array of bytes.
	 * 
	 * @return the user's profile picture in bytes.
	 */
	byte[] getProfilePicture();

	/**
	 * Retrieves a user's profile picture as an array of bytes.
	 * 
	 * @param profileId
	 *            the Facebook ID of the user.
	 * @return the user's profile picture in bytes.
	 */
	byte[] getProfilePicture(String profileId);
}
