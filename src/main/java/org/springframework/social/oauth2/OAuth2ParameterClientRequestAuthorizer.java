package org.springframework.social.oauth2;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.AbstractClientHttpRequest;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.social.oauth.AuthorizationException;
import org.springframework.social.oauth.OAuthClientRequestAuthorizer;

/**
 * Abstract implementation of {@link OAuthClientRequestAuthorizer} that
 * authorizes a request by adding a query parameter for the access token.
 * 
 * Per section 5.1.2 of the OAuth 2.0 Protocol draft specification, one option
 * for authenticating a request is to place the access token on the query string
 * as a parameter named oauth_token.
 * 
 * In Facebook's implementation of OAuth 2.0, the query parameter is the
 * <emph>only</emph> option for authenticating requests for the Graph API.
 * Moreover, when using Facebook, the query parameter must be named
 * access_token.
 * 
 * @author Craig Walls
 */
public abstract class OAuth2ParameterClientRequestAuthorizer implements OAuthClientRequestAuthorizer {
	private String parameterName = "oauth_token";

	public ClientHttpRequest authorize(ClientHttpRequest request) throws AuthorizationException {
		return new OAuth2ParameterDecoratedClientHttpRequest(request);
	}

	/**
	 * Sets the name of the query parameter to write the access token into.
	 * 
	 * This is "oauth_token" by default to match the OAuth 2.0 Protocol
	 * specification. But when used to authenticate with Facebook, you'll need
	 * to set this to "access_token".
	 * 
	 * @param parameterName
	 */
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	protected abstract String resolveAccessToken();

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
