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
package org.springframework.social.tripit;

import java.util.Date;

/**
 * Model class representing a trip taken by a TripIt user.
 * 
 * @author Craig Walls
 */
public class Trip {
	public Trip(long id, String displayName, String primaryLocation, Date startDate, Date endDate, String tripPath) {
		this.id = id;
		this.displayName = displayName;
		this.primaryLocation = primaryLocation;
		this.startDate = startDate;
		this.endDate = endDate;
		this.tripPath = tripPath;
	}

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
		return "http://www.tripit.com" + tripPath;
	}

	private final long id;
	private final String displayName;
	private final Date startDate;
	private final Date endDate;
	private final String primaryLocation;
	private final String tripPath;
}
