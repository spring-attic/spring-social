/*
 * Copyright 2011 the original author or authors.
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
package org.springframework.social.facebook;

public class Location {
	private String id;

	private String name;

	private double latitude;

	private double longitude;

	private String street;

	private String city;

	private String state;

	private String country;

	private String zip;

	private Location(String id, String name, double latitude, double longitude) {
		this.id = id;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public String getStreet() {
		return street;
	}

	public String getCity() {
		return city;
	}

	public String getState() {
		return state;
	}

	public String getCountry() {
		return country;
	}

	public String getZip() {
		return zip;
	}

	public static class Builder {
		private String id;

		private String name;

		private double latitude;

		private double longitude;

		private String street;

		private String city;

		private String state;

		private String country;

		private String zip;

		public Builder(String id, String name, double latitude, double longitude) {
			this.id = id;
			this.name = name;
			this.latitude = latitude;
			this.longitude = longitude;
		}

		public Builder street(String street) {
			this.street = street;
			return this;
		}

		public Builder city(String city) {
			this.city = city;
			return this;
		}

		public Builder state(String state) {
			this.state = state;
			return this;
		}

		public Builder country(String country) {
			this.country = country;
			return this;
		}

		public Builder zip(String zip) {
			this.zip = zip;
			return this;
		}

		public Location build() {
			Location location = new Location(id, name, latitude, longitude);
			location.street = street;
			location.city = city;
			location.state = state;
			location.country = country;
			location.zip = zip;
			return location;
		}
	}
}
