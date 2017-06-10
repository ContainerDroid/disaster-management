package com.androidiot.dm;

import java.util.ArrayList;

class ClientLocation {
	float latitude;
	float longitude;

	public ClientLocation(ClientMessage cm) {
		if (cm.msgtype.compareTo("client-location") != 0) {
			System.err.println("Invalid client message given in constructor");
			System.err.println("Expected client-location, given " + cm);
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
		return "ClientLocation: Longitude " + longitude +
			", Latitude " + latitude;
	}
}


public class ClientLocationService {
	ArrayList<ClientLocation> locations;

	private static class LazyHolder {
		static final ClientLocationService INSTANCE =
			new ClientLocationService();
	}

	private ClientLocationService() {
		locations = new ArrayList<ClientLocation>();
	}

	public static ClientLocationService getInstance() {
		return LazyHolder.INSTANCE;
	}

	public void add(ClientLocation l) {
		locations.add(l);
	}

	public void list() {
		for (ClientLocation l: locations) {
			System.out.println(l);
		}
	}
}
