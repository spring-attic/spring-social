package org.springframework.social.greenhouse;

import org.codehaus.jackson.annotate.JsonProperty;

public class GreenhouseProfile {
	@JsonProperty
	private long accountId;

	@JsonProperty
	private String displayName;

	@JsonProperty
	private String pictureUrl;

	public long getAccountId() {
		return accountId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}
}
