package org.springframework.social.connect.support;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.springframework.core.GenericTypeResolver;
import org.springframework.social.ServiceProvider;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2ServiceProvider;

class OAuth2ServiceApiFactory<S> {

	private final OAuth2ServiceProvider<S> serviceProvider;
	
	private Class<?> serviceApiType;
	
	private String accessToken;
	
	private String refreshToken;
	
	private Long expireTime;

	public OAuth2ServiceApiFactory(OAuth2ServiceProvider<S> serviceProvider, String accessToken, String refreshToken, Long expireTime) {
		this.serviceProvider = serviceProvider;
		this.serviceApiType = GenericTypeResolver.resolveTypeArgument(serviceProvider.getClass(), ServiceProvider.class);
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.expireTime = expireTime;
	}
	
	public String getAccessToken() {
		return accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public Long getExpireTime() {
		return expireTime;
	}

	@SuppressWarnings("unchecked")
	public S createServiceApi() {
		S serviceApi = serviceProvider.getServiceApi(accessToken);
		if (expireTime != null && serviceApiType.isInterface()) {
			return (S) Proxy.newProxyInstance(serviceApiType.getClassLoader(), new Class[] { serviceApiType }, new ServiceApiInvocationHandler(serviceApi, expireTime));
		} else {
			return serviceApi;			
		}
	}

	public boolean hasExpired() {
		return System.currentTimeMillis() >= expireTime;
	}
	
	public S refresh() {
		AccessGrant accessGrant = serviceProvider.getOAuthOperations().refreshAccess(refreshToken);
		setAccessFields(accessGrant.getAccessToken(), accessGrant.getRefreshToken(), accessGrant.getExpireTime());
		return createServiceApi();
	}
	
	// internal helpers
	
	private void setAccessFields(String accessToken, String refreshToken, Long expireTime) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.expireTime = expireTime;
	}
	
	private static class ServiceApiInvocationHandler implements InvocationHandler {

		private Object serviceApi;
		
		private Long expireTime;
		
		public ServiceApiInvocationHandler(Object serviceApi, Long expireTime) {
			this.serviceApi = serviceApi;
			this.expireTime = expireTime;
		}
		
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (System.currentTimeMillis() >= expireTime) {
				throw new IllegalStateException("The ServiceProviderConnection has expired: not possible to invoke service API");
			}
			return method.invoke(serviceApi, args);
		}
	}

}