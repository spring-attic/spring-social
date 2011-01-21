/*
 * Copyright 2011 the original author or authors.
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
package org.springframework.social.github.provider;

import org.springframework.social.github.GitHubOperations;
import org.springframework.social.github.GitHubTemplate;
import org.springframework.social.provider.oauth2.AbstractOAuth2ServiceProvider;
import org.springframework.social.provider.oauth2.OAuth2Template;
import org.springframework.social.provider.support.ConnectionRepository;

public class GitHubServiceProvider extends AbstractOAuth2ServiceProvider<GitHubOperations> {

	public GitHubServiceProvider(String clientId, String clientSecret, ConnectionRepository connectionRepository) {
		super("github", "Github", connectionRepository, new OAuth2Template(clientId, clientSecret));
	}

	protected GitHubOperations getApi(String accessToken) {
		return new GitHubTemplate(accessToken);
	}
}
