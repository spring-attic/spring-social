package org.springframework.security.oauth.client.oauth1;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth.client.ClientRequest;

class SigningUtils {
	public static String buildAuthorizationHeader(ClientRequest request, String consumerKey, String consumerSecret,
			OAuthToken accessToken) {
		Map<String, String> oauthParameters = getCommonOAuthParameters(consumerKey);
		oauthParameters.put("oauth_token", accessToken.getValue());
		Map<String, String> aditionalParameters = extractBodyParameters(request.getHeaders().getContentType(),
				request.getBody());
		Map<String, String> queryParameters = extractParameters(request.getUri().getQuery());
		aditionalParameters.putAll(queryParameters);
		String baseRequestUrl = getBaseUrlWithoutPortOrQueryString(request.getUri());
		return SigningUtils.buildAuthorizationHeader(baseRequestUrl, oauthParameters, aditionalParameters,
				request.getMethod(), consumerSecret, accessToken.getSecret());
	}

	public static String buildAuthorizationHeader(String targetUrl, Map<String, String> oauthParameters,
			Map<String, String> additionalParameters, HttpMethod method, String consumerSecret, String tokenSecret) {
		String baseString = buildBaseString(targetUrl, oauthParameters, additionalParameters, method);
		String signature = calculateSignature(baseString, consumerSecret, tokenSecret);
		String header = "OAuth ";
		for (String key : oauthParameters.keySet()) {
			header += key + "=\"" + encode(oauthParameters.get(key)) + "\", ";
		}
		header += "oauth_signature=\"" + encode(signature) + "\"";
		return header;
	}

	public static Map<String, String> getCommonOAuthParameters(String consumerKey) {
		Map<String, String> oauthParameters = new HashMap<String, String>();
		oauthParameters.put("oauth_consumer_key", consumerKey);
		oauthParameters.put("oauth_signature_method", HMAC_SHA1_SIGNATURE_NAME);
		oauthParameters.put("oauth_timestamp", String.valueOf(System.currentTimeMillis() / 1000));
		oauthParameters.put("oauth_nonce", UUID.randomUUID().toString());
		oauthParameters.put("oauth_version", "1.0");
		return oauthParameters;
	}

	private static String buildBaseString(String targetUrl, Map<String, String> parameters,
			Map<String, String> additionalParameters, HttpMethod method) {
		Map<String, String> allParameters = new HashMap<String, String>(parameters);
		allParameters.putAll(additionalParameters);
		String baseString = method.toString() + "&" + encode(targetUrl) + "&";
		List<String> keys = new ArrayList<String>(allParameters.keySet());
		Collections.sort(keys);
		String separator = "";
		for (String key : keys) {
			String parameterValue = allParameters.get(key);
			baseString += encode(separator + key + "=" + encode(parameterValue));
			separator = "&";
		}
		return baseString;
	}

	private static String calculateSignature(String baseString, String consumerSecret, String tokenSecret) {
		String key = consumerSecret + "&" + (tokenSecret == null ? "" : tokenSecret);
		return sign(baseString, key);
	}

	private static String sign(String signatureBaseString, String key) {
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

	private static Map<String, String> extractBodyParameters(MediaType bodyType, byte[] bodyBytes) {
		if (bodyType != null && bodyType.equals(MediaType.APPLICATION_FORM_URLENCODED)) {
			return extractParameters(new String(bodyBytes));
		}
		return new HashMap<String, String>();
	}

	private static Map<String, String> extractParameters(String parameterString) {
		Map<String, String> params = new HashMap<String, String>();
		if (parameterString != null) {
			String[] paramPairs = parameterString.split("&");
			for (String pair : paramPairs) {
				String[] keyValue = pair.split("=");
				if (keyValue.length == 2) {
					// TODO: Determine if this decode step is necessary, since
					// it's just going to be encoded again later
					params.put(keyValue[0], decode(keyValue[1]));
				}
			}
		}
		return params;
	}

	private static String getBaseUrlWithoutPortOrQueryString(URI uri) {
		String baseRequestUrl = uri.toString().replaceAll("\\?.*", "").replace("\\:\\d{4}", "");
		return baseRequestUrl;
	}

	private static String encode(String in) {
		try {
			// See http://oauth.net/core/1.0a/#encoding_parameters
			return URLEncoder.encode(in, "UTF-8").replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
		} catch (Exception wontHappen) {
			return null;
		}
	}

	private static String decode(String encoded) {
		try {
			return URLDecoder.decode(encoded, "UTF-8");
		} catch (UnsupportedEncodingException shouldntHappen) {
			return encoded;
		}
	}

	public static final String HMAC_SHA1_SIGNATURE_NAME = "HMAC-SHA1";

	private static final String HMAC_SHA1_MAC_NAME = "HmacSHA1";

	private SigningUtils() {
	}
}
