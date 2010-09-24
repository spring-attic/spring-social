package org.springframework.social.facebook;

import java.util.List;

import org.springframework.util.MultiValueMap;

/**
 * Interface specifying a basic set of operations for interacting with Facebook.
 * Implemented by {@link FacebookTemplate}. Not often used directly, but a
 * useful option to enhance testability, as it can easily be mocked or stubbed.
 * 
 * Many of the methods contained in this interface require an access token from
 * Facebook. When a method's description speaks of the "current user", it is
 * referring to the user for whom the access token has been issued.
 * 
 * @author Craig Walls
 */
public interface FacebookOperations {
	/**
	 * Retrieve the current user's Facebook profile information.
	 * 
	 * @param accessToken
	 * @return the user's profile information.
	 */
	FacebookUserInfo getUserInfo();

	/**
	 * Get a list of the user's friends.
	 * 
	 * @param accessToken
	 * @return a list of <code>String</code>s where each entry is the Facebook
	 *         ID of one of the user's friends.
	 */
	List<String> getFriendIds();

	/**
	 * Posts a message to the current user's wall.
	 * 
	 * @param accessToken
	 * @param message
	 *            The message to post
	 */
	void postToWall(String message);

	/**
	 * Posts a message to the current user's wall along with a link.
	 * 
	 * @param accessToken
	 * @param message
	 * @param link
	 */
	void postToWall(String message, FacebookLink link);

	void publish(String object, String connection, MultiValueMap<String, String> data);

	/**
	 * Retrieves the current user's profile picture as an array of bytes.
	 * 
	 * @param accessToken
	 * @return the user's profile picture in bytes.
	 */
	byte[] getProfilePicture();

	/**
	 * Retrieves a user's profile picture as an array of bytes.
	 * 
	 * @param accessToken
	 * @param profileId
	 *            the Facebook ID of the user.
	 * @return the user's profile picture in bytes.
	 */
	byte[] getProfilePicture(String profileId);
}
