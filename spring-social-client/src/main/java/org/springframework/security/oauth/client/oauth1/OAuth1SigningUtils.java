package org.springframework.security.oauth.client.oauth1;

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
import org.springframework.http.HttpMethod;

public class OAuth1SigningUtils {
	// TODO: Look into reducing the number of params here
	public static String buildAuthorizationHeader(String targetUrl, HttpMethod method, Map<String, String> parameters,
			String consumerKey, String consumerSecret, OAuthToken accessToken) {
		Map<String, String> oauthParameters = getCommonOAuthParameters(consumerKey);
		oauthParameters.put("oauth_token", accessToken.getValue());
		return OAuth1SigningUtils.buildAuthorizationHeader(targetUrl, oauthParameters, parameters, method,
				consumerSecret, accessToken.getSecret());
	}

	// TODO: Look into reducing the number of params here
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

	private static String buildBaseString(String targetUrl, Map<String, String> parameters,
			Map<String, String> additionalParameters, HttpMethod method) {
		Map<String, String> allParameters = new HashMap<String, String>(parameters);
		allParameters.putAll(additionalParameters);
		String baseString = method.toString() + "&" + encode(targetUrl) + "&";
		List<String> keys = new ArrayList<String>(allParameters.keySet());
		Collections.sort(keys);
		String separator = "";
		for (String key : keys) {
			baseString += encode(separator + key + "=" + encode(allParameters.get(key)).replace("+", "%20"));
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

	private static String encode(String in) {
		try {
			return URLEncoder.encode(in, "UTF-8");
		} catch (Exception wontHappen) {
			return null;
		}
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

	public static final String HMAC_SHA1_SIGNATURE_NAME = "HMAC-SHA1";
	private static final String HMAC_SHA1_MAC_NAME = "HmacSHA1";

	private OAuth1SigningUtils() {
	}
}
