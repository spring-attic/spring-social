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
package org.springframework.social.oauth1;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

class SigningUtils {
	
	public static Map<String, String> commonOAuthParameters(String consumerKey) {
		Map<String, String> oauthParameters = new HashMap<String, String>();
		oauthParameters.put("oauth_consumer_key", consumerKey);
		oauthParameters.put("oauth_signature_method", HMAC_SHA1_SIGNATURE_NAME);
		long timestamp = generateTimestamp();
		oauthParameters.put("oauth_timestamp", Long.toString(timestamp));
		oauthParameters.put("oauth_nonce", Long.toString(generateNonce(timestamp)));
		oauthParameters.put("oauth_version", "1.0");
		return oauthParameters;
	}
	
	public static String buildAuthorizationHeaderValue(URI targetUrl, Map<String, String> oauthParameters, MultiValueMap<String, String> additionalParameters, HttpMethod method, String consumerSecret, String tokenSecret) {
		StringBuilder header = new StringBuilder();
		header.append("OAuth ");
		for (Entry<String, String> entry : oauthParameters.entrySet()) {
			header.append(oauthEncode(entry.getKey())).append("=\"").append(oauthEncode(entry.getValue())).append("\", ");
		}
		String baseString = buildBaseString(getBaseStringUri(targetUrl), method, oauthParameters, additionalParameters);
		String signature = calculateSignature(baseString, consumerSecret, tokenSecret);		
		header.append(oauthEncode("oauth_signature")).append("=\"").append(oauthEncode(signature)).append("\"");
		return header.toString();
	}

	public static String buildAuthorizationHeaderValue(HttpRequest request, byte[] body, String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) {
		Map<String, String> oauthParameters = commonOAuthParameters(consumerKey);
		oauthParameters.put("oauth_token", accessToken);
		MultiValueMap<String, String> additionalParameters = new LinkedMultiValueMap<String, String>();
		additionalParameters.putAll(readFormParameters(request.getHeaders().getContentType(), body));
		additionalParameters.putAll(parseFormParameters(request.getURI().getQuery()));
		return buildAuthorizationHeaderValue(request.getURI(), oauthParameters, additionalParameters, request.getMethod(), consumerSecret, accessTokenSecret);
	}
	
	// spring 3.0 compatibility only: planned for removal in Spring Social 1.1

	public static String spring30buildAuthorizationHeaderValue(ClientHttpRequest request, byte[] body, String consumerKey, String consumerSecret, String accessToken, String accessTokenSecret) {
		Map<String, String> oauthParameters = commonOAuthParameters(consumerKey);
		oauthParameters.put("oauth_token", accessToken);
		MultiValueMap<String, String> additionalParameters = new LinkedMultiValueMap<String, String>();
		additionalParameters.putAll(readFormParameters(request.getHeaders().getContentType(), body));
		additionalParameters.putAll(parseFormParameters(request.getURI().getQuery()));
		return buildAuthorizationHeaderValue(request.getURI(), oauthParameters, additionalParameters, request.getMethod(), consumerSecret, accessTokenSecret);
	}

	// internal helpers

	private static long generateTimestamp() {
		return System.currentTimeMillis() / 1000;
	}
	
	private static long generateNonce(long timestamp) {
		return timestamp + RANDOM.nextInt();		
	}
	
	private static final Random RANDOM = new Random();
	
	private static String buildBaseString(String targetUrl, HttpMethod method, Map<String, String> oauthParameters, MultiValueMap<String, String> additionalParameters) {
		MultiValueMap<String, String> allParameters = new TreeMultiValueMap<String, String>();
		allParameters.setAll(oauthParameters);
		allParameters.putAll(additionalParameters);
		StringBuilder builder = new StringBuilder();
		builder.append(method.name()).append('&').append(oauthEncode(targetUrl)).append('&');
		for (Iterator<Entry<String, List<String>>> entryIt = allParameters.entrySet().iterator(); entryIt.hasNext();) {
			Entry<String, List<String>> entry = entryIt.next();
			String name = entry.getKey();
			List<String> values = entry.getValue();
			Collections.sort(values);
			for (Iterator<String> valueIt = values.iterator(); valueIt.hasNext();) {
				String value = valueIt.next();
				if (value != null) {
					builder.append(oauthEncode(name));
					builder.append("%3D");
					builder.append(oauthEncode(value));
					if (valueIt.hasNext()) {
						builder.append("%26");
					}
				}
			}
			if (entryIt.hasNext()) {
				builder.append("%26");
			}
		}
		return builder.toString();
	}

