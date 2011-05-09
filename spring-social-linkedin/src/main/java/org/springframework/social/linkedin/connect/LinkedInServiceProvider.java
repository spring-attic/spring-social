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
package org.springframework.social.linkedin.connect;

import org.springframework.social.linkedin.api.LinkedIn;
import org.springframework.social.linkedin.api.impl.LinkedInTemplate;
import org.springframework.social.oauth1.AbstractOAuth1ServiceProvider;
import org.springframework.social.oauth1.OAuth1Template;

/**
 * LinkedIn ServiceProvider implementation.
 * @author Keith Donald
 */
public class LinkedInServiceProvider extends AbstractOAuth1ServiceProvider<LinkedIn> {

	public LinkedInServiceProvider(String consumerKey, String consumerSecret) {
		super(consumerKey, consumerSecret, new OAuth1Template(consumerKey, consumerSecret,
			"https://api.linkedin.com/uas/oauth/requestToken",
			"https://www.linkedin.com/uas/oauth/authorize",
			"https://www.linkedin.com/uas/oauth/authenticate",			
			"https://api.linkedin.com/uas/oauth/accessToken"));
	}

	public LinkedIn getApi(String accessToken, String secret) {
		return new LinkedInTemplate(getConsumerKey(), getConsumerSecret(), accessToken, secret);
	}
	
}