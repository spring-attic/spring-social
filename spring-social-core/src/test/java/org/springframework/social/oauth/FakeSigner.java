package org.springframework.social.oauth;

import static org.springframework.social.oauth.OAuthSigningClientHttpRequestTest.*;

import java.util.Map;

import org.springframework.http.client.ClientHttpRequest;

public class FakeSigner implements OAuthClientRequestSigner {
	public void sign(ClientHttpRequest request, Map<String, String> bodyParameters) {
		String parameterString = "";
		for (String key : bodyParameters.keySet()) {
			parameterString += "&" + key + "=" + bodyParameters.get(key);
		}
		request.getHeaders().add("Authorization", TEST_AUTHORIZATION_HEADER + parameterString);
	}
}
