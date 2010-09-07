package org.springframework.social.oauth;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.AbstractClientHttpRequest;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

// TODO: Repackage under the facebook package. Kept here for now for convenience' sake
// TODO: Consider renaming to OAuth2ParameterClientRequestDecorator
public class FacebookClientRequestDecorator implements OAuthClientRequestDecorator {

	private final AccessTokenResolver<String> tokenResolver;

	public FacebookClientRequestDecorator(AccessTokenResolver<String> tokenResolver) {
		this.tokenResolver = tokenResolver;
	}

	@Override
	public ClientHttpRequest decorate(ClientHttpRequest request) throws AuthorizationException {
		return new FacebookAuthorizationDecoratedClientHttpRequest(request);
	}

	private String resolveAccessToken() {
		return tokenResolver.resolveAccessToken();
	}

	private class FacebookAuthorizationDecoratedClientHttpRequest extends AbstractClientHttpRequest {
		private final ClientHttpRequest targetRequest;

		private FacebookAuthorizationDecoratedClientHttpRequest(ClientHttpRequest targetRequest) {
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

			if (query == null || query.isEmpty()) {
				query = "?accessToken=" + resolveAccessToken();
			} else {
				query += "&accessToken=" + resolveAccessToken();
			}

			URI decoratedUri;
			try {
				decoratedUri = new URI(targetUri.getScheme(), targetUri.getAuthority(), targetUri.getPath(), query,
						targetUri.getFragment());
				return decoratedUri;
			} catch (URISyntaxException e) {
				// TODO: Revisit this...is this the right exception to throw???
				throw new AuthorizationException("Unabled to decorate request with access token", e);
			}
		}

		@Override
		protected ClientHttpResponse executeInternal(HttpHeaders headers, byte[] bufferedOutput) throws IOException {
			return targetRequest.execute();
		}
	}
}
