/*
 * Copyright 2010 the original author or authors.
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
package org.springframework.social.linkedin;

import static org.junit.Assert.*;
import static org.junit.internal.matchers.IsCollectionContaining.*;
import static org.mockito.Mockito.*;
import static org.springframework.social.linkedin.LinkedInTemplate.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestOperations;

/**
 * @author Craig Walls
 */
public class LinkedInTemplateTest {
	private LinkedInTemplate linkedIn;
	private RestOperations restOperations;

	@Before
	public void setup() {
		linkedIn = new LinkedInTemplate("API_KEY", "API_SECRET", "ACCESS_TOKEN", "ACCESS_TOKEN_SECRET");
		restOperations = mock(RestOperations.class);
		linkedIn.restOperations = restOperations;
	}

	@Test
	public void getUserProfile() {
		setupRestOperationsForReturningALinkedInProfile();

		LinkedInProfile actual = linkedIn.getUserProfile();
		assertEquals("24680", actual.getId());
		assertEquals("Java Developer", actual.getHeadline());
		assertEquals("Craig", actual.getFirstName());
		assertEquals("Walls", actual.getLastName());
		assertEquals("Software", actual.getIndustry());
		assertEquals("http://linkedin.com/pub/24680", actual.getPublicProfileUrl());
		assertEquals("http://linkedin.com/std/24680", actual.getStandardProfileUrl());
	}

	@Test
	public void getProfileId() {
		setupRestOperationsForReturningALinkedInProfile();
		assertEquals("24680", linkedIn.getProfileId());
	}

	@Test
	public void getProfileUrl() {
		setupRestOperationsForReturningALinkedInProfile();
		assertEquals("http://linkedin.com/pub/24680", linkedIn.getProfileUrl());
	}

	@Test
	public void getConnections() {
		LinkedInConnections connections = new LinkedInConnections();
		connections.connections = new ArrayList<LinkedInProfile>();
		LinkedInProfile keith = createProfile("11223", "Keith", "Donald", "Spring Developer", "Software",
				"http://linkedin.com/pub/11223", "http://linkedin.com/std/11223");
		LinkedInProfile roy = createProfile("99887", "Roy", "Clarkson", "Mobile Developer", "Software",
				"http://linkedin.com/pub/99887", "http://linkedin.com/std/99887");
		connections.connections.add(keith);
		connections.connections.add(roy);
		when(restOperations.getForObject("http://api.linkedin.com/v1/people/~/connections", LinkedInConnections.class))
				.thenReturn(connections);

		List<LinkedInProfile> actual = linkedIn.getConnections();
		assertThat(actual, hasItem(keith));
		assertThat(actual, hasItem(roy));
	}

	private void setupRestOperationsForReturningALinkedInProfile() {
		LinkedInProfile profile = createProfile("24680", "Craig", "Walls", "Java Developer", "Software",
				"http://linkedin.com/pub/24680", "http://linkedin.com/std/24680");
		when(restOperations.getForObject(GET_CURRENT_USER_INFO, LinkedInProfile.class)).thenReturn(profile);
	}

	private LinkedInProfile createProfile(String id, String firstName, String lastName, String headline,
			String industry, String publicUrl, String standardUrl) {
		LinkedInProfile profile = new LinkedInProfile();
		profile.id = id;
		profile.headline = headline;
		profile.firstName = firstName;
		profile.lastName = lastName;
		profile.industry = industry;
		profile.publicProfileUrls = new String[] { publicUrl };
		profile.standardProfileUrls = new String[] { standardUrl };
		return profile;
	}
}
