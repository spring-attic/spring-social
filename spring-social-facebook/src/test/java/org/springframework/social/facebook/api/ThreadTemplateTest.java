/**
 * 
 */
package org.springframework.social.facebook.api;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.social.test.client.RequestMatchers.header;
import static org.springframework.social.test.client.RequestMatchers.method;
import static org.springframework.social.test.client.RequestMatchers.requestTo;
import static org.springframework.social.test.client.ResponseCreators.withResponse;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;


/**
 * @author leandro.soler
 *
 */
public class ThreadTemplateTest extends AbstractFacebookApiTest {

	@Test
	public void getAllThreads() {
		mockServer.expect(requestTo("https://graph.facebook.com/123456/threads"))
		.andExpect(method(GET))
		.andExpect(header("Authorization", "OAuth someAccessToken"))
		.andRespond(withResponse(new ClassPathResource("testdata/threads.json", getClass()), responseHeaders));
		
		List<FacebookThread> threads = facebook.threadOperations().getAllThreads("123456");
		assertNotNull(threads);
		assertTrue(threads.size() > 0);
	}
	
	@Test
	public void getThread() {
		mockServer.expect(requestTo("https://graph.facebook.com/12345678912323"))
		.andExpect(method(GET))
		.andExpect(header("Authorization", "OAuth someAccessToken"))
		.andRespond(withResponse(new ClassPathResource("testdata/thread.json", getClass()), responseHeaders));
		
		FacebookThread thread = facebook.threadOperations().getThread("12345678912323");
		assertNotNull(thread);
		assertTrue(thread.getId().equalsIgnoreCase("1122334455"));
		assertTrue(thread.getMessageCount() == 3);
		assertTrue(thread.getUnreadCount() == 1);
		assertTrue(thread.getSnippet().equalsIgnoreCase("Snippet 1"));
	}
}
