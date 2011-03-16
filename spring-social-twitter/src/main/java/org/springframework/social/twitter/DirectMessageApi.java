package org.springframework.social.twitter;

import java.util.List;

public interface DirectMessageApi {

	/**
	 * Retrieve the 20 most recently received direct messages for the authenticating user.
	 * @return a collection of {@link DirectMessage} with the authenticating user as the recipient.
	 */
	List<DirectMessage> getDirectMessagesReceived();

	/**
	 * Sends a direct message to another Twitter user.
	 * The recipient of the message must follow the authenticated user in order
	 * for the message to be delivered. If the recipient is not following the
	 * authenticated user, an {@link InvalidMessageRecipientException} will be thrown.
	 * @param toScreenName the screen name of the recipient of the messages.
	 * @param text the message text.
	 * @throws InvalidMessageRecipientException if the recipient is not following the authenticating user.
	 * @throws DuplicateTweetException if the message duplicates a previously sent message.
	 */
	void sendDirectMessage(String toScreenName, String text);

	/**
	 * Sends a direct message to another Twitter user.
	 * The recipient of the message must follow the authenticated user in order
	 * for the message to be delivered. If the recipient is not following the
	 * authenticated user, an {@link InvalidMessageRecipientException} will be thrown.
	 * @param toUserId the Twitter user ID of the recipient of the messages.
	 * @param text the message text.
	 * @throws InvalidMessageRecipientException if the recipient is not following the authenticating user.
	 * @throws DuplicateTweetException if the message duplicates a previously sent message.
	 */
	void sendDirectMessage(long toUserId, String text);
	
}
