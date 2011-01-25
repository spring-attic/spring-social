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
import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;

/**
 * An implementation of {@link ClientHttpRequestFactory} that creates an
 * {@link OAuthSigningClientHttpRequest} so that the request can be signed with
 * an OAuth <code>Authorization</code> header.
 * @author Craig Walls
 */
public final class OAuthSigningClientHttpRequestFactory implements ClientHttpRequestFactory {

	private final OAuthClientRequestSigner signer;
	
	private final ClientHttpRequestFactory delegate;

	public OAuthSigningClientHttpRequestFactory(OAuthClientRequestSigner signer, ClientHttpRequestFactory delegate) {
		this.signer = signer;
		this.delegate = delegate;
	}

	public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
		return new OAuthSigningClientHttpRequest(delegate.createRequest(uri, httpMethod), signer);
	}
}
