package org.springframework.social.tripit;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TripItProfileResponse {
	private long timestamp;

	@JsonProperty("Profile")
	TripItProfile profile;

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public TripItProfile getProfile() {
		return profile;
	}
}
