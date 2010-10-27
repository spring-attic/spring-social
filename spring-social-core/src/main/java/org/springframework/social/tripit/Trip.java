package org.springframework.social.tripit;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Model class representing a trip taken by a TripIt user.
 * 
 * @author Craig Walls
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Trip {
	/**
	 * The trip ID
	 * 
	 * @return The trip ID
	 */
	public long getId() {
		return id;
	}

	/**
	 * The trip's display name
	 * 
	 * @return The trip's display name
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * The trip's primary location
	 * 
	 * @return The trip's primary location
	 */
	public String getPrimaryLocation() {
		return primaryLocation;
	}

	/**
	 * The date that the trip starts on
	 * 
	 * @return The date that the trip starts on
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * The date that the trip concludes
	 * 
	 * @return The date that the trip concludes
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * A URL to the trip page at TripIt
	 * 
	 * @return A URL to the trip page at TripIt
	 */
	public String getTripUrl() {
		return "http://www.tripit.com/" + tripPath;
	}

	@JsonProperty("id")
	long id;

	@JsonProperty("display_name")
	String displayName;

	@JsonProperty("start_date")
	Date startDate;

	@JsonProperty("end_date")
	Date endDate;

	@JsonProperty("primary_location")
	String primaryLocation;

	@JsonProperty("relative_url")
	String tripPath;
}
