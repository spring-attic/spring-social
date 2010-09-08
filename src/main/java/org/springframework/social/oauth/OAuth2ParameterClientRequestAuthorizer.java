package org.springframework.social.oauth;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.AbstractClientHttpRequest;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

public class OAuth2ParameterClientRequestAuthorizer implements OAuthClientRequestAuthorizer {
	private final AccessTokenResolver<String> tokenResolver;

	private String parameterName = "access_token";

	public OAuth2ParameterClientRequestAuthorizer(AccessTokenResolver<String> tokenResolver) {
		this.tokenResolver = tokenResolver;
	}

	@Override
	public ClientHttpRequest authorize(ClientHttpRequest request) throws AuthorizationException {
		return new OAuth2ParameterDecoratedClientHttpRequest(request);
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	private String resolveAccessToken() {
		return tokenResolver.resolveAccessToken();
	}

	private class OAuth2ParameterDecoratedClientHttpRequest extends AbstractClientHttpRequest {
		private final ClientHttpRequest targetRequest;

		private OAuth2ParameterDecoratedClientHttpRequest(ClientHttpRequest targetRequest) {
			this.targetRequest = targetRequest;
		}

		@Override
		public HttpMethod getMethod() {
			return targetRequest.getMethod();
		}

		@Override
		public URI getURI() {
			URI targetUri = targetRequest.getURI();
			String query = targetUri.getQuery();

			if (query == null) {
				query = parameterName + "=" + resolveAccessToken();
			} else {
				query += "&" + parameterName + "=" + resolveAccessToken();
			}

			URI decoratedUri;
			try {
				decoratedUri = new URI(targetUri.getScheme(), targetUri.getAuthority(), targetUri.getPath(), query,
						targetUri.getFragment());
				return decoratedUri;
			} catch (URISyntaxException e) {
				// TODO: Revisit this...is this the right exception to throw???
				throw new AuthorizationException("Unable to decorate request with access token", e);
			}
		}

		@Override
		protected ClientHttpResponse executeInternal(HttpHeaders headers, byte[] bufferedOutput) throws IOException {
			return targetRequest.execute();
		}
	}
}
