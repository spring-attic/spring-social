package org.springframework.social.oauth.support;

import java.io.IOException;

import org.springframework.http.client.ClientHttpResponse;

/**
 * Represents the context of a client-side HTTP request execution.
 * Used to invoke the next interceptor in the interceptor chain, or - if the calling interceptor is last - execute the request itself.
 * Borrowed from Spring 3.1 to support RestTemplate interceptors in Spring Social for applications using Spring 3.0
 *
 * @author Arjen Poutsma
 * @author Craig Walls
 * @see ClientHttpRequestInterceptor
 */
public interface ClientHttpRequestExecution {

	/**
	 * Execute the request with the given request attributes and body, and return the response.
	 *
	 * @param request the request, containing method, URI, and headers
	 * @param body the body of the request to execute
	 * @return the response
	 * @throws IOException in case of I/O errors
	 */
	ClientHttpResponse execute(HttpRequest request, byte[] body) throws IOException;

}
