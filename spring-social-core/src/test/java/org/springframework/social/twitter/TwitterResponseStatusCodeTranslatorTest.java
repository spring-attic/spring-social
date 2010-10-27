package org.springframework.social.twitter;

import static org.junit.Assert.*;
import static org.springframework.social.twitter.TwitterResponseStatusCodeTranslator.*;

import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.social.core.AccountNotConnectedException;
import org.springframework.social.core.OperationNotPermittedException;
import org.springframework.social.core.SocialException;

@SuppressWarnings("rawtypes")
public class TwitterResponseStatusCodeTranslatorTest {
	private TwitterResponseStatusCodeTranslator translator = new TwitterResponseStatusCodeTranslator();

	@Before
	public void setup() {
		new TwitterResponseStatusCodeTranslator();
	}

	@Test
	public void translate_forbiddenNonDuplicate() {
		ResponseEntity<Map> responseEntity = new ResponseEntity<Map>(Collections.singletonMap("error",
				"Some forbidden message"), HttpStatus.FORBIDDEN);
		SocialException socialException = translator.translate(responseEntity);
		assertTrue(socialException instanceof OperationNotPermittedException);
		assertEquals("Some forbidden message", socialException.getMessage());
	}

	@Test
	public void translate_duplicateTweet() {
		ResponseEntity<Map> responseEntity = new ResponseEntity<Map>(Collections.singletonMap("error",
				DUPLICATE_STATUS_TEXT), HttpStatus.FORBIDDEN);
		SocialException socialException = translator.translate(responseEntity);
		assertTrue(socialException instanceof DuplicateTweetException);
		assertEquals(DUPLICATE_STATUS_TEXT, socialException.getMessage());
	}

	@Test
	public void translate_unauthorized() {
		ResponseEntity<Map> responseEntity = new ResponseEntity<Map>(
				Collections.singletonMap("error", "That's a no-no"), HttpStatus.UNAUTHORIZED);
		SocialException socialException = translator.translate(responseEntity);
		assertTrue(socialException instanceof AccountNotConnectedException);
		assertEquals("That's a no-no", socialException.getMessage());
	}
}
