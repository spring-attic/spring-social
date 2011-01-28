package org.springframework.social.intercept;

import java.io.IOException;
import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.AbstractClientHttpRequest;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/*
 * This class is a placeholder, enabling me to work with interceptors while Arjen implements the real RestTemplate interceptor stuff.
 * 
 * Once Arjen is done, this class can go away and the regular RestTemplate can be used.
 */
public class ExtendedRestTemplate extends RestTemplate {
	
	public void addInterceptor(ClientRequestInterceptor interceptor) {
		this.setRequestFactory(new InterceptorClientHttpRequestFactory(new SimpleClientHttpRequestFactory(), interceptor));
	}

	private static class InterceptorClientHttpRequestFactory implements ClientHttpRequestFactory {

		private final ClientHttpRequestFactory delegate;
		
		private final ClientRequestInterceptor interceptor;

		public InterceptorClientHttpRequestFactory(ClientHttpRequestFactory delegate, ClientRequestInterceptor interceptor) {
			this.delegate = delegate;
			this.interceptor = interceptor;
		}

		public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
			return new InterceptorClientHttpRequest(delegate.createRequest(uri, httpMethod), interceptor);
		}
	}

	private static class InterceptorClientHttpRequest extends AbstractClientHttpRequest {

		private final ClientHttpRequest delegate;
		
		private final ClientRequestInterceptor interceptor;

		public InterceptorClientHttpRequest(ClientHttpRequest delegate, ClientRequestInterceptor interceptor) {
			this.delegate = delegate;
			this.interceptor = interceptor;
		}

		protected ClientHttpResponse executeInternal(HttpHeaders headers, byte[] bufferedOutput) throws IOException {
			ClientRequest clientRequest = new ClientRequest(headers, bufferedOutput, getURI(), getMethod());
			interceptor.beforeExecution(clientRequest);
			delegate.getBody().write(bufferedOutput);
			delegate.getHeaders().putAll(headers);
			return delegate.execute();
		}

		public URI getURI() {
			return delegate.getURI();
		}

		public HttpMethod getMethod() {
			return delegate.getMethod();
		}
		
	}
}
