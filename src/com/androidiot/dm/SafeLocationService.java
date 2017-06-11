package com.androidiot.dm;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;

public class SafeLocationService {
	private ArrayList<Location> locations;

	private static class LazyHolder {
		static final SafeLocationService INSTANCE =
			new SafeLocationService();
	}

	private SafeLocationService() {
		locations = new ArrayList<Location>();
	}

	public static SafeLocationService getInstance() {
		return LazyHolder.INSTANCE;
	}

	public void add(Location l) {
		locations.add(l);
	}

	public void list(PrintStream stream) {
		for (Location l: locations) {
			stream.println(l);
		}
	}
}
