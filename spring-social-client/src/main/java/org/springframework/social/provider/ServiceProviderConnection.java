package org.springframework.social.provider;

/**
 * A connection between a user account and a service provider.
 * @author Keith Donald
 * @param <S> the service API
 */
public interface ServiceProviderConnection<S> {

	/**
	 * The connection identifier.
	 */
	public String getId();

	/**
	 * The Service API.
	 */
	public S getApi();
	
	/**
	 * Severs this connection.
	 * This object should no longer be used after calling this method.
	 */
	public void disconnect();
	
}