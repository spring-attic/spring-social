package org.springframework.social.oauth;

import org.apache.commons.httpclient.HttpMethodBase;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.CommonsClientHttpRequestFactory;

public class OAuthEnabledClientHttpRequestFactory extends CommonsClientHttpRequestFactory {
	private final OAuthClientRequestSigner authorizer;

	public OAuthEnabledClientHttpRequestFactory(OAuthClientRequestSigner authorizer) {
		this.authorizer = authorizer;
	}
	
	protected HttpMethodBase createCommonsHttpMethod(HttpMethod httpMethod, String uri) {
		HttpMethodBase methodBase = super.createCommonsHttpMethod(httpMethod, uri);
		CommonsClientRequest clientRequest = new CommonsClientRequest(methodBase);
		authorizer.sign(clientRequest);
		return clientRequest.getMethodBase();
	}
}
