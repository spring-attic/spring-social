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
package org.springframework.social.security.test;

import org.springframework.core.GenericTypeResolver;
import org.springframework.social.ServiceProvider;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionFactory;

/**
 * used for mocking {@link ConnectionFactory} to support
 * {@link GenericTypeResolver} base lookup of API type
 */
public abstract class MockConnectionFactory extends ConnectionFactory<Object> {

	public MockConnectionFactory(String providerId, ServiceProvider<Object> serviceProvider,
			ApiAdapter<Object> apiAdapter) {
		super(providerId, serviceProvider, apiAdapter);
	}

	/**
	 * alternative
	 *
	 * @author stf@molindo.at
	 */
	public abstract static class StringFactory extends ConnectionFactory<String> {

		public StringFactory(String providerId, ServiceProvider<String> serviceProvider, ApiAdapter<String> apiAdapter) {
			super(providerId, serviceProvider, apiAdapter);
		}
	}
}
