package com.androidiot.dm;

import java.io.PrintStream;
import java.util.ArrayList;
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

	public void addLocation(String name, String strTimestamp, Location l) {
		double timestamp = Double.parseDouble(strTimestamp);
		AndroidClient ac = clients.get(name);
		if (ac == null) {
			ac = new AndroidClient();
			clients.put(name, ac);
		}
		ac.addLocation(timestamp, l);
	}

	public void setPreferences(String name, ClientPreferences pref) {
		AndroidClient ac = clients.get(name);
		if (ac == null) {
			ac = new AndroidClient();
			clients.put(name, ac);
		}
		ac.setPreferences(pref);
	}

	public void list(PrintStream stream) {
		stream.println("AndroidClientService listing");
		for (Map.Entry<String, AndroidClient> entry: clients.entrySet()) {
			stream.println("Client " + entry.getKey() + ": ");
			stream.println(entry.getValue());
		}
	}

	public ArrayList<AndroidClient> getClients() {
		return new ArrayList<AndroidClient>(clients.values());
	}

	public AndroidClient getClient(String name) {
		return clients.get(name);
	}

	public void deleteClients() {
		clients = new HashMap<String, AndroidClient>();
	}
}
