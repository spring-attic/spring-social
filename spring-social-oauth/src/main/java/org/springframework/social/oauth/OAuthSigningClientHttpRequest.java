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
package org.springframework.social.oauth;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.AbstractClientHttpRequest;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Implementation of {@link ClientHttpRequest} that wraps another
 * {@link ClientHttpRequest} and signs it with an OAuth
 * <code>Authorization</code> header before it is sent.
 * 
 * @author Craig Walls
 */
public class OAuthSigningClientHttpRequest extends AbstractClientHttpRequest {
	private final ClientHttpRequest delegate;
	private final OAuthClientRequestSigner signer;

	/**
	 * Creates an {@link OAuthSigningClientHttpRequest}.
	 * 
	 * @param delegate
	 *            The wrapped {@link ClientHttpRequest} that is to be signed
	 * @param signer
	 *            An OAuth signer
	 */
	public OAuthSigningClientHttpRequest(ClientHttpRequest delegate, OAuthClientRequestSigner signer) {
		this.delegate = delegate;
		this.signer = signer;
	}

	protected ClientHttpResponse executeInternal(HttpHeaders headers, byte[] bufferedOutput) throws IOException {
		Map<String, String> bodyParameters = extractBodyParameters(headers.getContentType(), bufferedOutput);
		signer.sign(delegate, bodyParameters);
		delegate.getBody().write(bufferedOutput);
		delegate.getHeaders().putAll(headers);
		return delegate.execute();
	}

	private Map<String, String> extractBodyParameters(MediaType bodyType, byte[] bodyBytes) {
		Map<String, String> params = new HashMap<String, String>();

		if (bodyType != null && bodyType.equals(MediaType.APPLICATION_FORM_URLENCODED)) {
			String[] paramPairs = new String(bodyBytes).split("&");
			for (String pair : paramPairs) {
				String[] keyValue = pair.split("=");
				if (keyValue.length == 2) {
					params.put(keyValue[0], keyValue[1]);
				}
			}
		}
		return params;
	}

	public URI getURI() {
		return delegate.getURI();
	}

	public HttpMethod getMethod() {
		return delegate.getMethod();
	}
}