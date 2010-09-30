package org.springframework.social.oauth2;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.social.oauth.OAuthClientRequestSigner;

/**
 * Abstract implementation of {@link OAuthClientRequestSigner} that
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
public abstract class OAuth2ParameterClientRequestSigner implements OAuthClientRequestSigner {
	private String parameterName = "oauth_token";

	public void sign(HttpMethod method, HttpHeaders headers, String url, Map<String, String> bodyParameters) {
		// TODO : FIX THIS
		// request.setParameter(parameterName, resolveAccessToken());
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
}
