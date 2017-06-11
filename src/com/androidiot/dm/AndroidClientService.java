package com.androidiot.dm;

import java.util.HashMap;
import java.util.Map;

public class AndroidClientService {
	HashMap<String, AndroidClient> clients;

	private static class LazyHolder {
		static final AndroidClientService INSTANCE =
			new AndroidClientService();
	}

	private AndroidClientService() {
		clients = new HashMap<String, AndroidClient>();
	}

	public static AndroidClientService getInstance() {
		return LazyHolder.INSTANCE;
	}

	public void addLocation(String name, Location l) {
		AndroidClient ac = clients.get(name);
		if (ac == null) {
			ac = new AndroidClient();
			clients.put(name, ac);
		}
		ac.trajectory.add(l);
	}

	public void setPreferences(String name, ClientPreferences pref) {
		AndroidClient ac = clients.get(name);
		if (ac == null) {
			ac = new AndroidClient();
			clients.put(name, ac);
		}
		ac.setPreferences(pref);
	}

	public void list() {
		for (Map.Entry<String, AndroidClient> entry: clients.entrySet()) {
			System.out.println("Client " + entry.getKey() + ": " + entry.getValue());
		}
	}
}
