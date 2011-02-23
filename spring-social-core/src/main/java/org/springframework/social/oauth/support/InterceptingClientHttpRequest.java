package org.springframework.social.oauth.support;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Iterator;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;

/**
 * Wrapper for a {@link ClientHttpRequest} that has support for {@link ClientHttpRequestInterceptor}s.
 * Borrowed from Spring 3.1 to support RestTemplate interceptors in Spring Social for applications using Spring 3.0
 * @author Arjen Poutsma
 * @author Craig Walls
 */
class InterceptingClientHttpRequest extends AbstractClientHttpRequest {

	private final ClientHttpRequestFactory requestFactory;

	private final ClientHttpRequestInterceptor[] interceptors;

	private HttpMethod method;

	private URI uri;

	protected InterceptingClientHttpRequest(ClientHttpRequestFactory requestFactory,
			ClientHttpRequestInterceptor[] interceptors, URI uri, HttpMethod method) {
		this.requestFactory = requestFactory;
		this.interceptors = interceptors;
		this.method = method;
		this.uri = uri;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public URI getURI() {
		return uri;
	}

	@Override
	protected final ClientHttpResponse executeInternal(HttpHeaders headers, byte[] bufferedOutput) throws IOException {
		RequestExecution requestExecution = new RequestExecution();
		return requestExecution.execute(new HttpRequest(this), bufferedOutput);
	}

	private class RequestExecution implements ClientHttpRequestExecution {

		private final Iterator<ClientHttpRequestInterceptor> iterator;

		private RequestExecution() {
			this.iterator = Arrays.asList(interceptors).iterator();
		}

		public ClientHttpResponse execute(HttpRequest request, byte[] body) throws IOException {
			if (iterator.hasNext()) {
				ClientHttpRequestInterceptor nextInterceptor = iterator.next();
				return nextInterceptor.intercept(request, body, this);
			} else {
				ClientHttpRequest delegate = requestFactory.createRequest(request.getURI(), request.getMethod());

				delegate.getHeaders().putAll(request.getHeaders());

				if (body.length > 0) {
					FileCopyUtils.copy(body, delegate.getBody());
				}
				return delegate.execute();
			}
		}
	}

}
