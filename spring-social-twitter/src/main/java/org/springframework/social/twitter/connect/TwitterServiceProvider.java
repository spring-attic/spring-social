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
package org.springframework.social.twitter.connect;

import org.springframework.social.oauth1.AbstractOAuth1ServiceProvider;
import org.springframework.social.oauth1.OAuth1Template;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;

/**
 * Twitter ServiceProvider implementation.
 * @author Keith Donald
 * @author Craig Walls
 */
public class TwitterServiceProvider extends AbstractOAuth1ServiceProvider<Twitter> {

	public TwitterServiceProvider(String consumerKey, String consumerSecret) {
		super(consumerKey, consumerSecret, new OAuth1Template(consumerKey, consumerSecret,
			"https://api.twitter.com/oauth/request_token",
			"https://api.twitter.com/oauth/authorize",
			"https://api.twitter.com/oauth/authenticate",			
			"https://api.twitter.com/oauth/access_token"));
	}

	public Twitter getApi(String accessToken, String secret) {
		return new TwitterTemplate(getConsumerKey(), getConsumerSecret(), accessToken, secret);
	}

}