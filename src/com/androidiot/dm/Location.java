package com.androidiot.dm;

public class Location {
	float latitude;
	float longitude;

	public Location(ClientMessage cm) {
		try {
			this.latitude  = Float.parseFloat(cm.latitude);
			this.longitude = Float.parseFloat(cm.longitude);
		} catch (NumberFormatException ex) {
			System.err.println("Invalid float for latitude " +
					"or longitude");
		}
	}

	@Override
	public String toString() {
		return "Location: Longitude " + longitude +
			", Latitude " + latitude;
	}
}
