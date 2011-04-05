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
package org.springframework.social.twitter;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.social.test.client.RequestMatchers.*;
import static org.springframework.social.test.client.ResponseCreators.*;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.social.BadCredentialsException;

public class ApiErrorTest extends AbstractTwitterApiTest {

	@Test(expected=BadCredentialsException.class)
	public void badOrMissingAccessToken() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/update.json"))
			.andExpect(method(POST))
			.andExpect(body("status=Some+message"))
			.andRespond(withResponse("", responseHeaders, HttpStatus.UNAUTHORIZED, ""));
		twitter.tweetApi().updateStatus("Some message");		
	}
	
	@Test(expected=EnhanceYourCalmException.class)
	public void enhanceYourCalm() {
		mockServer.expect(requestTo("https://search.twitter.com/search.json?q=%23spring&rpp=50&page=1"))
			.andExpect(method(GET))
			.andRespond(withResponse("{\"error\":\"You have been rate limited. Enhance your calm.\"}", responseHeaders, HttpStatus.valueOf(420), ""));		
		twitter.searchApi().search("#spring");
	}

	@Test(expected=InternalProviderErrorException.class)
	public void twitterIsBroken() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/home_timeline.json"))
			.andExpect(method(GET))
			.andRespond(withResponse("Non-JSON body", responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR, ""));
		twitter.tweetApi().getHomeTimeline();
	}
	
	@Test(expected=ProviderDownException.class)
	public void twitterIsDownOrBeingUpgraded() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/home_timeline.json"))
			.andExpect(method(GET))
			.andRespond(withResponse("Non-JSON body", responseHeaders, HttpStatus.BAD_GATEWAY, ""));
		twitter.tweetApi().getHomeTimeline();
	}
	
	@Test(expected=ProviderOverloadedException.class)
	public void twitterIsOverloaded() {
		mockServer.expect(requestTo("https://api.twitter.com/1/statuses/home_timeline.json"))
			.andExpect(method(GET))
			.andRespond(withResponse("Non-JSON body", responseHeaders, HttpStatus.SERVICE_UNAVAILABLE, ""));
		twitter.tweetApi().getHomeTimeline();
	}
}
