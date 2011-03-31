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
package org.springframework.social.facebook.support.extractors;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.social.facebook.FacebookProfile;

public class ProfileResponseExtractor extends AbstractResponseExtractor<FacebookProfile> {

	private WorkResponseExtractor workExtractor;
	private EducationResponseExtractor educationExtractor;

	public ProfileResponseExtractor() {
		workExtractor = new WorkResponseExtractor();
		educationExtractor = new EducationResponseExtractor();
	}

	public FacebookProfile extractObject(Map<String, Object> profileMap) {
		long id = Long.valueOf(String.valueOf(profileMap.get("id")));
		String username = String.valueOf(profileMap.get("username"));
		String name = String.valueOf(profileMap.get("name"));
		String firstName = String.valueOf(profileMap.get("first_name"));
		String lastName = String.valueOf(profileMap.get("last_name"));
		String gender = String.valueOf(profileMap.get("gender"));
		String locale = String.valueOf(profileMap.get("locale"));
		Object educationHistory = profileMap.get("education");
		List<Map<String, Object>> educationHistoryList = (List<Map<String, Object>>) (educationHistory != null ? educationHistory
				: Collections.emptyList());

		Object workHistory = profileMap.get("work");
		List<Map<String, Object>> workHistoryList = (List<Map<String, Object>>) (workHistory != null ? workHistory
				: Collections.emptyList());

		return new FacebookProfile.Builder(id, username, name, firstName, lastName, gender, locale)
				.email((String) profileMap.get("email")).link((String) profileMap.get("link"))
				.thirdPartyId((String) profileMap.get("third_party_id")).timezone((Integer) profileMap.get("timezone"))
				.updatedTime(toDate((String) profileMap.get("updated_time")))
				.verified((Boolean) profileMap.get("verified")).about((String) profileMap.get("about"))
				.bio((String) profileMap.get("bio")).birthday((String) profileMap.get("birthday"))
				.location(extractReferenceFromMap((Map<String, Object>) profileMap.get("location")))
				.hometown(extractReferenceFromMap((Map<String, Object>) profileMap.get("hometown")))
				.interestedIn((List<String>) profileMap.get("interested_in"))
				.religion((String) profileMap.get("religion")).political((String) profileMap.get("political"))
				.quotes((String) profileMap.get("quotes"))
				.relationshipStatus((String) profileMap.get("relationship_status"))
				.significantOther(extractReferenceFromMap((Map<String, Object>) profileMap.get("significant_other")))
				.website((String) profileMap.get("website")).work(workExtractor.extractObjects(workHistoryList))
				.education(educationExtractor.extractObjects(educationHistoryList)).build();
	}

}
