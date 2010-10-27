package org.springframework.social.linkedin;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Model class containing a user's LinkedIn profile information.
 * 
 * @author Craig Walls
 */
@XmlRootElement(name = "person")
public class LinkedInProfile {
	@XmlElement
	String id;

	@XmlElement(name = "first-name")
	String firstName;

	@XmlElement(name = "last-name")
	String lastName;

	@XmlElement
	String headline;

	@XmlElementWrapper(name = "site-standard-profile-request")
	@XmlElement(name = "url")
	String[] standardProfileUrls;

	@XmlElementWrapper(name = "site-public-profile-request")
	@XmlElement(name = "url")
	String[] publicProfileUrls;

	@XmlElement
	String industry;

	/**
	 * The user's LinkedIn profile ID
	 * 
	 * @return The user's LinkedIn profile ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * The user's first name
	 * 
	 * @return The user's first name
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * The user's last name
	 * 
	 * @return The user's last name
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * The user's headline
	 * 
	 * @return The user's headline
	 */
	public String getHeadline() {
		return headline;
	}

	/**
	 * The user's industry
	 * 
	 * @return The user's industry
	 */
	public String getIndustry() {
		return industry;
	}

	/**
	 * A URL to the user's standard profile. The content shown at this profile
	 * will depend upon what the requesting user is allowed to see.
	 * 
	 * @return the URL to the user's standard profile
	 */
	public String getStandardProfileUrl() {
		return standardProfileUrls != null && standardProfileUrls.length > 0 ? standardProfileUrls[0] : null;
	}

	/**
	 * A URL to the user's public profile. The content shown at this profile is
	 * intended for public display and is determined by the user's privacy
	 * settings.
	 * 
	 * @return the URL of the user's public profile
	 */
	public String getPublicProfileUrl() {
		return publicProfileUrls != null && publicProfileUrls.length > 0 ? publicProfileUrls[0] : null;
	}
}
