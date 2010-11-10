package org.springframework.social.connect;

/**
 * Strategy interface for resolving an account ID in a service provider.
 * 
 * @author Craig Walls
 */
public interface AccountIdResolver {
	Object resolveAccountId();
}
