package org.springframework.social.linkedin;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "person")
public class LinkedInProfile {
	@XmlElement
	private String id;

	@XmlElement(name = "first-name")
	private String firstName;

	@XmlElement(name = "last-name")
	private String lastName;

	@XmlElement
	private String headline;

	@XmlElementWrapper(name = "site-standard-profile-request")
	@XmlElement(name = "url")
	private String[] standardProfileUrls;

	@XmlElementWrapper(name = "site-public-profile-request")
	@XmlElement(name = "url")
	private String[] publicProfileUrls;

	@XmlElement
	private String industry;

	public String getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getHeadline() {
		return headline;
	}

	public String getIndustry() {
		return industry;
	}

	public String getStandardProfileUrl() {
		return standardProfileUrls != null && standardProfileUrls.length > 0 ? standardProfileUrls[0] : null;
	}

	public String getPublicProfileUrl() {
		return publicProfileUrls != null && publicProfileUrls.length > 0 ? publicProfileUrls[0] : null;
	}
}
