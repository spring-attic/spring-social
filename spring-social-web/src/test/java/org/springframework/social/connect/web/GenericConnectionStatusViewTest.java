/*
 * Copyright 2015 the original author or authors.
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
package org.springframework.social.connect.web;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.social.connect.web.GenericConnectionStatusView.GenericConnectionStatusHtmlCreator.generateConnectionViewHtml;

import org.junit.Test;
import org.springframework.social.connect.UserProfile;

public class GenericConnectionStatusViewTest {

	@Test
	public void testWithProfile() {
		String providerDisplayName = "Facebook";
		String providerId = "facebook";
		UserProfile profile= new UserProfile("1234567890", "habuma", "Craig", "Walls", "cwalls@pivotal.io", "habuma");
		String html = generateConnectionViewHtml(providerDisplayName, providerId, profile);
		assertThat(html, equalTo(
				"<h3>Connected to Facebook</h3><p>Hello, habuma!</p><p>You are now connected to Facebook as habuma.</p>"));
	}
	
	@Test
	public void testWithoutProfile() {
		String providerDisplayName = "Facebook";
		String providerId = "facebook";
		String html = generateConnectionViewHtml(providerDisplayName, providerId, null);
		assertThat(html, equalTo(
				"<h3>Connect to Facebook</h3><form action=\"/connect/facebook\" method=\"POST\"><div class=\"formInfo\">"
				+ "<p>You aren't connected to Facebook yet. Click the button to connect with your Facebook account.</p></div><p>"
				+ "<button type=\"submit\">Connect to Facebook</button></p></form>"));
	}
	
	@Test
	public void testWithProfile_escaped() {
		String providerDisplayName = "F{}ace<h2>boo</h2>k";
		String providerId = "facebook";
		UserProfile profile= new UserProfile("1234567890", 
				"{{this.constructor.constructor('alert(1337)')()}}", "Craig", "Walls", "cwalls@pivotal.io", "{{this.constructor.constructor('alert(1337)')()}}");
		String html = generateConnectionViewHtml(providerDisplayName, providerId, profile);
		assertThat(html, equalTo(
				"<h3>Connected to F&#123;&#125;ace&lt;h2&gt;boo&lt;/h2&gt;k</h3><p>Hello, "
				+ "&#123;&#123;this.constructor.constructor(&#39;alert(1337)&#39;)()&#125;&#125;!</p>"
				+ "<p>You are now connected to F&#123;&#125;ace&lt;h2&gt;boo&lt;/h2&gt;k as "
				+ "&#123;&#123;this.constructor.constructor(&#39;alert(1337)&#39;)()&#125;&#125;.</p>"));
	}
	
	@Test
	public void testWithoutProfile_escaped() {
		String providerDisplayName = "F{}ace<h2>boo</h2>k";
		String providerId = "f{}ace<h2>boo</h2>k";
		String html = generateConnectionViewHtml(providerDisplayName, providerId, null);
		assertThat(html, equalTo(
				"<h3>Connect to F&#123;&#125;ace&lt;h2&gt;boo&lt;/h2&gt;k</h3>"
				+ "<form action=\"/connect/f%7B%7Dace%3Ch2%3Eboo%3C/h2%3Ek\" method=\"POST\"><div class=\"formInfo\">"
				+ "<p>You aren't connected to F&#123;&#125;ace&lt;h2&gt;boo&lt;/h2&gt;k yet. "
				+ "Click the button to connect with your F&#123;&#125;ace&lt;h2&gt;boo&lt;/h2&gt;k account.</p></div><p>"
				+ "<button type=\"submit\">Connect to F&#123;&#125;ace&lt;h2&gt;boo&lt;/h2&gt;k</button></p></form>"));
	}
	
}
