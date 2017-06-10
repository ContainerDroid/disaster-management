package com.androidiot.dm;

import java.util.ArrayList;

class SafeLocation {
	int safetyScore;
	float latitude;
	float longitude;

	public SafeLocation(ClientMessage cm) {
		this.safetyScore = 0;
		if (cm.msgtype.compareTo("safe-location") != 0) {
			System.err.println("Invalid client msgtype given in constructor");
			System.err.println("Expected safe-location, given " + cm);
			return;
		}
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
		return "SafeLocation: Latitude " + latitude + ", Longitude " +
			longitude + ", Safety " + safetyScore;
	}
};

public class SafeLocationService {
	private ArrayList<SafeLocation> locations;

	private static class LazyHolder {
		static final SafeLocationService INSTANCE =
			new SafeLocationService();
	}

	private SafeLocationService() {
		locations = new ArrayList<SafeLocation>();
	}

	public static SafeLocationService getInstance() {
		return LazyHolder.INSTANCE;
	}

	public void add(SafeLocation l) {
		locations.add(l);
	}

	public void list() {
		for (SafeLocation l: locations) {
			System.out.println(l);
		}
	}
}
