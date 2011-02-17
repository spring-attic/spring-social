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
package org.springframework.social.facebook.web;

import static org.junit.Assert.*;

import javax.servlet.jsp.tagext.TagSupport;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;

public class FacebookInitTagTest {
	private MockPageContext pageContext;
	private MockHttpServletResponse response;
	private MockServletContext servletContext;
	private MockHttpServletRequest request;

	@Before
	public void setup() {
		servletContext = new MockServletContext();
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		pageContext = new MockPageContext(servletContext, request, response);
	}

	@Test
	@Ignore("for now")
	public void initWithExplicitApiKey() throws Exception {
		FacebookInitTag tag = new FacebookInitTag();
		tag.setPageContext(pageContext);
		tag.setApiKey("test-key");
		assertEquals(TagSupport.SKIP_BODY, tag.doStartTag());
		assertEquals(TagSupport.EVAL_PAGE, tag.doEndTag());
		assertEquals(
			"<script src='http://connect.facebook.net/en_US/all.js'></script><div id='fb-root'></div>" +
			"<script type='text/javascript'>FB.init({appId: 'test-key', status: true, cookie: true, xfbml: true});</script>",
			response.getContentAsString());
	}
}
