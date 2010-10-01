package org.springframework.social.oauth;

import java.io.IOException;
import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;

public class OAuthSigningClientHttpRequestFactory implements ClientHttpRequestFactory {
	private final OAuthClientRequestSigner signer;
	private final ClientHttpRequestFactory delegate;

	public OAuthSigningClientHttpRequestFactory(ClientHttpRequestFactory delegate, OAuthClientRequestSigner signer) {
		this.delegate = delegate;
		this.signer = signer;
	}

	public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
		return new OAuthSigningClientHttpRequest(delegate.createRequest(uri, httpMethod), signer);
	}
}
