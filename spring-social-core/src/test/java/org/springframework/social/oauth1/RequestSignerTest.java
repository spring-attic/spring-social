package org.springframework.social.oauth1;

import static org.junit.Assert.assertTrue;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

public class RequestSignerTest {

	@Test
	public void RSAsignatureTest() throws Exception {
		RequestSigner requestSigner = new RSARequestSigner();

		String baseString = "arbitrary_string";

		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(1024);
		KeyPair keyPair = keyPairGenerator.generateKeyPair();

		String consumerKey = Base64.encodeBase64String(keyPair.getPrivate().getEncoded());

		String signature = requestSigner.calculateSignature(baseString, consumerKey, null);

		String publicKey = Base64.encodeBase64String(keyPair.getPublic().getEncoded());
		assertTrue(requestSigner.verifySignature(baseString, publicKey, null, signature));
	}

	@Test
	public void HMACsignatureTest() throws Exception {
		RequestSigner requestSigner = new HMACRequestSigner();

		String baseString = "arbitrary_string";
		String signature = requestSigner.calculateSignature(baseString,"consumerSecret", "tokenSecret");

		assertTrue(requestSigner.verifySignature(baseString, "consumerSecret", "tokenSecret", signature));
	}

}
