package org.springframework.social.oauth;

import java.io.IOException;
import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class OAuthEnabledRestTemplate extends RestTemplate {
	private OAuthClientRequestDecorator requestDecorator;

	public OAuthEnabledRestTemplate(OAuthClientRequestDecorator requestDecorator) {
		this.requestDecorator = requestDecorator;
	}

	@Override
	protected <T> T doExecute(URI url, HttpMethod method, RequestCallback requestCallback,
			ResponseExtractor<T> responseExtractor) throws RestClientException {

		try {
			RequestCallback adapter = new OAuthHttpEntityRequestCallbackAdapter(requestCallback);
			return super.doExecute(url, method, adapter, responseExtractor);
		} catch (AuthorizationException e) {
			throw new RestClientException("Unable to add OAuth authorization details to request", e);
		}
	}

	private class OAuthHttpEntityRequestCallbackAdapter implements RequestCallback {
		private final RequestCallback targetCallback;

		private OAuthHttpEntityRequestCallbackAdapter(RequestCallback targetCallback) {
			this.targetCallback = targetCallback;
		}

		@Override
		public void doWithRequest(ClientHttpRequest request) throws IOException {
			targetCallback.doWithRequest(requestDecorator.decorate(request));
		}
	}
}
