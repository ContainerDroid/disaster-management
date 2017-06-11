package com.androidiot.dm;

public class Location {
	double latitude;
	double longitude;

	public Location(ClientMessage cm) {
		try {
			this.latitude  = Double.parseDouble(cm.latitude);
			this.longitude = Double.parseDouble(cm.longitude);
		} catch (NumberFormatException ex) {
			System.err.println("Invalid double for latitude " +
					"or longitude");
		}
	}

	@Override
	public String toString() {
		return "Location: Longitude " + longitude +
			", Latitude " + latitude;
	}
}
