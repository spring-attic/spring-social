package org.springframework.social.oauth;

import org.apache.commons.httpclient.HttpMethodBase;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.CommonsClientHttpRequestFactory;

/**
 * Implementation of {@link ClientHttpRequestFactory} that signs the request
 * with OAuth credentials. Delegates to an {@link OAuthClientRequestSigner} to
 * add OAuth credentials to the request.
 * 
 * This implementation is an extension of
 * {@link CommonsClientHttpRequestFactory}, so the underlying HTTP library is
 * Commons HTTP.
 * 
 * @author Craig Walls
 */
public class OAuthSigningClientHttpRequestFactory extends CommonsClientHttpRequestFactory {
	private final OAuthClientRequestSigner signer;

	public OAuthSigningClientHttpRequestFactory(OAuthClientRequestSigner signer) {
		this.signer = signer;
	}
	
	protected HttpMethodBase createCommonsHttpMethod(HttpMethod httpMethod, String uri) {
		HttpMethodBase methodBase = super.createCommonsHttpMethod(httpMethod, uri);
		CommonsClientRequest clientRequest = new CommonsClientRequest(methodBase);
		signer.sign(clientRequest);
		return clientRequest.getMethodBase();
	}
}
