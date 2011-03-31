package org.springframework.social.connect;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.social.ServiceProvider;
import org.springframework.social.connect.spi.ProviderProfile;
import org.springframework.social.connect.spi.ServiceApiAdapter;

public class ServiceProviderConnectionFactoryTest {

	@Test
	public void nullServiceApiAdapter() {
		ServiceProviderConnectionFactory<TestApi> connectionFactory = new ServiceProviderConnectionFactory<TestApi>("testProvider", new TestServiceProvider(), null, true) {
			@Override
			public ServiceProviderConnection<TestApi> createConnection(ServiceProviderConnectionMemento connectionMemento) {
				Assert.assertEquals("testProvider", getProviderId());
				Assert.assertTrue(getServiceProvider() instanceof TestServiceProvider);
				Assert.assertEquals(true, isAllowSignIn());
				TestApi api = new TestApi() { };
				Assert.assertNotNull(getServiceApiAdapter());
				Assert.assertEquals(true, getServiceApiAdapter().test(api));
				ProviderProfile profile = getServiceApiAdapter().getProfile(api);
				Assert.assertNull(profile.getId());
				Assert.assertNull(profile.getName());
				Assert.assertNull(profile.getUrl());
				Assert.assertNull(profile.getPictureUrl());
				getServiceApiAdapter().updateStatus(api, "no-op");
				return null;
			}
		};
		connectionFactory.createConnection(null);
	}
	
	@Test
	public void serviceApiAdapter() {
		final TestApiAdapter apiAdapter = new TestApiAdapter();
		ServiceProviderConnectionFactory<TestApi> connectionFactory = new ServiceProviderConnectionFactory<TestApi>("testProvider", new TestServiceProvider(), apiAdapter, false) {
			@Override
			public ServiceProviderConnection<TestApi> createConnection(ServiceProviderConnectionMemento connectionMemento) {
				Assert.assertSame(apiAdapter, getServiceApiAdapter());
				return null;
			}
		};
		connectionFactory.createConnection(null);
	}
	
	static class TestServiceProvider implements ServiceProvider<TestApi> {

	}
		
	interface TestApi {
		
	}
	
	static class TestApiAdapter implements ServiceApiAdapter<TestApi> {

		public boolean test(TestApi serviceApi) {
			return false;
		}

		public ProviderProfile getProfile(TestApi serviceApi) {
			return null;
		}

		public void updateStatus(TestApi serviceApi, String message) {
		}
		
	}
	
}