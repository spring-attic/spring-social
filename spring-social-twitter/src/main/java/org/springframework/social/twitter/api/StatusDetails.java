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
package org.springframework.social.twitter.api;

import java.util.HashMap;
import java.util.Map;

/**
 * Carries optional metadata pertaining to a Twitter status update.
 * @author Craig Walls
 */
public class StatusDetails {
	private Float latitude;
	private Float longitude;
	private boolean displayCoordinates;

	/**
	 * Sets the location of the status update in latitude and longitude.
	 * Latitude values must be between -90.0 (south) and +90.0 (north).
	 * Longitude values must be between -180.0 (west) and +180.0 (east).
	 * 
	 * For example, latitude=51.502 and longitude=-0.126 are the coordinates for
	 * Westminster, London.
	 * 
	 * @param latitude
	 *            The latitude element of the location. Must be between -90.0
	 *            and +90.0, where positive values are north and negative values
	 *            are south.
	 * @param longitude
	 *            The longitude element of the location. Must be between -180.0
	 *            and +180.0, where positive values are east and negative values
	 *            are west.
	 * @return The {@link StatusDetails} object
	 */
	public StatusDetails setLocation(float latitude, float longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		return this;
	}

	/**
	 * Indicates that Twitter should pinpoint the location precisely when
	 * displaying it on a map. By default, Twitter will display the status along
	 * with a map showing the general area where the tweet came from. If display
	 * coordinates is true, however, it will display a map with a pin indicating
	 * the precise location of the status update.
	 * 
	 * @param displayCoordinates
	 *            If true, will pinpoint the location of the status update.
	 * @return The {@link StatusDetails} object
	 */
	public StatusDetails setDisplayCoordinates(boolean displayCoordinates) {
		this.displayCoordinates = displayCoordinates;
		return this;
	}

	/**
	 * Maps the {@link StatusDetails} values to a Map of Twitter parameters.
	 * 
	 * @return A {@link Map} of parameters to be passed along in the status
	 *         update post to Twitter.
	 */
	public Map<String, Object> toParameterMap() {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		if (latitude != null && longitude != null) {
			parameterMap.put("lat", latitude.toString());
			parameterMap.put("long", longitude.toString());
		}

		if (displayCoordinates) {
			parameterMap.put("display_coordinates", "true");
		}
		return parameterMap;
	}
}
