package org.springframework.social.test.connect;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.social.connect.support.Connection;

public class MockConnectionRepositoryTest {

	private MockConnectionRepository repository = new MockConnectionRepository();
	
	@Test
	public void findByAccessToken() {
		repository.saveConnection("kdonald", "twitter", Connection.oauth1("123456789", "secret"));
		assertEquals("kdonald", repository.findAccountIdByConnectionAccessToken("twitter", "123456789"));
	}
}
