package org.springframework.social.oauth1;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.crypto.codec.Base64;

public class HMACRequestSigner implements RequestSigner {

	private static final String HMAC_SHA1_SIGNATURE_NAME = "HMAC-SHA1";

	private static final String HMAC_SHA1_MAC_NAME = "HmacSHA1";

	@Override
	public String getSignatureMethod() {
		return HMAC_SHA1_SIGNATURE_NAME;
	}

	@Override
	public String calculateSignature(String baseString, String consumerSecret, String tokenSecret) {
		String key = SigningSupport.oauthEncode(consumerSecret) + "&" + (tokenSecret != null ? SigningSupport.oauthEncode(tokenSecret) : "");
		return sign(baseString, key);
	}

	private String sign(String signatureBaseString, String key) {
		try {
			Mac mac = Mac.getInstance(HMAC_SHA1_MAC_NAME);
			SecretKeySpec spec = new SecretKeySpec(key.getBytes(), HMAC_SHA1_MAC_NAME);
			mac.init(spec);
			byte[] text = signatureBaseString.getBytes(SigningSupport.UTF8_CHARSET_NAME);
			byte[] signatureBytes = mac.doFinal(text);
			signatureBytes = Base64.encode(signatureBytes);
			String signature = new String(signatureBytes, SigningSupport.UTF8_CHARSET_NAME);
			return signature;
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}catch (InvalidKeyException e) {
			throw new IllegalStateException(e);
		} catch (UnsupportedEncodingException shouldntHappen) {
			throw new IllegalStateException(shouldntHappen);
		}
	}

	@Override
	public boolean verifySignature(String baseString, String consumerSecret, String tokenSecret, String signature) {
		String key = SigningSupport.oauthEncode(consumerSecret) + "&" + (tokenSecret != null ? SigningSupport.oauthEncode(tokenSecret) : "");
		return sign(baseString, key).equals(signature);
	}

}
