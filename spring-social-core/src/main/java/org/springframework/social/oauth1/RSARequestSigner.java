package org.springframework.social.oauth1;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.springframework.security.crypto.codec.Base64;

public class RSARequestSigner implements RequestSigner {

	private static final String RSA_SHA1_SIGNATURE_NAME = "RSA-SHA1";

	private static final String RSA_SHA1_MAC_NAME = "SHA1withRSA";

	private final KeyFactory RSA_KEY_FACTORY;

	public RSARequestSigner() {
		 try {
			RSA_KEY_FACTORY = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}

	public String getSignatureMethod() {
		return RSA_SHA1_SIGNATURE_NAME;
	}

	public String calculateSignature(String baseString, String consumerSecret, String tokenSecret) {
		return sign(baseString, consumerSecret);
	}

	private String sign(String signatureBaseString, String key) {
		try {
			KeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.decode(key.getBytes()));
			PrivateKey privateKey = RSA_KEY_FACTORY.generatePrivate(privateKeySpec);
			Signature signer = Signature.getInstance(RSA_SHA1_MAC_NAME);
			signer.initSign(privateKey);
			signer.update(signatureBaseString.getBytes(SigningSupport.UTF8_CHARSET_NAME));
			byte[] signatureBytes = signer.sign();
			return new String(Base64.encode(signatureBytes), SigningSupport.UTF8_CHARSET_NAME);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		} catch (InvalidKeyException e) {
			throw new IllegalStateException(e);
		} catch (UnsupportedEncodingException e ) {
			throw new IllegalStateException(e);
		} catch (InvalidKeySpecException e) {
			throw new IllegalStateException(e);
		} catch (SignatureException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public boolean verifySignature(String signatureBaseString, String consumerSecret, String tokenSecret, String signature) {
		return verify(signatureBaseString, consumerSecret, signature);
	}

	public boolean verify(String baseString, String key, String signature) {
		try {
			KeySpec publicKeySpec = new X509EncodedKeySpec(Base64.decode(key.getBytes()));
			PublicKey publicKey = RSA_KEY_FACTORY.generatePublic(publicKeySpec);
			Signature verifier = Signature.getInstance(RSA_SHA1_MAC_NAME);
			verifier.initVerify(publicKey);
			verifier.update(baseString.getBytes(SigningSupport.UTF8_CHARSET_NAME));
			return verifier.verify(Base64.decode(signature.getBytes(SigningSupport.UTF8_CHARSET_NAME)));
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		} catch (InvalidKeyException e) {
			throw new IllegalStateException(e);
		} catch (UnsupportedEncodingException e ) {
			throw new IllegalStateException(e);
		} catch (InvalidKeySpecException e) {
			throw new IllegalStateException(e);
		} catch (SignatureException e) {
			throw new IllegalStateException(e);
		}
	}

}
