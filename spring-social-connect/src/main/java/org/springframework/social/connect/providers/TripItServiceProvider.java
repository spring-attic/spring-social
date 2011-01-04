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
import org.springframework.social.tripit.TripItOperations;
import org.springframework.social.tripit.TripItTemplate;

/**
 * TripIt ServiceProvider implementation.
 * @author Craig Walls
 */
public final class TripItServiceProvider extends AbstractServiceProvider<TripItOperations> {
	
	public TripItServiceProvider(ServiceProviderParameters parameters,
 AccountConnectionRepository connectionRepository) {
		super(parameters, connectionRepository);
	}

	protected TripItOperations createServiceOperations(OAuthToken accessToken) {
		if (accessToken == null) {
			throw new IllegalStateException("Cannot access TripIt without an access token");
		}
		return new TripItTemplate(getApiKey(), getSecret(), accessToken.getValue(), accessToken.getSecret());
	}

	protected String fetchProviderAccountId(TripItOperations tripIt) {
		return tripIt.getProfileId();
	}

	protected String buildProviderProfileUrl(String tripItId, TripItOperations tripIt) {
		return tripIt.getProfileUrl();
	}
	
}