	private static String calculateSignature(String baseString, String consumerSecret, String tokenSecret) {
		String key = consumerSecret + "&" + (tokenSecret != null ? tokenSecret : "");
		return sign(baseString, key);
	}

	private static String sign(String signatureBaseString, String key) {
		try {
			Mac mac = Mac.getInstance(HMAC_SHA1_MAC_NAME);
			SecretKeySpec spec = new SecretKeySpec(key.getBytes(), HMAC_SHA1_MAC_NAME);
			mac.init(spec);
			byte[] text = signatureBaseString.getBytes(charset);
			byte[] signatureBytes = mac.doFinal(text);
			signatureBytes = Base64.encodeBase64(signatureBytes);
			String signature = new String(signatureBytes, charset);
			return signature;
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		} catch (InvalidKeyException e) {
			throw new IllegalStateException(e);
		} 
	}

	private static MultiValueMap<String, String> readFormParameters(MediaType bodyType, byte[] bodyBytes) {
		if (bodyType != null && bodyType.equals(MediaType.APPLICATION_FORM_URLENCODED)) {
			String body = new String(bodyBytes, charset);
			return parseFormParameters(body);
		} else {
			return EmptyMultiValueMap.instance();
		}
	}
	
	private static MultiValueMap<String, String> parseFormParameters(String parameterString) {
		if (parameterString == null || parameterString.length() == 0) {
			return EmptyMultiValueMap.instance();
		}		
		String[] pairs = StringUtils.tokenizeToStringArray(parameterString, "&");
		MultiValueMap<String, String> result = new LinkedMultiValueMap<String, String>(pairs.length);
		for (String pair : pairs) {
			int idx = pair.indexOf('=');
			if (idx == -1) {
				result.add(formDecode(pair), null);
			}
			else {
				String name = formDecode(pair.substring(0, idx));
				String value = formDecode(pair.substring(idx + 1));
				result.add(name, value);
			}
		}
		return result;		
	}

	private static String getBaseStringUri(URI uri) {
		try {
			// see: http://tools.ietf.org/html/rfc5849#section-3.4.1.2
			return new URI(uri.getScheme(), null, uri.getHost(), getPort(uri), uri.getPath(), null, null).toString();
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private static int getPort(URI uri) {
		if (uri.getScheme().equals("http") && uri.getPort() == 80 || uri.getScheme().equals("https") && uri.getPort() == 443) {
			return -1;
		} else {
			return uri.getPort();
		}
	}

	private static final BitSet UNRESERVED;
	
	static {
		BitSet alpha = new BitSet(256);
		for (int i = 'a'; i <= 'z'; i++) {
			alpha.set(i);
		}
		for (int i = 'A'; i <= 'Z'; i++) {
			alpha.set(i);
		}
		BitSet digit = new BitSet(256);
		for (int i = '0'; i <= '9'; i++) {
			digit.set(i);
		}
		BitSet unreserved = new BitSet(256);
		unreserved.or(alpha);
		unreserved.or(digit);
		unreserved.set('-');
		unreserved.set('.');
		unreserved.set('_');
		unreserved.set('~');
		UNRESERVED = unreserved;		
	}
	
	private static String oauthEncode(String param) {
		try {
			// See http://tools.ietf.org/html/rfc5849#section-3.6
			byte[] bytes = encode(param.getBytes("UTF-8"), UNRESERVED);
			return new String(bytes, "US-ASCII");
		} catch (Exception shouldntHappen) {
			throw new IllegalStateException(shouldntHappen);
		}
	}

	private static byte[] encode(byte[] source, BitSet notEncoded) {
		Assert.notNull(source, "'source' must not be null");
		ByteArrayOutputStream bos = new ByteArrayOutputStream(source.length * 2);
		for (int i = 0; i < source.length; i++) {
			int b = source[i];
			if (b < 0) {
				b += 256;
			}
			if (notEncoded.get(b)) {
				bos.write(b);
			}
			else {
				bos.write('%');
				char hex1 = Character.toUpperCase(Character.forDigit((b >> 4) & 0xF, 16));
				char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, 16));
				bos.write(hex1);
				bos.write(hex2);
			}
		}
		return bos.toByteArray();
	}
	
	private static String formDecode(String encoded) {
		try {
			return URLDecoder.decode(encoded, "UTF-8");
		} catch (UnsupportedEncodingException shouldntHappen) {
			throw new IllegalStateException(shouldntHappen);
		}
	}

	public static final String HMAC_SHA1_SIGNATURE_NAME = "HMAC-SHA1";

	private static final String HMAC_SHA1_MAC_NAME = "HmacSHA1";

	private static Charset charset = Charset.forName("UTF-8");
	
	private SigningUtils() {
	}	

}