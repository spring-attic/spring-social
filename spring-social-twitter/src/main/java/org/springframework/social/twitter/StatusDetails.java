package org.springframework.social.twitter;

public class StatusDetails {
	private Float latitude;
	private Float longitude;
	private boolean displayCoordinates;

	public StatusDetails setLocation(float latitude, float longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		return this;
	}

	public StatusDetails setDisplayCoordinates(boolean displayCoordinates) {
		this.displayCoordinates = displayCoordinates;
		return this;
	}

	public boolean hasLocation() {
		return latitude != null && longitude != null;
	}

	public Float getLatitude() {
		return this.latitude;
	}

	public Float getLongitude() {
		return this.longitude;
	}

	public boolean isDisplayCoordinates() {
		return displayCoordinates;
	}
}
