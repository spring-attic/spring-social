package org.springframework.social.oauth.support;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

/**
 * Extension of RestTemplate that allows for setting of request interceptors. If interceptors are set, then an
 * InterceptingClientHttpRequestFactory will be used to wrap the base request factory.
 * @author Craig Walls
 */
public class InterceptingRestTemplate extends RestTemplate {
	private ClientHttpRequestInterceptor[] interceptors;

	public InterceptingRestTemplate() {
		super();
	}

	public void setInterceptors(ClientHttpRequestInterceptor[] interceptors) {
		this.interceptors = interceptors;
	}

	public ClientHttpRequestInterceptor[] getInterceptors() {
		return interceptors;
	}

	public ClientHttpRequestFactory getRequestFactory() {
		ClientHttpRequestFactory delegate = super.getRequestFactory();
		if (!ObjectUtils.isEmpty(getInterceptors())) {
			return new InterceptingClientHttpRequestFactory(delegate, getInterceptors());
		} else {
			return delegate;
		}
	}
}
