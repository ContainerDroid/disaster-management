package com.androidiot.dm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

class ConnectionWorker implements Runnable {
	private Socket socket = null;
	private Gson g;
	volatile Boolean isStopped = false;

	public ConnectionWorker(Socket socket) {
		this.socket = socket;
		this.g = new Gson();
	}

	public void stop() {
		isStopped = true;
	}

	private void sendSafeLocations(OutputStream out, String clientName) {
		PrintStream stream = new PrintStream(out, true);
		AndroidClientService acs = AndroidClientService.getInstance();
		SafeLocationService sls = SafeLocationService.getInstance();

		sls.list(stream);
	}

	public void run() {
		AndroidClientService acs = AndroidClientService.getInstance();
		SafeLocationService sls = SafeLocationService.getInstance();

		try {
			OutputStream output = socket.getOutputStream();
			BufferedReader input = new BufferedReader(
				new InputStreamReader(socket.getInputStream()));

			ClientMessage cm;
			String msg;

			while ((msg = input.readLine()) != null) {
				if (isStopped) {
					input.close();
					break;
				}
				cm = g.fromJson(msg, ClientMessage.class);
				if (cm.msgtype.compareTo("client-location") == 0) {
					acs.addLocation(cm.name, new Location(cm));
					acs.list();
				} else if (cm.msgtype.compareTo("safe-location") == 0) {
					sls.add(new Location(cm));
					sls.list(System.out);
				} else if (cm.msgtype.compareTo("safe-location-preferences") == 0) {
					acs.setPreferences(cm.name, new ClientPreferences(cm));
				} else if (cm.msgtype.compareTo("safe-location-request") == 0) {
					sendSafeLocations(output, cm.name);
				} else {
					System.err.println("Unknown msgtype received for " + cm);
				}
				long time = System.currentTimeMillis();
				System.out.println("Request processed: " + time);
			}
		} catch (IOException e) {
			//report exception somewhere.
			e.printStackTrace();
		}
	}
}

public class AndroidProxyService implements Runnable {
	int port;
	volatile Boolean isStopped = false;
	ArrayList<ConnectionWorker> workers;

	public AndroidProxyService(int port) {
		this.port = port;
		workers = new ArrayList<ConnectionWorker>();
	}

	public void stop() {
		isStopped = true;
		for (ConnectionWorker t: workers) {
			t.stop();
		}
	}

	public void run() {
		ServerSocket ss;
		try {
			ss = new ServerSocket(port);
		} catch (java.io.IOException ex) {
			System.err.println("Failed to listen on port " + port);
			return;
		}

		while (isStopped == false) {
			try {
				Socket cs = ss.accept();
				ConnectionWorker w = new ConnectionWorker(cs);
				workers.add(w);
				new Thread(w).start();
			} catch (java.io.IOException ex) {
				System.err.println(
					"Failed to connect to client");
			}
		}
	}
}
