package org.springframework.social.tripit;

import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeName;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName("Profile")
public class TripItProfile {
	
	public String getId() {
		return attributes.get("ref");
	}

	public String getScreenName() {
		return screenName;
	}

	public String getPublicDisplayName() {
		return publicDisplayName;
	}

	public String getHomeCity() {
		return homeCity;
	}

	public String getCompany() {
		return company;
	}

	public String getProfileUrl() {
		return "http://www.tripit.com/" + profilePath;
	}

	@JsonProperty("@attributes")
	private Map<String, String> attributes;

	@JsonProperty("screen_name")
	private String screenName;

	@JsonProperty("public_display_name")
	private String publicDisplayName;

	@JsonProperty("home_city")
	private String homeCity;

	@JsonProperty("company")
	private String company;

	@JsonProperty("profile_url")
	private String profilePath;
}
