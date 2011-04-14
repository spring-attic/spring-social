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
package org.springframework.social.facebook.types;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Model class containing a Facebook user's profile information.
 * 
 * @author Craig Walls
 */
@SuppressWarnings("serial")
public class FacebookProfile implements Serializable {

	private final String id;

	private final String username;

	private final String name;

	private final String firstName;

	private final String lastName;

	private final String gender;

	private final Locale locale;

	private String link;

	private String website;

	private String email;
	
	private String thirdPartyId;

	private Integer timezone;
	
	private Date updatedTime;

	private Boolean verified;

	private String about;
	
	private String bio;
	
	private String birthday;
	
	private Reference location;
	
	private Reference hometown;
	
	private List<String> interestedIn;

	private String religion;

	private String political;

	private String quotes;

	private String relationshipStatus;

	private Reference significantOther;
	
	private List<WorkEntry> work;
	
	private List<EducationEntry> education;

	public FacebookProfile(String id, String username, String name, String firstName, String lastName, String gender, Locale locale) {
		this.id = id;
		this.username = username;
		this.name = name;
		this.firstName = firstName;
		this.lastName = lastName;
		this.gender = gender;
		this.locale = locale;
	}

	/**
	 * The user's Facebook ID
	 * @return The user's Facebook ID
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * The user's Facebook username
	 * @return the user's Facebook username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * The user's full name
	 * @return The user's full name
	 */
	public String getName() {
		return name;
	}

	/**
	 * The user's first name
	 * @return The user's first name
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * The user's last name
	 * @return The user's last name
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * The user's gender
	 * @return the user's gender
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * The user's locale
	 * @return the user's locale
	 */
	public Locale getLocale() {
		return locale;
	}

	/**
	 * The user's email address
	 * @return The user's email address
	 */
	public String getEmail() {
	    return email;
    }
	
	/**
	 * A link to the user's profile on Facebook.
	 * Available only if requested by an authenticated user.
	 * @return the user's profile link or null if requested anonymously
	 */
	public String getLink() {
		return link;
	}

	/**
	 * A link to the user's personal website. Available only with "user_website"
	 * or "friends_website" permission.
	 * 
	 * @return a link to the user's personal website.
	 */
	public String getWebsite() {
		return website;
	}

	/**
	 * An anonymous, but unique identifier for the user. Available only if
	 * requested by an authenticated user.
	 * 
	 * @return the user's third-party ID or null if not available
	 */
	public String getThirdPartyId() {
		return thirdPartyId;
	}
	
	/**
	 * The user's timezone offset from UTC.
	 * Available only for the authenticated user.
	 * @return the user's timezone offset from UTC or null if the user isn't the authenticated user
	 */
	public Integer getTimezone() {
		return timezone;
	}
	
	/**
	 * The last time the user's profile was updated.
	 * @return the time that the user's profile was updated
	 */
	public Date getUpdatedTime() {
		return updatedTime;
	}
	
	/**
	 * The user's account verification status.
	 * Available only if requested by an authenticated user.
	 * @return true if the profile has been verified, false if it has not, or null if not available.
	 */
	public Boolean isVerified() {
		return verified;
	}
	
	/**
	 * The user's brief about blurb.
	 * Available only with "user_about_me" permission for the authenticated user or "friends_about_me" for the authenticated user's friends.
	 * @return the user's about blurb, if available.
	 */
	public String getAbout() {
		return about;
	}

	/**
	 * The user's bio.
	 * Available only with "user_about_me" permission for the authenticated user.
	 * @return the user's bio, if available.
	 */
	public String getBio() {
		return bio;
	}
	
	/**
	 * The user's birthday.
	 * Available only with "user_birthday" permission for the authentication user or "friends_birthday" permission for the user's friends.
	 * @return the user's birthday
	 */
	public String getBirthday() {
		return birthday;
	}
	
	/**
	 * The user's location.
	 * Available only with "user_location" or "friends_location" permission.
	 * @return a {@link Reference} to the user's location, if available
	 */
	public Reference getLocation() {
		return location;
	}
	
	/**
	 * The user's hometown.
	 * Available only with "user_hometown" or "friends_hometown" permission.
	 * @return a {@link Reference} to the user's hometown, if available
	 */
	public Reference getHometown() {
		return hometown;
	}
	
	/**
	 * A list of the genders the user is interested in.
	 * Available only with "user_relationship_details" or "friends_relationship_details" permission.
	 * @return a list of genders the user is interested in, if available.
	 */
	public List<String> getInterestedIn() {
		return interestedIn;
	}

	/**
	 * The user's religion. 
	 * Available only with "user_religion_politics" or "friends_religion_politics" permission.
	 * @return the user's religion, if available.
	 */
	public String getReligion() {
		return religion;
	}

	/**
	 * The user's political affiliation. 
	 * Available only with "user_religion_politics" or "friends_religion_politics" permission.
	 * @return the user's political affiliation, if available.
	 */
	public String getPolitical() {
		return political;
	}

