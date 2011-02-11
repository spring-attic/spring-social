package org.springframework.social.connect.oauth1;

import org.springframework.social.connect.support.ConnectionRepository;
import org.springframework.social.oauth1.OAuth10aOperations;

public abstract class AbstractOAuth10aServiceProvider<S> extends AbstractOAuth1ServiceProvider<S> implements OAuth10aServiceProvider<S> {

	private final OAuth10aOperations oauth1Operations;

	public AbstractOAuth10aServiceProvider(String id, ConnectionRepository connectionRepository, String consumerKey,
			String consumerSecret, OAuth10aOperations oauth1Operations) {
		super(id, connectionRepository, consumerKey, consumerSecret);
		this.oauth1Operations = oauth1Operations;
	}

	public OAuth10aOperations getOAuth10aOperations() {
		return oauth1Operations;
	}

}
