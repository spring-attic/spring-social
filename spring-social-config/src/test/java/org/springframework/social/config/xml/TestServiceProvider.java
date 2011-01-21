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
package org.springframework.social.config.xml;

import java.io.Serializable;

import org.springframework.social.provider.OAuthToken;
import org.springframework.social.provider.support.AbstractOAuth1ServiceProvider;
import org.springframework.social.provider.support.ConnectionRepository;
import org.springframework.social.provider.support.ServiceProviderParameters;

public class TestServiceProvider extends AbstractOAuth1ServiceProvider<Object> {
	private final ServiceProviderParameters parameters;
	private final ConnectionRepository connectionRepository;

	public TestServiceProvider(ServiceProviderParameters parameters, ConnectionRepository connectionRepository) {
		super(parameters, connectionRepository);
		this.parameters = parameters;
		this.connectionRepository = connectionRepository;
	}

	@Override
	protected Object createServiceOperations(OAuthToken accessToken) {
		return null;
	}

	@Override
	protected String fetchProviderAccountId(Object serviceOperations) {
		return null;
	}

	@Override
	protected String buildProviderProfileUrl(String providerAccountId, Object serviceOperations) {
		return null;
	}

	public ServiceProviderParameters getParameters() {
		return parameters;
	}

	public ConnectionRepository getConnectionRepository() {
		return connectionRepository;
	}

	public Serializable getProviderUserProfile(OAuthToken accessToken) {
		return null;
	}
}
