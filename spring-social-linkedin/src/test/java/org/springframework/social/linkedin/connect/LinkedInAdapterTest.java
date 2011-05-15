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
package org.springframework.social.linkedin.connect;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.linkedin.api.LinkedIn;
import org.springframework.social.linkedin.api.LinkedInProfile;

public class LinkedInAdapterTest {

	private LinkedInAdapter apiAdapter = new LinkedInAdapter();
	
	private LinkedIn linkedin = Mockito.mock(LinkedIn.class);
	
	@Test
	public void fetchProfile() {
		Mockito.when(linkedin.getUserProfile()).thenReturn(new LinkedInProfile("50A3nOf73z", "Craig", "Walls", "Spring Guy", "Software", "http://www.linkedin.com/in/habuma", "http://www.linkedin.com/profile?viewProfile=&key=3630172&authToken=0IpZ&authType=name&trk=api*a121026*s129482*", "http://media.linkedin.com/mpr/mprx/0_9-Hjc8b0ViE1gGElNtdCcGh0s3pjxbRlNzpCciT05XHD8i2Asq4AM_zAN7yGp8VgcAoi4k1faewD"));
		UserProfile profile = apiAdapter.fetchUserProfile(linkedin);
		assertEquals("Craig Walls", profile.getName());
		assertEquals("Craig", profile.getFirstName());
		assertEquals("Walls", profile.getLastName());
		assertNull(profile.getEmail());
		assertNull(profile.getUsername());
	}

}
