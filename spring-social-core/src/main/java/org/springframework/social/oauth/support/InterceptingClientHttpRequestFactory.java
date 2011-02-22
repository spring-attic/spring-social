package org.springframework.social.oauth.support;

import java.io.IOException;
import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.util.Assert;

/**
 * Wrapper for a {@link ClientHttpRequestFactory} that has support for {@link ClientHttpRequestInterceptor}s.
 * Borrowed from Spring 3.1 to support RestTemplate interceptors in Spring Social for applications using Spring 3.0
 * @author Arjen Poutsma
 * @author Craig Walls
 */
public class InterceptingClientHttpRequestFactory implements ClientHttpRequestFactory {

	private final ClientHttpRequestFactory requestFactory;

	private final ClientHttpRequestInterceptor[] interceptors;

	/**
	 * Creates a new instance of the {@code InterceptingClientHttpRequestFactory} with the given parameters.
	 *
	 * @param requestFactory the request factory to wrap
	 * @param interceptors the interceptors that are to be applied. Can be {@code null}.
	 */
	public InterceptingClientHttpRequestFactory(ClientHttpRequestFactory requestFactory,
			ClientHttpRequestInterceptor[] interceptors) {
		Assert.notNull(requestFactory, "'requestFactory' must not be null");
		this.requestFactory = requestFactory;
		this.interceptors = interceptors != null ? interceptors : new ClientHttpRequestInterceptor[0];
	}

	public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
		return new InterceptingClientHttpRequest(requestFactory, interceptors, uri, httpMethod);
	}
}
