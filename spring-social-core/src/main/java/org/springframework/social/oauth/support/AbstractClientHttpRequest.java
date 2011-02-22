package org.springframework.social.oauth.support;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;

/**
 * Borrowed from Spring 3.0 so that we have a consistent AbstractClientHttpRequest implementation for both Spring 3.0 and Spring 3.1-based applications.
 * Note that this wouldn't be necessary if using Spring 3.0.6, which introduced AbstractBufferingHttpRequest.
 * @author Craig Walls
 */
public abstract class AbstractClientHttpRequest implements ClientHttpRequest {

	private boolean executed = false;

	private final HttpHeaders headers = new HttpHeaders();

	private final ByteArrayOutputStream bufferedOutput = new ByteArrayOutputStream();

	public final HttpHeaders getHeaders() {
		return executed ? HttpHeaders.readOnlyHttpHeaders(headers) : this.headers;
	}

	public final OutputStream getBody() throws IOException {
		checkExecuted();
		return this.bufferedOutput;
	}

	public final ClientHttpResponse execute() throws IOException {
		checkExecuted();
		ClientHttpResponse result = executeInternal(this.headers, this.bufferedOutput.toByteArray());
		this.executed = true;
		return result;
	}

	private void checkExecuted() {
		Assert.state(!this.executed, "ClientHttpRequest already executed");
	}

	/**
	 * Abstract template method that writes the given headers and content to the HTTP request.
	 * 
	 * @param headers
	 *            the HTTP headers
	 * @param bufferedOutput
	 *            the body content
	 * @return the response object for the executed request
	 */
	protected abstract ClientHttpResponse executeInternal(HttpHeaders headers, byte[] bufferedOutput)
			throws IOException;

}

