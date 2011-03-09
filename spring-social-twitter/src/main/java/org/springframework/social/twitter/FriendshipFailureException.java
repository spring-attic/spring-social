package org.springframework.social.twitter;

import org.springframework.social.SocialException;

/**
 * An exception that is thrown when a follow or and unfollow fails usually because the authenticated user is already
 * following the specified user or tried to unfollow the user not already following.
 * 
 * @author Gary Jarrel
 */
public class FriendshipFailureException extends SocialException {

    private static final long serialVersionUID = 1L;
    
    public FriendshipFailureException(String message) {
        super(message);
    }

    public FriendshipFailureException(String message, Throwable cause) {
        super(message, cause);
    }

}
