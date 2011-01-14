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
package org.springframework.social.connect.providers;

import java.io.Serializable;

import org.springframework.social.connect.AbstractOAuth2ServiceProvider;
import org.springframework.social.connect.AccountConnectionRepository;
import org.springframework.social.connect.OAuthToken;
import org.springframework.social.connect.ServiceProviderParameters;
import org.springframework.social.github.GitHubOperations;
import org.springframework.social.github.GitHubTemplate;

public class GitHubServiceProvider extends AbstractOAuth2ServiceProvider<GitHubOperations> {

	public GitHubServiceProvider(ServiceProviderParameters parameters, AccountConnectionRepository connectionRepository) {
		super(parameters, connectionRepository);
	}

	@Override
	protected GitHubOperations createServiceOperations(OAuthToken accessToken) {
		if (accessToken == null) {
			throw new IllegalStateException("Cannot access GitHub without an access token");
		}
		return new GitHubTemplate(accessToken.getValue());
	}

	@Override
	protected String fetchProviderAccountId(GitHubOperations github) {
		return github.getProfileId();
	}

	@Override
	protected String buildProviderProfileUrl(String providerAccountId, GitHubOperations github) {
		return github.getProfileUrl();
	}

	public Serializable getProviderUserProfile(OAuthToken accessToken) {
		return new GitHubTemplate(accessToken.getValue()).getUserProfile();
	}
}
