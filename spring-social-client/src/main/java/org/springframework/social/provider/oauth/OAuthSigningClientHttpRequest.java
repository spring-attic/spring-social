/*
 * Copyright 2010 the original author or authors.
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
package org.springframework.social.provider.oauth;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Implementation of {@link ClientHttpRequest} that wraps another {@link ClientHttpRequest}
 * and signs it with an OAuth <code>Authorization</code> header before it is sent.
 * @author Craig Walls
 */
public final class OAuthSigningClientHttpRequest implements ClientHttpRequest {

	private final ClientHttpRequest delegate;
	
	private final OAuthClientRequestSigner signer;

	/**
	 * Creates an {@link OAuthSigningClientHttpRequest}.
	 * @param delegate The wrapped {@link ClientHttpRequest} that is to be signed
	 * @param signer An OAuth signer
	 */
	public OAuthSigningClientHttpRequest(ClientHttpRequest delegate, OAuthClientRequestSigner signer) {
		this.delegate = delegate;
		this.signer = signer;
	}

	// implementing HttpMessage
	
	public HttpHeaders getHeaders() {
		return delegate.getHeaders();
	}

	// implementing HttpOutputMessage

	public OutputStream getBody() throws IOException {
		return delegate.getBody();
	}

	// implementing ClientHttpRequest

	public HttpMethod getMethod() {
		return delegate.getMethod();
	}
	
	public URI getURI() {
		return delegate.getURI();
	}

	public ClientHttpResponse execute() throws IOException {
		signer.sign(delegate);
		return delegate.execute();
	}

}