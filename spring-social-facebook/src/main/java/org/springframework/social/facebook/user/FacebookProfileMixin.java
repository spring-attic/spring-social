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
package org.springframework.social.facebook.user;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.social.facebook.shared.Reference;

/**
 * Annotated mixin to add Jackson annotations to FacebookProfile. 
 * @author Craig Walls
 */
public abstract class FacebookProfileMixin {

	@JsonCreator
	FacebookProfileMixin(
			@JsonProperty("id") String id, 
			@JsonProperty("username") String username, 
			@JsonProperty("name") String name, 
			@JsonProperty("first_name") String firstName, 
			@JsonProperty("last_name") String lastName, 
			@JsonProperty("gender") String gender, 
			@JsonProperty("locale") Locale locale) {}
	
	@JsonProperty("work")
	List<WorkEntry> work;
	
	@JsonProperty("education")
	List<EducationEntry> education;
	
	@JsonProperty("email")
	String email;
	
	@JsonProperty("link")
	String link;
	
	@JsonProperty("third_party_id")
	String thirdPartyId;
	
	@JsonProperty("timezone")
	Integer timezone;
	
	@JsonProperty("updated_time")
	Date updatedTime;
	
	@JsonProperty("verified")
	Boolean verified; 
	
	@JsonProperty("about")
	String about;
	
	@JsonProperty("bio")
	String bio;
	
	@JsonProperty("birthday")
	String birthday;
	
	@JsonProperty("location")
	Reference location;
	
	@JsonProperty("hometown")
	Reference hometown;
	
	@JsonProperty("interested_in")
	List<String> interestedIn;
	
	@JsonProperty("inspirational_people")
	List<Reference> inspirationalPeople;

	@JsonProperty("languages")
	List<Reference> languages;
	
	@JsonProperty("sports")
	List<Reference> sports;
	
	@JsonProperty("favorite_teams")
	List<Reference> favoriteTeams;
	
	@JsonProperty("favorite_athletes")
	List<Reference> favoriteAthletes;

	@JsonProperty("religion")
	String religion;

	@JsonProperty("political")
	String political;
	
	@JsonProperty("quotes")
	String quotes;
	
	@JsonProperty("relationship_status")
	String relationshipStatus;
	
	@JsonProperty("significant_other")
	Reference significantOther;
	
	@JsonProperty("website")
	String website;
}
