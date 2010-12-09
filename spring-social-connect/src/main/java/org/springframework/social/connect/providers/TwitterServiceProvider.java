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
package org.springframework.social.connect.providers;

import org.springframework.social.connect.AbstractServiceProvider;
import org.springframework.social.connect.AccountConnectionRepository;
import org.springframework.social.connect.OAuthToken;
import org.springframework.social.connect.ServiceProviderParameters;
import org.springframework.social.twitter.TwitterOperations;
import org.springframework.social.twitter.TwitterTemplate;

/**
 * Twitter ServiceProvider implementation.
 * @author Keith Donald
 * @author Craig Walls
 */
public final class TwitterServiceProvider extends AbstractServiceProvider<TwitterOperations> {
	
	public TwitterServiceProvider(ServiceProviderParameters parameters,
 AccountConnectionRepository connectionRepository) {
		super(parameters, connectionRepository);
	}

	protected TwitterOperations createServiceOperations(OAuthToken accessToken) {
		return accessToken != null ? new TwitterTemplate(getApiKey(), getSecret(), accessToken.getValue(), accessToken.getSecret()) : new TwitterTemplate();
	}

	protected String fetchProviderAccountId(TwitterOperations twitter) {
		return twitter.getProfileId();
	}

	protected String buildProviderProfileUrl(String screenName, TwitterOperations twitter) {
		return "http://www.twitter.com/" + screenName;
	}
	
}