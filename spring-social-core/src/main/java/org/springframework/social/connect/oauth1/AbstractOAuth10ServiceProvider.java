package org.springframework.social.connect.oauth1;

import org.springframework.social.connect.support.ConnectionRepository;
import org.springframework.social.oauth1.OAuth10Operations;

public abstract class AbstractOAuth10ServiceProvider<S> extends AbstractOAuth1ServiceProvider<S> implements OAuth10ServiceProvider<S> {

	private final OAuth10Operations oauth1Operations;

	public AbstractOAuth10ServiceProvider(String id, ConnectionRepository connectionRepository, String consumerKey,
			String consumerSecret, OAuth10Operations oauth1Operations) {
		super(id, connectionRepository, consumerKey, consumerSecret);
		this.oauth1Operations = oauth1Operations;
	}

	public OAuth10Operations getOAuth10Operations() {
		return oauth1Operations;
	}

}
