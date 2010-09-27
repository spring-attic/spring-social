package org.springframework.social.tripit;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Trip {
	@JsonProperty("id")
	private long id;

	@JsonProperty("display_name")
	private String displayName;

	@JsonProperty("start_date")
	private Date startDate;

	@JsonProperty("end_date")
	private Date endDate;

	@JsonProperty("primary_location")
	private String primaryLocation;

	@JsonProperty("relative_url")
	private String tripPath;

	public long getId() {
		return id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getPrimaryLocation() {
		return primaryLocation;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public String getTripUrl() {
		return "http://www.tripit.com/" + tripPath;
	}
}
