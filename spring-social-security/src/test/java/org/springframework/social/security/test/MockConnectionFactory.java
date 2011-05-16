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