	/**
	 * The user's quotations. 
	 * Available only with "user_about_me" persmission.
	 * @return the user's quotations, if available.
	 */
	public String getQuotes() {
		return quotes;
	}

	/**
	 * The user's relationship status. 
	 * Available only with "user_relationships" or "friends_relationships" permission.
	 * @return the user's relationship status, if available.
	 */
	public String getRelationshipStatus() {
		return relationshipStatus;
	}

	/**
	 * The user's significant other. 
	 * Available only for certain relationship statuses and with "user_relationship_details" or "friends_relationship_details" permission.
	 * @return a {@link Reference} to the user's significant other, if available.
	 */
	public Reference getSignificantOther() {
		return significantOther;
	}

	/**
	 * The user's work history.
	 * Available only with "user_work_history" or "friends_work_history" permission.
	 * @return a list of {@link WorkEntry} items, one for each entry in the user's work history.
	 */
	public List<WorkEntry> getWork() {
		return work;
	}
	
	/**
	 * The user's education history.
	 * Available only with "user_education_history" or "friends_education_history" permission.
	 * @return a list of {@link EducationEntry} items, one for each entry in the user's education history.
	 */
	public List<EducationEntry> getEducation() {
		return education;
	}
	
	public static class Builder {
		private String id;

		private String name;

		private String firstName;

		private String lastName;
		
		private String username;
		
		private String gender;
		
		private Locale locale;

		private String email;

		private String link;
		
		private String website;

		private String thirdPartyId;

		private Integer timezone;

		private Date updatedTime;

		private Boolean verified;

		private String about;

		private String bio;

		private String birthday;

		private Reference location;

		private Reference hometown;
		
		private List<String> interestedIn;

		private String religion;

		private String political;

		private String quotes;

		private String relationshipStatus;

		private Reference significantOther;

		private List<WorkEntry> work;

		private List<EducationEntry> education;

		public Builder(String id, String username, String name, String firstName, String lastName, String gender, String locale) {
			this.id = id;
			this.username = username;
			this.name = name;
			this.firstName = firstName;
			this.lastName = lastName;
			this.gender = gender;
			this.locale = new Locale(locale);
		}

		public Builder email(String email) {
			this.email = email;
			return this;
		}

		public Builder link(String link) {
			this.link = link;
			return this;
		}
		
		public Builder website(String website) {
			this.website = website;
			return this;
		}

		public Builder thirdPartyId(String thirdPartyId) {
			this.thirdPartyId = thirdPartyId;
			return this;
		}

		public Builder timezone(Integer timezone) {
			this.timezone = timezone;
			return this;
		}

		public Builder updatedTime(Date updatedTime) {
			this.updatedTime = updatedTime;
			return this;
		}

		public Builder verified(Boolean verified) {
			this.verified = verified;
			return this;
		}

		public Builder about(String about) {
			this.about = about;
			return this;
		}

		public Builder bio(String bio) {
			this.bio = bio;
			return this;
		}

		public Builder birthday(String birthday) {
			this.birthday = birthday;
			return this;
		}

		public Builder location(Reference location) {
			this.location = location;
			return this;
		}

		public Builder hometown(Reference hometown) {
			this.hometown = hometown;
			return this;
		}
		
		public Builder interestedIn(List<String> interestedIn) {
			this.interestedIn = interestedIn;
			return this;
		}

		public Builder religion(String religion) {
			this.religion = religion;
			return this;
		}

		public Builder political(String political) {
			this.political = political;
			return this;
		}

		public Builder quotes(String quotes) {
			this.quotes = quotes;
			return this;
		}

		public Builder relationshipStatus(String relationshipStatus) {
			this.relationshipStatus = relationshipStatus;
			return this;
		}

		public Builder significantOther(Reference significantOther) {
			this.significantOther = significantOther;
			return this;
		}

		public Builder work(List<WorkEntry> work) {
			this.work = work;
			return this;
		}

		public Builder education(List<EducationEntry> education) {
			this.education = education;
			return this;
		}

		public FacebookProfile build() {
			FacebookProfile profile = new FacebookProfile(id, username, name, firstName, lastName, gender, locale);
			profile.email = email;
			profile.link = link;
			profile.thirdPartyId = thirdPartyId;
			profile.timezone = timezone;
			profile.updatedTime = updatedTime;
			profile.verified = verified;
			profile.about = about;
			profile.bio = bio;
			profile.birthday = birthday;
			profile.location = location;
			profile.hometown = hometown;
			profile.interestedIn = interestedIn;
			profile.religion = religion;
			profile.political = political;
			profile.quotes = quotes;
			profile.relationshipStatus = relationshipStatus;
			profile.significantOther = significantOther;
			profile.website = website;
			profile.work = work;
			profile.education = education;
			return profile;
		}
	}

}
