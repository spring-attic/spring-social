/*
 * Copyright 2011 the original author or authors.
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
package org.springframework.social.provider.oauth1;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

/**
 * OAuth1Operations implementation that uses REST-template to make the OAuth calls.
 * @author Keith Donald
 */
public class OAuth1Template implements OAuth1Operations {

	private final String consumerKey;
	
	private final String consumerSecret;
	
	private final String requestTokenUrl;
	
	private final UriTemplate authorizeUrlTemplate;
	
	private final String accessTokenUrl;
	
	public OAuth1Template(String consumerKey, String consumerSecret, String requestTokenUrl, String authorizeUrl, String accessTokenUrl) {
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
		this.requestTokenUrl = requestTokenUrl;
		this.authorizeUrlTemplate = new UriTemplate(authorizeUrl);
		this.accessTokenUrl = accessTokenUrl;
	}

	public OAuthToken fetchNewRequestToken(String callbackUrl) {
		Map<String, String> requestTokenParameters = new HashMap<String, String>();
		requestTokenParameters.put("oauth_callback", callbackUrl);
		return getTokenFromProvider(requestTokenParameters, requestTokenUrl);
	}

	public String buildAuthorizeUrl(String requestToken) {
		return authorizeUrlTemplate.expand(requestToken).toString();
	}

	public OAuthToken exchangeForAccessToken(AuthorizedRequestToken requestToken) {
		Map<String, String> accessTokenParameters = new HashMap<String, String>();
		accessTokenParameters.put("oauth_token", requestToken.getValue());
		accessTokenParameters.put("oauth_verifier", requestToken.getVerifier());
		return getTokenFromProvider(accessTokenParameters, accessTokenUrl);
	}

	// private helpers
	private String buildAuthorizationHeader(String targetUrl, Map<String, String> parameters, HttpMethod method,
			String tokenSecret) {
		String baseString = buildBaseString(targetUrl, parameters, method);
		String signature = calculateSignature(baseString, tokenSecret);
		String header = "OAuth ";
		for (String key : parameters.keySet()) {
			header += key + "=\"" + encode(parameters.get(key)) + "\", ";
		}
		header += "oauth_signature=\"" + encode(signature) + "\"";
		return header;
	}

	private OAuthToken getTokenFromProvider(Map<String, String> tokenRequestParameters, String tokenUrl) {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("oauth_consumer_key", consumerKey);
		parameters.put("oauth_signature_method", HMAC_SHA1_SIGNATURE_NAME);
		parameters.put("oauth_timestamp", String.valueOf(System.currentTimeMillis() / 1000));
		parameters.put("oauth_nonce", UUID.randomUUID().toString());
		parameters.put("oauth_version", "1.0");
		parameters.putAll(tokenRequestParameters);
		String authHeader = buildAuthorizationHeader(tokenUrl, parameters, HttpMethod.POST, null);
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add("Authorization", authHeader);
		HttpEntity<String> request = new HttpEntity<String>(headers);

		ResponseEntity<String> response = new RestTemplate().exchange(tokenUrl, HttpMethod.POST, request, String.class);

		Map<String, String> responseMap = parseResponse(response.getBody());
		return new OAuthToken(responseMap.get("oauth_token"), responseMap.get("oauth_token_secret"));
	}

	private Map<String, String> parseResponse(String response) {
		Map<String, String> responseMap = new HashMap<String, String>();
		String[] responseEntries = response.split("&");
		for (String entry : responseEntries) {
			String[] keyValuePair = entry.split("=");
			if (keyValuePair.length > 1) {
				responseMap.put(keyValuePair[0], keyValuePair[1]);
			}
		}
		return responseMap;
	}

	private String buildBaseString(String targetUrl, Map<String, String> parameters, HttpMethod method) {

		String baseString = method.toString() + "&" + encode(targetUrl) + "&";
		List<String> keys = new ArrayList<String>(parameters.keySet());
		Collections.sort(keys);
		String separator = "";
		for (String key : keys) {
			baseString += separator + encode(key) + "%3D" + encode(encode(parameters.get(key)));
			separator = "%26";
		}

		return baseString;
	}

	private String calculateSignature(String baseString, String tokenSecret) {
		String key = consumerSecret + "&" + (tokenSecret == null ? "" : tokenSecret);
		return sign(baseString, key);
	}

	private String sign(String signatureBaseString, String key) {
		try {
			Mac mac = Mac.getInstance(HMAC_SHA1_MAC_NAME);
			SecretKeySpec spec = new SecretKeySpec(key.getBytes(), HMAC_SHA1_MAC_NAME);
			mac.init(spec);
			byte[] text = signatureBaseString.getBytes("UTF-8");
			byte[] signatureBytes = mac.doFinal(text);
			signatureBytes = Base64.encodeBase64(signatureBytes);
			String signature = new String(signatureBytes, "UTF-8");
			return signature;
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		} catch (InvalidKeyException e) {
			throw new IllegalStateException(e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private String encode(String in) {
		try {
			return URLEncoder.encode(in, "UTF-8");
		} catch (Exception wontHappen) {
			return null;
		}
	}

	private static final String HMAC_SHA1_SIGNATURE_NAME = "HMAC-SHA1";
	private static final String HMAC_SHA1_MAC_NAME = "HmacSHA1";
}
