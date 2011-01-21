/*
 * Copyright 2010 the original author or authors.
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
package org.springframework.social.twitter.provider;

import org.springframework.social.provider.oauth1.AbstractOAuth1ServiceProvider;
import org.springframework.social.provider.support.ConnectionRepository;
import org.springframework.social.twitter.TwitterOperations;
import org.springframework.social.twitter.TwitterTemplate;

/**
 * Twitter ServiceProvider implementation.
 * @author Keith Donald
 * @author Craig Walls
 */
public final class TwitterServiceProvider extends AbstractOAuth1ServiceProvider<TwitterOperations> {

	public TwitterServiceProvider(String id, String displayName, ConnectionRepository connectionRepository, String consumerKey,
			String consumerSecret, String requestTokenUrl, String authorizeUrl, String accessTokenUrl) {
		super(id, displayName, connectionRepository, consumerKey, consumerSecret, requestTokenUrl, authorizeUrl, accessTokenUrl);
	}

	@Override
	protected TwitterOperations getApi(String accessToken, String secret) {
		return new TwitterTemplate(getConsumerKey(), getConsumerSecret(), accessToken, secret);
	}
	
}