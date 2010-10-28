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

import static org.springframework.social.oauth.OAuthSigningClientHttpRequestTest.*;

import java.util.Map;

import org.springframework.http.client.ClientHttpRequest;

/**
 * @author Craig Walls
 */
public class FakeSigner implements OAuthClientRequestSigner {
	public void sign(ClientHttpRequest request, Map<String, String> bodyParameters) {
		String parameterString = "";
		for (String key : bodyParameters.keySet()) {
			parameterString += "&" + key + "=" + bodyParameters.get(key);
		}
		request.getHeaders().add("Authorization", TEST_AUTHORIZATION_HEADER + parameterString);
	}
}
