/*
 * Copyright 2015 the original author or authors.
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
package org.springframework.social.oauth1;

/**
 * Base class for ServiceProviders that use the OAuth1 protocol.
 * OAuth1-based ServiceProvider implementors should extend and implement {@link #getApi(String, String)}.
 * They should also define a single constructor that accepts the consumerKey/consumerSecret
 * and internally creates and passes up a {@link OAuth1Operations} instance.
 * @author Keith Donald
 * @param <S> the service API type
 */
public abstract class AbstractOAuth1ServiceProvider<S> implements OAuth1ServiceProvider<S> {

	private final String consumerKey;
	
	private final String consumerSecret;
	
	private final OAuth1Operations oauth1Operations;

	/**
	 * Creates a OAuth1ServiceProvider.
	 * @param consumerKey the consumer (or client) key assigned to the application by the provider.
	 * @param consumerSecret the consumer (or client) secret assigned to the application by the provider.
	 * @param oauth1Operations the template that allows the OAuth1-based authorization flow to be conducted with the provider.
	 */
	public AbstractOAuth1ServiceProvider(String consumerKey, String consumerSecret, OAuth1Operations oauth1Operations) {
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.oauth1Operations = oauth1Operations;
	}

	// implementing OAuth1ServiceProvider
	
	public final OAuth1Operations getOAuthOperations() {
		return oauth1Operations;
	}
	
	public abstract S getApi(String accessToken, String secret);
	
	// subclassing hooks
	
	/**
	 * The consumer (or client) key assigned to the application by the provider.
	 * Exposed to subclasses to support constructing service API instances.
	 * @see #getApi(String, String)
	 * @return The consumer (or client) key assigned to the application by the provider.
	 */
	protected final String getConsumerKey() {
		return consumerKey;
	}

	/**
	 * The consumer (or client) secret assigned to the application by the provider.
	 * Exposed to subclasses to support constructing service API instances.
	 * @see #getApi(String, String)
	 * @return The consumer (or client) secret assigned to the application by the provider.
	 */
	protected final String getConsumerSecret() {
		return consumerSecret;
	}

}
