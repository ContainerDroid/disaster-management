package com.androidiot.dm;

import java.lang.Math;

public class Location {
	double latitude;
	double longitude;

	public double getDistanceKmTo(Location other) {
		/* Courtesy goes to:
		 * http://jonisalonen.com/2014/computing-distance-between-coordinates-can-be-simple-and-fast/
		 */
		/* Length in km of a degree in longitude at Equator */
		final double degreeLength = 110.25;
		double x = (this.latitude  - other.latitude);
		double y = (this.longitude - other.longitude) * Math.cos(this.longitude);
		return degreeLength * Math.sqrt(x*x + y*y);
	}

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
